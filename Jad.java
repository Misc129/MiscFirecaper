package miscfirecaper;

import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Tile;

public class Jad {
	//TODO
		//if healer gets stuck behind jad,
	/*
	 * if(jad.getX is between playerX and healerX (same for y) and distance > 12?
	 * then healer is on the other side of jad, and we need to shift 90 degrees
	 */
	public static final int JAD_ID = 2745;
	public static final int HEALER_ID = 2746;
	
	public static final int RANGED_ANIMATION = 16202;
	public static final int MAGE_ANIMATION = 16195;
	public static final int MELEE_ANIMATION = 0;

	public static Npc getJad(MethodContext ctx){
		return ctx.npcs.nearest().id(JAD_ID).iterator().next();
	}

	public static Npc getNextHealer(final MethodContext ctx, final Npc jad){
		BasicNamedQuery<Npc> qry = ctx.npcs.select(new Filter<Npc>(){
			@Override
			public boolean accept(Npc npc) {
				return (npc.getId() == HEALER_ID) 
						&& !Monster.isAfterMe(ctx, npc)
						&& npc.getLocation().distanceTo(jad) < 10;
			}
		});
		return qry.iterator().next();
	}

	public static Tile getFightTile(Npc jad){
		Tile jLoc = jad.getLocation();
		Tile result = new Tile(jLoc.getX()+13, jLoc.getY()-9, 0);
		//if(result.canReach())
			return result;
//		result = new Tile(jLoc.getX()+13, jLoc.getY()+9, 0);
//		if(result.canReach())
//			return result;
//		result = new Tile(jLoc.getX()+11, jLoc.getY(), 0);
//		if(result.canReach())
//			return result;
//		return null;
	}
	
	public static boolean isRanging(Npc jad){
		if(jad == null) return false;
		return jad.getAnimation() == RANGED_ANIMATION;
	}

	public static boolean isMaging(Npc jad){
		if(jad == null) return false;
		return jad.getAnimation() == MAGE_ANIMATION;
	}

	public static boolean hasHealers(final MethodContext ctx, final org.powerbot.script.wrappers.Npc jad){
		BasicNamedQuery<Npc> qry = ctx.npcs.select(new Filter<Npc>(){

			@Override
			public boolean accept(Npc n) {
				return n.getId() == HEALER_ID
						&& jad.getLocation().distanceTo(ctx.players.local()) < 7;
			}
		});
		return qry.iterator().next() != null;
	}
	
//	public static boolean isCorrectPrayerOn(final Npc jad){
//		boolean jadMaging = jad.getAnimation() == MAGE_ANIMATION;
//		boolean jadRanging = jad.getAnimation() == RANGED_ANIMATION;
//		ProtectionPrayer currentPrayer = ProtectionPrayer.getProtect();
//		if(jadMaging)
//			return currentPrayer == Prayer.Protect.MAGIC;
//		else if(jadRanging)
//			return currentPrayer == Prayer.Protect.RANGED;
//		else
//			return false;
//			
//	}
}
