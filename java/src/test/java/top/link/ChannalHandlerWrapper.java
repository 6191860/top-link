package top.link;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import top.link.channel.ChannelContext;
import top.link.channel.ChannelHandler;

public class ChannalHandlerWrapper implements ChannelHandler {
	public Object sync = new Object();
	
	public AtomicInteger connect = new AtomicInteger();
	public AtomicInteger receive = new AtomicInteger();
	public AtomicInteger error = new AtomicInteger();
	
	public void onConnect(ChannelContext context) {
		connect.incrementAndGet();
	}
	
	public void onMessage(ChannelContext context) {
		receive.incrementAndGet();
		this.notifyHandler();
	}
	
	public void onError(ChannelContext context) {
		error.incrementAndGet();
		this.notifyHandler();
	}
	
	public void waitHandler() throws InterruptedException {
		this.waitHandler(0);
	}
	
	public void waitHandler(int timeout) throws InterruptedException {
		synchronized (this.sync) {
			if (timeout > 0)
				this.sync.wait(timeout);
			else
				this.sync.wait();
		}
	}
	
	public void notifyHandler() {
		synchronized (this.sync) {
			this.sync.notify();
		}
	}
	
	public void assertHandler(int receive, int error) {
		Assert.assertEquals(receive, this.receive.get());
		Assert.assertEquals(error, this.error.get());
	}
	
	public void onClosed(String reason) {
	}
}
