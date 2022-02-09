package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.SiegeScheduleDate;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.util.IXmlReader;
import com.l2jserver.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class SiegeScheduleData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(SiegeScheduleData.class);

  private final List<SiegeScheduleDate> _scheduleData = new ArrayList<>();

  protected SiegeScheduleData() {
    load();
  }

  public static SiegeScheduleData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public synchronized void load() {
    _scheduleData.clear();
    parseFile(new File("config/SiegeSchedule.xml"));
    LOG.info("Loaded {} siege schedulers.", _scheduleData.size());

    if (_scheduleData.isEmpty()) {
      _scheduleData.add(new SiegeScheduleDate(new StatsSet()));
      LOG.info("Emergency Loaded {} default siege schedulers.", _scheduleData.size());
    }
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node cd = n.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
          if ("schedule".equals(cd.getNodeName())) {
            final StatsSet set = new StatsSet();
            final NamedNodeMap attrs = cd.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
              Node node = attrs.item(i);
              String key = node.getNodeName();
              String val = node.getNodeValue();
              if ("day".equals(key)) {
                if (!Util.isDigit(val)) {
                  val = Integer.toString(getValueForField(val));
                }
              }
              set.set(key, val);
            }
            _scheduleData.add(new SiegeScheduleDate(set));
          }
        }
      }
    }
  }

  private int getValueForField(String field) {
    try {
      return Calendar.class.getField(field).getInt(Calendar.class);
    } catch (Exception ex) {
      LOG.warn("Unable to get value!", ex);
      return -1;
    }
  }

  public List<SiegeScheduleDate> getScheduleDates() {
    return _scheduleData;
  }

  private static class SingletonHolder {
    protected static final SiegeScheduleData INSTANCE = new SiegeScheduleData();
  }
}
