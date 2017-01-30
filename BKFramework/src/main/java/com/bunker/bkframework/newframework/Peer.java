package com.bunker.bkframework.newframework;

/**
 *
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 * 
 * 네트워크 송수신과 비즈니스 로직 사이의 로직, Peer 인터페이스는 오직 네트워크 데이터 처리 로직만 관련되어 있다.
 * 하위 클래스에서 필요한 경우 암호화, 복호화 처리를 구현한다.
 * 클라이언트측과 서버측에서 공통으로 사용한다.
 * 
 * ----------------------
 * 권고사항
 * 1.
 * @see LifeCycle Peer의 생명 주기를 직접 관리하지 않고 LifeCycle클래스를 통해 생명주기를 관리하도록 한다.
 * 다시말해 {@link #dispatch(Object)}로 들어온 데이터를 곧바로 핸들링 하지 않고
 * {@link #setLifeCycle(LifeCycle)}로 들어온 LifeCycle을 저장해 두었다가 
 * {@link LifeCycle#manageLife(PeerLife)}를 호출하여 생명주기를 관리받도록 한다.
 * 생명 주기를 분리시킴으로서 Core의 동작 형태(roop, event-driven)에 따른 의존을 없앨 수 있다.
 * 
 * 2.
 * 연결이 완료된 후에 {@link #networkInited()}를 호출해 줄 것을 권고한다.
 * ----------------------
 * 
 * 
 * ----------------------
 * 주의사항
 * 1.
 * @see #networkInited()가 완료된 후 남은 로직이 모두 수행되도록 한다.
 * ----------------------
 * 
 * 
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 5.
 *
 *
 */
public interface Peer <PacketType> extends Runnable, Cloneable, PeerLife {
	/**
	 * 패킷을 핸들링 하기 위해 네트워크 송수신 로직에서 읽어들인 데이터를 전송받는다.
	 * @param read 패킷
	 * @return
	 */
	public boolean dispatch(PacketType read);
	
	/**
	 * peer의 연결을 종료한다.
	 * close를 부르는 객체가 Peer의 Thread가 아니라 Core일 수도 있다. 
	 */
	public void close();
	
	/**
	 * clone
	 * @return 복제된 클래스
	 * @throws CloneNotSupportedException
	 */
	public Peer<PacketType >clone() throws CloneNotSupportedException;
	
	/**
	 * 생명 주기를 관리할 LifeCycle 클래스를 전달받는다.
	 * 자세한 사항은 권고사항 1.을 참조한다.
	 * 
	 * @param l
	 */
	public void setLifeCycle(LifeCycle l);
	
	/**
	 * 쓰기 위한 객체를 지정한다.
	 * @see Writer 쓰기 부분을 분리 시킴으로서 버퍼의 형태와 관련된 의존을 없앤다. 
	 * 
	 * @param writer
	 */
	public void setWriter(Writer<PacketType> writer);
	
	/**
	 * 네트워크가 연결되어
	 * writer와 packetReceiver가 지정 되었을 때 호출한다.
	 * 반드시 모든 로직은 networkInited()가 완료 된 후 처리된다.
	 * 
	 * @param resource 객체의 리소스
	 */
	public void networkInited(Resource<PacketType> resource);
}