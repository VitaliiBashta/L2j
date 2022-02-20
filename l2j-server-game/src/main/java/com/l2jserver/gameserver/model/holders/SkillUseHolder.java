package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.model.skills.Skill;

public class SkillUseHolder extends SkillHolder {
  private final boolean ctrlPressed;
  private final boolean shiftPressed;

  public SkillUseHolder(Skill skill, boolean ctrlPressed, boolean shiftPressed) {
    super(skill);
    this.ctrlPressed = ctrlPressed;
    this.shiftPressed = shiftPressed;
  }

  public boolean isCtrlPressed() {
    return ctrlPressed;
  }

  public boolean isShiftPressed() {
    return shiftPressed;
  }
}
