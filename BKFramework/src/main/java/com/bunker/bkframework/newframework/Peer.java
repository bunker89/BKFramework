package com.bunker.bkframework.newframework;

/**
 *
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 * 
 * ��Ʈ��ũ �ۼ��Ű� ����Ͻ� ���� ������ ����, Peer �������̽��� ���� ��Ʈ��ũ ������ ó�� ������ ���õǾ� �ִ�.
 * ���� Ŭ�������� �ʿ��� ��� ��ȣȭ, ��ȣȭ ó���� �����Ѵ�.
 * Ŭ���̾�Ʈ���� ���������� �������� ����Ѵ�.
 * 
 * ----------------------
 * �ǰ����
 * 1.
 * @see LifeCycle Peer�� ���� �ֱ⸦ ���� �������� �ʰ� LifeCycleŬ������ ���� �����ֱ⸦ �����ϵ��� �Ѵ�.
 * �ٽø��� {@link #dispatch(Object)}�� ���� �����͸� ��ٷ� �ڵ鸵 ���� �ʰ�
 * {@link #setLifeCycle(LifeCycle)}�� ���� LifeCycle�� ������ �ξ��ٰ� 
 * {@link LifeCycle#manageLife(PeerLife)}�� ȣ���Ͽ� �����ֱ⸦ �����޵��� �Ѵ�.
 * ���� �ֱ⸦ �и���Ŵ���μ� Core�� ���� ����(roop, event-driven)�� ���� ������ ���� �� �ִ�.
 * 
 * 2.
 * ������ �Ϸ�� �Ŀ� {@link #networkInited()}�� ȣ���� �� ���� �ǰ��Ѵ�.
 * ----------------------
 * 
 * 
 * ----------------------
 * ���ǻ���
 * 1.
 * @see #networkInited()�� �Ϸ�� �� ���� ������ ��� ����ǵ��� �Ѵ�.
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
	 * ��Ŷ�� �ڵ鸵 �ϱ� ���� ��Ʈ��ũ �ۼ��� �������� �о���� �����͸� ���۹޴´�.
	 * @param read ��Ŷ
	 * @return
	 */
	public boolean dispatch(PacketType read);
	
	/**
	 * peer�� ������ �����Ѵ�.
	 * close�� �θ��� ��ü�� Peer�� Thread�� �ƴ϶� Core�� ���� �ִ�. 
	 */
	public void close();
	
	/**
	 * clone
	 * @return ������ Ŭ����
	 * @throws CloneNotSupportedException
	 */
	public Peer<PacketType >clone() throws CloneNotSupportedException;
	
	/**
	 * ���� �ֱ⸦ ������ LifeCycle Ŭ������ ���޹޴´�.
	 * �ڼ��� ������ �ǰ���� 1.�� �����Ѵ�.
	 * 
	 * @param l
	 */
	public void setLifeCycle(LifeCycle l);
	
	/**
	 * ���� ���� ��ü�� �����Ѵ�.
	 * @see Writer ���� �κ��� �и� ��Ŵ���μ� ������ ���¿� ���õ� ������ ���ش�. 
	 * 
	 * @param writer
	 */
	public void setWriter(Writer<PacketType> writer);
	
	/**
	 * ��Ʈ��ũ�� ����Ǿ�
	 * writer�� packetReceiver�� ���� �Ǿ��� �� ȣ���Ѵ�.
	 * �ݵ�� ��� ������ networkInited()�� �Ϸ� �� �� ó���ȴ�.
	 * 
	 * @param resource ��ü�� ���ҽ�
	 */
	public void networkInited(Resource<PacketType> resource);
}