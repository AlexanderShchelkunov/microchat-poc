package com.epam.microchat.censorship;

import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CensorshipHandler {

  private static final String REPLACEMENT = "****";
  private final CensorshipProperties properties;

  @GetMapping
  public String censor(@NotEmpty @RequestParam String input) {
    log.debug("censor(), input = {}", input);
    Set<String> badWords = properties.getBadWords();
    Pattern pattern = Pattern.compile(StringUtils.join(badWords, "|"), Pattern.CASE_INSENSITIVE);
    return pattern.matcher(input).replaceAll(REPLACEMENT);
  }
}
