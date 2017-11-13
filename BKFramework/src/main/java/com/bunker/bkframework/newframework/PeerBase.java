package com.bunker.bkframework.newframework;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.bunker.bkframework.sec.Secure;
import com.bunker.bkframework.sec.SecureCallback;
import com.bunker.bkframework.sec.SecureFactory;

/**
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 * 
 * non-thread safe(�����δ� ������ ���������� �����ֱ�� �ϳ��� ����Ѵ�), non-reEntrant, ����������Ŭ�� ���� ����(��Ŷó�� ������ ����ȭ)
 * Ŭ���̾�Ʈ�� �ϳ��� �����忡���� ����Ǹ� ���� ����ǰ� �ִ�
 * �����尡 ���� ��� �������� �����忡 ó���� ������ �ְ����� ���´�.
 * �����尡 ����Ǹ� ó������ ���� ��Ŷ�� ���� ���� ���
 * ����������Ŭ�� ����� ��Ų��.
 * 
 * ����Ͻ� ������ �� �ܰ�
 * ��Ŷ�� ������� �����ϰ� ��Ŷ�� �������� ��� ������ ��Ŷ���� ���ļ�
 * ����Ͻ� �������� �����Ѵ�.
 * 
 * 
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 26.
 *
 *
 */
abstract public class PeerBase<PacketType> implements Peer<PacketType>, PacketReceiver<PacketType>, SecureCallback {
	// ----------------------------------공유되는 자원----------------------------------
	public static long currentTime = Calendar.getInstance().getTimeInMillis();
	private PacketFactory<PacketType> mPacketFactory;
	private SecureFactory<PacketType> mSecureFactory;
	private final String _TAG = "PeerBase";

	// ----------------------------------개별 자원----------------------------------

	/**
	 * ���⼭ �����ϴ� �⺻���� �ƴ� ��ü���� ������Ÿ������ �����ȴ�.
	 */
	/** ó���� �������� ������ ��Ŷ�� �������� ���� ��Ŷ��*/
	private List<Packet<PacketType>> mAccumList;
	/** ����ؾ��� ������ ����Ŭ*/
	private LifeCycle mLifeCycle;
	/** ���� ó������ ���� ��Ŷ��  */
	private List<PacketType> mNonPrehandleList;
	private Object mNonPreHandleMutex;
	/** Ŭ���̾�Ʈ �����尡 �����ֱ� �ȿ� �ִ���*/
	private boolean isHandling = false;
	/** ������ ���� Ŭ����*/
	protected Writer<PacketType> mWriter;
	protected PacketReceiver<PacketType> mReceiver;
	/** ��ȣȭ ���� Ŭ����*/
	private Secure<PacketType> mSecure;
	private boolean isStreamSet = false;
	private boolean mClosed = false;

	// ----------------------------------��Ȳ�� ���� ������ ���� �ִ� �ڿ���----------------------------------

	public PeerBase(PacketFactory<PacketType> factory) {
		this(factory, null);
	}

	public PeerBase(PacketFactory<PacketType> packFact, SecureFactory<PacketType> secFac) {
		mPacketFactory = packFact;
		mSecureFactory = secFac;
		init();
	}

	/**
	 * ����������Ŭ�� �����Ѵ�
	 * @param lifeCycle ����� ������ ����Ŭ
	 */
	@Override
	public void setLifeCycle(LifeCycle lifeCycle) {
		mLifeCycle = lifeCycle;
	}

	/**
	 * ��Ŷ�� ����� ���̷ε�� �����ؼ� ��üȭ ��Ų��.
	 * @param readCurrent �о���� ���� (��� + ���̷ε�)
	 * @return
	 */
	private Packet<PacketType> preHandling(PacketType readCurrent) {
		return mPacketFactory.creatPacket(readCurrent);
	}

	@Override
	public void life() {
		synchronized (mNonPreHandleMutex) {
			Iterator<PacketType> iterator = mNonPrehandleList.iterator();

			while (iterator.hasNext()) {
				pushPacket(iterator.next());
				iterator.remove();
			}
		}
	}

	/**
	 * ��Ŷ�� �� ������ ó���ؼ� {@link #mAccumList}�� �ִ� �޼���
	 * ���� ��Ŷ�� ������ ��Ŷ�� ��� ������ ��Ŷ���� �ջ��Ͽ� �ϳ��� ��Ŷ���� �����
	 * ���� Ŭ������ �����Ѵ�.
	 * @param bytes ó���� ��Ŷ(��� + ���̷ε�)
	 */
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
			Logger.err(_TAG, "run exception");
		}
	}

	/**
	 * {@link #mAccumList}�� �׿��ִ� ��Ŷ���� �ϳ��� ��ģ��.
	 * @return �ϳ��� ������ ��Ŷ
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
		//����ȭ ����
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
		 * ������ ���� �߻��ؼ��� �ȵȴ�.
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
	 * ������ �ʱ�ȭ
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
		//����ȭ ����
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
}