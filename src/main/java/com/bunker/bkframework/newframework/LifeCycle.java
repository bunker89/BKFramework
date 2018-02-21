package com.bunker.bkframework.newframework;

/**
 * PeerLife를 관리하는 인터페이스
 * 
 * ----------------------
 * 권고사항
 * 1.
 * 적어도 Thread-safe 해야하며 ReEntrant하게 만들기를 권고한다.
 * 
 * 2.
 * 연결이 완료된 후에 {@link #networkInited()}를 호출해 줄 것을 권고한다.
 * ----------------------
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 6.
 *
 *
 */
public interface LifeCycle {
	public void manageLife(PeerLife life);
}