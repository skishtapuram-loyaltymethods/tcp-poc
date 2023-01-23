package com.example.server.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class TcpServer {

	//	@Bean
	//	public TcpReceivingChannelAdapter inboundAdapter(AbstractServerConnectionFactory connectionFactory) {
	//		TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
	//		adapter.setConnectionFactory(connectionFactory);
	//		adapter.setOutputChannel(inboundChannel());
	//		return adapter;
	//	}

	@Bean
	public TcpInboundGateway inboundAdapter(AbstractServerConnectionFactory connectionFactory) {
		TcpInboundGateway adapter = new TcpInboundGateway();
		adapter.setConnectionFactory(connectionFactory);
		adapter.setRequestChannelName("inboundChannel");
		adapter.setClientMode(false);
		adapter.setRetryInterval(1000);
		adapter.retryConnection();
		adapter.isClientModeConnected();
		return adapter;
	}

	//	TcpConnectionEvent
	//	@Bean
	//	@ServiceActivator(inputChannel = "inboundChannel")
	//	public MessageHandler handler() {
	//		return message -> message.getPayload();
	//	}

	@ServiceActivator(inputChannel = "inboundChannel")
	public String handler(Message<String> name) {
		Message<String> request = MessageBuilder.createMessage(name.getPayload().toUpperCase(), name.getHeaders());
		return request.getPayload();
	}

	@Bean
	public AbstractServerConnectionFactory serverCF(
			@Qualifier("inSerializer") ByteArrayLengthHeaderSerializer serializer) {
		TcpNioServerConnectionFactory connectionFactory = new TcpNioServerConnectionFactory(7860);
		connectionFactory.setSerializer(serializer);
		connectionFactory.setDeserializer(serializer);
		return connectionFactory;
	}

	@Bean("inSerializer")
	public ByteArrayLengthHeaderSerializer byteArrayLengthHeaderSerializer() {
		return new ByteArrayLengthHeaderSerializer();
	}

}



