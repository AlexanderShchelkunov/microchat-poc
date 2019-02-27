package com.epam.microchat.web;

import com.epam.microchat.web.CensorshipFeignClient.CensorshipFeignClientFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "censorship-service", fallback = CensorshipFeignClientFallback.class)
public interface CensorshipFeignClient {

  @GetMapping
  String censor(@RequestParam("input") String input);

  @Slf4j
  @Component
  class CensorshipFeignClientFallback implements CensorshipFeignClient {

    @Override
    public String censor(String input) {
      log.warn("Censorship service unavailable, returning unprocessed input = {}", input);
      return input;
    }
  }

}
