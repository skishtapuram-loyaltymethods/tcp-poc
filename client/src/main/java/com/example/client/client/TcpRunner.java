package com.example.client.client;

import com.example.client.client.TcpClient.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TcpRunner implements CommandLineRunner {

	@Autowired
	Gateway gateway;

	@Override
	public void run(String... args) throws Exception {
		String name = "docker";
		gateway.viaTcp(name);
	}
}
