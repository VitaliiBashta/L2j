
package com.l2jserver.datapack.gracia;

import com.l2jserver.datapack.gracia.ai.EnergySeeds;
import com.l2jserver.datapack.gracia.ai.Lindvior;
import com.l2jserver.datapack.gracia.ai.SeedOfAnnihilation.SeedOfAnnihilation;
import com.l2jserver.datapack.gracia.ai.StarStones;
import com.l2jserver.datapack.gracia.ai.npc.FortuneTelling.FortuneTelling;
import com.l2jserver.datapack.gracia.ai.npc.GeneralDilios.GeneralDilios;
import com.l2jserver.datapack.gracia.ai.npc.Lekon.Lekon;
import com.l2jserver.datapack.gracia.ai.npc.Nemo.Nemo;
import com.l2jserver.datapack.gracia.ai.npc.Nottingale.Nottingale;
import com.l2jserver.datapack.gracia.ai.npc.Seyo.Seyo;
import com.l2jserver.datapack.gracia.ai.npc.ZealotOfShilen.ZealotOfShilen;
import com.l2jserver.datapack.gracia.instances.SecretArea.SecretArea;
import com.l2jserver.datapack.gracia.instances.SeedOfDestruction.Stage1;
import com.l2jserver.datapack.gracia.instances.SeedOfInfinity.HallOfSuffering.HallOfSuffering;
import com.l2jserver.datapack.gracia.vehicles.AirShipGludioGracia.AirShipGludioGracia;
import com.l2jserver.datapack.gracia.vehicles.KeucereusNorthController.KeucereusNorthController;
import com.l2jserver.datapack.gracia.vehicles.KeucereusSouthController.KeucereusSouthController;
import com.l2jserver.datapack.gracia.vehicles.SoDController.SoDController;
import com.l2jserver.datapack.gracia.vehicles.SoIController.SoIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GraciaLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(GraciaLoader.class);
	
	private static final Class<?>[] SCRIPTS = {
		// AIs
		EnergySeeds.class,
		Lindvior.class,
		StarStones.class,
		// NPCs
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		Seyo.class,
		ZealotOfShilen.class,
		// Seed of Annihilation
		SeedOfAnnihilation.class,
		// Instances
		SecretArea.class,
		Stage1.class, // Seed of Destruction
		HallOfSuffering.class, // Seed of Infinity
		// Vehicles
		AirShipGludioGracia.class,
		KeucereusNorthController.class,
		KeucereusSouthController.class,
		SoIController.class,
		SoDController.class,
	};
	
	public static void main(String[] args) {
		int n = 0;
		for (var script : SCRIPTS) {
			try {
				script.getDeclaredConstructor().newInstance();
				n++;
			} catch (Exception ex) {
				LOG.error("Failed loading {}!", script.getSimpleName(), ex);
			}
		}
		LOG.info("Loaded {} Gracia scripts.", n);
	}
}
