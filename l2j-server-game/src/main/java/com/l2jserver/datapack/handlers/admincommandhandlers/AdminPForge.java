package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.L2GamePacketHandler;
import com.l2jserver.gameserver.network.clientpackets.L2GameClientPacket;
import com.l2jserver.gameserver.network.serverpackets.AdminForgePacket;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.mmocore.NioNetStringBuffer;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class AdminPForge implements IAdminCommandHandler {

  private static final String[] ADMIN_COMMANDS = {
    "admin_forge", "admin_forge_values", "admin_forge_send"
  };
  private final L2GamePacketHandler gamePacketHandler;

  public AdminPForge(L2GamePacketHandler gamePacketHandler) {
    this.gamePacketHandler = gamePacketHandler;
  }


  private List<String> getOpCodes(StringTokenizer st) {
    List<String> opCodes = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (";".equals(token)) {
        break;
      }
      opCodes.add(token);
    }
    return opCodes;
  }

  private boolean validateOpCodes(List<String> opCodes) {
    if ((opCodes.isEmpty()) || (opCodes.size() > 3)) {
      return false;
    }

    for (int i = 0; i < opCodes.size(); ++i) {
      final String opCode = opCodes.get(i);
      long opCodeLong;
      try {
        opCodeLong = Long.parseLong(opCode);
      } catch (Exception e) {
        if (i > 0) {
          return true;
        }

        return false;
      }

      if (opCodeLong < 0) {
        return false;
      }

      if ((i == 0) && (opCodeLong > 255)) {
        return false;
      } else if ((i == 1) && (opCodeLong > 65535)) {
        return false;
      } else if ((i == 2) && (opCodeLong > 4294967295L)) {
        return false;
      }
    }

    return true;
  }

  private boolean validateFormat(String format) {
    for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
      switch (format.charAt(chIdx)) {
        case 'b':
        case 'B':
        case 'x':
        case 'X':
          // array
          break;
        case 'c':
        case 'C':
          // byte
          break;
        case 'h':
        case 'H':
          // word
          break;
        case 'd':
        case 'D':
          // dword
          break;
        case 'q':
        case 'Q':
          // qword
          break;
        case 'f':
        case 'F':
          // double
          break;
        case 's':
        case 'S':
          // string
          break;
        default:
          return false;
      }
    }

    return true;
  }

  private boolean validateMethod(String method) {
    switch (method) {
      case "sc":
      case "sb":
      case "cs":
        return true;
    }

    return false;
  }

  private void showValuesUsage(L2PcInstance activeChar) {
    activeChar.sendMessage("Usage: //forge_values opcode1[ opcode2[ opcode3]] ;[ format]");
    showMainPage(activeChar);
  }

  private void showSendUsage(L2PcInstance activeChar, List<String> opCodes, String format) {
    activeChar.sendMessage(
        "Usage: //forge_send sc|sb|cs opcode1[;opcode2[;opcode3]][ format value1 ... valueN] ");
    if (opCodes == null) {
      showMainPage(activeChar);
    } else {
      showValuesPage(activeChar, opCodes, format);
    }
  }

  private void showMainPage(L2PcInstance activeChar) {
    AdminHtml.showAdminHtml(activeChar, "pforge/main.htm");
  }

  private void showValuesPage(L2PcInstance activeChar, List<String> opCodes, String format) {
    String sendBypass = String.join(";", opCodes);
    String valuesHtml =
        HtmCache.getInstance()
            .getHtm(activeChar.getHtmlPrefix(), "data/html/admin/pforge/values.htm");
    if (opCodes.size() == 3) {
      valuesHtml = valuesHtml.replace("%opformat%", "chd");
    } else if (opCodes.size() == 2) {
      valuesHtml = valuesHtml.replace("%opformat%", "ch");
    } else {
      valuesHtml = valuesHtml.replace("%opformat%", "c");
    }

    valuesHtml = valuesHtml.replace("%opcodes%", sendBypass);

    String editorsHtml = "";

    if (format == null) {
      valuesHtml = valuesHtml.replace("%format%", "");
      editorsHtml = "";
    } else {
      valuesHtml = valuesHtml.replace("%format%", format);
      sendBypass += " " + format;

      String editorTemplate =
          HtmCache.getInstance()
              .getHtm(activeChar.getHtmlPrefix(), "data/html/admin/pforge/inc/editor.htm");

      if (editorTemplate != null) {
        StringBuilder singleCharSequence = new StringBuilder(1);
        singleCharSequence.append(' ');

        for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
          char ch = format.charAt(chIdx);
          singleCharSequence.setCharAt(0, ch);
          editorsHtml +=
              editorTemplate
                  .replace("%format%", singleCharSequence)
                  .replace("%editor_index%", String.valueOf(chIdx));
          sendBypass += " $v" + chIdx;
        }
      } else {
        editorsHtml = "";
      }
    }

    valuesHtml = valuesHtml.replace("%editors%", editorsHtml);
    valuesHtml = valuesHtml.replace("%send_bypass%", sendBypass);
    activeChar.sendPacket(new NpcHtmlMessage(valuesHtml));
  }

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.equals("admin_forge")) {
      showMainPage(activeChar);
    } else if (command.startsWith("admin_forge_values ")) {
      try {
        StringTokenizer st = new StringTokenizer(command);
        st.nextToken(); // skip command token

        if (!st.hasMoreTokens()) {
          showValuesUsage(activeChar);
          return false;
        }

        var opCodes = getOpCodes(st);
        if (!validateOpCodes(opCodes)) {
          activeChar.sendMessage("Invalid op codes!");
          showValuesUsage(activeChar);
          return false;
        }

        String format = null;
        if (st.hasMoreTokens()) {
          format = st.nextToken();
          if (!validateFormat(format)) {
            activeChar.sendMessage("Format invalid!");
            showValuesUsage(activeChar);
            return false;
          }
        }

        showValuesPage(activeChar, opCodes, format);
      } catch (Exception e) {
        e.printStackTrace();
        showValuesUsage(activeChar);
        return false;
      }
    } else if (command.startsWith("admin_forge_send ")) {
      try {
        StringTokenizer st = new StringTokenizer(command);
        st.nextToken(); // skip command token

        if (!st.hasMoreTokens()) {
          showSendUsage(activeChar, null, null);
          return false;
        }

        String method = st.nextToken();
        if (!validateMethod(method)) {
          activeChar.sendMessage("Invalid method!");
          showSendUsage(activeChar, null, null);
          return false;
        }

        var opCodes = Arrays.asList(st.nextToken().split(";"));
        if (!validateOpCodes(opCodes)) {
          activeChar.sendMessage("Invalid op codes!");
          showSendUsage(activeChar, null, null);
          return false;
        }

        String format = null;
        if (st.hasMoreTokens()) {
          format = st.nextToken();
          if (!validateFormat(format)) {
            activeChar.sendMessage("Format invalid!");
            showSendUsage(activeChar, null, null);
            return false;
          }
        }

        AdminForgePacket afp = null;
        ByteBuffer bb = null;
        for (int i = 0; i < opCodes.size(); ++i) {
          char type;
          if (i == 0) {
            type = 'c';
          } else if (i == 1) {
            type = 'h';
          } else {
            type = 'd';
          }
          if (method.equals("sc") || method.equals("sb")) {
            if (afp == null) {
              afp = new AdminForgePacket();
            }
            afp.addPart((byte) type, opCodes.get(i));
          } else {
            if (bb == null) {
              bb = ByteBuffer.allocate(32767);
            }
            write((byte) type, opCodes.get(i), bb);
          }
        }

        if (format != null) {
          for (int i = 0; i < format.length(); ++i) {
            if (!st.hasMoreTokens()) {
              activeChar.sendMessage("Not enough values!");
              showSendUsage(activeChar, null, null);
              return false;
            }

            L2Object target;
            L2BoatInstance boat;
            String value = st.nextToken();
            switch (value) {
              case "$oid" -> value = String.valueOf(activeChar.getObjectId());
              case "$boid" -> {
                boat = activeChar.getBoat();
                if (boat != null) {
                  value = String.valueOf(boat.getObjectId());
                } else {
                  value = "0";
                }
              }
              case "$title" -> value = activeChar.getTitle();
              case "$name" -> value = activeChar.getName();
              case "$x" -> value = String.valueOf(activeChar.getX());
              case "$y" -> value = String.valueOf(activeChar.getY());
              case "$z" -> value = String.valueOf(activeChar.getZ());
              case "$heading" -> value = String.valueOf(activeChar.getHeading());
              case "$toid" -> value = String.valueOf(activeChar.getTargetId());
              case "$tboid" -> {
                target = activeChar.getTarget();
                if ((target != null) && (target instanceof L2Playable)) {
                  boat = ((L2Playable) target).getActingPlayer().getBoat();
                  if (boat != null) {
                    value = String.valueOf(boat.getObjectId());
                  } else {
                    value = "0";
                  }
                }
              }
              case "$ttitle" -> {
                target = activeChar.getTarget();
                if ((target != null) && (target instanceof L2Character)) {
                  value = String.valueOf(((L2Character) target).getTitle());
                } else {
                  value = "";
                }
              }
              case "$tname" -> {
                target = activeChar.getTarget();
                if (target != null) {
                  value = String.valueOf(target.getName());
                } else {
                  value = "";
                }
              }
              case "$tx" -> {
                target = activeChar.getTarget();
                if (target != null) {
                  value = String.valueOf(target.getX());
                } else {
                  value = "0";
                }
              }
              case "$ty" -> {
                target = activeChar.getTarget();
                if (target != null) {
                  value = String.valueOf(target.getY());
                } else {
                  value = "0";
                }
              }
              case "$tz" -> {
                target = activeChar.getTarget();
                if (target != null) {
                  value = String.valueOf(target.getZ());
                } else {
                  value = "0";
                }
              }
              case "$theading" -> {
                target = activeChar.getTarget();
                if (target != null) {
                  value = String.valueOf(target.getHeading());
                } else {
                  value = "0";
                }
              }
            }

            if (method.equals("sc") || method.equals("sb")) {
              if (afp != null) {
                afp.addPart((byte) format.charAt(i), value);
              }
            } else {
              write((byte) format.charAt(i), value, bb);
            }
          }
        }

        if (method.equals("sc")) {
          activeChar.sendPacket(afp);
        } else if (method.equals("sb")) {
          activeChar.broadcastPacket(afp);
        } else if (bb != null) {
          bb.flip();
          L2GameClientPacket p =
              (L2GameClientPacket)
                  gamePacketHandler.handlePacket(bb, activeChar.getClient());
          if (p != null) {
            p.setBuffers(bb, activeChar.getClient(), new NioNetStringBuffer(2000));
            if (p.read()) {
              ThreadPoolManager.getInstance().executePacket(p);
            }
          }
        }

        showValuesPage(activeChar, opCodes, format);
      } catch (Exception e) {
        e.printStackTrace();
        showSendUsage(activeChar, null, null);
        return false;
      }
    }

    return true;
  }

  private boolean write(byte b, String string, ByteBuffer buf) {
    if ((b == 'C') || (b == 'c')) {
      buf.put(Byte.decode(string));
      return true;
    } else if ((b == 'D') || (b == 'd')) {
      buf.putInt(Integer.decode(string));
      return true;
    } else if ((b == 'H') || (b == 'h')) {
      buf.putShort(Short.decode(string));
      return true;
    } else if ((b == 'F') || (b == 'f')) {
      buf.putDouble(Double.parseDouble(string));
      return true;
    } else if ((b == 'S') || (b == 's')) {
      final int len = string.length();
      for (int i = 0; i < len; i++) {
        buf.putChar(string.charAt(i));
      }
      buf.putChar('\000');
      return true;
    } else if ((b == 'B') || (b == 'b') || (b == 'X') || (b == 'x')) {
      buf.put(new BigInteger(string).toByteArray());
      return true;
    } else if ((b == 'Q') || (b == 'q')) {
      buf.putLong(Long.decode(string));
      return true;
    }
    return false;
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
