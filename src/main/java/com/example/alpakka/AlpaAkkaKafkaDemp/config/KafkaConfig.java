package com.example.alpakka.AlpaAkkaKafkaDemp.config;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerMessage;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class KafkaConfig {

    @Autowired
    ActorSystem system;

    @Bean
    public ConsumerSettings<String, String> consumerSettings( ) {
        final Config consumerConfig = system.settings().config().getConfig("akka.kafka.consumer");
        return ConsumerSettings.create(consumerConfig, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .withStopTimeout(Duration.ofSeconds(5));
    }

    @Bean
    public Source<ConsumerMessage.CommittableMessage<String, String>, akka.kafka.javadsl.Consumer.Control> getKafkaCommittableSource() {
        return Consumer.committableSource(consumerSettings(), Subscriptions.topics("topic1"));
    }

}
