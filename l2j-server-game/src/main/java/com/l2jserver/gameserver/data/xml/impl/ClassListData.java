package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.ClassInfo;
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
public class ClassListData implements IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(ClassListData.class);

  private final Map<ClassId, ClassInfo> _classData = new HashMap<>();

  public static ClassListData getInstance() {
    return SingletonHolder.INSTANCE;
  }

  @Override
  public void load() {
    _classData.clear();
    parseDatapackFile("data/stats/chars/classList.xml");
    LOG.info("Loaded {} class data.", _classData.size());
  }

  @Override
  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equals(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          final NamedNodeMap attrs = d.getAttributes();
          if ("class".equals(d.getNodeName())) {
            Node attr = attrs.getNamedItem("classId");
            final ClassId classId = ClassId.getClassId(parseInteger(attr));
            attr = attrs.getNamedItem("name");
            final String className = attr.getNodeValue();
            attr = attrs.getNamedItem("parentClassId");
            final ClassId parentClassId =
                (attr != null) ? ClassId.getClassId(parseInteger(attr)) : null;
            _classData.put(classId, new ClassInfo(classId, className, parentClassId));
          }
        }
      }
    }
  }

  /**
   * Gets the class list.
   *
   * @return the complete class list
   */
  public Map<ClassId, ClassInfo> getClassList() {
    return _classData;
  }

  /**
   * Gets the class info.
   *
   * @param classId the class ID
   * @return the class info related to the given {@code classId}
   */
  public ClassInfo getClass(ClassId classId) {
    return _classData.get(classId);
  }

  /**
   * Gets the class info.
   *
   * @param classId the class Id as integer
   * @return the class info related to the given {@code classId}
   */
  public ClassInfo getClass(int classId) {
    final ClassId id = ClassId.getClassId(classId);
    return (id != null) ? _classData.get(id) : null;
  }

  private static class SingletonHolder {
    protected static final ClassListData INSTANCE = new ClassListData();
  }
}
