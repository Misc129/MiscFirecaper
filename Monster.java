package miscfirecaper;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.BasicNamedQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Npcs;
import org.powerbot.script.wrappers.Actor;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;
public enum Monster{
	BIRD(new int[]{2734,2735}, 16089, 16109),
	MELEER(new int[]{2736,2737}, 16163, 16164),
	MELEER_SPLIT(new int[]{2738}, 16167,16168),
	RANGER(new int[]{2739,2740}, 16132, 16110),
	BIG_MELEER(new int[]{2741,2742}, 16102, 16103),
	MAGER(new int[]{2743,2744}, 16136, 16111);
	
	public static Monster[] priorityOrder = {BIG_MELEER,BIRD,MELEER_SPLIT,MELEER,MAGER,RANGER};
	
	private int[] _ids;
	
	private int _attackAnimation;
	private int _deathAnimation;
	
	Monster(int[] ids, int attackAnimation, int deathAnimation){
		_ids = ids;
		_attackAnimation = attackAnimation;
		_deathAnimation = deathAnimation;
	}
	
	public boolean matchesWith(int id){
		for(int i : _ids){
			if(id == i)
				return true;
		}
		return false;
	}
	
	public int[] getIds(){
		return _ids;
	}
	
	public int getAttackAnimation(){
		return _attackAnimation;
	}
	
	public int getDeathAnimation(){
		return _deathAnimation;
	}
	
	public static int getDeathAnimation(Npc n){
		if(n == null)
			return 0;
		Monster m = getMonster(n);
		if(m == null)
			return 0;
		return m.getDeathAnimation();
	}
	
	public static Monster getMonster(Npc npc){
		if(npc == null) return null;
		for(Monster m : Monster.values()){
			if(m.matchesWith(npc.getId()))
				return m;
		}
		return null;
	}
	
	public static Actor getInteracting(Player p){
		return p.getInteracting();
	}

	public static Npc getAggressor(MethodContext ctx, final Monster monster){
		//return ctx.npcs.select().id(monster.getIds()).iterator().next();
		for(Npc n:ctx.npcs.select().id(monster.getIds())){
			return n;
		}
		return null;
	}

	public static List<Npc> getMonsters(MethodContext ctx){
		ArrayList<Npc> result = new ArrayList<Npc>();
		BasicNamedQuery<Npc> qry = ctx.npcs.select().select().select(new Filter<Npc>(){
			@Override
			public boolean accept(Npc npc) {
				Monster m = getMonster(npc);
				return (m != null);
			}});
		for(Npc n : qry)
			result.add(n);
		return result;
	}
	
	public static Npc getNextInRadius(final MethodContext ctx, final double radius){
		BasicNamedQuery<Npc> qry = ctx.npcs.select().select().select(new Filter<Npc>(){

			@Override
			public boolean accept(Npc n) {
				return n.getLocation().distanceTo(ctx.players.local()) <= radius;
			}});
		for(Npc n : qry){
			return n;
		}
		return null;
	}
	
	public static boolean contains(List<Npc> npcs, Monster... monsters){
		if(npcs == null || monsters == null)
			return false;
		for(Npc npc : npcs){
			if(npc == null)
				continue;
			for(Monster m : monsters){
				if(m.matchesWith(npc.getId()))
					return true;
			}
		}
		return false;
	}
	
	public static boolean playerIsAttacking(MethodContext ctx, Npc npc){
		Npc attacking = (Npc)ctx.players.local().getInteracting();
		if(attacking == null) return false;
		return attacking.getId() == npc.getId();
	}
	
	public static boolean isAfterMe(MethodContext ctx, Npc npc){
		Actor attacking = ctx.players.local().getInteracting();
		if(attacking == null) return false;
		return attacking == ctx.players.local() || attacking.equals(ctx.players.local());
	}
	
	public static boolean isAttackingMe(Npc npc){
		Monster e = getMonster(npc);
		if(e == null) return false;
		return npc.getAnimation() == e.getAttackAnimation();
	}
	
	public static BasicNamedQuery<Npc> getInArea(final Area a, MethodContext ctx){
		BasicNamedQuery<Npc> result = ctx.npcs.select().select(new Filter<Npc>(){
			@Override
			public boolean accept(Npc n) {
				return(a.contains(n) && n.getAnimation() != getDeathAnimation(n));
			}});
		return result;
	}
	
	public static List<Npc> getInArea(MethodContext ctx, final Area... areas){
		ArrayList<Npc> result = new ArrayList<Npc>();
		BasicNamedQuery<Npc> qry = ctx.npcs.select().select(new Filter<Npc>(){
			@Override
			public boolean accept(Npc n) {
				for(Area a : areas){
					if(a.contains(n) && n.getAnimation() != getDeathAnimation(n))
						return true;
				}
				return false;
			}});
		for(Npc n : qry)
			result.add(n);
		return result;
	}
	
	public static Npc getNearest(MethodContext ctx, final List<Npc> npcs){
		if(npcs == null || npcs.size() == 0) return null;
		Npc result = npcs.get(0);
		for(Npc n : npcs){
			if(n.getLocation().distanceTo(ctx.players.local()) < result.getLocation().distanceTo(ctx.players.local()))
				result = n;
		}
		return result;
	}
	
	public static Npc getNearest(MethodContext ctx, int[] ids) {
		if(ids == null || ids.length == 0) return null;
		for(Npc n : ctx.npcs.select().nearest().id(ids))
			return n;
		return null;
	}
	
	public static Npc getSouthwestMost(final List<Npc> npcs){
		if(npcs == null || npcs.size() == 0) return null;
		Npc result = npcs.get(0);
		for(Npc n : npcs){
			Tile resultLoc = result.getLocation();
			Tile nLoc = n.getLocation();
			if(nLoc.getX() < resultLoc.getX() || nLoc.getY() < resultLoc.getY())
				result = n;
		}
		return result;
	}
	
	public static Npc getWestMostPriority(final List<Npc> npcs){
		if(npcs == null || npcs.size() == 0) 
			return null;
		Npc result = null;
		for(Npc n : npcs){
			for(Monster m : priorityOrder){
				if(m.matchesWith(n.getId())){
					result = n;
				}
			}
		}
		if(result == null)
			return null;
		Tile t = result.getLocation();
		for(Npc n : npcs){
			if(n.getId() == result.getId()
					&& n.getLocation().getX() > t.getX())
				result = n;
		}
		return result;
	}
	
	public static Npc getWestMost(final List<Npc> npcs){
		if(npcs == null || npcs.size() == 0)
			return null;
		Npc result = npcs.get(0);
		for(Npc n : npcs){
			if(n.getLocation().getX() > result.getLocation().getX())
				result = n;
		}
		return result;
	}
	
	public static Npc getPriority(MethodContext ctx, final List<Npc> npcs){
		if(npcs == null || npcs.size() == 0) 
			return null;
		for(Npc npc1 : npcs){
			for(Monster m : priorityOrder){
				if(m.matchesWith(npc1.getId())){
					for(Npc npc2 : ctx.npcs.select().nearest().id(npc1.getId()))
						return npc2;
				}
			}
		}
		return null;
	}

}
