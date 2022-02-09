package com.l2jserver.gameserver.scripting;

import com.l2jserver.datapack.conquerablehalls.DevastatedCastle.DevastatedCastle;
import com.l2jserver.datapack.conquerablehalls.FortressOfResistance.FortressOfResistance;
import com.l2jserver.datapack.conquerablehalls.FortressOfTheDead.FortressOfTheDead;
import com.l2jserver.datapack.conquerablehalls.RainbowSpringsChateau.RainbowSpringsChateau;
import com.l2jserver.datapack.conquerablehalls.flagwar.BanditStronghold.BanditStronghold;
import com.l2jserver.datapack.conquerablehalls.flagwar.WildBeastReserve.WildBeastReserve;
import com.l2jserver.datapack.custom.Validators.SubClassSkills;
import com.l2jserver.datapack.custom.events.Elpies.Elpies;
import com.l2jserver.datapack.custom.events.Rabbits.Rabbits;
import com.l2jserver.datapack.custom.events.Race.Race;
import com.l2jserver.datapack.custom.events.TvT.TvTManager.TvTManager;
import com.l2jserver.datapack.custom.events.Wedding.Wedding;
import com.l2jserver.datapack.custom.service.buffer.BufferService;
import com.l2jserver.datapack.custom.service.teleporter.TeleporterService;
import com.l2jserver.datapack.events.CharacterBirthday.CharacterBirthday;
import com.l2jserver.datapack.events.FreyaCelebration.FreyaCelebration;
import com.l2jserver.datapack.events.GiftOfVitality.GiftOfVitality;
import com.l2jserver.datapack.events.HeavyMedal.HeavyMedal;
import com.l2jserver.datapack.events.LoveYourGatekeeper.LoveYourGatekeeper;
import com.l2jserver.datapack.events.MasterOfEnchanting.MasterOfEnchanting;
import com.l2jserver.datapack.events.TheValentineEvent.TheValentineEvent;
import com.l2jserver.datapack.features.SkillTransfer.SkillTransfer;
import com.l2jserver.datapack.vehicles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public final class ScriptEngineManager {
  private static final Logger LOG = LoggerFactory.getLogger(ScriptEngineManager.class);

  private static final List<Class<?>> scripts =
      List.of(
          SkillTransfer.class,
          SubClassSkills.class,
          BufferService.class,
          TeleporterService.class,
          Elpies.class,
          Rabbits.class,
          Race.class,
          TvTManager.class,
          Wedding.class,
          BoatTalkingGludin.class,
          BoatGiranTalking.class,
          BoatInnadrilTour.class,
          BoatGludinRune.class,
          BoatRunePrimeval.class,
          BanditStronghold.class,
          WildBeastReserve.class,
          DevastatedCastle.class,
          FortressOfResistance.class,
          FortressOfTheDead.class,
          RainbowSpringsChateau.class,
          CharacterBirthday.class,
          GiftOfVitality.class,
          HeavyMedal.class,
          TheValentineEvent.class,
          FreyaCelebration.class,
          MasterOfEnchanting.class,
          LoveYourGatekeeper.class);

  private static final String MAIN = "main";

  private static final Object[] MAIN_METHOD_ARGS = new Object[] {new String[0]};

  private static final Class<?>[] ARG_MAIN = new Class[] {String[].class};

  private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] args) {
    try {
      final var mainMethod = clazz.getMethod(methodName, args);
      final int modifiers = mainMethod.getModifiers();
      if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
        return mainMethod;
      }
    } catch (NoSuchMethodException ignored) {
    }
    return null;
  }

  public static ScriptEngineManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void runMainOnscripts() {
    for (Class<?> script : scripts) {
      runInit(script);
    }
  }

  private void runInit(Class<?> clazz) {
    final var mainMethod = findMethod(clazz, MAIN, ARG_MAIN);
    if (mainMethod == null) {
      LOG.warn("Unable to find main method in class {}!", clazz);
      return;
    }
    try {
      clazz.getConstructor().newInstance();
    } catch (InstantiationException
        | NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException("Object cannot be instantiated:" + clazz, e);
    }
    //    mainMethod.invoke(null, MAIN_METHOD_ARGS);
  }

  public void executeScript(Class<?> clazz) {
    runInit(clazz);
  }

  private static class SingletonHolder {
    protected static final ScriptEngineManager INSTANCE = new ScriptEngineManager();
  }
}
