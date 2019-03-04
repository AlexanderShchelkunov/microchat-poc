package com.epam.microchat.web.client;

import com.epam.microchat.web.client.DiceFeignClient.DiceServiceClientFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "dice-service", fallback = DiceServiceClientFallback.class)
public interface DiceFeignClient {

  @PostMapping
  Integer roll();

  @Slf4j
  @Component
  class DiceServiceClientFallback implements DiceFeignClient {

    @Override
    public Integer roll() {
      log.warn("Dice service unavailable, it's your lucky day!");
      return 100;
    }
  }

}
