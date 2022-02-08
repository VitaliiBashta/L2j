package com.l2jserver.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.l2jserver.commons.util.Util;

public class UtilTest {

  private static final String IGNORE_QUESTS = "-noquest";

  private static final String DP = "-dp";

  private static final String DP_PATH = "../../../L2J_DataPack/dist/game";

  @ParameterizedTest
  @MethodSource("provideArgs")
  public void testParseArg(String[] args, String arg, boolean hasArgValue, String expected) {
    assertEquals(expected, Util.parseArg(args, arg, hasArgValue));
  }

  @ParameterizedTest
  @MethodSource("provideArgsFail")
  public void testParseArgFail(String[] args, String arg, boolean hasArgValue) {
    assertThrows(IllegalArgumentException.class, () -> Util.parseArg(args, arg, hasArgValue));
  }

  private static Object[][] provideArgs() {
    return new Object[][] {
      {null, null, false, null},
      {new String[] {}, IGNORE_QUESTS, false, null},
      {new String[] {IGNORE_QUESTS}, null, false, null},
      {new String[] {IGNORE_QUESTS}, "", false, null},
      {new String[] {DP, DP_PATH}, DP, true, DP_PATH},
      {new String[] {IGNORE_QUESTS}, IGNORE_QUESTS, false, IGNORE_QUESTS},
      {new String[] {IGNORE_QUESTS, DP, DP_PATH}, DP, true, DP_PATH}
    };
  }

  private static Object[][] provideArgsFail() {
    return new Object[][] {{new String[] {IGNORE_QUESTS}, IGNORE_QUESTS, true}};
  }
}
