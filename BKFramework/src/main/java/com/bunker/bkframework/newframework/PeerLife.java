package com.bunker.bkframework.newframework;

/**
 * Ŭ���̾�Ʈ�� ����������Ŭ�� �����ϱ� ���� �������̽�
 * ����������Ŭ��ŭ {@link #life()}�޼ҵ尡 ��� ����ȴ�.
 * �����ֱⰡ ����Ǳ� �� {@link #needRecycle()}�� ������ ������ �ʿ�����
 * Ȯ���� �ϰ� ���ᰡ ������ �����̸� false�� ��ȯ�Ͽ� �����ֱ⸦ �����Ų��.
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
	 * �����⵿ֱ�� �ݺ� ȣ��Ǵ� �޼���
	 */
	public void life();
	/**
	 * �����ֱⰡ ����Ǳ��� ���� ���θ� �˾ƺ��� �޼���
	 * ���� ������ �ʿ���
	 * @return �����ֱ� ������ �ʿ����� ����
	 */
	public boolean needRecycle();
	
	/**
	 * �����ֱ⸦ ������.
	 */
	public boolean interceptCycle();
}
