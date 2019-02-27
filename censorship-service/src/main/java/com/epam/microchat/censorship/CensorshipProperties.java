package com.epam.microchat.censorship;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("censorship")
public class CensorshipProperties {

  private Set<String> badWords;
}
