package com.bunker.bkframework.newframework;

import static com.bunker.bkframework.newframework.Constants.FLAG_OFFSET;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_SIZE_OFFSET;
import static com.bunker.bkframework.newframework.Constants.SEQUENCE_OFFSET;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_OFFSET;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.bunker.bkframework.nio.ByteBufferPacket;

public class FixedSizeByteBufferPacketFactory implements PacketFactory<ByteBuffer> {
	@Override
	public Packet<ByteBuffer> creatPacket(ByteBuffer pack) {
		return new ByteBufferPacket(pack);
	}

	@Override
	public Packet<ByteBuffer> creatPacket(int size) {
		return new ByteBufferPacket(size);
	}

	@Override
	public void logging(OutputStream output, ByteBuffer pack) {
		ByteBuffer buffer = ByteBuffer.allocate(pack.remaining());
		buffer.put(pack);
		pack.position(0);
		try {
			if (pack.limit() > PAYLOAD_OFFSET) {
				short flag = pack.getShort(FLAG_OFFSET);
				short payloadSize = pack.getShort(PAYLOAD_SIZE_OFFSET);
				int seq = pack.getInt(SEQUENCE_OFFSET);
				output.write(("pack size:" + pack.remaining() + "\n").getBytes());
				output.write(("flag:" + flag + "\n" ).getBytes());
				output.write(("payload size:" + payloadSize + "\n" ).getBytes());
				output.write(("seq:" + seq + "\n" ).getBytes());
			}
			output.write("data\n".getBytes());
			pack.position(0);
			output.write(buffer.array());
		} catch (IOException e) {
			Logger.err(getClass().getName(), "byte buffer log err", e);
		}
	}

	@Override
	public void logging(OutputStream output, Packet<ByteBuffer> pack) {
		try {
			output.write(pack.getData().duplicate().array());
		} catch (IOException e) {
			Logger.err(getClass().getName(), "packet log err", e);
		}
	}
}