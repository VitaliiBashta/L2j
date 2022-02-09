package com.l2jserver.gameserver;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.idfactory.IdFactory;
import org.springframework.stereotype.Service;

@Service
public class Context {

  public final ThreadPoolManager threadPoolManager;
  public final ConnectionFactory connectionFactory;
  public final IdFactory idFactory;

  public Context(ThreadPoolManager threadPoolManager, ConnectionFactory connectionFactory, IdFactory idFactory) {
    this.threadPoolManager = threadPoolManager;
    this.connectionFactory = connectionFactory;
    this.idFactory = idFactory;
  }
}
