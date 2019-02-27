package com.epam.microchat.censorship;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CensorshipHandlerTest {

  @Mock
  private CensorshipProperties properties;

  @InjectMocks
  private CensorshipHandler handler;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldReplaceBadWordsWithAsterisks() {
    // given
    when(properties.getBadWords()).thenReturn(Sets.newHashSet("webshere", "jquery", "cloud", "spring"));

    // when
    String output = handler.censor("Hi guys! I wanna build an app with Spring Framework and run it in the cloud!");

    //then
    assertEquals("Hi guys! I wanna build an app with **** Framework and run it in the ****!", output);
  }
}