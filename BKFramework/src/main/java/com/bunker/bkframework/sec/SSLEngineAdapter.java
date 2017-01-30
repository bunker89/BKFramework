package com.bunker.bkframework.sec;

import java.nio.ByteBuffer;
import java.security.KeyManagementException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import com.bunker.bkframework.newframework.Logger;
import com.bunker.bkframework.newframework.PacketReceiver;
import com.bunker.bkframework.newframework.Writer;

public class SSLEngineAdapter implements Secure<ByteBuffer> {
	public static final String _Tag = "SSLEngineAdapter";
	private PacketReceiver<ByteBuffer> mPeer;
	private Writer<ByteBuffer> mWriter;
	private boolean isHandShaked = false;
	private SSLEngine engine;
	private ByteBuffer payLoadIn;
	private ByteBuffer netDataOutBuffer;
	private ByteBuffer handShakeBuffer;
	private ByteBuffer accum;
	private int accumNumber;
	private SecureCallback mCallback;

	/**
	 * 클라이언트 츠겡서 사용
	 */
	public SSLEngineAdapter(String addr, int port, SSLContext context) throws KeyManagementException, Exception {
		engine = context.createSSLEngine(addr, port);
		engine.setUseClientMode(true);
		createBuffers(context);
	}

	/**
	 * 서버측에서 사용
	 */
	public SSLEngineAdapter(SSLContext context) throws KeyManagementException, Exception {
		engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.setNeedClientAuth(true);
		createBuffers(context);
	}

	public void startHandShaking(Writer<ByteBuffer> writer, PacketReceiver<ByteBuffer> peer, SecureCallback callback) {
		mWriter = writer;
		mPeer = peer;
		mCallback = callback;
		try {
			handShakeBuffer = ByteBuffer.wrap("h".getBytes());
			sendHandShake();
		} catch (Exception e) {
			destroyInternal();
			e.printStackTrace();
		}
	}

	/*
	 * Create and size the buffers appropriately.
	 */
	private void createBuffers(SSLContext context) {
		/*
		 * We'll assume the buffer sizes are the same
		 * between client and server.
		 */
		SSLSession session = context.createSSLEngine("as", 1).getSession();
		int appBufferMax = session.getApplicationBufferSize();
		int netBufferMax = session.getPacketBufferSize();

		/*
		 * We'll make the input buffers a bit bigger than the max needed
		 * size, so that unwrap()s following a successful data transfer
		 * won't generate BUFFER_OVERFLOWS.
		 *
		 * We'll use a mix of direct and indirect ByteBuffers for
		 * tutorial purposes only.  In reality, only use direct
		 * ByteBuffers when they give a clear performance enhancement.
		 */
		payLoadIn = ByteBuffer.allocate(appBufferMax + 50);
		netDataOutBuffer = ByteBuffer.allocate(netBufferMax);
	}

	private void decodeHandShake(ByteBuffer netDataInBytes) throws Exception {
		int loopGuard = 0;
		while (netDataInBytes.hasRemaining()) {
			SSLEngineResult clientResult = null;
			clientResult = engine.unwrap(netDataInBytes, payLoadIn);
			runDelegatedTasks(clientResult, engine);
			if (!checkState(clientResult, engine))
				break;

			if (payLoadIn.position() > 0) {
				isHandShaked = true;
//				System.out.println("handShake!");
				mCallback.handShaked();
				if (accum != null)
					write(accum, accumNumber);
				return;
			}

			sendHandShake();
			if (++loopGuard > 15) {
				Logger.err(_Tag, "Loop Over at decodeHandShake");
				return;
			}
		}
	}

	private void sendHandShake() throws Exception {
		int loopGuard = 0;
		while (true) {
			SSLEngineResult clientResult = engine.wrap(handShakeBuffer, netDataOutBuffer);
			runDelegatedTasks(clientResult, engine);
			if (!checkState(clientResult, engine))
				break;
			if (netDataOutBuffer.position() == 0)
				break;
			netDataOutBuffer.flip();
			mWriter.write(netDataOutBuffer);
			netDataOutBuffer.compact();
			if (++loopGuard > 15) {
				Logger.err(_Tag, "Loop Over at sendHandShake");
				return;
			}
		}
	}

	/*
	 * If the result indicates that we have outstanding tasks to do,
	 * go ahead and run them in this thread.
	 */
	private static void runDelegatedTasks(SSLEngineResult result,
			SSLEngine engine) throws Exception {

		if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
			Runnable runnable;
			int loopGuard = 0;
			while ((runnable = engine.getDelegatedTask()) != null) {
				runnable.run();
				if (++loopGuard > 15) {
					Logger.err(_Tag, "Loop Over at runDelegatedTasks");
					return;
				}
			}
			HandshakeStatus hsStatus = engine.getHandshakeStatus();
			if (hsStatus == HandshakeStatus.NEED_TASK) {
				throw new Exception(
						"handshake shouldn't need additional tasks");
			}
		}
	}
	
	private static boolean checkState(SSLEngineResult result, SSLEngine engine) {
		switch(result.getStatus()) {
		case CLOSED:
			engine.closeOutbound();;
			return false;
		default:
			break;
		}
		return true;
	}

	@Override
	public void write(ByteBuffer packet, Integer sequence) {
		if (!isHandShaked) {
			throw new RuntimeException();
		}

		try {
			netDataOutBuffer.clear();
			engine.wrap(packet, netDataOutBuffer);
			netDataOutBuffer.flip();
			mWriter.write(netDataOutBuffer);
		} catch (Exception e) {
			destroyInternal();
			e.printStackTrace();
		}
	}

	@Override
	public void write(ByteBuffer b) {
		write(b, null);
	}

	static int errCount = 0;
	@Override
	public void decodePacket(ByteBuffer packet, int sequence) {
		payLoadIn.clear();
		if (isHandShaked) {
			try {
				int loopGuard = 0;
				while (packet.hasRemaining()) {
					engine.unwrap(packet, payLoadIn);
					if (++loopGuard > 15) {
						Logger.err(_Tag, "Loop Over at decodePacket");
						return;
					}
				}
				payLoadIn.flip();
				mPeer.decodePacket(payLoadIn, sequence);				
			} catch (SSLException e) {
				destroyInternal();
				e.printStackTrace();
			}
		} else {
			try {
				decodeHandShake(packet);
			} catch (Exception e) {
				destroyInternal();
				e.printStackTrace();
			}
		}
	}

	private void destroyInternal() {
		mCallback.secureFault();
	}

	@Override
	public void destroy() {
		engine.closeOutbound();
		if (mWriter != null)
			mWriter.destroy();
	}

	@Override
	public Writer<ByteBuffer> unDecoWriter() {
		return mWriter;
	}

	@Override
	public PacketReceiver<ByteBuffer> unDecoReceiver() {
		return mPeer;
	}

	@Override
	public Secure<ByteBuffer> unDecoSecure(Secure<ByteBuffer> sec) {
		return null;
	}
}