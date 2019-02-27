package com.epam.microchat.web;

import com.epam.microchat.web.domain.Event;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class ChatWebSocketHandler {

  @Bean
  public WebSocketHandlerAdapter webSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }

  @Bean
  public HandlerMapping handlerMapping(WebSocketHandler webSocketHandler) {
    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
    mapping.setUrlMap(Collections.singletonMap("/chat", webSocketHandler));
    return mapping;
  }

  @Bean
  public UnicastProcessor<Event> eventPublisher() {
    return UnicastProcessor.create();
  }

  @Bean
  public Flux<Event> events(UnicastProcessor<Event> eventPublisher) {
    return eventPublisher
        .replay(20)
        .autoConnect();
  }

  public static void main(String[] args) {
    SpringApplication.run(ChatWebSocketHandler.class, args);
  }

}

