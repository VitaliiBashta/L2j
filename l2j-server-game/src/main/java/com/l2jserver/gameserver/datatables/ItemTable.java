package com.l2jserver.gameserver.datatables;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.engines.items.Item;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.enums.ItemLocation;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.EffectHandler;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.instance.L2EventMonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.PlayerState;
import com.l2jserver.gameserver.model.conditions.*;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.item.OnItemCreate;
import com.l2jserver.gameserver.model.interfaces.Identifiable;
import com.l2jserver.gameserver.model.items.L2Armor;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.EffectScope;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;
import com.l2jserver.gameserver.util.GMAudit;
import com.l2jserver.gameserver.util.IXmlReader;
import com.l2jserver.gameserver.util.file.filter.XMLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

import static com.l2jserver.gameserver.config.Configuration.*;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.ADENA_ID;
import static com.l2jserver.gameserver.model.items.type.EtcItemType.ARROW;
import static com.l2jserver.gameserver.model.items.type.EtcItemType.SHOT;

@Service
public class ItemTable extends IXmlReader {

  public static final Map<String, Integer> SLOTS = new HashMap<>();
  private static final Logger LOG = LoggerFactory.getLogger(ItemTable.class);
  private static final Logger LOG_ITEM = LoggerFactory.getLogger("item");

  static {
    SLOTS.put("shirt", L2Item.SLOT_UNDERWEAR);
    SLOTS.put("lbracelet", L2Item.SLOT_L_BRACELET);
    SLOTS.put("rbracelet", L2Item.SLOT_R_BRACELET);
    SLOTS.put("talisman", L2Item.SLOT_DECO);
    SLOTS.put("chest", L2Item.SLOT_CHEST);
    SLOTS.put("fullarmor", L2Item.SLOT_FULL_ARMOR);
    SLOTS.put("head", L2Item.SLOT_HEAD);
    SLOTS.put("hair", L2Item.SLOT_HAIR);
    SLOTS.put("hairall", L2Item.SLOT_HAIRALL);
    SLOTS.put("underwear", L2Item.SLOT_UNDERWEAR);
    SLOTS.put("back", L2Item.SLOT_BACK);
    SLOTS.put("neck", L2Item.SLOT_NECK);
    SLOTS.put("legs", L2Item.SLOT_LEGS);
    SLOTS.put("feet", L2Item.SLOT_FEET);
    SLOTS.put("gloves", L2Item.SLOT_GLOVES);
    SLOTS.put("chest,legs", L2Item.SLOT_CHEST | L2Item.SLOT_LEGS);
    SLOTS.put("belt", L2Item.SLOT_BELT);
    SLOTS.put("rhand", L2Item.SLOT_R_HAND);
    SLOTS.put("lhand", L2Item.SLOT_L_HAND);
    SLOTS.put("lrhand", L2Item.SLOT_LR_HAND);
    SLOTS.put("rear;lear", L2Item.SLOT_R_EAR | L2Item.SLOT_L_EAR);
    SLOTS.put("rfinger;lfinger", L2Item.SLOT_R_FINGER | L2Item.SLOT_L_FINGER);
    SLOTS.put("wolf", L2Item.SLOT_WOLF);
    SLOTS.put("greatwolf", L2Item.SLOT_GREATWOLF);
    SLOTS.put("hatchling", L2Item.SLOT_HATCHLING);
    SLOTS.put("strider", L2Item.SLOT_STRIDER);
    SLOTS.put("babypet", L2Item.SLOT_BABYPET);
    SLOTS.put("none", L2Item.SLOT_NONE);

    // retail compatibility
    SLOTS.put("onepiece", L2Item.SLOT_FULL_ARMOR);
    SLOTS.put("hair2", L2Item.SLOT_HAIR2);
    SLOTS.put("dhair", L2Item.SLOT_HAIRALL);
    SLOTS.put("alldress", L2Item.SLOT_ALLDRESS);
    SLOTS.put("deco1", L2Item.SLOT_DECO);
    SLOTS.put("waist", L2Item.SLOT_BELT);
  }

  protected final java.util.logging.Logger _log = java.util.logging.Logger.getLogger(getClass().getName());
  protected final Map<String, String[]> tables = new HashMap<>();
  private final Map<Integer, L2EtcItem> _etcItems = new HashMap<>();
  private final List<File> _itemFiles = new ArrayList<>();
  private final Map<Integer, L2Armor> _armors = new HashMap<>();
  private final Map<Integer, L2Weapon> _weapons = new HashMap<>();
  private final List<L2Item> _itemsInFile = new ArrayList<>();
  private L2Item[] _allTemplates;
  private final EffectHandler effectHandler;
  private File file;
  private Item _currentItem = null;

  protected ItemTable(EffectHandler effectHandler) {
    this.effectHandler = effectHandler;


  }

  public static ItemTable getInstance() {
    return SingletonHolder._instance;
  }

  private void hashFiles(String dirname, List<File> hash) {
    final var dir = new File(server().getDatapackRoot(), dirname);
    if (!dir.exists()) {
      LOG.warn("Directory {} does not exists!", dir.getAbsolutePath());
      return;
    }

    final var files = dir.listFiles(new XMLFilter());
    if (files != null) {
      Collections.addAll(hash, files);
    }
  }

  public List<L2Item> loadItems() {
    List<L2Item> list = new ArrayList<>();
    for (File f : _itemFiles) {
      parseFile(f);

    }
    return list;
  }

  public void load() {
    int highest = 0;
    _armors.clear();
    _etcItems.clear();
    _weapons.clear();
    parseDatapackDirectory("data/stats/items", true);
    for (L2Item item : loadItems()) {
      if (highest < item.getId()) {
        highest = item.getId();
      }
      if (item instanceof L2EtcItem) {
        _etcItems.put(item.getId(), (L2EtcItem) item);
      } else if (item instanceof L2Armor) {
        _armors.put(item.getId(), (L2Armor) item);
      } else {
        _weapons.put(item.getId(), (L2Weapon) item);
      }
    }

    buildFastLookupTable(highest);

    LOG.info("Loaded {} Etc items.", _etcItems.size());
    LOG.info("Loaded {} Armor items.", _armors.size());
    LOG.info("Loaded {} Weapon items.", _weapons.size());
    LOG.info("Loaded {} items in total.", (_etcItems.size() + _armors.size() + _weapons.size()));
  }

  public void parseDocument(Document doc) {
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("list".equalsIgnoreCase(n.getNodeName())) {

        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("item".equalsIgnoreCase(d.getNodeName())) {
            try {
              _currentItem = new Item();
              parseItem(d);
              _itemsInFile.add(_currentItem.item);
              resetTable();
            } catch (Exception e) {
              _log.log(Level.WARNING, "Cannot create item " + _currentItem.id, e);
            }
          }
        }
      }
    }
  }

  protected void parseItem(Node n) throws InvocationTargetException {
    int itemId = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
    String className = n.getAttributes().getNamedItem("type").getNodeValue();
    String itemName = n.getAttributes().getNamedItem("name").getNodeValue();

    _currentItem.id = itemId;
    _currentItem.name = itemName;
    _currentItem.type = className;
    _currentItem.set = new StatsSet();
    _currentItem.set.set("item_id", itemId);
    _currentItem.set.set("name", itemName);

    Node first = n.getFirstChild();
    for (n = first; n != null; n = n.getNextSibling()) {
      if ("table".equalsIgnoreCase(n.getNodeName())) {
        if (_currentItem.item != null) {
          throw new IllegalStateException("Item created but table node found! Item " + itemId);
        }
        parseTable(n);
      } else if ("set".equalsIgnoreCase(n.getNodeName())) {
        if (_currentItem.item != null) {
          throw new IllegalStateException("Item created but set node found! Item " + itemId);
        }
        parseBeanSet(n, _currentItem.set, 1);
      } else if ("for".equalsIgnoreCase(n.getNodeName())) {
        makeItem();
        parseTemplate(n, _currentItem.item);
      } else if ("cond".equalsIgnoreCase(n.getNodeName())) {
        makeItem();
        Condition condition = parseCondition(n.getFirstChild(), _currentItem.item);
        Node msg = n.getAttributes().getNamedItem("msg");
        Node msgId = n.getAttributes().getNamedItem("msgId");
        if ((condition != null) && (msg != null)) {
          condition.setMessage(msg.getNodeValue());
        } else if ((condition != null) && (msgId != null)) {
          condition.setMessageId(Integer.decode(getValue(msgId.getNodeValue(), null)));
          Node addName = n.getAttributes().getNamedItem("addName");
          if ((addName != null) && (Integer.decode(getValue(msgId.getNodeValue(), null)) > 0)) {
            condition.addName();
          }
        }
        _currentItem.item.attach(condition);
      }
    }
    // bah! in this point item doesn't have to be still created
    makeItem();
  }
  protected void resetTable() {
    tables.clear();
  }

  protected StatsSet getStatsSet() {
    return _currentItem.set;
  }

  protected String getTableValue(String name) {
    return tables.get(name)[_currentItem.currentLevel];
  }

  protected String getTableValue(String name, int idx) {
    return tables.get(name)[idx - 1];
  }



  protected void parseBeanSet(Node n, StatsSet set, Integer level) {
    String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
    String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
    char ch = value.isEmpty() ? ' ' : value.charAt(0);
    if ((ch == '#') || (ch == '-') || Character.isDigit(ch)) {
      set.set(name, String.valueOf(getValue(value, level)));
    } else {
      set.set(name, value);
    }
  }

  protected String getValue(String value, Object template) {
    // is it a table?
    if (value.charAt(0) == '#') {
      if (template instanceof Skill) {
        return getTableValue(value);
      } else if (template instanceof Integer) {
        return getTableValue(value, (Integer) template);
      } else {
        throw new IllegalStateException();
      }
    }
    return value;
  }

  protected void parseTable(Node n) {
    NamedNodeMap attrs = n.getAttributes();
    String name = attrs.getNamedItem("name").getNodeValue();
    if (name.charAt(0) != '#') {
      throw new IllegalArgumentException("Table name must start with #");
    }
    StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
    List<String> array = new ArrayList<>(data.countTokens());
    while (data.hasMoreTokens()) {
      array.add(data.nextToken());
    }
    setTable(name, array.toArray(new String[array.size()]));
  }


  protected void setTable(String name, String[] table) {
    tables.put(name, table);
  }



  protected Condition parseGameCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node a = attrs.item(i);
      if ("skill".equalsIgnoreCase(a.getNodeName())) {
        boolean val = Boolean.parseBoolean(a.getNodeValue());
        cond = joinAnd(cond, new ConditionWithSkill(val));
      }
      if ("night".equalsIgnoreCase(a.getNodeName())) {
        boolean val = Boolean.parseBoolean(a.getNodeValue());
        cond = joinAnd(cond, new ConditionGameTime(ConditionGameTime.CheckGameTime.NIGHT, val));
      }
      if ("chance".equalsIgnoreCase(a.getNodeName())) {
        int val = Integer.decode(getValue(a.getNodeValue(), null));
        cond = joinAnd(cond, new ConditionGameChance(val));
      }
    }
    if (cond == null) {
      _log.severe("Unrecognized <game> condition in " + file);
    }
    return cond;
  }

  protected Condition parseCondition(Node n, Object template) {
    while ((n != null) && (n.getNodeType() != Node.ELEMENT_NODE)) {
      n = n.getNextSibling();
    }

    Condition condition = null;
    if (n != null) {
      switch (n.getNodeName().toLowerCase()) {
        case "and" -> condition = parseLogicAnd(n, template);
        case "or" -> condition = parseLogicOr(n, template);
        case "not" -> condition = parseLogicNot(n, template);
        case "player" -> condition = parsePlayerCondition(n, template);
        case "target" -> condition = parseTargetCondition(n, template);
        case "using" -> condition = parseUsingCondition(n);
        case "game" -> condition = parseGameCondition(n);
      }
    }
    return condition;
  }

  protected Condition parseLogicAnd(Node n, Object template) {
    ConditionLogicAnd cond = new ConditionLogicAnd();
    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        cond.add(parseCondition(n, template));
      }
    }
    if ((cond.conditions == null) || (cond.conditions.length == 0)) {
      _log.severe("Empty <and> condition in " + file);
    }
    return cond;
  }

  protected Condition parseLogicOr(Node n, Object template) {
    ConditionLogicOr cond = new ConditionLogicOr();
    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        cond.add(parseCondition(n, template));
      }
    }
    if ((cond.conditions == null) || (cond.conditions.length == 0)) {
      _log.severe("Empty <or> condition in " + file);
    }
    return cond;
  }

  protected Condition parseLogicNot(Node n, Object template) {
    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        return new ConditionLogicNot(parseCondition(n, template));
      }
    }
    _log.severe("Empty <not> condition in " + file);
    return null;
  }

  protected Condition parsePlayerCondition(Node n, Object template) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node a = attrs.item(i);
      switch (a.getNodeName().toLowerCase()) {
        case "races" -> {
          final String[] racesVal = a.getNodeValue().split(",");
          final Race[] races = new Race[racesVal.length];
          for (int r = 0; r < racesVal.length; r++) {
            if (racesVal[r] != null) {
              races[r] = Race.valueOf(racesVal[r]);
            }
          }
          cond = joinAnd(cond, new ConditionPlayerRace(races));
        }
        case "level" -> {
          int lvl = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerLevel(lvl));
        }
        case "levelrange" -> {
          String[] range = getValue(a.getNodeValue(), template).split(";");
          if (range.length == 2) {
            final int minimumLevel = Integer.decode(getValue(a.getNodeValue(), template).split(";")[0]);
            final int maximumLevel = Integer.decode(getValue(a.getNodeValue(), template).split(";")[1]);
            cond = joinAnd(cond, new ConditionPlayerLevelRange(minimumLevel, maximumLevel));
          }
        }
        case "resting" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.RESTING, val));
        }
        case "flying" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.FLYING, val));
        }
        case "moving" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.MOVING, val));
        }
        case "running" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.RUNNING, val));
        }
        case "standing" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.STANDING, val));
        }
        case "behind" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.BEHIND, val));
        }
        case "front" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.FRONT, val));
        }
        case "chaotic" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.CHAOTIC, val));
        }
        case "olympiad" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerState(PlayerState.OLYMPIAD, val));
        }
        case "ishero" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerIsHero(val));
        }
        case "transformationid" -> {
          int id = Integer.parseInt(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerTransformationId(id));
        }
        case "hp" -> {
          int hp = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerHp(hp));
        }
        case "mp" -> {
          int hp = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerMp(hp));
        }
        case "cp" -> {
          int cp = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerCp(cp));
        }
        case "grade" -> {
          int expIndex = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerGrade(expIndex));
        }
        case "pkcount" -> {
          int expIndex = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerPkCount(expIndex));
        }
        case "siegezone" -> {
          int value = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionSiegeZone(value, true));
        }
        case "siegeside" -> {
          int value = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerSiegeSide(value));
        }
        case "charges" -> {
          int value = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerCharges(value));
        }
        case "souls" -> {
          int value = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerSouls(value));
        }
        case "weight" -> {
          int weight = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerWeight(weight));
        }
        case "invsize" -> {
          int size = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerInvSize(size));
        }
        case "isclanleader" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerIsClanLeader(val));
        }
        case "ontvtevent" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerTvTEvent(val));
        }
        case "pledgeclass" -> {
          int pledgeClass = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerPledgeClass(pledgeClass));
        }
        case "clanhall" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          ArrayList<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionPlayerHasClanHall(array));
        }
        case "fort" -> {
          int fort = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerHasFort(fort));
        }
        case "castle" -> {
          int castle = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerHasCastle(castle));
        }
        case "sex" -> {
          int sex = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionPlayerSex(sex));
        }
        case "flymounted" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerFlyMounted(val));
        }
        case "vehiclemounted" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerVehicleMounted(val));
        }
        case "landingzone" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerLandingZone(val));
        }
        case "active_effect_id" -> {
          int effectId = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerActiveEffectId(effectId));
        }
        case "active_effect_id_lvl" -> {
          String val = getValue(a.getNodeValue(), template);
          int effect_id = Integer.decode(getValue(val.split(",")[0], template));
          int effect_lvl = Integer.decode(getValue(val.split(",")[1], template));
          cond = joinAnd(cond, new ConditionPlayerActiveEffectId(effect_id, effect_lvl));
        }
        case "active_skill_id" -> {
          int skill_id = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionPlayerActiveSkillId(skill_id));
        }
        case "active_skill_id_lvl" -> {
          String val = getValue(a.getNodeValue(), template);
          int skill_id = Integer.decode(getValue(val.split(",")[0], template));
          int skill_lvl = Integer.decode(getValue(val.split(",")[1], template));
          cond = joinAnd(cond, new ConditionPlayerActiveSkillId(skill_id, skill_lvl));
        }
        case "class_id_restriction" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          ArrayList<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionPlayerClassIdRestriction(array));
        }
        case "subclass" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerSubclass(val));
        }
        case "instanceid" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          ArrayList<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionPlayerInstanceId(array));
        }
        case "agathionid" -> {
          int agathionId = Integer.decode(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerAgathionId(agathionId));
        }
        case "cloakstatus" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionPlayerCloakStatus(val));
        }
        case "haspet" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          ArrayList<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionPlayerHasPet(array));
        }
        case "hasservitor" -> cond = joinAnd(cond, new ConditionPlayerHasServitor());
        case "npcidradius" -> {
          final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          if (st.countTokens() == 3) {
            final String[] ids = st.nextToken().split(";");
            final int[] npcIds = new int[ids.length];
            for (int index = 0; index < ids.length; index++) {
              npcIds[index] = Integer.parseInt(getValue(ids[index], template));
            }
            final int radius = Integer.parseInt(st.nextToken());
            final boolean val = Boolean.parseBoolean(st.nextToken());
            cond = joinAnd(cond, new ConditionPlayerRangeFromNpc(npcIds, radius, val));
          }
        }
        case "callpc" -> cond = joinAnd(cond, new ConditionPlayerCallPc(Boolean.parseBoolean(a.getNodeValue())));
        case "cancreatebase" -> cond = joinAnd(cond, new ConditionPlayerCanCreateBase(Boolean.parseBoolean(a.getNodeValue())));
        case "cancreateoutpost" -> cond = joinAnd(cond, new ConditionPlayerCanCreateOutpost(Boolean.parseBoolean(a.getNodeValue())));
        case "canescape" -> cond = joinAnd(cond, new ConditionPlayerCanEscape(Boolean.parseBoolean(a.getNodeValue())));
        case "canrefuelairship" -> cond = joinAnd(cond, new ConditionPlayerCanRefuelAirship(Integer.parseInt(a.getNodeValue())));
        case "canresurrect" -> cond = joinAnd(cond, new ConditionPlayerCanResurrect(Boolean.parseBoolean(a.getNodeValue())));
        case "cansummon" -> cond = joinAnd(cond, new ConditionPlayerCanSummon(Boolean.parseBoolean(a.getNodeValue())));
        case "cansummonsiegegolem" -> cond = joinAnd(cond, new ConditionPlayerCanSummonSiegeGolem(Boolean.parseBoolean(a.getNodeValue())));
        case "cansweep" -> cond = joinAnd(cond, new ConditionPlayerCanSweep(Boolean.parseBoolean(a.getNodeValue())));
        case "cantakecastle" -> cond = joinAnd(cond, new ConditionPlayerCanTakeCastle());
        case "cantakefort" -> cond = joinAnd(cond, new ConditionPlayerCanTakeFort(Boolean.parseBoolean(a.getNodeValue())));
        case "cantransform" -> cond = joinAnd(cond, new ConditionPlayerCanTransform(Boolean.parseBoolean(a.getNodeValue())));
        case "canuntransform" -> cond = joinAnd(cond, new ConditionPlayerCanUntransform(Boolean.parseBoolean(a.getNodeValue())));
        case "insidezoneid" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          List<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionPlayerInsideZoneId(array));
        }
        case "checkabnormal" -> {
          final String value = a.getNodeValue();
          if (value.contains(";")) {
            final String[] values = value.split(";");
            final var type = AbnormalType.valueOf(values[0]);
            final var level = Integer.decode(getValue(values[1], template));
            final var mustHave = Boolean.parseBoolean(values[2]);
            cond = joinAnd(cond, new ConditionCheckAbnormal(type, level, mustHave));
          } else {
            final var level = Integer.decode(getValue(value, template));
            cond = joinAnd(cond, new ConditionCheckAbnormal(AbnormalType.valueOf(value), level, true));
          }
          break;
        }
        case "categorytype" -> {
          final String[] values = a.getNodeValue().split(",");
          final Set<CategoryType> array = new HashSet<>(values.length);
          for (String value : values) {
            array.add(CategoryType.valueOf(getValue(value, null)));
          }
          cond = joinAnd(cond, new ConditionCategoryType(array));
        }
        case "hasagathion" -> cond = joinAnd(cond, new ConditionPlayerHasAgathion(Boolean.parseBoolean(a.getNodeValue())));
        case "agathionenergy" -> cond = joinAnd(cond, new ConditionPlayerAgathionEnergy(Integer.decode(getValue(a.getNodeValue(), null))));
        default -> _log.severe("Unrecognized <player> condition " + a.getNodeName().toLowerCase() + " in " + file);
      }
    }

    if (cond == null) {
      _log.severe("Unrecognized <player> condition in " + file);
    }
    return cond;
  }

  protected Condition parseTargetCondition(Node n, Object template) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node a = attrs.item(i);
      switch (a.getNodeName().toLowerCase()) {
        case "aggro" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionTargetAggro(val));
        }
        case "siegezone" -> {
          int value = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionSiegeZone(value, false));
        }
        case "level" -> {
          int lvl = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionTargetLevel(lvl));
        }
        case "levelrange" -> {
          String[] range = getValue(a.getNodeValue(), template).split(";");
          if (range.length == 2) {
            int minimumLevel = Integer.decode(getValue(a.getNodeValue(), template).split(";")[0]);
            int maximumLevel = Integer.decode(getValue(a.getNodeValue(), template).split(";")[1]);
            cond = joinAnd(cond, new ConditionTargetLevelRange(minimumLevel, maximumLevel));
          }
        }
        case "myparty" -> cond = joinAnd(cond, new ConditionTargetMyParty(a.getNodeValue()));
        case "playable" -> cond = joinAnd(cond, new ConditionTargetPlayable());
        case "class_id_restriction" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          List<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionTargetClassIdRestriction(array));
        }
        case "active_effect_id" -> {
          int effectId = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionTargetActiveEffectId(effectId));
        }
        case "active_effect_id_lvl" -> {
          String val = getValue(a.getNodeValue(), template);
          int effect_id = Integer.decode(getValue(val.split(",")[0], template));
          int effect_lvl = Integer.decode(getValue(val.split(",")[1], template));
          cond = joinAnd(cond, new ConditionTargetActiveEffectId(effect_id, effect_lvl));
        }
        case "active_skill_id" -> {
          int skill_id = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id));
        }
        case "active_skill_id_lvl" -> {
          String val = getValue(a.getNodeValue(), template);
          int skill_id = Integer.decode(getValue(val.split(",")[0], template));
          int skill_lvl = Integer.decode(getValue(val.split(",")[1], template));
          cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id, skill_lvl));
        }
        case "abnormal" -> {
          int abnormalId = Integer.decode(getValue(a.getNodeValue(), template));
          cond = joinAnd(cond, new ConditionTargetAbnormal(abnormalId));
        }
        case "mindistance" -> {
          int distance = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionMinDistance(distance * distance));
        }
        case "race" -> cond = joinAnd(cond, new ConditionTargetRace(Race.valueOf(a.getNodeValue())));
        case "using" -> {
          int mask = 0;
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            for (WeaponType wt : WeaponType.values()) {
              if (wt.name().equals(item)) {
                mask |= wt.mask();
                break;
              }
            }
            for (ArmorType at : ArmorType.values()) {
              if (at.name().equals(item)) {
                mask |= at.mask();
                break;
              }
            }
          }
          cond = joinAnd(cond, new ConditionTargetUsesWeaponKind(mask));
        }
        case "npcid" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          List<Integer> array = new ArrayList<>(st.countTokens());
          while (st.hasMoreTokens()) {
            String item = st.nextToken().trim();
            array.add(Integer.decode(getValue(item, null)));
          }
          cond = joinAnd(cond, new ConditionTargetNpcId(array));
        }
        case "npctype" -> {
          String values = getValue(a.getNodeValue(), template).trim();
          String[] valuesSplit = values.split(",");
          InstanceType[] types = new InstanceType[valuesSplit.length];
          for (int j = 0; j < valuesSplit.length; j++) {
            types[j] = Enum.valueOf(InstanceType.class, valuesSplit[j]);
          }
          cond = joinAnd(cond, new ConditionTargetNpcType(types));
        }
        case "weight" -> {
          int weight = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionTargetWeight(weight));
        }
        case "invsize" -> {
          int size = Integer.decode(getValue(a.getNodeValue(), null));
          cond = joinAnd(cond, new ConditionTargetInvSize(size));
        }
        case "checkabnormal" -> {
          final String value = a.getNodeValue();
          if (value.contains(";")) {
            final String[] values = value.split(";");
            final var type = AbnormalType.valueOf(values[0]);
            final var level = Integer.decode(getValue(values[1], template));
            final var mustHave = Boolean.parseBoolean(values[2]);
            cond = joinAnd(cond, new ConditionCheckAbnormal(type, level, mustHave));
          } else {
            final var level = Integer.decode(getValue(value, template));
            cond = joinAnd(cond, new ConditionCheckAbnormal(AbnormalType.valueOf(value), level, true));
          }
          break;
        }
      }
    }

    if (cond == null) {
      _log.severe("Unrecognized <target> condition in " + file);
    }
    return cond;
  }

  protected Condition parseUsingCondition(Node n) {
    Condition cond = null;
    NamedNodeMap attrs = n.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node a = attrs.item(i);
      switch (a.getNodeName().toLowerCase()) {
        case "kind" -> {
          int mask = 0;
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          while (st.hasMoreTokens()) {
            int old = mask;
            String item = st.nextToken().trim();
            for (WeaponType wt : WeaponType.values()) {
              if (wt.name().equals(item)) {
                mask |= wt.mask();
              }
            }

            for (ArmorType at : ArmorType.values()) {
              if (at.name().equals(item)) {
                mask |= at.mask();
              }
            }

            if (old == mask) {
              _log.info("[parseUsingCondition=\"kind\"] Unknown item type name: " + item);
            }
          }
          cond = joinAnd(cond, new ConditionUsingItemType(mask));
        }
        case "slot" -> {
          int mask = 0;
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
          while (st.hasMoreTokens()) {
            int old = mask;
            String item = st.nextToken().trim();
            if (ItemTable.SLOTS.containsKey(item)) {
              mask |= ItemTable.SLOTS.get(item);
            }

            if (old == mask) {
              _log.info("[parseUsingCondition=\"slot\"] Unknown item slot name: " + item);
            }
          }
          cond = joinAnd(cond, new ConditionUsingSlotType(mask));
        }
        case "skill" -> {
          int id = Integer.parseInt(a.getNodeValue());
          cond = joinAnd(cond, new ConditionUsingSkill(id));
        }
        case "slotitem" -> {
          StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
          int id = Integer.parseInt(st.nextToken().trim());
          int slot = Integer.parseInt(st.nextToken().trim());
          int enchant = 0;
          if (st.hasMoreTokens()) {
            enchant = Integer.parseInt(st.nextToken().trim());
          }
          cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
        }
        case "weaponchange" -> {
          boolean val = Boolean.parseBoolean(a.getNodeValue());
          cond = joinAnd(cond, new ConditionChangeWeapon(val));
        }
      }
    }

    if (cond == null) {
      _log.severe("Unrecognized <using> condition in " + file);
    }
    return cond;
  }

  protected void parseTemplate(Node n, Object template, EffectScope effectScope) {
    Condition condition = null;
    n = n.getFirstChild();
    if (n == null) {
      return;
    }
    if ("cond".equalsIgnoreCase(n.getNodeName())) {
      condition = parseCondition(n.getFirstChild(), template);
      Node msg = n.getAttributes().getNamedItem("msg");
      Node msgId = n.getAttributes().getNamedItem("msgId");
      if ((condition != null) && (msg != null)) {
        condition.setMessage(msg.getNodeValue());
      } else if ((condition != null) && (msgId != null)) {
        condition.setMessageId(Integer.decode(getValue(msgId.getNodeValue(), null)));
        Node addName = n.getAttributes().getNamedItem("addName");
        if ((addName != null) && (Integer.decode(getValue(msgId.getNodeValue(), null)) > 0)) {
          condition.addName();
        }
      }
      n = n.getNextSibling();
    }
    for (; n != null; n = n.getNextSibling()) {
      final String name = n.getNodeName().toLowerCase();

      switch (name) {
        case "effect" -> {
          if (template instanceof AbstractEffect) {
            throw new RuntimeException("Nested effects");
          }
          attachEffect(n, template, condition, effectScope);
        }
        case "add", "sub", "mul", "div", "set", "share", "enchant", "enchanthp" -> attachFunc(n, template, name, condition);
      }
    }
  }

  protected void attachEffect(Node n, Object template, Condition attachCond, EffectScope effectScope) {
    final NamedNodeMap attrs = n.getAttributes();
    final StatsSet set = new StatsSet();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node att = attrs.item(i);
      set.set(att.getNodeName(), getValue(att.getNodeValue(), template));
    }

    final StatsSet parameters = parseParameters(n.getFirstChild(), template);
    final Condition applyCond = parseCondition(n.getFirstChild(), template);

    if (template instanceof Identifiable) {
      set.set("id", ((Identifiable) template).getId());
    }

    final AbstractEffect effect = effectHandler.createEffect(attachCond, applyCond, set, parameters);
    parseTemplate(n, effect);
    if (template instanceof L2Item) {
      _log.severe("Item " + template + " with effects!!!");
    } else if (template instanceof Skill) {
      final Skill skill = (Skill) template;
      if (effectScope != null) {
        skill.addEffect(effectScope, effect);
      } else if (skill.isPassive()) {
        skill.addEffect(EffectScope.PASSIVE, effect);
      } else {
        skill.addEffect(EffectScope.GENERAL, effect);
      }
    }
  }
  protected void parseTemplate(Node n, Object template) {
    parseTemplate(n, template, null);
  }

  protected void attachFunc(Node n, Object template, String functionName, Condition attachCond) {
    Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
    int order = -1;
    final Node orderNode = n.getAttributes().getNamedItem("order");
    if (orderNode != null) {
      order = Integer.parseInt(orderNode.getNodeValue());
    }

    String valueString = n.getAttributes().getNamedItem("val").getNodeValue();
    double value;
    if (valueString.charAt(0) == '#') {
      value = Double.parseDouble(getTableValue(valueString));
    } else {
      value = Double.parseDouble(valueString);
    }

    final Condition applyCond = parseCondition(n.getFirstChild(), template);
    final FuncTemplate ft = new FuncTemplate(attachCond, applyCond, functionName, order, stat, value);
    if (template instanceof L2Item) {
      ((L2Item) template).attach(ft);
    } else if (template instanceof AbstractEffect) {
      ((AbstractEffect) template).attach(ft);
    } else {
      throw new RuntimeException("Attaching stat to a non-effect template!!!");
    }
  }


  private void makeItem() throws InvocationTargetException {
    if (_currentItem.item != null) {
      return; // item is already created
    }
    try {
      Constructor<?> c = Class.forName("com.l2jserver.gameserver.model.items.L2" + _currentItem.type).getConstructor(StatsSet.class);
      _currentItem.item = (L2Item) c.newInstance(_currentItem.set);
    } catch (Exception e) {
      throw new InvocationTargetException(e);
    }
  }


  protected Condition joinAnd(Condition cond, Condition c) {
    if (cond == null) {
      return c;
    }
    if (cond instanceof ConditionLogicAnd) {
      ((ConditionLogicAnd) cond).add(c);
      return cond;
    }
    ConditionLogicAnd and = new ConditionLogicAnd();
    and.add(cond);
    and.add(c);
    return and;
  }
  public List<L2Item> getItemList() {
    return _itemsInFile;
  }


  /**
   * Parse effect's parameters.
   * @param n the node to start the parsing
   * @param template the effect template
   * @return the list of parameters if any, {@code null} otherwise
   */
  private StatsSet parseParameters(Node n, Object template) {
    StatsSet parameters = null;
    while ((n != null)) {
      // Parse all parameters.
      if ((n.getNodeType() == Node.ELEMENT_NODE) && "param".equals(n.getNodeName())) {
        if (parameters == null) {
          parameters = new StatsSet();
        }
        NamedNodeMap params = n.getAttributes();
        for (int i = 0; i < params.getLength(); i++) {
          Node att = params.item(i);
          parameters.set(att.getNodeName(), getValue(att.getNodeValue(), template));
        }
      }
      n = n.getNextSibling();
    }
    return parameters == null ? StatsSet.EMPTY_STATSET : parameters;
  }

  /** Builds a variable in which all items are putting in in function of their ID. */
  private void buildFastLookupTable(int size) {
    // Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
    LOG.info("Highest item Id used {}.", size);
    _allTemplates = new L2Item[size + 1];

    // Insert armor item in Fast Look Up Table
    for (L2Armor item : _armors.values()) {
      _allTemplates[item.getId()] = item;
    }

    // Insert weapon item in Fast Look Up Table
    for (L2Weapon item : _weapons.values()) {
      _allTemplates[item.getId()] = item;
    }

    // Insert etcItem item in Fast Look Up Table
    for (L2EtcItem item : _etcItems.values()) {
      _allTemplates[item.getId()] = item;
    }
  }

  /**
   * Returns the item corresponding to the item ID
   *
   * @param id : int designating the item
   * @return L2Item
   */
  public L2Item getTemplate(int id) {
    if ((id >= _allTemplates.length) || (id < 0)) {
      return null;
    }

    return _allTemplates[id];
  }

  /**
   * Create the L2ItemInstance corresponding to the Item Identifier and quantitiy add logs the
   * activity. <B><U> Actions</U> :</B>
   * <li>Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity
   * <li>Add the L2ItemInstance object to _allObjects of L2world
   * <li>Logs Item creation according to log settings
   *
   * @param process : String Identifier of process triggering this action
   * @param itemId : int Item Identifier of the item to be created
   * @param count : int Quantity of items to be created for stackable items
   * @param actor : L2PcInstance Player requesting the item creation
   * @param reference : Object Object referencing current action like NPC selling item or previous
   *     item in transformation
   * @return L2ItemInstance corresponding to the new item
   */
  public L2ItemInstance createItem(
      String process, int itemId, long count, L2PcInstance actor, Object reference) {
    // Create and Init the L2ItemInstance corresponding to the Item Identifier
    L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);

    if (process.equalsIgnoreCase("loot")) {
      ScheduledFuture<?> itemLootShedule;
      if ((reference instanceof L2Attackable)
          && ((L2Attackable) reference).isRaid()) // loot privilege for raids
      {
        L2Attackable raid = (L2Attackable) reference;
        // if in CommandChannel and was killing a World/RaidBoss
        if ((raid.getFirstCommandChannelAttacked() != null) && !character().autoLootRaids()) {
          item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
          itemLootShedule =
              ThreadPoolManager.getInstance()
                  .scheduleGeneral(new ResetOwner(item), character().getRaidLootRightsInterval());
          item.setItemLootSchedule(itemLootShedule);
        }
      } else if (!character().autoLoot()
          || ((reference instanceof L2EventMonsterInstance)
              && ((L2EventMonsterInstance) reference).eventDropOnGround())) {
        item.setOwnerId(actor.getObjectId());
        itemLootShedule =
            ThreadPoolManager.getInstance().scheduleGeneral(new ResetOwner(item), 15000);
        item.setItemLootSchedule(itemLootShedule);
      }
    }

    if (general().debug()) {
      LOG.info("Item created object Id {} and item Id {}.", item.getObjectId(), itemId);
    }

    // Add the L2ItemInstance object to _allObjects of L2world
    L2World.getInstance().storeObject(item);

    // Set Item parameters
    if (item.isStackable() && (count > 1)) {
      item.setCount(count);
    }

    if (general().logItems() && !process.equals("Reset")) {
      if (!general().logItemsSmallLog()
          || (general().logItemsSmallLog() && (item.isEquipable() || (item.getId() == ADENA_ID)))) {
        if ((item.getItemType() != ARROW) && (item.getItemType() != SHOT)) {
          LOG_ITEM.info("CREATED {} by {}, referenced by {}.", item, actor, reference);
        }
      }
    }

    if (actor != null) {
      if (actor.isGM()) {
        String referenceName = "no-reference";
        if (reference instanceof L2Object) {
          referenceName =
              (((L2Object) reference).getName() != null
                  ? ((L2Object) reference).getName()
                  : "no-name");
        } else if (reference instanceof String) {
          referenceName = (String) reference;
        }
        String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
        if (general().gmAudit()) {
          GMAudit.auditGMAction(
              actor.getName() + " [" + actor.getObjectId() + "]",
              process
                  + "(id: "
                  + itemId
                  + " count: "
                  + count
                  + " name: "
                  + item.getItemName()
                  + " objId: "
                  + item.getObjectId()
                  + ")",
              targetName,
              "L2Object referencing this action is: " + referenceName);
        }
      }
    }

    // Notify to scripts
    EventDispatcher.getInstance()
        .notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getItem());
    return item;
  }

  public L2ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor) {
    return createItem(process, itemId, count, actor, null);
  }

  /**
   * Destroys the L2ItemInstance.<br>
   * <B><U> Actions</U> :</B>
   *
   * <ul>
   *   <li>Sets L2ItemInstance parameters to be unusable
   *   <li>Removes the L2ItemInstance object to _allObjects of L2world
   *   <li>Logs Item deletion according to log settings
   * </ul>
   *
   * @param process a string identifier of process triggering this action.
   * @param item the item instance to be destroyed.
   * @param actor the player requesting the item destroy.
   * @param reference the object referencing current action like NPC selling item or previous item
   *     in transformation.
   */
  public void destroyItem(
      String process, L2ItemInstance item, L2PcInstance actor, Object reference) {
    synchronized (this) {
      long old = item.getCount();
      item.setCount(0);
      item.setOwnerId(0);
      item.setItemLocation(ItemLocation.VOID);
      item.setLastChange(L2ItemInstance.REMOVED);

      L2World.getInstance().removeObject(item);
      IdFactory.getInstance().releaseId(item.getObjectId());

      if (general().logItems()) {
        if (!general().logItemsSmallLog()
            || (general().logItemsSmallLog()
                && (item.isEquipable() || (item.getId() == ADENA_ID)))) {
          if ((item.getItemType() != ARROW) && (item.getItemType() != SHOT)) {
            LOG_ITEM.info(
                "DELETED {} amount {} by {}, referenced by {}.", item, old, actor, reference);
          }
        }
      }

      if (actor != null) {
        if (actor.isGM()) {
          String referenceName = "no-reference";
          if (reference instanceof L2Object) {
            referenceName =
                (((L2Object) reference).getName() != null
                    ? ((L2Object) reference).getName()
                    : "no-name");
          } else if (reference instanceof String) {
            referenceName = (String) reference;
          }
          String targetName =
              (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
          if (general().gmAudit()) {
            GMAudit.auditGMAction(
                actor.getName() + " [" + actor.getObjectId() + "]",
                process
                    + "(id: "
                    + item.getId()
                    + " count: "
                    + item.getCount()
                    + " itemObjId: "
                    + item.getObjectId()
                    + ")",
                targetName,
                "L2Object referencing this action is: " + referenceName);
          }
        }
      }

      // if it's a pet control item, delete the pet as well
      if (item.getItem().isPetItem()) {
        try (var con = ConnectionFactory.getInstance().getConnection();
            var statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?")) {
          statement.setInt(1, item.getObjectId());
          statement.execute();
        } catch (Exception ex) {
          LOG.warn("Could not delete pet object Id {}!", item.getObjectId(), ex);
        }
      }
    }
  }

  public Set<Integer> getAllArmorsId() {
    return _armors.keySet();
  }

  public Set<Integer> getAllWeaponsId() {
    return _weapons.keySet();
  }

  public int getArraySize() {
    return _allTemplates.length;
  }

  protected static class ResetOwner implements Runnable {
    L2ItemInstance _item;

    public ResetOwner(L2ItemInstance item) {
      _item = item;
    }

    @Override
    public void run() {
      _item.setOwnerId(0);
      _item.setItemLootSchedule(null);
    }
  }

  private static class SingletonHolder {
    protected static final ItemTable _instance = new ItemTable(null);
  }
}
