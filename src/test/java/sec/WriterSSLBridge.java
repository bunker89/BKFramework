package sec;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.bunker.bkframework.newframework.PacketReceiver;
import com.bunker.bkframework.newframework.Writer;

public class WriterSSLBridge implements Writer<ByteBuffer> {
	private PacketReceiver<ByteBuffer> mTo;
	private String name;
	private List<ByteBuffer> list = new LinkedList<>();
	
	public WriterSSLBridge(String name, PacketReceiver<ByteBuffer> receiver) {
		mTo = receiver;
		this.name = name;
	}

	@Override
	public void write(ByteBuffer b, Integer sequence) {
	}

	@Override
	public void write(ByteBuffer b) {
		System.out.println(name);
		ByteBuffer newB = ByteBuffer.allocate(b.capacity());
		newB.put(b);
		newB.flip();
		list.add(newB);
		b.position(b.limit());
	}

	@Override
	public Writer<ByteBuffer> unDecoWriter() {
		return null;
	}

	@Override
	public void destroy() {		
	}

	@Override
	public void setWriteBufferSize(int size) {
	}

	public void dequeue() {
		while (list.size() > 0) {
			ByteBuffer packet = list.remove(0);
			mTo.decodePacket(packet, 0);
		}
	}
}
