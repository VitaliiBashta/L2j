package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.items.PcItemTemplate;
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

import static com.l2jserver.gameserver.config.Configuration.character;

@Service
public class InitialEquipmentData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(InitialEquipmentData.class);

  private static final String NORMAL = "data/stats/initialEquipment.xml";

  private static final String EVENT = "data/stats/initialEquipmentEvent.xml";

  private final Map<ClassId, List<PcItemTemplate>> _initialEquipmentList = new HashMap<>();

  protected InitialEquipmentData() {
    load();
  }

  public static InitialEquipmentData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    _initialEquipmentList.clear();
    parseDatapackFile(character().initialEquipmentEvent() ? EVENT : NORMAL);
    LOG.info("Loaded {} initial equipment data.", _initialEquipmentList.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("equipment".equalsIgnoreCase(d.getNodeName())) {
            parseEquipment(d);
          }
        }
      }
    }
  }

  /**
   * Parses the equipment.
   *
   * @param d parse an initial equipment and add it to {@link #_initialEquipmentList}
   */
  private void parseEquipment(Node d) {
    NamedNodeMap attrs = d.getAttributes();
    final ClassId classId =
        ClassId.getClassId(Integer.parseInt(attrs.getNamedItem("classId").getNodeValue()));
    final List<PcItemTemplate> equipList = new ArrayList<>();
    for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
      if ("item".equalsIgnoreCase(c.getNodeName())) {
        final StatsSet set = new StatsSet();
        attrs = c.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
          Node attr = attrs.item(i);
          set.set(attr.getNodeName(), attr.getNodeValue());
        }
        equipList.add(new PcItemTemplate(set));
      }
    }
    _initialEquipmentList.put(classId, equipList);
  }

  /**
   * Gets the equipment list.
   *
   * @param cId the class Id for the required initial equipment.
   * @return the initial equipment for the given class Id.
   */
  public List<PcItemTemplate> getEquipmentList(ClassId cId) {
    return _initialEquipmentList.get(cId);
  }

  /**
   * Gets the equipment list.
   *
   * @param cId the class Id for the required initial equipment.
   * @return the initial equipment for the given class Id.
   */
  public List<PcItemTemplate> getEquipmentList(int cId) {
    return _initialEquipmentList.get(ClassId.getClassId(cId));
  }

  private static class SingletonHolder {
    protected static final InitialEquipmentData INSTANCE = new InitialEquipmentData();
  }
}
