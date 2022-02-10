package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.holders.MinionHolder;
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

/**
 * This class handles minions from Spawn System<br>
 * Once Spawn System gets reworked delete this class<br>
 */
@Service
public class MinionData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(MinionData.class);

  public final Map<Integer, List<MinionHolder>> _tempMinions = new HashMap<>();

  @Override
  public void load() {
    _tempMinions.clear();
    parseDatapackFile("data/minionData.xml");
    LOG.info("Loaded {} minions data.", _tempMinions.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
      if ("list".equals(node.getNodeName())) {
        for (Node listNode = node.getFirstChild();
            listNode != null;
            listNode = listNode.getNextSibling()) {
          if ("npc".equals(listNode.getNodeName())) {
            final List<MinionHolder> minions = new ArrayList<>(1);
            NamedNodeMap attrs = listNode.getAttributes();
            int id = parseInteger(attrs, "id");
            for (Node npcNode = listNode.getFirstChild();
                npcNode != null;
                npcNode = npcNode.getNextSibling()) {
              if ("minion".equals(npcNode.getNodeName())) {
                attrs = npcNode.getAttributes();
                minions.add(
                    new MinionHolder(
                        parseInteger(attrs, "id"),
                        parseInteger(attrs, "count"),
                        parseInteger(attrs, "respawnTime"),
                        0));
              }
            }
            _tempMinions.put(id, minions);
          }
        }
      }
    }
  }
}
