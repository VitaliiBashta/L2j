package com.l2jserver.gameserver.datatables;

import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.l2jserver.gameserver.config.Configuration.server;

@Service
public class MerchantPriceConfigTable {

  // Zoey76: TODO: Implement using IXmlReader.
  private static final Logger LOG = LoggerFactory.getLogger(MerchantPriceConfigTable.class);
  private static final String MPCS_FILE = "MerchantPriceConfig.xml";
  private final Map<Integer, MerchantPriceConfig> _mpcs = new HashMap<>();
  private MerchantPriceConfig _defaultMpc;

  public static MerchantPriceConfigTable getInstance() {
    return SingletonHolder._instance;
  }

  public MerchantPriceConfig getMerchantPriceConfig(L2MerchantInstance npc) {
    for (MerchantPriceConfig mpc : _mpcs.values()) {
      if ((npc.getWorldRegion() != null) && npc.getWorldRegion().containsZone(mpc.getZoneId())) {
        return mpc;
      }
    }
    return _defaultMpc;
  }

  @PostConstruct
  public void loadInstances() {
    try {
      loadXML();
      LOG.info("Loaded {} merchant price configs.", _mpcs.size());
    } catch (Exception ex) {
      LOG.error("Failed loading MerchantPriceConfigTable!", ex);
    }
  }

  public void loadXML() throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringComments(true);
    File file = new File(server().getDatapackRoot(), "data/" + MPCS_FILE);
    if (file.exists()) {
      int defaultPriceConfigId;
      Document doc = factory.newDocumentBuilder().parse(file);

      Node n = doc.getDocumentElement();
      Node dpcNode = n.getAttributes().getNamedItem("defaultPriceConfig");
      if (dpcNode == null) {
        throw new IllegalStateException("merchantPriceConfig must define an 'defaultPriceConfig'");
      }
      defaultPriceConfigId = Integer.parseInt(dpcNode.getNodeValue());

      MerchantPriceConfig mpc;
      for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
        mpc = parseMerchantPriceConfig(n);
        if (mpc != null) {
          _mpcs.put(mpc.getId(), mpc);
        }
      }

      MerchantPriceConfig defaultMpc = this.getMerchantPriceConfig(defaultPriceConfigId);
      if (defaultMpc == null) {
        throw new IllegalStateException("'defaultPriceConfig' points to an non-loaded priceConfig");
      }
      _defaultMpc = defaultMpc;
    }
  }

  public MerchantPriceConfig getMerchantPriceConfig(int id) {
    return _mpcs.get(id);
  }

  private MerchantPriceConfig parseMerchantPriceConfig(Node n) {
    if (n.getNodeName().equals("priceConfig")) {
      final int id;
      final int baseTax;
      int castleId = -1;
      int zoneId = -1;
      final String name;

      Node node = n.getAttributes().getNamedItem("id");
      if (node == null) {
        throw new IllegalStateException("Must define the priceConfig 'id'");
      }
      id = Integer.parseInt(node.getNodeValue());

      node = n.getAttributes().getNamedItem("name");
      if (node == null) {
        throw new IllegalStateException("Must define the priceConfig 'name'");
      }
      name = node.getNodeValue();

      node = n.getAttributes().getNamedItem("baseTax");
      if (node == null) {
        throw new IllegalStateException("Must define the priceConfig 'baseTax'");
      }
      baseTax = Integer.parseInt(node.getNodeValue());

      node = n.getAttributes().getNamedItem("castleId");
      if (node != null) {
        castleId = Integer.parseInt(node.getNodeValue());
      }

      node = n.getAttributes().getNamedItem("zoneId");
      if (node != null) {
        zoneId = Integer.parseInt(node.getNodeValue());
      }

      return new MerchantPriceConfig(id, name, baseTax, castleId, zoneId);
    }
    return null;
  }

  public void updateReferences() {
    for (final MerchantPriceConfig mpc : _mpcs.values()) {
      mpc.updateReferences();
    }
  }

  public static final class MerchantPriceConfig {
    private final int _id;
    private final String _name;
    private final int _baseTax;
    private final int _castleId;
    private final int _zoneId;
    private Castle _castle;

    public MerchantPriceConfig(
        final int id, final String name, final int baseTax, final int castleId, final int zoneId) {
      _id = id;
      _name = name;
      _baseTax = baseTax;
      _castleId = castleId;
      _zoneId = zoneId;
    }

    /** @return Returns the id. */
    public int getId() {
      return _id;
    }

    /** @return Returns the name. */
    public String getName() {
      return _name;
    }

    /** @return Returns the baseTax / 100.0. */
    public double getBaseTaxRate() {
      return _baseTax / 100.0;
    }

    /** @return Returns the zoneId. */
    public int getZoneId() {
      return _zoneId;
    }

    public double getCastleTaxRate() {
      return hasCastle() ? getCastle().getTaxRate() : 0.0;
    }

    public boolean hasCastle() {
      return getCastle() != null;
    }

    /** @return Returns the castle. */
    public Castle getCastle() {
      return _castle;
    }

    public double getTotalTaxRate() {
      return getTotalTax() / 100.0;
    }

    public int getTotalTax() {
      return hasCastle() ? (getCastle().getTaxPercent() + getBaseTax()) : getBaseTax();
    }

    /** @return Returns the baseTax. */
    public int getBaseTax() {
      return _baseTax;
    }

    public void updateReferences() {
      if (_castleId > 0) {
        _castle = CastleManager.getInstance().getCastleById(_castleId);
      }
    }
  }

  private static class SingletonHolder {
    protected static final MerchantPriceConfigTable _instance = new MerchantPriceConfigTable();
  }
}
