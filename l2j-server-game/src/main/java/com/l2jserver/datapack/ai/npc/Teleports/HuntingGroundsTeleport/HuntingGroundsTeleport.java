
package com.l2jserver.datapack.ai.npc.Teleports.HuntingGroundsTeleport;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;

/**
 * Hunting Grounds teleport AI.
 * @author Charus
 */
public final class HuntingGroundsTeleport extends AbstractNpcAI {
	// NPCs
	// @formatter:off
	private final static int[] PRIESTS =
	{
		31078, 31079, 31080, 31081, 31082, 31083, 31084, 31085, 31086, 31087,
		31088, 31089, 31090, 31091, 31168, 31169, 31692, 31693, 31694, 31695,
		31997, 31998
	};
	
	private static final int[] DAWN_NPCS =
	{
		31078, 31079, 31080, 31081, 31082, 31083, 31084, 31168, 31692, 31694,
		31997
	};
	// @formatter:on
	
	public HuntingGroundsTeleport() {
		super(HuntingGroundsTeleport.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(PRIESTS);
		addTalkId(PRIESTS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final SevenSigns ss = SevenSigns.getInstance();
		final int playerCabal = ss.getPlayerCabal(player.getObjectId());
		
		if (playerCabal == SevenSigns.CABAL_NULL) {
			return Util.contains(DAWN_NPCS, npc.getId()) ? "dawn_tele-no.htm" : "dusk_tele-no.htm";
		}
		
		String htmltext = "";
		final boolean check = ss.isSealValidationPeriod() && (playerCabal == ss.getSealOwner(SevenSigns.SEAL_GNOSIS)) && (ss.getPlayerSeal(player.getObjectId()) == SevenSigns.SEAL_GNOSIS);
		switch (npc.getId()) {
			case 31078:
			case 31085: {
				htmltext = check ? "low_gludin.htm" : "hg_gludin.htm";
				break;
			}
			case 31079:
			case 31086: {
				htmltext = check ? "low_gludio.htm" : "hg_gludio.htm";
				break;
			}
			case 31080:
			case 31087: {
				htmltext = check ? "low_dion.htm" : "hg_dion.htm";
				break;
			}
			case 31081:
			case 31088: {
				htmltext = check ? "low_giran.htm" : "hg_giran.htm";
				break;
			}
			case 31082:
			case 31089: {
				htmltext = check ? "low_heine.htm" : "hg_heine.htm";
				break;
			}
			case 31083:
			case 31090: {
				htmltext = check ? "low_oren.htm" : "hg_oren.htm";
				break;
			}
			case 31084:
			case 31091: {
				htmltext = check ? "low_aden.htm" : "hg_aden.htm";
				break;
			}
			case 31168:
			case 31169: {
				htmltext = check ? "low_hw.htm" : "hg_hw.htm";
				break;
			}
			case 31692:
			case 31693: {
				htmltext = check ? "low_goddard.htm" : "hg_goddard.htm";
				break;
			}
			case 31694:
			case 31695: {
				htmltext = check ? "low_rune.htm" : "hg_rune.htm";
				break;
			}
			case 31997:
			case 31998: {
				htmltext = check ? "low_schuttgart.htm" : "hg_schuttgart.htm";
				break;
			}
			default: {
				break;
			}
		}
		return htmltext;
	}
}
