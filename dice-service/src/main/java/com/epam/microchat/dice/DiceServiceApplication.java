package com.epam.microchat.dice;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DiceServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiceServiceApplication.class, args);
  }

  @RestController
  public class DiceController {

    @PostMapping
    public synchronized Integer roll() {
      sleep();
      return ThreadLocalRandom.current().nextInt(0, 100);
    }

    @SneakyThrows
    private void sleep() {
      long delay = TimeUnit.SECONDS.toMillis(5);
      Thread.sleep(delay);
    }
  }

}
