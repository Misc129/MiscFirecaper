package miscfirecaper.util;

import java.util.ArrayList;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Tile;

public class Util {

	public static void stepTowards(Tile t, MethodContext ctx){
		Tile winner = null;

		for(Tile tile : getSurroundingTiles(ctx)){
			if(winner == null || tile.distanceTo(t) < winner.distanceTo(t))
				winner = tile;
		}
		ctx.movement.stepTowards(winner);
	}

	public static ArrayList<Tile> getSurroundingTiles(MethodContext ctx){
		ArrayList<Tile> l = new ArrayList<Tile>();

		int xTiles = ctx.widgets.get(1465, 12).getScrollWidth()/10;
		int yTiles = ctx.widgets.get(1465, 12).getScrollHeight()/10;
		int myX = ctx.players.local().getLocation().getX();
		int myY = ctx.players.local().getLocation().getY();
		int myPlane = ctx.players.local().getLocation().getPlane();

		for(int i = 0; i < xTiles; i++)
			for(int j = 0; j < yTiles; j++){
				if(new Tile(myX - i, myY - j, myPlane).getMatrix(ctx).isOnMap()){
					l.add(new Tile(myX - i, myY - j, myPlane));
				}
				if(new Tile(myX + i, myY - j, myPlane).getMatrix(ctx).isOnMap()){
					l.add(new Tile(myX + i, myY - j, myPlane));
				}
				if(new Tile(myX - i, myY + j, myPlane).getMatrix(ctx).isOnMap()){
					l.add(new Tile(myX - i, myY + j, myPlane));
				}
				if(new Tile(myX + i, myY + j, myPlane).getMatrix(ctx).isOnMap() ){
					l.add(new Tile(myX + i, myY + j, myPlane));
				}
			}
		return l;
	}

	
	public static double distanceTo(MethodContext ctx, Tile t){
		return t.distanceTo(ctx.players.local());
	}
}
