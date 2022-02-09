package com.l2jserver.gameserver.script.faenor;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.script.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.script.ScriptContext;
import java.time.Duration;
import java.time.LocalDateTime;

public class FaenorEventParser extends FaenorParser {

  private static final Logger LOG = LoggerFactory.getLogger(FaenorEventParser.class);

  static {
    ScriptEngine.parserFactories.put(getParserName("Event"), new FaenorEventParserFactory());
  }

  private DateRange _eventDates = null;

  @Override
  public void parseScript(final Node eventNode, ScriptContext context) {
    String id = attribute(eventNode, "ID");
    _eventDates = DateRange.parse(attribute(eventNode, "Active"), "dd MMM yyyy");

    var currentDate = LocalDateTime.now();
    if (_eventDates.getEndDate().isBefore(currentDate)) {
      LOG.info("Event Id {} has passed... Ignored.", id);
      return;
    }

    if (_eventDates.getStartDate().isAfter(currentDate)) {
      LOG.info("Event Id {} is not active yet... Ignored.", id);
      ThreadPoolManager.getInstance()
          .scheduleGeneral(
              () -> parseEventDropAndMessage(eventNode),
              Duration.between(currentDate, _eventDates.getStartDate()).toMillis());
      return;
    }

    parseEventDropAndMessage(eventNode);
  }

  protected void parseEventDropAndMessage(Node eventNode) {
    for (Node node = eventNode.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (isNodeName(node, "DropList")) {
        parseEventDropList(node);
      } else if (isNodeName(node, "Message")) {
        parseEventMessage(node);
      }
    }
  }

  private void parseEventMessage(Node sysMsg) {
    try {
      String type = attribute(sysMsg, "Type");
      String message = attribute(sysMsg, "Msg");

      if (type.equalsIgnoreCase("OnJoin")) {
        _bridge.onPlayerLogin(message, _eventDates);
      }
    } catch (Exception ex) {
      LOG.warn("There has been an error in event parser!", ex);
    }
  }

  private void parseEventDropList(Node dropList) {
    for (Node node = dropList.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (isNodeName(node, "AllDrop")) {
        parseEventDrop(node);
      }
    }
  }

  private void parseEventDrop(Node drop) {
    try {
      int[] items = IntList.parse(attribute(drop, "Items"));
      int[] count = IntList.parse(attribute(drop, "Count"));
      double chance = getPercent(attribute(drop, "Chance"));

      _bridge.addEventDrop(items, count, chance, _eventDates);
    } catch (Exception ex) {
      LOG.warn("There has been an error parsing drops!", ex);
    }
  }

  static class FaenorEventParserFactory extends ParserFactory {
    @Override
    public Parser create() {
      return (new FaenorEventParser());
    }
  }
}
