
package com.l2jserver.datapack.custom.service.buffer.model;

import java.util.LinkedList;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.buffer.BufferServiceRepository.BuffType;
import com.l2jserver.datapack.custom.service.buffer.model.entity.BuffSkill;

/**
 * This class is here so we can actually get the name of this list and make placeholder adjustments easily while keeping outside code cleaner
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class UniqueBufflist extends LinkedList<BuffSkill> {
	private static final long serialVersionUID = -2586607798277226501L;
	
	public final int ulistId;
	public final String ulistName;
	public int numBuffs;
	public int numSongsDances;
	public HTMLTemplatePlaceholder placeholder;
	
	public UniqueBufflist(int ulistId, String ulistName) {
		this.ulistId = ulistId;
		this.ulistName = ulistName;
		this.numBuffs = 0;
		this.numSongsDances = 0;
		this.placeholder = new HTMLTemplatePlaceholder("unique", null).addChild("buffs", null).addChild("name", ulistName).addChild("num_buffs", "0").addChild("num_songs_dances", "0");
	}
	
	@Override
	public boolean add(BuffSkill e) {
		if (super.add(e)) {
			if (e.getType() == BuffType.BUFF) {
				++this.numBuffs;
				this.placeholder.getChild("num_buffs").setValue(String.valueOf(Integer.parseInt(this.placeholder.getChild("num_buffs").getValue()) + 1));
			} else {
				++this.numSongsDances;
				this.placeholder.getChild("num_songs_dances").setValue(String.valueOf(Integer.parseInt(this.placeholder.getChild("num_songs_dances").getValue()) + 1));
			}
			this.placeholder.getChild("buffs").addAliasChild(e.getId(), e.getPlaceholder());
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			switch (((BuffSkill) o).getType()) {
				case BUFF:
					--numBuffs;
					break;
				case SONG_DANCE:
					--numSongsDances;
					break;
			}
			
			this.placeholder = new HTMLTemplatePlaceholder("unique", null).addChild("buffs", null).addChild("name", this.ulistName).addChild("num_buffs", String.valueOf(numBuffs)).addChild("num_songs_dances", String.valueOf(numSongsDances));
			for (BuffSkill buff : this) {
				this.placeholder.getChild("buffs").addAliasChild(buff.getId(), buff.getPlaceholder());
			}
			return true;
		}
		
		return false;
	}
}
