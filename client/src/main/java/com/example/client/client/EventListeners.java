package com.example.client.client;

import org.springframework.context.event.EventListener;
import org.springframework.integration.ip.tcp.connection.TcpConnectionCloseEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Component;

@Component
public class EventListeners {

	@EventListener(TcpConnectionOpenEvent.class)
	public void whenConnectionIsOpen(TcpConnectionOpenEvent tcpConnectionOpenEvent) {
		System.out.println("**************** CONNECTION OPEN EVENT FIRED ******************");
		System.out.println("Connection ID : " + tcpConnectionOpenEvent.getConnectionId());
		System.out.println("tcpConnectionOpenEvent : " + tcpConnectionOpenEvent);
	}

	@EventListener(TcpConnectionCloseEvent.class)
	public void whenConnectionIsClose(TcpConnectionCloseEvent tcpConnectionCloseEvent) {
		System.out.println("**************** CONNECTION CLOSE EVENT FIRED ******************");
		System.out.println("Connection ID : " + tcpConnectionCloseEvent.getConnectionId());
		System.out.println("tcpConnectionCloseEvent : " + tcpConnectionCloseEvent);
	}

}