package sec;

import org.junit.Test;

import com.bunker.bkframework.newframework.LifeCycle;
import com.bunker.bkframework.newframework.PeerBase;
import com.bunker.bkframework.newframework.PeerLife;
import com.bunker.bkframework.text.TextPacketFactory;
import com.bunker.bkframework.text.TextWriter;

public class TextPeerTest {

	@Test
	public void testTextBusinessPeer() {
		final TextWriter writer = new TextWriter();
		PeerBase<String> peer = new PeerBase<String>(new TextPacketFactory()) {
			
			@Override
			public void decodePacket(String packet, int sequence) {
				writer.write(packet);
			}
		};
		peer.setWriter(writer);
		peer.setLifeCycle(new LifeCycle() {
			
			@Override
			public void manageLife(PeerLife life) {
				while (life.needRecycle()) {
					life.life();
				}
			}
		});
		peer.dispatch("asbcc");
		peer.run();
		System.out.println(writer.getResult());
	}
}