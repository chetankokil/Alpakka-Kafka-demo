package com.example.alpakka.AlpaAkkaKafkaDemp.config;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.spring.web.AkkaStreamsRegistrar;
import akka.stream.alpakka.spring.web.SpringWebAkkaStreamsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;

import java.util.Objects;

@Configuration
@ConditionalOnClass(akka.stream.javadsl.Source.class)
@EnableConfigurationProperties(SpringWebAkkaStreamsProperties.class)
public class SpringWebAkkaStreamsConfiguration {

  private static final String DEFAULT_ACTORY_SYSTEM_NAME = "SpringWebAkkaStreamsSystem";

  private final ActorSystem system;
  private final ActorMaterializer mat;
  private final SpringWebAkkaStreamsProperties properties;

  public SpringWebAkkaStreamsConfiguration(final SpringWebAkkaStreamsProperties properties) {
    this.properties = properties;
    final ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();

    system = ActorSystem.create(getActorSystemName(properties));
    mat = ActorMaterializer.create(system);
    new AkkaStreamsRegistrar(system).registerAdapters(registry);
  }

  @Bean
  @ConditionalOnMissingBean(ActorSystem.class)
  public ActorSystem getActorSystem() {
    return system;
  }

  @Bean
  @ConditionalOnMissingBean(Materializer.class)
  public ActorMaterializer getMaterializer() {
    return mat;
  }

  public SpringWebAkkaStreamsProperties getProperties() {
    return properties;
  }

  private String getActorSystemName(final SpringWebAkkaStreamsProperties properties) {
    Objects.requireNonNull(
        properties,
        String.format(
            "%s is not present in application context",
            SpringWebAkkaStreamsProperties.class.getSimpleName()));

    if (isBlank(properties.getActorSystemName())) {
      return DEFAULT_ACTORY_SYSTEM_NAME;
    }

    return properties.getActorSystemName();
  }

  private boolean isBlank(String str) {
    return (str == null || str.isEmpty());
  }
}