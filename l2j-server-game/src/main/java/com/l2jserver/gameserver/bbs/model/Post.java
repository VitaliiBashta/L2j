package com.l2jserver.gameserver.bbs.model;

public class Post {

  private int id;
  private String ownerName;
  private int ownerId;
  private long date;
  private int topicId;
  private int forumId;
  private String txt;

  public Post(
      int id, String ownerName, int ownerId, long date, int topicId, int forumId, String txt) {
    this.id = id;
    this.ownerName = ownerName;
    this.ownerId = ownerId;
    this.date = date;
    this.topicId = topicId;
    this.forumId = forumId;
    this.txt = txt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public int getTopicId() {
    return topicId;
  }

  public void setTopicId(int topicId) {
    this.topicId = topicId;
  }

  public int getForumId() {
    return forumId;
  }

  public void setForumId(int forumId) {
    this.forumId = forumId;
  }

  public String getTxt() {
    return txt;
  }

  public void setTxt(String txt) {
    this.txt = txt;
  }
}
