
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

import static com.l2jserver.gameserver.config.Configuration.character;

@Service
public class SkillList implements IBypassHandler {
	private static final String[] COMMANDS = {
		"SkillList"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!(target instanceof L2NpcInstance)) {
			return false;
		}
		
		if (character().skillLearn()) {
			try {
				String id = command.substring(9).trim();
				if (id.length() != 0) {
					L2NpcInstance.showSkillList(activeChar, (L2Npc) target, ClassId.getClassId(Integer.parseInt(id)));
				} else {
					boolean own_class = false;
					
					final List<ClassId> classesToTeach = ((L2NpcInstance) target).getClassesToTeach();
					for (ClassId cid : classesToTeach) {
						if (cid.equalsOrChildOf(activeChar.getClassId())) {
							own_class = true;
							break;
						}
					}
					
					String text = "<html><body><center>Skill learning:</center><br>";
					
					if (!own_class) {
						String charType = activeChar.getClassId().isMage() ? "fighter" : "mage";
						text += "Skills of your class are the easiest to learn.<br>" + "Skills of another class of your race are a little harder.<br>" + "Skills for classes of another race are extremely difficult.<br>" + "But the hardest of all to learn are the  " + charType + "skills!<br>";
					}
					
					// make a list of classes
					if (!classesToTeach.isEmpty()) {
						int count = 0;
						ClassId classCheck = activeChar.getClassId();
						
						while ((count == 0) && (classCheck != null)) {
							for (ClassId cid : classesToTeach) {
								if (cid.level() > classCheck.level()) {
									continue;
								}
								
								if (SkillTreesData.getInstance().getAvailableSkills(activeChar, cid, false, false).isEmpty()) {
									continue;
								}
								
								text += "<a action=\"bypass -h npc_%objectId%_SkillList " + cid.getId() + "\">Learn " + cid + "'s class Skills</a><br>\n";
								count++;
							}
							classCheck = classCheck.getParent();
						}
						classCheck = null;
					} else {
						text += "No Skills.<br>";
					}
					text += "</body></html>";
					
					final NpcHtmlMessage html = new NpcHtmlMessage(((L2Npc) target).getObjectId());
					html.setHtml(text);
					html.replace("%objectId%", String.valueOf(((L2Npc) target).getObjectId()));
					activeChar.sendPacket(html);
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
			} catch (Exception e) {
				_log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
			}
		} else {
			L2NpcInstance.showSkillList(activeChar, (L2Npc) target, activeChar.getClassId());
		}
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
