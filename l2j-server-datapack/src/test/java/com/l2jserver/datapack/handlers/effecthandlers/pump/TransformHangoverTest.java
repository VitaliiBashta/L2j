package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TransformHangoverTest {

  @Mock private BuffInfo buffInfo;

  private TransformHangover effect;

  @BeforeEach
  void init() {
    final var set = new StatsSet(Map.of("name", "TransformHangover"));
    final var params = new StatsSet(Map.of());
    effect = new TransformHangover(null, null, set, params);
  }

  @Test
  void test_on_action_time() {
    effect.onActionTime(buffInfo);
  }
}
