package com.l2jserver.gameserver.script.faenor;

import com.l2jserver.gameserver.script.Parser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.script.ScriptContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class FaenorParser extends Parser {
  protected static FaenorInterface _bridge = FaenorInterface.getInstance();
  protected final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.US);

  /*
   * UTILITY FUNCTIONS
   */
  public static String attribute(Node node, String attributeName) {
    return attribute(node, attributeName, null);
  }

  public static String element(Node node, String elementName) {
    return element(node, elementName, null);
  }

  public static String attribute(Node node, String attributeName, String defaultValue) {
    try {
      return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    } catch (Exception e) {
      if (defaultValue != null) {
        return defaultValue;
      }
      throw new NullPointerException(e.getMessage());
    }
  }

  public static String element(Node parentNode, String elementName, String defaultValue) {
    try {
      NodeList list = parentNode.getChildNodes();
      for (int i = 0; i < list.getLength(); i++) {
        Node node = list.item(i);
        if (node.getNodeName().equalsIgnoreCase(elementName)) {
          return node.getTextContent();
        }
      }
    } catch (Exception e) {
    }
    if (defaultValue != null) {
      return defaultValue;
    }
    throw new NullPointerException();
  }

  public static boolean isNodeName(Node node, String name) {
    return node.getNodeName().equalsIgnoreCase(name);
  }

  public static double getPercent(String percent) {
    return (Double.parseDouble(percent.split("%")[0]) / 100.0);
  }

  protected static int getInt(String number) {
    return Integer.parseInt(number);
  }

  protected static double getDouble(String number) {
    return Double.parseDouble(number);
  }

  protected static float getFloat(String number) {
    return Float.parseFloat(number);
  }

  protected static String getParserName(String name) {
    return "faenor.Faenor" + name + "Parser";
  }

  /**
   * @param node
   * @param context
   */
  @Override
  public abstract void parseScript(Node node, ScriptContext context);
}
