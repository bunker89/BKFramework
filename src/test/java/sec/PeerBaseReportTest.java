package sec;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.bunker.bkframework.newframework.Packet;
import com.bunker.bkframework.newframework.PacketFactory;
import com.bunker.bkframework.newframework.PeerBase;

public class PeerBaseReportTest {
	class TestPeer extends  PeerBase<String> {
		public TestPeer(PacketFactory<String> factory) {
			super(factory);
		}

		@Override
		public void decodePacket(String packet, int sequence) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void logPacket(String logId, List<Packet<String>> accum, List<String> nonPre, List<String> processing,
				String message) throws IOException {
			super.logPacket(logId, accum, nonPre, processing, message);
		}
	};
	private TestPeer testpeer = new TestPeer(null);
	
	@Test
	public void reportTest() {
		try {
			testpeer.logPacket("1", null, null, null, "asgbsa");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
