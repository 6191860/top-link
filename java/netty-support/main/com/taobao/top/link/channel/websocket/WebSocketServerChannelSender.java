package com.taobao.top.link.channel.websocket;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.taobao.top.link.channel.ServerChannelSender;

public class WebSocketServerChannelSender extends WebSocketChannelSender implements ServerChannelSender {
	private Map<Object, Object> context;

	public WebSocketServerChannelSender(ChannelHandlerContext ctx) {
		super(ctx != null ? ctx.getChannel() : null);
		this.context = new HashMap<Object, Object>();
	}

	@Override
	public Object getContext(Object key) {
		return this.context.get(key);
	}

	@Override
	public void setContext(Object key, Object value) {
		this.context.put(key, value);
	}

	@Override
	public boolean isOpen() {
		return this.channel.isOpen();
	}
}
