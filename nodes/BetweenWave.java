package miscfirecaper.nodes;

import java.util.List;

import org.powerbot.script.AbstractScript;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Players;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Npc;

import miscfirecaper.MiscFireCaper;
import miscfirecaper.Monster;
import miscfirecaper.Wave;
import miscfirecaper.util.AbilitySetup;
import miscfirecaper.util.Condition;
import miscfirecaper.util.Consumables;
import miscfirecaper.util.ProtectionPrayer;
import miscfirecaper.util.Task;
import miscfirecaper.util.Util;

public class BetweenWave extends Task{
	
	private MethodContext ctx;
	
	public BetweenWave(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public void execute() {
		MiscFireCaper.currentWave = Wave.getWave(ctx);
		if(MiscFireCaper.currentWave == 63){
			MiscFireCaper.currentNpc = "Jad";
			MiscFireCaper.jadAttacked = new Timer(0);
			MiscFireCaper.freeTime = new Timer(0);
			MiscFireCaper.waitToAttack = new Timer(0);
			MiscFireCaper.tillNextPot = new Timer(0);
			MiscFireCaper.handleWave = MiscFireCaper.prepWave = false;
			MiscFireCaper.handleJad = true;
		}
		else if(MiscFireCaper.currentWave > 61){
			if( !ProtectionPrayer.MAGIC.isActive(ctx)){
				System.out.println("put magic protection on");
				AbilitySetup.Ability.Magic_Protection.use(ctx);
				ctx.game.sleep(1500);
			}
			if(ctx.backpack.count() > 26){
				System.out.println("make inventory room");
				Consumables.eatFood(ctx, MiscFireCaper.foodId);
				ctx.game.sleep(1500);
			}
		}

//		if(ctx.camera.getYaw() > 5)
//			ctx.camera.setYaw(3);
		
		MiscFireCaper.waitFor(new Condition(){
			@Override
			public boolean accept() {
				return !ctx.players.local().isInCombat()
						|| Monster.getInArea(ctx, MiscFireCaper.southwestZone).size() > 0;
			}
		}, 5000);
		
		if(!ctx.players.local().isInCombat() && AbilitySetup.Ability.Regenerate.isReady(ctx)){
			AbilitySetup.Ability.Regenerate.use(ctx);
			ctx.game.sleep(500);
		}
		
		if(Wave.outsideCave(ctx)){
			MiscFireCaper.handleWave = MiscFireCaper.prepWave = false;
		}
		
		List<Npc> monsters = Monster.getMonsters(ctx);
		if(Util.distanceTo(ctx, MiscFireCaper.safeSpot) < 3
				&& (monsters.size() > 0)){
			MiscFireCaper.prepWave = false;
			MiscFireCaper.handleWave = true;
			return;
		}else{
			//ctx.movement.stepTowards(MiscFireCaper.safeSpot);
			Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			ctx.game.sleep(1000);
		}
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.prepWave;
	}
}
