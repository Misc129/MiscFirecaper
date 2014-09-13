package miscfirecaper;



import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Npcs;
import org.powerbot.script.methods.Settings;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Tile;

import miscfirecaper.util.ProtectionPrayer;

public class Wave {
	private static final int NON_MAGER_WAVE = 0;
	private static final int MAGER_WAVE = 1;
	private static final int PILLAR_ID = 68406;
	public static final int WAVE_SETTING = 3008;
	public static final int BANKER_ID = 2617;
	
	public static final Area TZHAAR_AREA = new Area(new Tile(4606,5181,0),new Tile(4748,5063,0));

	private static final String[] M = {
		"BIRD, ", "MELEER, ", "RANGER, ", "BIG_MELEER, ", "MAGER, "
	};
	private static final String[] MONSTERS_INDEXED = {
		"n/a  ",
		M[0], 
		"2x"+M[0],
		M[1], 
		M[1]+M[0],
		M[1]+"2x"+M[0], 
		"2x"+M[1], 
		M[2],
		M[2]+M[0], 
		M[2]+"2x"+M[0],
		M[2]+M[1], 
		M[2]+M[1]+M[0],
		M[2]+M[1]+"2x"+M[0], 
		M[2]+"2x"+M[1], 
		"2x"+M[2],
		M[3],
		M[3]+M[0], 
		M[3]+"2x"+M[0],
		M[3]+M[1], 
		M[3]+M[1]+M[0],
		M[3]+M[1]+"2x"+M[0], 
		M[3]+"2x"+M[1], 
		M[3]+M[2],
		M[3]+M[2]+M[0], 
		M[3]+M[2]+"2x"+M[0],
		M[3]+M[2]+M[1], 
		M[3]+M[2]+M[1]+M[0],
		M[3]+M[2]+M[1]+"2x"+M[0], 
		M[3]+M[2]+"2x"+M[1], 
		M[3]+"2x"+M[2],
		"2x"+M[3],
		M[4],
		M[4]+M[0], 
		M[4]+"2x"+M[0],
		M[4]+M[1], 
		M[4]+M[1]+M[0],
		M[4]+M[1]+"2x"+M[0], 
		M[4]+"2x"+M[1], 
		M[4]+M[2],
		M[4]+M[2]+M[0], 
		M[4]+M[2]+"2x"+M[0],
		M[4]+M[2]+M[1], 
		M[4]+M[2]+M[1]+M[0],
		M[4]+M[2]+M[1]+"2x"+M[0], 
		M[4]+M[2]+"2x"+M[1], 
		M[4]+"2x"+M[2],
		M[4]+M[3],
		M[4]+M[3]+M[0], 
		M[4]+M[3]+"2x"+M[0],
		M[4]+M[3]+M[1], 
		M[4]+M[3]+M[1]+M[0],
		M[4]+M[3]+M[1]+"2x"+M[0], 
		M[4]+M[3]+"2x"+M[1], 
		M[4]+M[3]+M[2],
		M[4]+M[3]+M[2]+M[0], 
		M[4]+M[3]+M[2]+"2x"+M[0],
		M[4]+M[3]+M[2]+M[1], 
		M[4]+M[3]+M[2]+M[1]+M[0],
		M[4]+M[3]+M[2]+M[1]+"2x"+M[0], 
		M[4]+M[3]+M[2]+"2x"+M[1], 
		M[4]+M[3]+"2x"+M[2],
		M[4]+"2x"+M[3],
		"2x"+M[4],
		"JAD  "
	};
	public static int getWave(MethodContext ctx){
		return ctx.settings.get(WAVE_SETTING);
	}
	
	public static String getMonstersString(int wave){
		if(wave < 0 || wave > 63)
			return "n/a";
		String result = MONSTERS_INDEXED[wave];
		return result.substring(0, result.length()-2);
	}

	//get monsters within 3-4 yards, if none, att ranger
	public static Npc getNextNpc(MethodContext ctx, List<Npc> incoming, List<Npc> stuckZoneEnemies, List<Npc> northwestZoneEnemies, List<Npc> southeastZoneEnemies){
		Npc bird = Monster.getNearest(ctx, Monster.BIRD.getIds());
		if(bird != null && bird.getLocation().distanceTo(ctx.players.local()) < 3)
			return bird;
		Npc closeNpc = Monster.getNextInRadius(ctx, 1.5);
		if(closeNpc != null){
			return closeNpc;
		}
		Npc ranger = Monster.getAggressor(ctx, Monster.RANGER);
		if(ranger != null && !MiscFireCaper.northwestZone.contains(ranger)){
			return ranger;
		}
		if(stuckZoneEnemies != null && stuckZoneEnemies.size() > 0){
			return Monster.getSouthwestMost(Monster.getInArea(ctx, MiscFireCaper.stuckArea));
		}
		if(incoming != null && incoming.size() > 0){
			if(Monster.contains(incoming, Monster.BIG_MELEER))
				return Monster.getWestMost(incoming);
			return Monster.getWestMostPriority(incoming);
		}
		if(southeastZoneEnemies != null && southeastZoneEnemies.size() > 0){
			return Monster.getPriority(ctx, southeastZoneEnemies);
		}
		if(northwestZoneEnemies != null && northwestZoneEnemies.size() > 0){
			return Monster.getNearest(ctx, northwestZoneEnemies);
		}
		return null;
	}
	
	private static GameObject getNode(MethodContext ctx){
        ArrayList<GameObject> nodes = new ArrayList<GameObject>();
		BasicNamedQuery<GameObject> qry = ctx.objects.select(new Filter<GameObject>(){
			@Override
			public boolean accept(GameObject o) {
				return(o.getId() == 68240);
			}});
		for(GameObject go : qry)
			nodes.add(go);
		GameObject result = nodes.get(0);
		for(GameObject go : nodes){
			Tile loc = go.getLocation();
			Tile resultLoc = result.getLocation();
			if(loc.getY() < resultLoc.getY())
				result = go;
		}
		return result;
	}
	
	public static GameObject getPillarNode(MethodContext ctx){
		ArrayList<GameObject> nodes = new ArrayList<GameObject>();
		BasicNamedQuery<GameObject> qry = ctx.objects.select().id(PILLAR_ID);
		for(GameObject go : qry)
			nodes.add(go);
		GameObject result = nodes.get(0);
		for(GameObject go : nodes){
			Tile loc = go.getLocation();
			Tile resultLoc = result.getLocation();
			if(loc.getX() < resultLoc.getX())
				result = go;
		}
		return result;
	}
	
	public static void constructArea(MethodContext ctx){
		//GameObject node = getNode(ctx);
		//int nx = node.getLocation().getX();
		//int ny = node.getLocation().getY();
		GameObject pillarNode = getPillarNode(ctx);
		
		int pnx = pillarNode.getLocation().getX()+35;
		int pny = pillarNode.getLocation().getY()-21;
		MiscFireCaper.stuckZone = new Area(new Tile(pnx-25,pny+22,0),new Tile(pnx+17,pny+17,0));
		MiscFireCaper.stuckArea = new Area(new Tile(pnx-25,pny+22,0),new Tile(pnx-18,pny+17,0));
		MiscFireCaper.southwestZone = new Area(new Tile(pnx-39,pny+20,0),new Tile(pnx-25,pny-4,0));
		MiscFireCaper.southeastZone = new Area(new Tile(pnx-25,pny+17,0),new Tile(pnx+17,pny-4,0));
		MiscFireCaper.northwestZone = new Area(new Tile(pnx-38,pny+47,0),new Tile(pnx-20,pny+22,0));
		MiscFireCaper.safeSpot = new Tile(pnx-34,pny+19,0);//y+14
		MiscFireCaper.southeastTile = new Tile(pnx-27,pny+17,0);
		MiscFireCaper.secondarySpot = new Tile(pnx-25,pny+19,0);
		MiscFireCaper.northTile = new Tile(pnx-34,pny+33,0);
		MiscFireCaper.currentSpot = MiscFireCaper.safeSpot;
		MiscFireCaper.jadTile = new Tile(pnx-20,pny+10,0);
		System.out.println(MiscFireCaper.safeSpot);
	}
	
	public static ProtectionPrayer getCorrectPrayer(MethodContext ctx, List<Npc> incoming, List<Npc> stuckZoneEnemies, List<Npc> northwestZoneEnemies){
		int toSwitch = -1;
		if(getWave(ctx) < 31)
			toSwitch = NON_MAGER_WAVE;
		else
			toSwitch = MAGER_WAVE;
		switch(toSwitch){
		case NON_MAGER_WAVE:{
			if(incoming != null && incoming.size() > 0){
				if(Monster.contains(incoming, Monster.BIG_MELEER)){
					return ProtectionPrayer.MELEE;
				}
				else if(Monster.contains(incoming, Monster.RANGER)){
					return ProtectionPrayer.RANGED;
				}
			}
			//TODO if SE contains 90, prot range
			if(stuckZoneEnemies != null && stuckZoneEnemies.size() > 0){
				if(Monster.contains(stuckZoneEnemies, Monster.RANGER)){
					return ProtectionPrayer.RANGED;
				}
			}
			if(northwestZoneEnemies != null && northwestZoneEnemies.size() > 0){
				if(Monster.contains(northwestZoneEnemies, Monster.RANGER)
						&& incoming.size() == 0
						&& stuckZoneEnemies.size() == 0){
					return ProtectionPrayer.RANGED;
				}
			}
			return null;
		}
		case MAGER_WAVE:{
			if(Monster.contains(northwestZoneEnemies, Monster.MAGER) && 
					ctx.players.local().getLocation().getX() < MiscFireCaper.southeastTile.getX()){
				//TODO should be ok to use ranged prayer if stuckzone contains a 90
				if(Monster.contains(incoming, Monster.RANGER))
					return ProtectionPrayer.RANGED;
				if(Monster.contains(incoming, Monster.BIG_MELEER))
					return ProtectionPrayer.MELEE;
				if(Monster.contains(stuckZoneEnemies, Monster.RANGER))
					return ProtectionPrayer.RANGED;
			}
			Npc bigMeleer = Monster.getAggressor(ctx, Monster.BIG_MELEER);
			if(Monster.contains(stuckZoneEnemies, Monster.MAGER)
					&& ctx.players.local().getLocation().equals(MiscFireCaper.safeSpot)
					&& bigMeleer != null
					&& bigMeleer.getLocation().distanceTo(ctx.players.local()) < 6){
				return ProtectionPrayer.MELEE;
			}
			return ProtectionPrayer.MAGIC;
		}
		default:{
			return null;
		}
		}
	}
	
	public static boolean outsideCave(MethodContext ctx){
		return TZHAAR_AREA.contains(ctx.players.local());
	}
}
