package com.l2jserver.gameserver.config.converter;

import com.l2jserver.gameserver.model.holders.ItemHolder;
import org.aeonbits.owner.Converter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ClassMasterSettingConverter implements Converter<ClassMasterSetting> {
	
	@Override
	public ClassMasterSetting convert(Method method, String input) {
		final var classMasterSetting = new ClassMasterSetting();
		input = input.trim();
		if (input.isEmpty()) {
			return classMasterSetting;
		}
		
		final StringTokenizer st = new StringTokenizer(input, ";");
		while (st.hasMoreTokens()) {
			// get allowed class change
			final int job = Integer.parseInt(st.nextToken());
			classMasterSetting.addAllowedClassChange(job);
			
			final List<ItemHolder> requiredItems = new ArrayList<>();
			// parse items needed for class change
			if (st.hasMoreTokens()) {
				final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				while (st2.hasMoreTokens()) {
					final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					final int itemId = Integer.parseInt(st3.nextToken());
					final int quantity = Integer.parseInt(st3.nextToken());
					requiredItems.add(new ItemHolder(itemId, quantity));
				}
			}
			classMasterSetting.addClaimItems(job, requiredItems);
			
			final List<ItemHolder> rewardItems = new ArrayList<>();
			// parse gifts after class change
			if (st.hasMoreTokens()) {
				final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
				while (st2.hasMoreTokens()) {
					final StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
					final int itemId = Integer.parseInt(st3.nextToken());
					final int quantity = Integer.parseInt(st3.nextToken());
					rewardItems.add(new ItemHolder(itemId, quantity));
				}
			}
			classMasterSetting.addRewardItems(job, rewardItems);
		}
		return classMasterSetting;
	}
}
