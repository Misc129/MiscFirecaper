package miscfirecaper.nodes;

import java.awt.Point;

import miscfirecaper.Jad;
import miscfirecaper.MiscFireCaper;
import miscfirecaper.Monster;
import miscfirecaper.Wave;
import miscfirecaper.util.AbilitySetup;
import miscfirecaper.util.Condition;
import miscfirecaper.util.Consumables;
import miscfirecaper.util.ProtectionPrayer;
import miscfirecaper.util.Task;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu.Entry;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Npc;

public class HandleJad extends Task{

	private MethodContext ctx;
	
	public HandleJad(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.handleJad;
	}

	@Override
	public void execute() {
		if(Wave.outsideCave(ctx)){
			MiscFireCaper.handleJad = false;
			MiscFireCaper.finish = true;
			MiscFireCaper.currentWave = 1;
			return;
		}
		Npc jad = Jad.getJad(ctx);
		if(jad == null){
			if(MiscFireCaper.jadAttacked.getRemaining() == 0)
				ctx.movement.stepTowards(MiscFireCaper.safeSpot);
			return;
		}
		if(MiscFireCaper.northwestZone.contains(jad) && !Monster.playerIsAttacking(ctx,jad) && jad.click(true)){
			MiscFireCaper.waitFor(new Condition(){
				//wait until player walks around blind spot
				@Override
				public boolean accept() {
					Npc thisJad = Jad.getJad(ctx);
					return thisJad != null 
							&& (Jad.isMaging(thisJad) || Jad.isRanging(thisJad));
				}
			}, 8000);
		}
		AbilitySetup.useRotation(ctx, jad);
		if(!Jad.isMaging(jad) && !Jad.isRanging(jad)){
			MiscFireCaper.currentAttack = "?";
		}
		else if(MiscFireCaper.currentAttack.equals("?") && Jad.isMaging(jad)){
			MiscFireCaper.currentAttack = "Magic";
			resetTimer(MiscFireCaper.freeTime, 900);
		}
		else if(MiscFireCaper.currentAttack.equals("?") && Jad.isRanging(jad)){
			MiscFireCaper.currentAttack = "Ranged";
			resetTimer(MiscFireCaper.freeTime, 900);
		}
		MiscFireCaper.jadHp = jad.getHealthPercent();
		if(Jad.isMaging(jad) && MiscFireCaper.currentPrayer != ProtectionPrayer.MAGIC){
			AbilitySetup.Ability.Magic_Protection.use(ctx);
			MiscFireCaper.currentPrayer = ProtectionPrayer.MAGIC;
		}
		else if(Jad.isRanging(jad) && MiscFireCaper.currentPrayer != ProtectionPrayer.RANGED){
			AbilitySetup.Ability.Ranged_Protection.use(ctx);
			MiscFireCaper.currentPrayer = ProtectionPrayer.RANGED;
		}
		else if(jad.getLocation().distanceTo(ctx.players.local()) < 7){
			ctx.movement.stepTowards(Jad.getFightTile(jad));
			resetTimer(MiscFireCaper.waitToAttack, 2000);
			return;
		}
		else if(jad.getLocation().distanceTo(ctx.players.local()) < 8){
			ctx.movement.stepTowards(Jad.getFightTile(jad));
			return;
		}
		else if(ctx.players.local().getHealthPercent() < 60){
			Consumables.eatFood(ctx,MiscFireCaper.foodId);
			if(MiscFireCaper.currentPrayer.isActive(ctx)){
				jad.click(true);
				ctx.camera.turnTo(jad);
			}
			resetTimer(MiscFireCaper.tillNextPot, 1500);
			resetTimer(MiscFireCaper.waitToAttack, 1000);
			return;
		}
		else if(ProtectionPrayer.getPoints(ctx) < MiscFireCaper.prayerBoostPoint){
			Consumables.restorePrayer(ctx);
			if(MiscFireCaper.currentPrayer.isActive(ctx)){
				jad.click(true);
				ctx.camera.turnTo(jad);
			}
			resetTimer(MiscFireCaper.tillNextPot, 1000);
			resetTimer(MiscFireCaper.waitToAttack, 1000);
			return;
		}
		else if(Jad.getNextHealer(ctx, jad) != null && MiscFireCaper.waitToAttack.getRemaining() == 0){
			Npc healer = Jad.getNextHealer(ctx, jad);
			if(healer.isOnScreen() && attackHealer(healer)){
				resetTimer(MiscFireCaper.waitToAttack, 2500);
				AbilitySetup.useRotation(ctx, jad);
				return;
			}
			else
				ctx.camera.turnTo(healer);
		}
		else if(Jad.hasHealers(ctx, jad) && MiscFireCaper.waitToAttack.getRemaining() == 0){
			ctx.movement.stepTowards(Jad.getFightTile(jad));
			return;
		}
		else if(!Monster.playerIsAttacking(ctx,jad) && MiscFireCaper.waitToAttack.getRemaining() == 0){
			ctx.camera.turnTo(jad);
			ctx.camera.turnTo(jad);
			jad.click(true);
			sleep(200);
			resetTimer(MiscFireCaper.waitToAttack, 3000);
		}
		MiscFireCaper.freeTime = new Timer(0);
		
	}

	private boolean attackHealer(Npc healer){
		if(ctx.mouse.click(healer.getCenterPoint(), false))
			sleep(300);
		if(ctx.menu.isOpen() 
				//&& ctx.menu.contains("Attack","Yt-HurKot (level: 130)") 
				&& ctx.menu.click(new Filter<Entry>(){
					@Override
					public boolean accept(Entry arg0) {
						return arg0.action.equals("Attack") && arg0.option.equals("Yt-HurKot (level: 130)");
					}}))
			return true;
		else{
			Point p = ctx.mouse.getLocation();
			ctx.mouse.move(new Point(p.x+150,p.y+26));
			return false;
		}
	}
	
	private void resetTimer(Timer timer, long newEnd){
		timer = new Timer(0);
		timer.setEndIn(newEnd);
	}
}
