package com.bunker.bkframework.business;

import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.PeerBase;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;

/**
 * ����Ͻ� ���� ������ ������ Ŭ����
 * ByteBufferPeer�� ���� ������ Ŭ������ ���� ���
 * �� Ŭ������ BusinessConnector�� �����ؼ� ����Ѵ�.
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 7.
 * s
 * 
 */
public class BusinessPeer<PacketType, SendDataType, ReceiveDataType> extends PeerBase<PacketType> {
	private BusinessConnector<PacketType, SendDataType, ReceiveDataType> mBusiness;

	public BusinessPeer(PacketFactory<PacketType> factory, SecureFactory<PacketType> secFac, BusinessConnector<PacketType, SendDataType, ReceiveDataType> business) {
		super(factory, secFac);
		mBusiness = business;
	}

	public BusinessPeer(PacketFactory<PacketType> factory, BusinessConnector<PacketType, SendDataType, ReceiveDataType> business) {
		this(factory, null, business);
	}

	@Override
	public void networkInited(Resource<PacketType> resouce) {
		try {
			mBusiness = mBusiness.getInstance(this);
			mBusiness.establishedBusinessLogic();
		} catch (CloneNotSupportedException e) {
			close();
			e.printStackTrace();
		}
		super.networkInited(resouce);
	}

	@Override
	public void decodePacket(PacketType packet, int sequence) {
		mBusiness.sendToBusiness(packet, sequence);
	}

	public void write(PacketType data, Integer sequence) {
		mWriter.write(data, sequence);
	}

	@Override
	public void close() {
		super.close();
		mBusiness.removePeerAtBusiness();
	}
}