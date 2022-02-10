package com.l2jserver.gameserver.config;

import com.l2jserver.gameserver.GameServer;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class IPConfigData extends IXmlReader {

  private static final Logger LOG = LoggerFactory.getLogger(IPConfigData.class);

  private static final String IP_CONFIG_FILE = "./config/ipconfig.xml";

  private final List<String> _subnets = new ArrayList<>(5);

  private final List<String> _hosts = new ArrayList<>(5);

  @Override
  public void load() {
    GameServer.printSection("Network Configuration");
    final File f = new File(IP_CONFIG_FILE);
    if (f.exists()) {
      LOG.info("Using existing ipconfig.xml.");
      parseFile(f);
    } else {
      LOG.info("Using automatic network configuration.");
      autoIpConfig();
    }
  }

  @Override
  public void parseDocument(Document doc) {
    NamedNodeMap attrs;
    for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
      if ("gameserver".equalsIgnoreCase(n.getNodeName())) {
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
          if ("define".equalsIgnoreCase(d.getNodeName())) {
            attrs = d.getAttributes();
            _subnets.add(attrs.getNamedItem("subnet").getNodeValue());
            _hosts.add(attrs.getNamedItem("address").getNodeValue());

            if (_hosts.size() != _subnets.size()) {
              LOG.warn(
                  "Failed to load {} file - subnets does not match server addresses.",
                  IP_CONFIG_FILE);
            }
          }
        }

        Node att = n.getAttributes().getNamedItem("address");
        if (att == null) {
          LOG.warn("Failed to load {} file - default server address is missing.", IP_CONFIG_FILE);
          _hosts.add("127.0.0.1");
        } else {
          _hosts.add(att.getNodeValue());
        }
        _subnets.add("0.0.0.0/0");
      }
    }
  }

  protected void autoIpConfig() {
    String externalIp;
    try {
      URL autoIp = new URL("http://ip1.dynupdate.no-ip.com:8245/");
      try (BufferedReader in = new BufferedReader(new InputStreamReader(autoIp.openStream()))) {
        externalIp = in.readLine();
      }
    } catch (IOException e) {
      LOG.warn(
          "Failed to connect to ip1.dynupdate.no-ip.com:8245 please check your internet connection using 127.0.0.1!");
      externalIp = "127.0.0.1";
    }

    try {
      Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();

      while (niList.hasMoreElements()) {
        NetworkInterface ni = niList.nextElement();

        if (!ni.isUp() || ni.isVirtual()) {
          continue;
        }

        if (!ni.isLoopback()
            && ((ni.getHardwareAddress() == null) || (ni.getHardwareAddress().length != 6))) {
          continue;
        }

        for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
          if (ia.getAddress() instanceof Inet6Address) {
            continue;
          }

          final String hostAddress = ia.getAddress().getHostAddress();
          final int subnetPrefixLength = ia.getNetworkPrefixLength();
          final int subnetMaskInt =
              IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0)
                  << (32 - subnetPrefixLength);
          final int hostAddressInt =
              Arrays.stream(hostAddress.split("\\."))
                  .mapToInt(Integer::parseInt)
                  .reduce((r, e) -> (r << 8) + e)
                  .orElse(0);
          final int subnetAddressInt = hostAddressInt & subnetMaskInt;
          final String subnetAddress =
              ((subnetAddressInt >> 24) & 0xFF)
                  + "."
                  + ((subnetAddressInt >> 16) & 0xFF)
                  + "."
                  + ((subnetAddressInt >> 8) & 0xFF)
                  + "."
                  + (subnetAddressInt & 0xFF);
          final String subnet = subnetAddress + '/' + subnetPrefixLength;
          if (!_subnets.contains(subnet) && !subnet.equals("0.0.0.0/0")) {
            _subnets.add(subnet);
            _hosts.add(hostAddress);
            LOG.info("Adding new subnet: " + subnet + " address: " + hostAddress);
          }
        }
      }

      // External host and subnet
      _hosts.add(externalIp);
      _subnets.add("0.0.0.0/0");
      LOG.info("Adding new subnet: 0.0.0.0/0 address: {}", externalIp);
    } catch (SocketException e) {
      LOG.error("Configuration failed please manually configure ipconfig.xml", e);
      System.exit(0);
    }
  }

  public List<String> getSubnets() {
    if (_subnets.isEmpty()) {
      return List.of("0.0.0.0/0");
    }
    return _subnets;
  }

  public List<String> getHosts() {
    if (_hosts.isEmpty()) {
      return List.of("127.0.0.1");
    }
    return _hosts;
  }
}
