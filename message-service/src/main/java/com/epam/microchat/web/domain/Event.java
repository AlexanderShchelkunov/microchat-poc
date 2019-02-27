package com.epam.microchat.web.domain;

import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Event {

  private EventType type;
  private String user;
  private Payload payload;
  private Instant timestamp;
}
