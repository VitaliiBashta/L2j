package com.l2jserver.gameserver.network.serverpackets;

import java.util.List;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;

/** MagicSkillLaunched server packet implementation. */
public class MagicSkillLaunched extends L2GameServerPacket {
  private final int _charObjId;
  private final int _skillId;
  private final int _skillLevel;
  private final List<L2Object> _targets;

  public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, L2Object target) {
    this(cha, skillId, skillLevel, List.of(target));
  }

  public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, List<L2Object> targets) {
    _charObjId = cha.getObjectId();
    _skillId = skillId;
    _skillLevel = skillLevel;

    if (targets == null) {
      targets = List.of(cha);
    }
    _targets = targets;
  }

  public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel) {
    this(cha, skillId, skillLevel, List.of(cha));
  }

  @Override
  protected final void writeImpl() {
    writeC(0x54);
    writeD(_charObjId);
    writeD(_skillId);
    writeD(_skillLevel);
    writeD(_targets.size());
    for (L2Object target : _targets) {
      writeD(target.getObjectId());
    }
  }
}
