package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillLearnData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(SkillLearnData.class);

  private final Map<Integer, List<ClassId>> skillLearn = new HashMap<>();

  protected SkillLearnData() {
    load();
  }

  @Override
  public synchronized void load() {
    skillLearn.clear();
    parseDatapackFile("data/skillLearn.xml");
    LOG.info("Loaded {} skill learn data.", skillLearn.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
      if ("list".equalsIgnoreCase(node.getNodeName())) {
        for (Node list_node = node.getFirstChild();
            list_node != null;
            list_node = list_node.getNextSibling()) {
          if ("npc".equalsIgnoreCase(list_node.getNodeName())) {
            final List<ClassId> classIds = new ArrayList<>();
            for (Node c = list_node.getFirstChild(); c != null; c = c.getNextSibling()) {
              if ("classId".equalsIgnoreCase(c.getNodeName())) {
                classIds.add(ClassId.getClassId(Integer.parseInt(c.getTextContent())));
              }
            }
            skillLearn.put(parseInteger(list_node.getAttributes(), "id"), classIds);
          }
        }
      }
    }
  }

  /**
   * @param npcId
   * @return {@link List} of {@link ClassId}'s that this npcId can teach.
   */
  public List<ClassId> getSkillLearnData(int npcId) {
    return skillLearn.get(npcId);
  }
}
