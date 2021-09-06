package com.example.alpakka.AlpaAkkaKafkaDemp;

import com.example.alpakka.AlpaAkkaKafkaDemp.listener.KafkaTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlpaAkkaKafkaDempApplication implements CommandLineRunner {

	@Autowired
	KafkaTestListener kafkaTestListener;

	public static void main(String[] args) {
		SpringApplication.run(AlpaAkkaKafkaDempApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		kafkaTestListener.listen();
	}
}
