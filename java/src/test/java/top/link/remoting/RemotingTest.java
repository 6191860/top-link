package top.link.remoting;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import top.link.Text;
import top.link.channel.ChannelException;
import top.link.channel.websocket.WebSocketServerChannel;
import top.link.remoting.DynamicProxy;
import top.link.remoting.FormatterException;
import top.link.remoting.MethodCall;
import top.link.remoting.MethodCallContext;
import top.link.remoting.MethodReturn;
import top.link.remoting.RemotingException;
import top.link.remoting.RemotingServerChannelHandler;

public class RemotingTest {

	@Test
	public void send_test() throws URISyntaxException, ChannelException {
		URI uri = new URI("ws://localhost:9001/link");
		send_test(uri, false);
	}

	@Test
	public void cumulative_test() throws URISyntaxException, ChannelException {
		URI uri = new URI("ws://localhost:9002/link");
		send_test(uri, true);
	}

	@Test(expected = RemotingException.class)
	public void execution_timeout_test() throws URISyntaxException, ChannelException, RemotingException, FormatterException {
		URI uri = new URI("ws://localhost:9003/link");
		WebSocketServerChannel serverChannel = new WebSocketServerChannel(uri.getPort());
		serverChannel.setChannelHandler(new RemotingServerChannelHandler() {
			@Override
			public MethodReturn onMethodCall(MethodCall methodCall, MethodCallContext callContext) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		serverChannel.run();

		DynamicProxy proxy = RemotingUtil.connect(uri);

		try {
			proxy.invoke(new MethodCall(), 2000);
		} catch (RemotingException e) {
			assertEquals("remoting execution timeout", e.getMessage());
			throw e;
		}
	}

	@Test(expected = RemotingException.class)
	public void channel_broken_while_calling_then_recall_test() throws Throwable {
		URI uri = new URI("ws://localhost:9004/link");
		final WebSocketServerChannel serverChannel = new WebSocketServerChannel(uri.getPort());
		serverChannel.setChannelHandler(new RemotingServerChannelHandler() {
			@Override
			public MethodReturn onMethodCall(MethodCall methodCall, MethodCallContext callContext) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		serverChannel.run();

		DynamicProxy proxy = RemotingUtil.connect(uri);

		// make server broken
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				serverChannel.stop();
			}
		}).start();

		try {
			proxy.invoke(new MethodCall());
		} catch (RemotingException e) {
			assertEquals(Text.RPC_CHANNEL_BROKEN, e.getMessage());
			throw e;
		}

		// redo
		serverChannel.run();
		proxy.invoke(new MethodCall());
	}

	@Test
	public void transportHeaders_got_error_statusCode_test() {

	}

	private void send_test(URI uri, boolean cumulative) throws URISyntaxException, ChannelException {
		WebSocketServerChannel serverChannel = new WebSocketServerChannel(uri.getPort(), cumulative);
		serverChannel.setChannelHandler(new RemotingServerChannelHandler() {
			@Override
			public MethodReturn onMethodCall(MethodCall methodCall, MethodCallContext callContext) {
				MethodReturn methodReturn = new MethodReturn();
				methodReturn.ReturnValue = "ok";
				return methodReturn;
			}
		});
		serverChannel.run();

		DynamicProxy proxy = RemotingUtil.connect(uri);
		MethodCall methodCall = new MethodCall();
		MethodReturn methodReturn = null;
		try {
			methodReturn = proxy.invoke(methodCall);
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (FormatterException e) {
			e.printStackTrace();
		}
		assertNull(methodReturn.Exception);
		assertEquals("ok", methodReturn.ReturnValue);
	}
}
