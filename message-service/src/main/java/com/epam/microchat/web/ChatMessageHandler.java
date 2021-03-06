package com.epam.microchat.web;

import com.epam.microchat.web.client.CensorshipFeignClient;
import com.epam.microchat.web.client.DiceFeignClient;
import com.epam.microchat.web.domain.Event;
import com.epam.microchat.web.domain.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageHandler implements WebSocketHandler {
	
  private static final String ROLL_COMMAND = "/roll";

  private final CensorshipFeignClient censorshipClient;
  private final DiceFeignClient diceClient;
  private final UnicastProcessor<Event> eventPublisher;
  private final Flux<Event> events;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    var subscriber = new ClientSubscriber();

    session.receive()
        .map(WebSocketMessage::getPayloadAsText)
        .map(this::fromEvent)
        .flatMap(event -> {
          if (event.getType() == EventType.CHAT_MESSAGE) {
            String text = event.getPayload().getText();
            if (text.startsWith(ROLL_COMMAND)) {
              return Mono.fromSupplier(() -> rollDice(event))
                  .subscribeOn(Schedulers.elastic());
            } else {
              return Mono.just(censorMessage(event));
            }
          }
          return Mono.just(event);
        })
        .doOnNext(subscriber::onNext)
        .doOnComplete(subscriber::onComplete)
        .doOnError(subscriber::onError)
        .subscribe();
    return session.send(events
        .map(this::toEvent)
        .map(session::textMessage)
    );
  }

  private Event rollDice(Event event) {
    long start = System.currentTimeMillis();
    Integer value = diceClient.roll();
    long end = System.currentTimeMillis();
    event.getPayload().setText(value != null 
		? String.format("My roll: %s (took %.2f seconds)", value, (double) (end - start) / 1000)
		: ROLL_COMMAND);
    return event;
  }

  private Event censorMessage(Event event) {
    String originalMessage = event.getPayload().getText();
    String censoredMessage = censorshipClient.censor(originalMessage);
    event.setTimestamp(Instant.now())
        .getPayload().setText(censoredMessage);
    return event;
  }

  @SneakyThrows
  private Event fromEvent(String event) {
    return objectMapper.readValue(event, Event.class);
  }

  @SneakyThrows
  private String toEvent(Event event) {
    return objectMapper.writeValueAsString(event);
  }

  private class ClientSubscriber implements Subscriber<Event> {

    private Optional<Event> lastReceivedEvent = Optional.empty();

    @Override
    public void onSubscribe(Subscription s) {
    }

    @Override
    public void onNext(Event event) {
      lastReceivedEvent = Optional.of(event);
      eventPublisher.onNext(event);
    }

    @Override
    public void onError(Throwable t) {
      log.warn("onError()", t);
    }

    @Override
    public void onComplete() {
      lastReceivedEvent.ifPresent(event -> {
        Event userLeftEvent = new Event()
            .setType(EventType.USER_LEFT)
            .setUser(event.getUser());
        eventPublisher.onNext(userLeftEvent);
      });

    }
  }
}