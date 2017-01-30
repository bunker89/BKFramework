package com.bunker.bkframework.newframework;

/**
 * PeerLife�� �����ϴ� �������̽�
 * 
 * ----------------------
 * �ǰ����
 * 1.
 * ��� Thread-safe �ؾ��ϸ� ReEntrant�ϰ� ����⸦ �ǰ��Ѵ�.
 * 
 * 2.
 * ������ �Ϸ�� �Ŀ� {@link #networkInited()}�� ȣ���� �� ���� �ǰ��Ѵ�.
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