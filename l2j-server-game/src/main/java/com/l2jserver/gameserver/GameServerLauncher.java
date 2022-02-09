package com.l2jserver.gameserver;

import com.l2jserver.commons.util.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.l2jserver.gameserver.config.Configuration.geodata;
import static com.l2jserver.gameserver.config.Configuration.server;

@SpringBootApplication(scanBasePackages = "com.l2jserver.*")
public class GameServerLauncher {
  private static final String DATAPACK = "-dp";
  private static final String SCRIPT = "-s";
  private static final String GEODATA = "-gd";

  public static void main(String[] args) {
    final String datapackRoot = Util.parseArg(args, DATAPACK, true);
    if (datapackRoot != null) {
      server().setProperty("DatapackRoot", datapackRoot);
    }

    final String scriptRoot = Util.parseArg(args, SCRIPT, true);
    if (scriptRoot != null) {
      server().setProperty("ScriptRoot", scriptRoot);
    }

    final String geodata = Util.parseArg(args, GEODATA, true);
    if (geodata != null) {
      geodata().setProperty("GeoDataPath", geodata);
    }

    SpringApplication.run(GameServerLauncher.class, args);
  }
}
