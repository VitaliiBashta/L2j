package com.l2jserver.gameserver;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.l2jserver.gameserver.config.Configuration.fortress;

public class FortUpdater implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FortUpdater.class);
  private final L2Clan clan;
  private final Fort fort;
  private final UpdaterType updaterType;
  private int runCount;

	public enum UpdaterType {
		MAX_OWN_TIME, // gives fort back to NPC clan
		PERIODIC_UPDATE // raise blood oath/supply level
	}
	
	public FortUpdater(Fort fort, L2Clan clan, int runCount, UpdaterType ut) {
    this.fort = fort;
    this.clan = clan;
    this.runCount = runCount;
    updaterType = ut;
	}
	
	@Override
	public void run() {
		try {
      if (updaterType == UpdaterType.PERIODIC_UPDATE) {
        runCount++;
        if ((fort.getOwnerClan() == null) || (fort.getOwnerClan() != clan)) {
          return;
        }

        fort.getOwnerClan().increaseBloodOathCount();

        if (fort.getFortState() == 2) {
          if (clan.getWarehouse().getAdena() >= fortress().getFeeForCastle()) {
            clan.getWarehouse()
                .destroyItemByItemId(
                    "FS_fee_for_Castle",
                    Inventory.ADENA_ID,
                    fortress().getFeeForCastle(),
                    null,
                    null);
            fort.getContractedCastle().addToTreasuryNoTax(fortress().getFeeForCastle());
            fort.raiseSupplyLvL();
          } else {
            fort.setFortState(1, 0);
          }
        }
        fort.saveFortVariables();
      } else if (updaterType == UpdaterType.MAX_OWN_TIME) {
        if ((fort.getOwnerClan() == null) || (fort.getOwnerClan() != clan)) {
          return;
        }
        if (fort.getOwnedTime() > (fortress().getMaxKeepTime() * 3600)) {
          fort.removeOwner(true);
          fort.setFortState(0, 0);
				}
			}
		} catch (Exception e) {
			LOG.error("There has been a problem updating forts!", e);
		}
	}
	
	public int getRunCount() {
    return runCount;
	}
}