package com.l2jserver.gameserver;

import com.l2jserver.commons.database.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.l2jserver.gameserver.config.Configuration.database;

@Configuration
public class BeanConfiguration {

  @Bean
  public ConnectionFactory connectionFactory() {
    return ConnectionFactory.builder() //
        .withUrl(database().getURL()) //
        .withUser(database().getUser()) //
        .withPassword(database().getPassword()) //
        .withMaxIdleTime(database().getMaxIdleTime()) //
        .withMaxPoolSize(database().getMaxConnections()) //
        .build();
  }
}
