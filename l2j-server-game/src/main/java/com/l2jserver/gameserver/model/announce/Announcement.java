package com.l2jserver.gameserver.model.announce;

import com.l2jserver.commons.database.ConnectionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class Announcement implements IAnnouncement {
	
	protected static final Logger _log = Logger.getLogger(Announcement.class.getName());
	
	private static final String INSERT_QUERY = "INSERT INTO announcements (type, content, author) VALUES (?, ?, ?)";
	
	private static final String UPDATE_QUERY = "UPDATE announcements SET type = ?, content = ?, author = ? WHERE id = ?";
	
	private static final String DELETE_QUERY = "DELETE FROM announcements WHERE id = ?";
	
	protected int _id;
	
	private AnnouncementType _type;
	
	private String _content;
	
	private String _author;
	
	public Announcement(AnnouncementType type, String content, String author) {
		_type = type;
		_content = content;
		_author = author;
	}
	
	public Announcement(int id, AnnouncementType type, String content, String author) {
		this(type, content, author);
		_id = id;
	}
	
	@Override
	public int getId() {
		return _id;
	}
	
	@Override
	public AnnouncementType getType() {
		return _type;
	}
	
	@Override
	public void setType(AnnouncementType type) {
		_type = type;
	}
	
	@Override
	public String getContent() {
		return _content;
	}
	
	@Override
	public void setContent(String content) {
		_content = content;
	}
	
	@Override
	public String getAuthor() {
		return _author;
	}
	
	@Override
	public void setAuthor(String author) {
		_author = author;
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	public boolean storeMe() {
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(INSERT_QUERY, RETURN_GENERATED_KEYS)) {
			ps.setInt(1, _type.ordinal());
			ps.setString(2, _content);
			ps.setString(3, _author);
			ps.execute();
			try (var rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					_id = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't store announcement: ", e);
			return false;
		}
		return true;
	}
	
	public boolean updateMe() {
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(UPDATE_QUERY)) {
			ps.setInt(1, _type.ordinal());
			ps.setString(2, _content);
			ps.setString(3, _author);
			ps.setInt(4, _id);
			ps.execute();
		} catch (Exception e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't store announcement: ", e);
			return false;
		}
		return true;
	}
	
	public boolean deleteMe() {
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(DELETE_QUERY)) {
			ps.setInt(1, _id);
			ps.execute();
		} catch (Exception e) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't remove announcement: ", e);
			return false;
		}
		return true;
	}
}
