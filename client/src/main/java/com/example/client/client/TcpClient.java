package com.example.client.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.CachingClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.FailoverClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionInterceptorFactoryChain;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.ThreadAffinityClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;

import java.util.Collections;
import java.util.List;

@Configuration
public class TcpClient {

	@MessagingGateway(defaultRequestChannel = "toTcp")
	public interface Gateway {
		String viaTcp(String in);
	}

	@Bean
	@ServiceActivator(inputChannel = "toTcp")
	public TcpOutboundGateway outboundAdapter(
			//			@Qualifier("clientConnectionFactory1") AbstractClientConnectionFactory connectionFactory
			@Qualifier("failOverClientConnectionFactory") FailoverClientConnectionFactory connectionFactory) {
		TcpOutboundGateway handler = new TcpOutboundGateway();
		handler.setConnectionFactory(connectionFactory);
		handler.setOutputChannelName("finalOutput");
		//		handler.setCloseStreamAfterSend(true);
		return handler;
	}

	@ServiceActivator(inputChannel = "finalOutput")
	public void finalMessage(String output) {
		System.out.println(output);
	}

	@Bean
	public CachingClientConnectionFactory cacheClientFactory(
			@Qualifier("clientConnectionFactory1") AbstractClientConnectionFactory connectionFactory) {
		CachingClientConnectionFactory clientConnectionFactory = new CachingClientConnectionFactory(connectionFactory,
				10);
		clientConnectionFactory.setConnectionWaitTimeout(30);
		clientConnectionFactory.setConnectionTest(test -> test.isServer());
		//		clientConnectionFactory.setConnectionTest(TcpConnectionSupport::isServer);
		return clientConnectionFactory;
	}

	@Bean
	public TcpConnectionInterceptorFactoryChain interceptorFactoryChain() {
		TcpConnectionInterceptorFactoryChain chain = new TcpConnectionInterceptorFactoryChain();
		return chain;
	}

	@Bean
	public AbstractClientConnectionFactory clientConnectionFactory1(
			@Qualifier("outSerializer") ByteArrayLengthHeaderSerializer serializer) {
		AbstractClientConnectionFactory connectionFactory = new TcpNetClientConnectionFactory("localhost", 7860);
		//		connectionFactory.setSingleUse(true);
		connectionFactory.setSerializer(serializer);
		connectionFactory.setDeserializer(serializer);
		//		connectionFactory.setAp
		return connectionFactory;
	}

	//	@Bean
	//	public AbstractClientConnectionFactory clientConnectionFactory2(
	//			@Qualifier("outSerializer") ByteArrayLengthHeaderSerializer serializer) {
	//		TcpNetClientConnectionFactory connectionFactory = new TcpNetClientConnectionFactory("localhost", 9002);
	//		connectionFactory.setSerializer(serializer);
	//		connectionFactory.setDeserializer(serializer);
	//		return connectionFactory;
	//	}

	@Bean
	public FailoverClientConnectionFactory failOverClientConnectionFactory(
			@Qualifier("clientConnectionFactory1") AbstractClientConnectionFactory cacheClientFactory) {
		List<AbstractClientConnectionFactory> connectionFactories = Collections.singletonList(cacheClientFactory);
		FailoverClientConnectionFactory fcf = new FailoverClientConnectionFactory(connectionFactories);
		//				fcf.setRefreshSharedInterval(100);
		return fcf;
	}

	@Bean
	public ThreadAffinityClientConnectionFactory threadAffinityClientConnectionFactory(
			@Qualifier("clientConnectionFactory1") AbstractClientConnectionFactory underlyingFactory) {
		return new ThreadAffinityClientConnectionFactory(underlyingFactory);
	}

	@Bean("outSerializer")
	public ByteArrayLengthHeaderSerializer byteArrayLengthHeaderSerializer() {
		return new ByteArrayLengthHeaderSerializer();
	}

}


