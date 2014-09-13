package miscfirecaper.nodes;

import java.awt.Point;
import java.util.List;

import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Npcs;
import org.powerbot.script.methods.Menu.Entry;
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

public class HandleWave extends Task{

	private MethodContext ctx;
	
	public HandleWave(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.handleWave;
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
				sleep(1500);
			}
			if(ctx.backpack.select().count() > 26){
				System.out.println("make inventory room");
				Consumables.eatFood(ctx, MiscFireCaper.foodId);
				sleep(1500);
			}
		}

		//if(ctx.camera.getPitch() > 5)
		//	ctx.camera.setPitch(3);

		if(!ctx.players.local().isInCombat() && AbilitySetup.Ability.Regenerate.isReady(ctx)){
			AbilitySetup.Ability.Regenerate.use(ctx);
			sleep(500);
		}

		List<Npc> monsters = Monster.getMonsters(ctx);
		List<Npc> southwestZoneEnemies = Monster.getInArea(ctx, MiscFireCaper.southwestZone);
		List<Npc> stuckZoneEnemies = Monster.getInArea(ctx, MiscFireCaper.stuckZone);
		List<Npc> stuckAreaEnemies = Monster.getInArea(ctx, MiscFireCaper.stuckArea);
		List<Npc> northwestZoneEnemies = Monster.getInArea(ctx, MiscFireCaper.northwestZone);
		List<Npc> southeastZoneEnemies = Monster.getInArea(ctx, MiscFireCaper.southeastZone);

		//checkEmpty(monsters, southwestZoneEnemies, stuckZoneEnemies, stuckAreaEnemies, 
			//	northwestZoneEnemies, southeastZoneEnemies);

		if(monsters.size() == 0){
			System.out.println("no monsters");
			Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			sleep(3000);
			MiscFireCaper.handleWave = false;
			MiscFireCaper.prepWave = true;
			return;
		}

		Npc target = Wave.getNextNpc(ctx, southwestZoneEnemies, stuckZoneEnemies, northwestZoneEnemies, southeastZoneEnemies);
		if(target == null){
			MiscFireCaper.currentNpc = "?";
			Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			sleep(3000);
			return;
		}
		else{
			Monster m = Monster.getMonster(target);
			if(m != null)
				MiscFireCaper.currentNpc = m.toString();
		}
		//MiscFireCaper.currentNpc = toFight.getName()+"("+toFight.getLevel()+")";


		Npc bigMeleer = Monster.getAggressor(ctx, Monster.BIG_MELEER);
		if(bigMeleer != null
				&& bigMeleer.getLocation().distanceTo(ctx.players.local()) < 4){
			System.out.println("too close to big meleer");
			if(Monster.contains(southwestZoneEnemies, Monster.BIG_MELEER)){
				if(Monster.contains(stuckZoneEnemies, Monster.BIG_MELEER)
						||Monster.contains(southeastZoneEnemies, Monster.BIG_MELEER)){
					Util.stepTowards(MiscFireCaper.northTile, ctx);
					sleep(3000);
					return;
				}
				else if(Monster.contains(northwestZoneEnemies, Monster.BIG_MELEER)){
					Util.stepTowards(MiscFireCaper.secondarySpot, ctx);
					sleep(3000);
					return;
				}
			}
			if(Monster.contains(northwestZoneEnemies, Monster.BIG_MELEER)){
				Util.stepTowards(MiscFireCaper.southeastTile, ctx);
			}
			else if(Monster.contains(stuckZoneEnemies, Monster.BIG_MELEER)){
				Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			}
			else if(Monster.contains(southwestZoneEnemies, Monster.BIG_MELEER)){
				Util.stepTowards(MiscFireCaper.secondarySpot, ctx);
			}
			else if(Monster.contains(southeastZoneEnemies, Monster.BIG_MELEER)){
				Util.stepTowards(MiscFireCaper.safeSpot, ctx);
			}
			sleep(3000);
			return;
		}

		if(MiscFireCaper.northwestZone.contains(target) 
				&& ctx.players.local().getLocation().getX() < MiscFireCaper.southeastTile.getX()){
			Util.stepTowards(MiscFireCaper.southeastTile, ctx);
			sleep(4000);
		}
		//		if(stuckArea.contains(toFight)
		//				&& ctx.players.local().getLocation().equals(safeSpot)){
		//			ctx.movement.stepTowards(safeSpot.derive(0, -3));
		//			sleep(2000);
		//		}

		if(target.getAnimation() == Monster.getDeathAnimation(target))
			return;
		ctx.camera.turnTo(target);
		fightNpc(target);
	}

	private void fightNpc(final Npc target){
		if(target == null)
			return;
		ctx.camera.turnTo(target);
		if(!playerAttacking(target)){
			if(attackNpc(target)){
				MiscFireCaper.waitFor(new Condition(){
					@Override
					public boolean accept() {
						if(ctx.players.local().getInteracting() == null)
							return false;
						return ctx.players.local().getInteracting().equals(target);
					}
				}, 3000);
			}
			else
				return;
		}
		AbilitySetup.useRotation(ctx,target);
	}

	private boolean attackNpc(final Npc npc){
		if(npc.click(false)){
			for(int i = 0; i < 30 && !ctx.menu.isOpen(); i++){
				sleep(20);
			}
			sleep(100);
			//&& ctx.menu.contains("Attack",npc.getName()+" (level: "+npc.getLevel()+")")
			if(ctx.menu.isOpen() 
					&& ctx.menu.click(new Filter<Entry>(){
						@Override
						public boolean accept(Entry arg0) {
							return arg0.action.equals("Attack") && arg0.option.equals(""+npc.getName()+" (level: "+npc.getLevel()+")");
						}}))
				return true;
			
		}
		else{
			Point p = ctx.mouse.getLocation();
			ctx.mouse.move(new Point(p.x+150,p.y+26));
		}
		if(npc.click(true)){
			return true;
		}
		else{
			//should only happen when duplicate npcs are directly on top of each other
			Npc dupe = getDuplicate(npc);
			if(dupe == null)
				return false;
			if(dupe.click(false))
				sleep(400);
			// && ctx.menu.contains("Attack",npc.getName()+" (level: "+npc.getLevel()+")")
			if(ctx.menu.isOpen())
				return(	ctx.menu.click(new Filter<Entry>(){
					@Override
					public boolean accept(Entry arg0) {
						return arg0.action.equals("Attack") && arg0.option.equals(""+npc.getName()+" (level: "+npc.getLevel()+")");
					}}));
		
		}
		return false;
	}


	private boolean playerAttacking(Npc npc){
		if(npc == null)
			return false;
		return (ctx.players.local().getInteracting() != null
				&& ctx.players.local().getInteracting().equals(npc));
	}

	private Npc getDuplicate(final Npc npc){
		BasicNamedQuery<Npc> qry = ctx.npcs.select().select(new Filter<Npc>(){

			@Override
			public boolean accept(Npc n) {
				return(n.getLevel() == npc.getLevel() && n.isOnScreen());
			}
		});
		for(Npc n : qry){
			return n;
		}
		return null;
	}

	public void checkEmpty(Npc[]... ids){
		for(Npc[] i : ids){
			if(i == null)
				i = new Npc[]{};
		}
	}
}
