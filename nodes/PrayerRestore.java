package miscfirecaper.nodes;

import org.powerbot.script.methods.MethodContext;

import miscfirecaper.MiscFireCaper;
import miscfirecaper.util.Consumables;
import miscfirecaper.util.ProtectionPrayer;
import miscfirecaper.util.Task;

public class PrayerRestore extends Task{
	
	private MethodContext ctx;
	
	public PrayerRestore(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public void execute() {
		if(ctx.backpack.select().id(MiscFireCaper.VIAL_ID).count() > 0){
			ctx.backpack.select().id(MiscFireCaper.VIAL_ID).iterator().next().interact("Drop");
			sleep(1500);
		}
		if(ProtectionPrayer.getPoints(ctx) < MiscFireCaper.prayerBoostPoint){
			if(Consumables.restorePrayer(ctx))
				sleep(1500);
		}
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.start 
				&& !MiscFireCaper.handleJad
				&& MiscFireCaper.currentWave > 61
				&& (ProtectionPrayer.getPoints(ctx) < MiscFireCaper.prayerBoostPoint);
	}
	
//	private boolean shouldPot(int skill){
//		return(Skills.getLevel(skill) == Skills.getRealLevel(skill));
//	}
}
