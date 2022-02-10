package com.l2jserver.gameserver.data.xml.impl;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.options.Options;
import com.l2jserver.gameserver.model.options.OptionsSkillHolder;
import com.l2jserver.gameserver.model.options.OptionsSkillType;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;
import com.l2jserver.gameserver.util.IXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

@Service
public class OptionData extends IXmlReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(OptionData.class);
	
	private final Map<Integer, Options> _optionData = new HashMap<>();
	
	@Override
	public synchronized void load() {
		_optionData.clear();
		parseDatapackDirectory("data/stats/options");
		LOG.info("Loaded {} item options.", _optionData.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("option".equalsIgnoreCase(d.getNodeName())) {
						final int id = parseInteger(d.getAttributes(), "id");
						final Options op = new Options(id);
						
						for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
							switch (cd.getNodeName()) {
								case "for" -> {
									for (Node fd = cd.getFirstChild(); fd != null; fd = fd.getNextSibling()) {
										switch (fd.getNodeName()) {
											case "add", "sub", "mul", "div", "set", "share", "enchant", "enchanthp" -> parseFuncs(fd.getAttributes(), fd.getNodeName(), op);
										}
									}
								}
								case "active_skill" -> op.setActiveSkill(new SkillHolder(parseInteger(cd.getAttributes(), "id"), parseInteger(cd.getAttributes(), "level")));
								case "passive_skill" -> op.setPassiveSkill(new SkillHolder(parseInteger(cd.getAttributes(), "id"), parseInteger(cd.getAttributes(), "level")));
								case "attack_skill" -> op.addActivationSkill(new OptionsSkillHolder(parseInteger(cd.getAttributes(), "id"), parseInteger(cd.getAttributes(), "level"), parseDouble(cd.getAttributes(), "chance"), OptionsSkillType.ATTACK));
								case "magic_skill" -> op.addActivationSkill(new OptionsSkillHolder(parseInteger(cd.getAttributes(), "id"), parseInteger(cd.getAttributes(), "level"), parseDouble(cd.getAttributes(), "chance"), OptionsSkillType.MAGIC));
								case "critical_skill" -> op.addActivationSkill(new OptionsSkillHolder(parseInteger(cd.getAttributes(), "id"), parseInteger(cd.getAttributes(), "level"), parseDouble(cd.getAttributes(), "chance"), OptionsSkillType.CRITICAL));
							}
						}
						_optionData.put(op.getId(), op);
					}
				}
			}
		}
	}
	
	private void parseFuncs(NamedNodeMap attrs, String functionName, Options op) {
		Stats stat = Stats.valueOfXml(parseString(attrs, "stat"));
		double val = parseDouble(attrs, "val");
		int order = -1;
		final Node orderNode = attrs.getNamedItem("order");
		if (orderNode != null) {
			order = Integer.parseInt(orderNode.getNodeValue());
		}
		op.addFunc(new FuncTemplate(null, null, functionName, order, stat, val));
	}
	
	public Options getOptions(int id) {
		return _optionData.get(id);
	}
	
	public static OptionData getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final OptionData INSTANCE = new OptionData();
	}
}
