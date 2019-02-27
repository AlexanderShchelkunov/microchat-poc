package com.epam.microchat.web.domain;

import java.util.List;
import lombok.Data;

@Data
public class Payload {

  private String text;
  private List<String> users;
}
