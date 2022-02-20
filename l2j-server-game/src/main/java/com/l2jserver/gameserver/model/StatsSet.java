package com.l2jserver.gameserver.model;

import com.l2jserver.gameserver.model.holders.MinionHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StatsSet {
  /**
   * Static empty immutable map, used to avoid multiple null checks over the source.
   */
  public static final StatsSet EMPTY_STATSET = new StatsSet(Map.of());
  private static final Logger _log = Logger.getLogger(StatsSet.class.getName());
  private final Map<String, Object> set;

  public StatsSet() {
    this(new LinkedHashMap<>());
  }

  public StatsSet(Map<String, Object> map) {
    set = map;
  }

  /**
   * Add a set of couple values in the current set
   * @param newSet : StatsSet pointing out the list of couples to add in the current set
   */
  public void add(StatsSet newSet) {
    set.putAll(newSet.getSet());
  }

  /**
   * Returns the set of values
   * @return HashMap
   */
  public final Map<String, Object> getSet() {
    return set;
  }

  /**
   * Verifies if the stat set is empty.
   * @return {@code true} if the stat set is empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return set.isEmpty();
  }

  public boolean containsKey(String key) {
    return set.containsKey(key);
  }

  /**
   * Return the boolean value associated with key.
   *
   * @param key : String designating the key in the set
   * @return boolean : value associated to the key
   * @throws IllegalArgumentException : If value is not set or value is not boolean
   */
  public boolean getBoolean(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Boolean value required, but not specified");
    }
    if (val instanceof Boolean) {
      return (Boolean) val;
    }
    try {
      return Boolean.parseBoolean((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Boolean value required, but found: " + val);
    }
  }

  /**
   * Return the boolean value associated with key.<br>
   * If no value is associated with key, or type of value is wrong, returns defaultValue.
   *
   * @param key : String designating the key in the entry set
   * @return boolean : value associated to the key
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Boolean) {
      return (Boolean) val;
    }
    try {
      return Boolean.parseBoolean((String) val);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public byte getByte(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Byte value required, but not specified");
    }
    if (val instanceof Number) {
      return ((Number) val).byteValue();
    }
    try {
      return Byte.parseByte((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Byte value required, but found: " + val);
    }
  }

  public byte getByte(String key, byte defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number number) {
      return number.byteValue();
    }
    return Byte.parseByte((String) val);
  }

  public List<Byte> getByteList(String key, String splitOn) {
    return getByteArray(key, splitOn);
  }

  private List<Byte> getByteArray(String key, String splitOn) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Byte value required, but not specified");
    }
    if (val instanceof Number number) {
      return List.of(number.byteValue());
    }
    String[] values = ((String) val).split(splitOn);
    return Arrays.stream(values).map(Byte::parseByte).toList();
  }

  public short getShort(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Short value required, but not specified");
    }
    if (val instanceof Number) {
      return ((Number) val).shortValue();
    }
    try {
      return Short.parseShort((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Short value required, but found: " + val);
    }
  }

  public short getShort(String key, short defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number) {
      return ((Number) val).shortValue();
    }
    try {
      return Short.parseShort((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Short value required, but found: " + val);
    }
  }

  public int getInt(String key) {
    final Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Integer value required, but not specified: " + key + "!");
    }

    if (val instanceof Number) {
      return ((Number) val).intValue();
    }

    try {
      return Integer.parseInt((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Integer value required, but found: " + val + "!");
    }
  }

  public int getInt(String key, int defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number) {
      return ((Number) val).intValue();
    }
    try {
      return Integer.parseInt((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Integer value required, but found: " + val);
    }
  }

  public List<Integer> getIntegerList(String key, String splitOn) {
    List<Integer> result = new ArrayList<>();
    for (int i : getIntArray(key, splitOn)) {
      result.add(i);
    }
    return result;
  }

  public int[] getIntArray(String key, String splitOn) {
    return getIntArray(key, null, splitOn);
  }

  public int[] getIntArray(String key, String defaultValue, String splitOn) {
    Object val = set.getOrDefault(key, defaultValue);
    if (val == null) {
      throw new IllegalArgumentException("Integer value required, but not specified");
    }
    if (val instanceof Number) {
      return new int[] {((Number) val).intValue()};
    }
    int c = 0;
    String[] values = ((String) val).split(splitOn);
    int[] result = new int[values.length];
    for (String v : values) {
      try {
        result[c++] = Integer.parseInt(v);
      } catch (Exception e) {
        throw new IllegalArgumentException("Integer value required, but found: " + val);
      }
    }
    return result;
  }

  public long getLong(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Long value required, but not specified");
    }
    if (val instanceof Number) {
      return ((Number) val).longValue();
    }
    try {
      return Long.parseLong((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Long value required, but found: " + val);
    }
  }

  public long getLong(String key, long defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number) {
      return ((Number) val).longValue();
    }
    try {
      return Long.parseLong((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Long value required, but found: " + val);
    }
  }

  public float getFloat(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Float value required, but not specified");
    }
    if (val instanceof Number) {
      return ((Number) val).floatValue();
    }
    try {
      return Float.parseFloat((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Float value required, but found: " + val);
    }
  }

  public float getFloat(String key, float defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number) {
      return ((Number) val).floatValue();
    }
    try {
      return Float.parseFloat((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Float value required, but found: " + val);
    }
  }

  public double getDouble(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("Double value required, but not specified");
    }
    if (val instanceof Number) {
      return ((Number) val).doubleValue();
    }
    try {
      return Double.parseDouble((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Double value required, but found: " + val);
    }
  }

  public double getDouble(String key, double defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (val instanceof Number) {
      return ((Number) val).doubleValue();
    }
    try {
      return Double.parseDouble((String) val);
    } catch (Exception e) {
      throw new IllegalArgumentException("Double value required, but found: " + val);
    }
  }

  public String getString(String key) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException("String value required, but not specified");
    }
    return String.valueOf(val);
  }

  public String getString(String key, String defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    return String.valueOf(val);
  }

  @SuppressWarnings("unchecked")
  public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
    Object val = set.get(key);
    if (val == null) {
      throw new IllegalArgumentException(
          "Enum value of type " + enumClass.getName() + " required, but not specified");
    }
    if (enumClass.isInstance(val)) {
      return (T) val;
    }
    try {
      return Enum.valueOf(enumClass, String.valueOf(val));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Enum value of type " + enumClass.getName() + " required, but found: " + val);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
    Object val = set.get(key);
    if (val == null) {
      return defaultValue;
    }
    if (enumClass.isInstance(val)) {
      return (T) val;
    }
    try {
      return Enum.valueOf(enumClass, String.valueOf(val));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Enum value of type " + enumClass.getName() + " required, but found: " + val);
    }
  }

  @SuppressWarnings("unchecked")
  public final <A> A getObject(String name, Class<A> type) {
    Object obj = set.get(name);
    if ((obj == null) || !type.isAssignableFrom(obj.getClass())) {
      return null;
    }

    return (A) obj;
  }

  public SkillHolder getSkillHolder(String key) {
    Object obj = set.get(key);
    if (!(obj instanceof SkillHolder)) {
      return null;
    }
    return (SkillHolder) obj;
  }

  @SuppressWarnings("unchecked")
  public List<MinionHolder> getMinionList(String key) {
    Object obj = set.get(key);
    if (!(obj instanceof List<?>)) {
      return List.of();
    }
    return (List<MinionHolder>) obj;
  }

  public void set(String name, Object value) {
    set.put(name, value);
  }

  public void set(String key, boolean value) {
    set.put(key, value);
  }

  public void set(String key, byte value) {
    set.put(key, value);
  }

  public void set(String key, short value) {
    set.put(key, value);
  }

  public void set(String key, long value) {
    set.put(key, value);
  }

  public void set(String key, float value) {
    set.put(key, value);
  }

  public void set(String key, double value) {
    set.put(key, value);
  }

  public void set(String key, String value) {
    set.put(key, value);
  }

  public void set(String key, Enum<?> value) {
    set.put(key, value);
  }

  public void safeSet(String key, int value, int min, int max, String reference) {
    assert !(((min <= max) && ((value < min) || (value >= max))));
    if ((min <= max) && ((value < min) || (value >= max))) {
      _log.log(Level.SEVERE, "Incorrect value: " + value + "for: " + key + "Ref: " + reference);
    }

    set(key, value);
  }

  public void set(String key, int value) {
    set.put(key, value);
  }
}
