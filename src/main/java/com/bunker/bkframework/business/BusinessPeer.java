package com.bunker.bkframework.business;

import java.nio.ByteBuffer;

import com.bunker.bkframework.newframework.FixedSizeByteBufferPacketFactory;
import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.PacketByteBufferWrapper;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.PeerBase;
import com.bunker.bkframework.newframework.PeerLife;
import com.bunker.bkframework.newframework.Resource;
import com.bunker.bkframework.sec.SecureFactory;

/**
 * 占쏙옙占쏙옙絿占� 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 클占쏙옙占쏙옙
 * ByteBufferPeer占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 클占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占�
 * 占쏙옙 클占쏙옙占쏙옙占쏙옙 BusinessConnector占쏙옙 占쏙옙占쏙옙占쌔쇽옙 占쏙옙占쏙옙磯占�.
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

	public static void main(String []args) {
		Business<ByteBuffer, byte[], byte[]> business = new Business<ByteBuffer, byte[], byte[]>() {
			
			@Override
			public void removeBusinessData(PeerConnection<byte[]> connector) {
			}
			
			@Override
			public void receive(PeerConnection<byte[]> connector, byte[] data, int sequence) {
			}
			
			@Override
			public void established(PeerConnection<byte[]> b) {
			}
		};
		PeerBase<ByteBuffer> peer = new BusinessPeer<>(new FixedSizeByteBufferPacketFactory(), new ByteBufferBusinessConnector(business));
		peer.setLifeCycle(new LifeCycle() {
			
			@Override
			public void manageLife(PeerLife life) {
				while (life.needRecycle()) {
					life.life();
				}
			}
		});

		String ab = "";

		for (int i = 0; i < 1000; i++) {
			ab += "ab";
		}

		ByteBuffer second = ByteBuffer.wrap(ab.getBytes());
		PacketByteBufferWrapper wrapper = new PacketByteBufferWrapper(second);
		ByteBuffer first = wrapper.next();
		peer.dispatch(first);
		peer.dispatch(ByteBuffer.allocate(2));
		new Thread(peer).start();
	}
}