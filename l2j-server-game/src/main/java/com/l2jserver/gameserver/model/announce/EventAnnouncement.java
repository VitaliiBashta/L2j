package com.l2jserver.gameserver.model.announce;

import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.script.DateRange;

import java.time.LocalDateTime;

public class EventAnnouncement implements IAnnouncement {
  private final int id;
  private final DateRange range;
  private String content;

  public EventAnnouncement(DateRange range, String content) {
    id = IdFactory.getInstance().getNextId();
    this.range = range;
    this.content = content;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public AnnouncementType getType() {
    return AnnouncementType.EVENT;
  }

  @Override
  public void setType(AnnouncementType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isValid() {
    return range.isWithinRange(LocalDateTime.now());
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String getAuthor() {
    return "N/A";
  }

  @Override
  public void setAuthor(String author) {
    throw new UnsupportedOperationException();
  }

  public boolean deleteMe() {
    IdFactory.getInstance().releaseId(id);
    return true;
  }

  public boolean storeMe() {
    return true;
  }

  public boolean updateMe() {
    throw new UnsupportedOperationException();
  }
}
