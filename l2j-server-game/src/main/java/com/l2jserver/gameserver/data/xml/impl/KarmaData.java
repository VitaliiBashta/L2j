package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

@Service
public class KarmaData extends IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(KarmaData.class);

  private final Map<Integer, Double> _karmaTable = new HashMap<>();

  public static KarmaData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public synchronized void load() {
    _karmaTable.clear();
    parseDatapackFile("data/stats/chars/pcKarmaIncrease.xml");
    LOG.info("Loaded {} karma modifiers.", _karmaTable.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("pcKarmaIncrease".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("increase".equalsIgnoreCase(d.getNodeName())) {
            final NamedNodeMap attrs = d.getAttributes();
            _karmaTable.put(parseInteger(attrs, "lvl"), parseDouble(attrs, "val"));
          }
        }
      }
    }
  }

  /**
   * @param level
   * @return {@code double} modifier used to calculate karma lost upon death.
   */
  public double getMultiplier(int level) {
    return _karmaTable.get(level);
  }

  private static class SingletonHolder {
    protected static final KarmaData INSTANCE = new KarmaData();
  }
}
