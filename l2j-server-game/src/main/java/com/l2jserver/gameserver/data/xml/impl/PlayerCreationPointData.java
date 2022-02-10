package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlayerCreationPointData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(PlayerCreationPointData.class);

  private final Map<ClassId, List<Location>> creationPointData = new HashMap<>();

  public static PlayerCreationPointData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    creationPointData.clear();
    parseDatapackFile("data/stats/chars/pcCreationPoints.xml");
    LOG.info(
        "Loaded {} character creation points.",
        creationPointData.values().stream().mapToInt(List::size).sum());
  }

  public Location getCreationPoint(ClassId classId) {
    List<Location> locations = creationPointData.get(classId);
    return locations.get(Rnd.get(locations.size()));
  }

  @Override
  public void parseDocument(Document doc) {
    NamedNodeMap attrs;
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("startpoints".equalsIgnoreCase(d.getNodeName())) {
            List<Location> creationPoints = new ArrayList<>();
            for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
              if ("spawn".equalsIgnoreCase(c.getNodeName())) {
                attrs = c.getAttributes();
                creationPoints.add(
                    new Location(
                        parseInteger(attrs, "x"),
                        parseInteger(attrs, "y"),
                        parseInteger(attrs, "z")));
              } else if ("classid".equalsIgnoreCase(c.getNodeName())) {
                creationPointData.put(
                    ClassId.getClassId(Integer.parseInt(c.getTextContent())), creationPoints);
              }
            }
          }
        }
      }
    }
  }

  private static class SingletonHolder {
    protected static final PlayerCreationPointData INSTANCE = new PlayerCreationPointData();
  }
}
