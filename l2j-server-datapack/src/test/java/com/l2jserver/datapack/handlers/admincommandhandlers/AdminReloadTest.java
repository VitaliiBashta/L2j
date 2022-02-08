package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.l2jserver.gameserver.config.Configuration.general;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReloadTest {

  @Mock private L2PcInstance player;

  private final AdminReload adminReload = new AdminReload();

  @Test
  void useAdminCommandTest() {
    general().setProperty("ServerListBrackets", "True");
    when(player.getName()).thenReturn("Zoey76");
    player.sendMessage("bla bla");

    adminReload.useAdminCommand("admin_reload config general", player);
    assertFalse(general().getServerListBrackets());
  }
}
