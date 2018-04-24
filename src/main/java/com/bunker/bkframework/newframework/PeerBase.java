package com.bunker.bkframework.newframework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bunker.bkframework.sec.Secure;
import com.bunker.bkframework.sec.SecureCallback;
import com.bunker.bkframework.sec.SecureFactory;

/**
 * 
 * 한글한글
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 * 
 * 
 * 
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 26.
 *
 *
 */
abstract public class PeerBase<PacketType> implements Peer<PacketType>, PacketReceiver<PacketType>, SecureCallback {
	// ----------------------------------怨듭쑀�릺�뒗 �옄�썝----------------------------------
	public static long currentTime = Calendar.getInstance().getTimeInMillis();
	private PacketFactory<PacketType> mPacketFactory;
	private SecureFactory<PacketType> mSecureFactory;
	private final String _TAG = getClass().getName();

	// ----------------------------------媛쒕퀎 �옄�썝----------------------------------

	private List<Packet<PacketType>> mAccumList;
	private LifeCycle mLifeCycle;
	private List<PacketType> mNonPrehandleList;
	private List<PacketType> mProcessing;
	private Object mNonPreHandleMutex;
	private boolean isHandling = false;
	protected Writer<PacketType> mWriter;
	protected PacketReceiver<PacketType> mReceiver;
	private Secure<PacketType> mSecure;
	private boolean isStreamSet = false;
	private boolean mClosed = false;

	// ----------------------------------占쏙옙황占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쌍댐옙 占쌘울옙占쏙옙----------------------------------

	public PeerBase(PacketFactory<PacketType> factory) {
		this(factory, null);
	}

	public PeerBase(PacketFactory<PacketType> packFact, SecureFactory<PacketType> secFac) {
		mPacketFactory = packFact;
		mSecureFactory = secFac;
		init();
	}

	/**
	 * @param lifeCycle 占쏙옙占쏙옙占� 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙클
	 */
	@Override
	public void setLifeCycle(LifeCycle lifeCycle) {
		mLifeCycle = lifeCycle;
	}

	/**
	 * @param readCurrent 占싻억옙占쏙옙占� 占쏙옙占쏙옙 (占쏙옙占� + 占쏙옙占싱로듸옙)
	 * @return
	 */
	private Packet<PacketType> preHandling(PacketType readCurrent) {
		return mPacketFactory.creatPacket(readCurrent);
	}

	@Override
	public void life() {
		Iterator<PacketType> iterator;
		synchronized (mNonPreHandleMutex) {
			iterator = mNonPrehandleList.iterator();
			mProcessing = mNonPrehandleList;
			mNonPrehandleList = new LinkedList<>();
		}

		while (iterator.hasNext()) {
			pushPacket(iterator.next());
			iterator.remove();
		}
		mProcessing = null;
	}

	private void pushPacket(PacketType bytes) {
		Packet<PacketType> packet = preHandling(bytes);
		mAccumList.add(packet);

		if (packet.isFinal()) {
			if (packet.isFramworkPacket()) {

			} else {
				Packet<PacketType> result = combinePacket();
				mReceiver.decodePacket(result.getData(), packet.getSequence());
				mAccumList.clear();
			}
		}
	}

	@Override
	final public void run() {
		try {
			mLifeCycle.manageLife(this);
		} catch (Exception e) {
			long id = Logger.err(_TAG, "run exception", e);
			try {
				logPacket(id, mAccumList, mNonPrehandleList, mProcessing, null);
			} catch (IOException e1) {
				Logger.err(_TAG, "framework packet logging exception", e1);
			}
		}
	}

	/**
	 * {@link #mAccumList}占쏙옙 占쌓울옙占쌍댐옙 占쏙옙킷占쏙옙占쏙옙 占싹놂옙占쏙옙 占쏙옙친占쏙옙.
	 * @return 占싹놂옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙킷
	 */
	private Packet<PacketType> combinePacket() {
		int size = 0;

		for (Packet<PacketType> p : mAccumList) {
			size += p.getSize();
		}

		Packet<PacketType> result = mPacketFactory.creatPacket(size);

		for (Packet<PacketType> p: mAccumList) {
			result.putDataAtLast(p.getData());
		}

		result.putFrameworkPacketFlag(mAccumList.get(0).isFramworkPacket());
		return result;
	}

	@Override
	final public boolean dispatch(PacketType read) {
		boolean ret;
		//占쏙옙占쏙옙화 占쏙옙占쏙옙
		synchronized (mNonPreHandleMutex) {
			ret = isHandling;
			if (isHandling == false) {
				isHandling = true;
			}
			mNonPrehandleList.add(read);
		}
		return ret;
	}

	@Override
	public boolean needRecycle() {
		boolean ret;
		synchronized (mNonPreHandleMutex) {
			if (mNonPrehandleList.isEmpty())
				isHandling = false;
			ret = isHandling;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Peer<PacketType> clone() throws CloneNotSupportedException {
		PeerBase<PacketType> cloned = (PeerBase<PacketType>) super.clone();
		cloned.init();
		return cloned;
	}

	@Override
	public void close() {
		/**
		 * 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쌩삼옙占쌔쇽옙占쏙옙 占싫된댐옙.
		 */
		if (mWriter != null) {
			mWriter.destroy();
		}
		mClosed = true;
	}

	@Override
	public void setWriter(Writer<PacketType> writer) {
		mWriter = writer;
	}

	/**
	 * 占쏙옙占쏙옙占쏙옙 占십깍옙화
	 */
	private final void init() {
		mReceiver = this;
		mAccumList = new LinkedList<>();
		mNonPrehandleList = new LinkedList<>();
		mNonPreHandleMutex = new Object();
	}

	@Override
	public PacketReceiver<PacketType> unDecoReceiver() {
		return this;
	}

	public void setSecureDynamic(Secure<PacketType> sec) {
		if (!isStreamSet)
			throw new RuntimeException("PeerBase" + "'s network is already connected, Dynamic secure setting must be called after networkConnected()");
		sec.startHandShaking(mWriter, mReceiver, this);
		mReceiver = sec;
		mWriter = sec;
		mSecure = sec;
	}

	public void disableSec(Secure<PacketType> sec) {
		mReceiver = sec.unDecoReceiver();
		mWriter = sec.unDecoWriter();
		mSecure = sec.unDecoSecure(sec);
	}

	@Override
	public void networkInited(Resource<PacketType> resource) {
		if (!isStreamSet) {
			isStreamSet = true;
			if (mSecureFactory != null) {
				setSecureDynamic(mSecureFactory.createSecure());
			}
		} else 
			throw new RuntimeException("Peerbase networkConnected() is called before, this method must be called once");
	}

	@Override
	public boolean interceptCycle() {
		boolean ret;
		//占쏙옙占쏙옙화 占쏙옙占쏙옙
		synchronized (mNonPreHandleMutex) {
			ret = isHandling;
			if (isHandling == false) {
				isHandling = true;
			}
		}
		return !ret;
	}

	@Override
	public void handShaked() {
	}

	@Override
	public void secureFault() {
	}

	@Override
	public boolean isClosed() {
		return mClosed;
	}

	protected void logPacket(long logId, List<Packet<PacketType>> accum, List<PacketType> nonPre, List<PacketType> processing, String message) throws IOException {
		File errorFolder = new File("framework_error");
		errorFolder.mkdirs();
		File folder = new File(errorFolder, logId + "");
		folder.mkdir();

		logMessage(logId, folder, message);

		int index = 0;

		if (accum != null) {
			Iterator<Packet<PacketType>> accumIter = accum.iterator();

			while (accumIter.hasNext()) {
				File file = new File(folder, logId + "_" + index++);
				FileOutputStream output = new FileOutputStream(file);
				mPacketFactory.logging(output, accumIter.next());
				output.close();
			}
		}

		if (nonPre != null)
			index = logPacketType(logId, folder, nonPre, index);
		if (processing != null) 
			index = logPacketType(logId, folder, processing, index);
	}

	private int logPacketType(long logId, File folder, List<PacketType> packets, int index) throws IOException {
		Iterator<PacketType> nonPreIter = packets.iterator();

		while (nonPreIter.hasNext()) {
			File file = new File(folder, logId + "_" + index++);
			FileOutputStream output = new FileOutputStream(file);
			mPacketFactory.logging(output, nonPreIter.next());
			output.close();
		}

		return index;
	}

	private void logMessage(long logId, File folder, String message) throws IOException {
		File file = new File(folder, logId + "_message");
		FileOutputStream output = new FileOutputStream(file, true);
		output.write(message.getBytes());
		output.close();		
	}
}