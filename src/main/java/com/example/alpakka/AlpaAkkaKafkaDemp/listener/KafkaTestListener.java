package com.example.alpakka.AlpaAkkaKafkaDemp.listener;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerMessage;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.typesafe.config.Config;

import java.time.Duration;
import java.util.concurrent.TimeoutException;


@Component

public class KafkaTestListener {

    Logger log = LoggerFactory.getLogger(KafkaTestListener.class);

    @Autowired
    Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> kafkaCommittableSource;

    @Autowired
    ActorSystem system;

    @Autowired
    Materializer materializer;

//    public Flow<ConsumerMessage.CommittableMessage, String, NotUsed> flow() {
//        return Flow.of(ConsumerMessage.CommittableMessage.class).map(str -> str.record().value().toString().toUpperCase());
//    }

//    Flow<ConsumerMessage.CommittableMessage, String, NotUsed> flow = Flow.fromGraph(return )

    public void listen() {
        kafkaCommittableSource
//                .idleTimeout(Duration.ofMinutes(1L))
                .recoverWith(TimeoutException.class, () -> {
                    log.info(
                            "TimeoutException encountered, meaning timeout of {} min has reached",
                            Duration.ofMinutes(1L)
                    );
                    return Source.empty();
                })
                // throttling with elements = 100, per = 1 sec, maxBurst = 1000, mode = shaping
                .throttle(100, Duration.ofSeconds(1L), 1000, (ThrottleMode) ThrottleMode.shaping())
                .map(committableMessage -> {
                    System.out.printf("%s Message key %s, value %s, partition %d%n",
                            Thread.currentThread().getName(), committableMessage.record().key(),
                            committableMessage.record().value(), committableMessage.record().partition());
                    return committableMessage;
                })
//                .via(flow())
                .map(ConsumerMessage.CommittableMessage::committableOffset)
                .toMat(Committer.sink(CommitterSettings.create(system)), Keep.both())
                .mapMaterializedValue(Consumer::createDrainingControl)
                .run(materializer);
    }

}
