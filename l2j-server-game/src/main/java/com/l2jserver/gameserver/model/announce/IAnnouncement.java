package com.l2jserver.gameserver.model.announce;

public interface IAnnouncement {
  int getId();

  AnnouncementType getType();

  void setType(AnnouncementType type);

  boolean isValid();

  String getContent();

  void setContent(String content);

  String getAuthor();

  void setAuthor(String author);
}
