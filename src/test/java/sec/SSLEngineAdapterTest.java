package sec;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import com.bunker.bkframework.newframework.PacketReceiver;
import com.bunker.bkframework.sec.SSLSecureFactory;
import com.bunker.bkframework.sec.Secure;
import com.bunker.bkframework.sec.SecureCallback;

public class SSLEngineAdapterTest {
	private Secure<ByteBuffer> mClientEngine, mServerEngine;
	private boolean flag = false;
	PacketReceiver<ByteBuffer> dummy = new PacketReceiver<ByteBuffer>() {

		@Override
		public void decodePacket(ByteBuffer packet, int sequence) {
			// TODO Auto-generated method stub
		}

		@Override
		public PacketReceiver<ByteBuffer> unDecoReceiver() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@Before
	public void setUp() throws Exception {
		mServerEngine = new SSLSecureFactory("server.jks", "server", "server.jks", "server").createSecure();
		mClientEngine = new SSLSecureFactory("client.jks", "server", "client.jks", "server", "", 1).createSecure();
	}

	@Test
	public void test() {
		SecureCallback callback = new SecureCallback() {

			@Override
			public void secureFault() {
				flag = true;
				System.out.println("fault");
			}

			@Override
			public void handShaked() {
				flag = true;
				System.out.println("shaked");
			}
		};
		WriterSSLBridge serverBridge = new WriterSSLBridge("s to c", mClientEngine);
		WriterSSLBridge clientBridge = new WriterSSLBridge("c to s", mServerEngine);
		while (!flag) {
			mServerEngine.startHandShaking(serverBridge, dummy, callback);
			mClientEngine.startHandShaking(clientBridge, dummy, callback);
			serverBridge.dequeue();
			clientBridge.dequeue();
		}
	}
}
