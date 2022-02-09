

/**
 * Group Template AI:<br>
 * This folder contains AI scripts for group templates.<br>
 * That is, if many different mobs share the same behavior, a group AI script can be created for all of them.<br>
 * Such group templates ought to be here.<br>
 * <br>
 * Group templates can be sub-classed.<br>
 * In other words, a group may inherit from another group.<br>
 * For example, one group template might define mobs that cast spells.<br>
 * Another template may then define the AI for mobs that cast spells AND use shots.<br>
 * In that case, instead of rewriting all the attack and spell-use AI, we can inherit from the first group template, then add the new behaviors, and split up the NPC registrations appropriately.<br>
 * <br>
 * "NPC registrations" refers to the addition of NPCs in the various events of the scripts, such as onAttack, onKill, etc.<br>
 * Those are done by using methods such as addKillId(..) etc.<br>
 * @see com.l2jserver.datapack.quests
 * @author Fulminus, Zoey76
 */
package com.l2jserver.datapack.ai.group_template;