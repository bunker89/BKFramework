package com.bunker.bkframework.newframework;

/**
 * 클라이언트의 라이프사이클을 관리하기 위한 인터페이스
 * 라이프사이클만큼 {@link #life()}메소드가 계속 실행된다.
 * 생명주기가 종료되기 전 {@link #needRecycle()}로 생명의 연장이 필요한지
 * 확인을 하고 종료가 가능한 상태이면 false를 반환하여 생명주기를 종료시킨다.
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 27.
 *
 *
 */
public interface PeerLife {
	/**
	 * 생명주기동안 반복 호출되는 메서드
	 */
	public void life();
	/**
	 * 생명주기가 종료되기전 연장 여부를 알아보는 메서드
	 * 생명 연장이 필오ㅛ
	 * @return 생명주기 연장이 필요한지 여부
	 */
	public boolean needRecycle();
	
	/**
	 * 생명주기를 빌린다.
	 */
	public boolean interceptCycle();
}
