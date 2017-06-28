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
 * non-thread safe(실제로는 쓰레드 세이프지만 생명주기는 하나만 사용한다), non-reEntrant, 라이프사이클은 단일 실행(패킷처리 구조만 동기화)
 * 클라이언트는 하나의 쓰레드에서만 실행되며 만약 실행되고 있는
 * 쓰레드가 있을 경우 실행중인 쓰레드에 처리할 내용을 주가시켜 놓는다.
 * 쓰레드가 종료되며 처리되지 않은 패킷이 남아 있을 경우
 * 라이프사이클을 재실행 시킨다.
 * 
 * 비즈니스 로직의 전 단계
 * 패킷을 순서대로 저장하고 패킷의 마지막일 경우 누적된 패킷들을 합쳐서
 * 비즈니스 로직으로 전달한다.
 * 
 * 동기화순서
 * 1. 라이프에서 무조건 현재값 먼저 동기화 시킨다.
 * 2. 현재값을 다 처리하고 밀린 값들을 처리한다
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 26.
 *
 *
 */
abstract public class PeerBase<PacketType> implements Peer<PacketType>, PacketReceiver<PacketType>, SecureCallback {
	// ----------------------------------공유되는 자원들----------------------------------
	public static long currentTime = Calendar.getInstance().getTimeInMillis();
	private PacketFactory<PacketType> mPacketFactory;
	private SecureFactory<PacketType> mSecureFactory;

	// ----------------------------------개별 생성되는 자원들----------------------------------

	/**
	 * 여기서 생성하는 기본형이 아닌 객체들은 프로토타입으로 공유된다.
	 */
	/** 처리가 끝났으나 마지막 패킷이 도착하지 않은 패킷들*/
	private List<Packet<PacketType>> mAccumList;
	/** 사용해야할 라이프 사이클*/
	private LifeCycle mLifeCycle;
	/** 아직 처리되지 않은 패킷들  */
	private List<PacketType> mNonPrehandleList;
	/** 클라이언트 쓰레드가 생명주기 안에 있는지*/
	private boolean isHandling = false;
	/** 답장을 위한 클래스*/
	protected Writer<PacketType> mWriter;
	protected PacketReceiver<PacketType> mReceiver;
	/** 암호화 래핑 클래스*/
	private Secure<PacketType> mSecure;
	private boolean isStreamSet = false;
	private Resource<PacketType> mResource;

	// ----------------------------------상황에 따라 공유할 수도 있는 자원들----------------------------------

	public PeerBase(PacketFactory<PacketType> factory) {
		this(factory, null);
	}

	public PeerBase(PacketFactory<PacketType> packFact, SecureFactory<PacketType> secFac) {
		mPacketFactory = packFact;
		mSecureFactory = secFac;
		init();
	}

	/**
	 * 라이프사이클을 지정한다
	 * @param lifeCycle 사용할 라이프 사이클
	 */
	@Override
	public void setLifeCycle(LifeCycle lifeCycle) {
		mLifeCycle = lifeCycle;
	}

	/**
	 * 패킷을 헤더와 페이로드로 구별해서 객체화 시킨다.
	 * @param readCurrent 읽어들인 버퍼 (헤더 + 페이로드)
	 * @return
	 */
	private Packet<PacketType> preHandling(PacketType readCurrent) {
		return mPacketFactory.creatPacket(readCurrent);
	}

	@Override
	public void life() {
		synchronized (mNonPrehandleList) {
			Iterator<PacketType> iterator = mNonPrehandleList.iterator();

			while (iterator.hasNext()) {
				pushPacket(iterator.next());
				iterator.remove();
			}
		}
	}

	/**
	 * 패킷의 한 단위를 처리해서 {@link #mAccumList}에 넣는 메서드
	 * 만약 패킷이 마지막 패킷일 경우 이전의 패킷들을 합산하여 하나의 패킷으로 만들고
	 * 하위 클래스로 전달한다.
	 * @param bytes 처리할 패킷(헤더 + 페이로드)
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
		mLifeCycle.manageLife(this);
	}

	/**
	 * {@link #mAccumList}에 쌓여있는 패킷을을 하나로 합친다.
	 * @return 하나로 합쳐진 패킷
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
		//동기화 시작
		synchronized (mNonPrehandleList) {
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
		synchronized (mNonPrehandleList) {
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
		 * 에러가 절대 발생해서는 안된다.
		 */
		if (mWriter != null) {
			mWriter.destroy();
		}
	}

	@Override
	public void setWriter(Writer<PacketType> writer) {
		mWriter = writer;
	}

	/**
	 * 실행전 초기화
	 */
	private final void init() {
		mReceiver = this;
		mAccumList = new LinkedList<>();
		mNonPrehandleList = new LinkedList<>();
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
			throw new RuntimeException("Peerbase networkConnected() is called before, this methos must be called once");
	}

	@Override
	public boolean interceptCycle() {
		boolean ret;
		//동기화 시작
		synchronized (mNonPrehandleList) {
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
}