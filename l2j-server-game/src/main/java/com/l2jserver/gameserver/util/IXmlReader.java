package com.l2jserver.gameserver.util;

import com.l2jserver.gameserver.util.file.filter.XMLFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.l2jserver.gameserver.config.Configuration.server;

public abstract class IXmlReader {

  private static final String JAXP_SCHEMA_LANGUAGE =
      "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  /** Theprotected file filter, ".xml" files only. */
  private static final XMLFilter XML_FILTER = new XMLFilter();

  protected static Logger LOG = LogManager.getLogger(IXmlReader.class);

  /**
   * This method can be used to load/reload the data.<br>
   * It's highly recommended to clear the data storage, either the list or map.
   */
  @PostConstruct
  protected abstract void load();

  /** Wrapper for {@link #parseFile(File)} method. */
  protected void parseDatapackFile(String path) {
    parseFile(new File(server().getDatapackRoot(), path));
  }

  /**
   * Parses a single XML file.<br>
   * If the file was successfully parsed, call {@link #parseDocument(Document, File)} for the parsed
   * document.<br>
   * <b>Validation is enforced.</b>
   *
   * @param f the XML file to parse.
   */
  protected void parseFile(File f) {
    if (!getCurrentFileFilter().accept(f)) {
      LOG.warn(
          "{}: Could not parse {} is not a file or it doesn't exist!",
          getClass().getSimpleName(),
          f.getName());
      return;
    }

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    dbf.setIgnoringComments(true);
    dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      db.setErrorHandler(new XMLErrorHandler());
      parseDocument(db.parse(f), f);
    } catch (SAXParseException e) {
      LOG.warn(
          "{}: Could not parse file {} at line {}, column {}",
          getClass().getSimpleName(),
          f.getName(),
          e.getLineNumber(),
          e.getColumnNumber(),
          e);
    } catch (Exception e) {
      LOG.warn("{}: Could not parse file {}", getClass().getSimpleName(), f.getName(), e);
    }
  }

  protected FileFilter getCurrentFileFilter() {
    return XML_FILTER;
  }

  /**
   * Abstract method that when implemented will parse the current document.<br>
   * Is expected to be call from {@link #parseFile(File)}.
   *
   * @param doc the current document to parse
   * @param f the current file
   */
  protected void parseDocument(Document doc, File f) {
    parseDocument(doc);
  }

  /**
   * Abstract method that when implemented will parse the current document.<br>
   * Is expected to be call from {@link #parseFile(File)}.
   */
  protected abstract void parseDocument(Document doc);

  protected boolean parseDirectory(Path strDir, boolean recursive) {
    if (!Files.exists(strDir)) {
      LOG.warn("Folder {} doesn't exist!", strDir);
      return false;
    }

    File[] files = strDir.toFile().listFiles();
    if (files != null) {
      for (File f : files) {
        if (recursive && f.isDirectory()) {
          parseDirectory(Path.of(f.getAbsolutePath()), recursive);
        } else if (getCurrentFileFilter().accept(f)) {
          parseFile(f);
        }
      }
    }
    return true;
  }

  protected boolean parseDatapackDirectory(String path, boolean recursive) {
    return parseDirectory(Path.of(path), recursive);
  }

  protected boolean parseDatapackDirectory(String path) {
    return parseDirectory(Path.of(path), false);
  }

  /** Parses a boolean value. */
  protected Boolean parseBoolean(NamedNodeMap attrs, String name) {
    return parseBoolean(attrs.getNamedItem(name));
  }

  /** Parses a boolean value. */
  protected Boolean parseBoolean(Node node) {
    return parseBoolean(node, null);
  }

  /** Parses a boolean value. */
  protected Boolean parseBoolean(Node node, Boolean defaultValue) {
    return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses a boolean value. */
  protected Boolean parseBoolean(NamedNodeMap attrs, String name, Boolean defaultValue) {
    return parseBoolean(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses a byte value. */
  protected Byte parseByte(NamedNodeMap attrs, String name) {
    return parseByte(attrs.getNamedItem(name));
  }

  /** Parses a byte value. */
  protected Byte parseByte(Node node) {
    return node != null ? Byte.valueOf(node.getNodeValue()) : null;
  }

  /** Parses a short value. */
  protected Short parseShort(NamedNodeMap attrs, String name) {
    return parseShort(attrs.getNamedItem(name));
  }

  /** Parses a short value. */
  protected Short parseShort(Node node) {
    return parseShort(node, null);
  }

  /** Parses a short value. */
  protected Short parseShort(Node node, Short defaultValue) {
    return node != null ? Short.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses a short value. */
  protected Short parseShort(NamedNodeMap attrs, String name, Short defaultValue) {
    return parseShort(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses an int value. */
  protected int parseInt(Node node) {
    return parseInt(node, -1);
  }

  /** Parses an int value. */
  protected int parseInt(Node node, Integer defaultValue) {
    return node != null ? Integer.parseInt(node.getNodeValue()) : defaultValue;
  }

  /** Parses an integer value. */
  protected Integer parseInteger(NamedNodeMap attrs, String name) {
    return parseInteger(attrs.getNamedItem(name));
  }

  /** Parses an integer value. */
  protected Integer parseInteger(Node node) {
    return parseInteger(node, null);
  }

  /** Parses an integer value. */
  protected Integer parseInteger(Node node, Integer defaultValue) {
    return node != null ? Integer.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses an integer value. */
  protected Integer parseInteger(NamedNodeMap attrs, String name, Integer defaultValue) {
    return parseInteger(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses a long value. */
  protected Long parseLong(NamedNodeMap attrs, String name) {
    return parseLong(attrs.getNamedItem(name));
  }

  /** Parses a long value. */
  protected Long parseLong(Node node) {
    return parseLong(node, null);
  }

  /** Parses a long value. */
  protected Long parseLong(Node node, Long defaultValue) {
    return node != null ? Long.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses a long value. */
  protected Long parseLong(NamedNodeMap attrs, String name, Long defaultValue) {
    return parseLong(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses a float value. */
  protected Float parseFloat(NamedNodeMap attrs, String name) {
    return parseFloat(attrs.getNamedItem(name));
  }

  /** Parses a float value. */
  protected Float parseFloat(Node node) {
    return parseFloat(node, null);
  }

  /** Parses a float value. */
  protected Float parseFloat(Node node, Float defaultValue) {
    return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses a float value. */
  protected Float parseFloat(NamedNodeMap attrs, String name, Float defaultValue) {
    return parseFloat(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses a double value. */
  protected Double parseDouble(NamedNodeMap attrs, String name) {
    return parseDouble(attrs.getNamedItem(name));
  }

  /** Parses a double value. */
  protected Double parseDouble(Node node) {
    return parseDouble(node, null);
  }

  /** Parses a double value. */
  protected Double parseDouble(Node node, Double defaultValue) {
    return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
  }

  /** Parses a double value. */
  protected Double parseDouble(NamedNodeMap attrs, String name, Double defaultValue) {
    return parseDouble(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses a string value. */
  protected String parseString(NamedNodeMap attrs, String name) {
    return parseString(attrs.getNamedItem(name));
  }

  /** Parses a string value. */
  protected String parseString(Node node) {
    return parseString(node, null);
  }

  /** Parses a string value. */
  protected String parseString(Node node, String defaultValue) {
    return node != null ? node.getNodeValue() : defaultValue;
  }

  /**
   * Parses a string value.
   *
   * @param attrs the attributes
   * @param name the name of the attribute to parse
   * @param defaultValue theprotected value
   * @return if the node is not null, the value of the parsed node, otherwise theprotected value
   */
  protected String parseString(NamedNodeMap attrs, String name, String defaultValue) {
    return parseString(attrs.getNamedItem(name), defaultValue);
  }

  /** Parses an enumerated value. */
  protected <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name) {
    return parseEnum(attrs.getNamedItem(name), clazz);
  }

  /** Parses an enumerated value. */
  protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz) {
    return parseEnum(node, clazz, null);
  }

  /** Parses an enumerated value. */
  protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue) {
    if (node == null) {
      return defaultValue;
    }

    try {
      return Enum.valueOf(clazz, node.getNodeValue());
    } catch (IllegalArgumentException e) {
      LOG.warn(
          "Invalid value specified for node: {} specified value: {} should be enum value of \"{}\" usingprotected value: {}",
          node.getNodeName(),
          node.getNodeValue(),
          clazz.getSimpleName(),
          defaultValue);
      return defaultValue;
    }
  }

  protected <T extends Enum<T>> T parseEnum(
      NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue) {
    return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
  }

  class XMLErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException e) throws SAXParseException {
      throw e;
    }

    @Override
    public void error(SAXParseException e) throws SAXParseException {
      throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXParseException {
      throw e;
    }
  }
}
