package com.l2jserver.datapack.quests.TerritoryWarScripts;

import com.l2jserver.gameserver.network.NpcStringId;
import org.springframework.stereotype.Service;

@Service
public class Q00735_MakeSpearsDull extends TerritoryWarSuperClass {
  public Q00735_MakeSpearsDull() {
    super(735, Q00735_MakeSpearsDull.class.getSimpleName(), "Make Spears Dull");
    CLASS_IDS =
        new int[] {
          23, 101, 36, 108, 8, 93, 2, 88, 3, 89, 48, 114, 46, 113, 55, 117, 9, 92, 24, 102, 37, 109,
          34, 107, 21, 100, 127, 131, 128, 132, 129, 133, 130, 134, 135, 136
        };
    RANDOM_MIN = 15;
    RANDOM_MAX = 20;
    npcString =
        new NpcStringId[] {
          NpcStringId.YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES,
          NpcStringId.YOU_WEAKENED_THE_ENEMYS_ATTACK
        };
  }
}
