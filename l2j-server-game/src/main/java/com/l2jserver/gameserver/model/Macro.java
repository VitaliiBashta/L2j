package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.model.interfaces.Identifiable;

import java.util.List;

public class Macro implements Identifiable {
  private final int icon;
  private final String name;
  private final String description;
  private final String acronym;
  private final List<MacroCmd> commands;
  private int id;

  public Macro(
      int id, int icon, String name, String description, String acronym, List<MacroCmd> list) {
    this.id = id;
    this.icon = icon;
    this.name = name;
    this.description = description;
    this.acronym = acronym;
    commands = list;
  }

  @Override
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getAcronym() {
    return acronym;
  }

  public List<MacroCmd> getCommands() {
    return commands;
  }
}
