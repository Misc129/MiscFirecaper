package miscfirecaper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.powerbot.client.Node;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.event.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Area;
import org.powerbot.script.wrappers.Tile;

import miscfirecaper.nodes.BetweenWave;
import miscfirecaper.nodes.Eat;
import miscfirecaper.nodes.EnterCave;
import miscfirecaper.nodes.Finish;
import miscfirecaper.nodes.HandleJad;
import miscfirecaper.nodes.HandleWave;
import miscfirecaper.nodes.PrayerRestore;
import miscfirecaper.nodes.Setup;
import miscfirecaper.util.Condition;
import miscfirecaper.util.ProtectionPrayer;
import miscfirecaper.util.Task;

@org.powerbot.script.Manifest(name = "MiscFireCaper", description = "Gets you a fire cape.", version = 2.1, authors = { "Misc" })
public class MiscFireCaper extends PollingScript implements PaintListener, MessageListener{
	/*
	 * TODO
	 * 
	 * 
	 * 
	 * BUGS
	 */

	public static Tile safeSpot;
	public static Tile secondarySpot;
	public static Tile northTile;
	public static Tile southeastTile;
	public static Tile jadTile;

	public static Tile currentSpot;

	public static Area stuckArea;
	public static Area stuckZone;
	public static Area southwestZone;
	public static Area southeastZone;
	public static Area northwestZone;

	public static final Tile ENTRANCE_TILE = new Tile(4611,5129,0);

	public static final int PURPLE_SWEETS_ID = 4561;
	public static final int CAVE_ENTRANCE_ID = 68444;
	public static final int VIAL_ID = 229;
	public static final int FIRE_CAPE_ID = 6570;

	public static final int[] SARADOMIN_BREWS = {6691,6689,6687,6685};
	public static final int[] SARADOMIN_FLASKS = {23361,23359,23357,23355,23353,23351};
	public static final int[] SUPER_RESTORE_POTIONS = {3030,3028,3026,3024};
	public static final int[] SUPER_RESTORE_FLASKS = {23409,23407,23405,23403,23401,23399};
	public static final int[] PRAYER_POTIONS = {143,141,139,2434};
	public static final int[] PRAYER_FLASKS = {23253,23251,23249,23247,23245,23243};
	public static final int[] RANGING_POTIONS = {173,171,169,2444};
	public static final int[] RANGING_FLASKS = {23313,23311,23309,23307,23305,23303};
	public static final int[] EXTREME_RANGING_POTIONS = {15327,15326,15325,15324};
	public static final int[] EXTREME_RANGING_FLASKS = {23524,23523,23522,23521,23520,23519};
	public static final int[] MAGIC_POTIONS = {3046,3044,3042,3040};
	public static final int[] MAGIC_FLASKS = {23433,23431,23429,23427,23425,23423};
	public static final int[] EXTREME_MAGIC_POTIONS = {15323,15322,15321,15320};
	public static final int[] EXTREME_MAGIC_FLASKS = {23518,23517,23516,23515,23514,23513};
	public static final int[] PRAYER_RENEWAL_POTIONS = {21636,21634,21632,21630};
	public static final int[] PRAYER_RENEWAL_FLASKS = {23619,23617,23615,23613,23611,23609};
	public static final int[] OVERLOADS = {};
	public static final int[] OVERLOAD_FLASKS = {23536,23535,23534,23533,23532,23531};

	public static int currentWave;

	public static long startTime;
	long millis;
	long hours;
	long minutes;
	long seconds;
	long last;

	public static int foodId, eatPercent, eatHealth, prayerBoostPoint, jadHp;

	public static String currentNpc, errorMsg, currentAttack = "?";

	public static Tile paintTile;
	public static Area paintArea;
	public static Area[] paintAreas;

	public static boolean scriptRunning, error, boostRange, boostMage, prayRenewed, enterCave, prepWave,
	handleWave, handleJad, finish, busy, start, guiDone, doPaint, dataSubmitted;

	public static Timer waitToAttack, tillNextPot, jadAttacked, freeTime;

	public static ProtectionPrayer currentPrayer = ProtectionPrayer.MAGIC;

	private static List<Node> jobList = Collections.synchronizedList(new ArrayList<Node>());

	private List<Task> taskList; 

	public MiscFireCaper(){

		this.getExecQueue(State.START).offer(new Runnable(){
			@Override
			public void run(){
				Setup setupTask = new Setup(ctx);
				setupTask.execute();
				taskList = new ArrayList<Task>();
				taskList.add(new Eat(ctx));
				taskList.add(new PrayerRestore(ctx));
				taskList.add(new BetweenWave(ctx));
				taskList.add(new HandleWave(ctx));
				taskList.add(new HandleJad(ctx));
				taskList.add(new Finish(ctx));
				taskList.add(new EnterCave(ctx));
			}
		});
	}


	@Override
	public int poll() {

		for(Task t : taskList){
			if(t.validate()){
				t.execute();
			}
		}
		return 50;
	}

	private void formatTime(){
		millis = System.currentTimeMillis() - startTime;
		hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		seconds = millis / 1000;
	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}

	private void paintMouse(Graphics2D g2d) {

		int mx = ctx.mouse.getLocation().x;
		int my = ctx.mouse.getLocation().y;
		g2d.drawOval(mx - 5, my - 5, 10, 10);
		g2d.drawOval(mx - 3, my - 3, 6, 6);
		g2d.drawLine(mx, my - 5, mx, my + 5);
		g2d.drawLine(mx - 5, my, mx + 5, my);
	}

	public static void waitFor(Condition condition, int timeout){
		for(int i = 0; i < timeout; i += 200){
			if(condition.accept())
				return;
		}
	}

	@Override
	public void messaged(MessageEvent m) {
		if(m.getMessage().contains("Your prayer renewal has ended.")){
			prayRenewed = false;
		}
	}

	public void sleep(int mili){
		ctx.game.sleep(mili);
	}

	@Override
	public void repaint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 50);
		if(error){
			g.setColor(Color.red);
			g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,30));
			g.drawString(errorMsg,20,250);
		}
		if(doPaint){
			if(!handleJad){
				formatTime();
				String monsters = "";
				monsters = Wave.getMonstersString(currentWave);
				g.setColor(Color.RED);
				paintMouse(g);
				g.setComposite(makeComposite(.5f));
				g.fillRoundRect(5, 5, 75, 20,20,20);
				g.fillRoundRect(5, 28, currentNpc.equals("?") ? 75:75+currentNpc.length() * 8, 20,20,20);
				g.fillRoundRect(85, 5, 90 + (monsters.length() * 8), 20,20,20);//205,13
				g.setComposite(makeComposite(1f));

				g.setColor(Color.BLACK);
				g.setFont(new Font("Times New Roman",Font.BOLD,14));
				g.drawString("Wave: "+currentWave, 10, 19);
				g.drawString("Target: "+currentNpc, 10, 43);
				g.drawString("Monsters: "+monsters, 90, 19);
			}
			else if(currentAttack != null){
				g.setComposite(makeComposite(.5f));
				g.setColor(Color.red);
				g.fillRoundRect(5, 5, 200, 45,20,20);
				g.setColor(Color.black);
				g.setComposite(makeComposite(1f));
				g.setFont(new Font(Font.SERIF, Font.BOLD, 38));
				g.drawString("Tz Tok-Jad", 10, 40);
				double drawDecimal = (100 - jadHp) / 100.0;
				g.setColor(Color.green);
				g.fillRect(400, 22, 200, 11);//
				g.setColor(Color.RED);
				g.fillRect(600 - (int)(200 * drawDecimal), 22, (int)(200 * drawDecimal), 11);

				g.setComposite(makeComposite(1f));
				if(currentAttack.equals("Magic"))
					g.setColor(Color.CYAN);
				if(currentAttack.equals("?"))
					g.setColor(Color.WHITE);
				if(currentAttack.equals("Ranged"))
					g.setColor(Color.GREEN);
				g.fillRoundRect(240, 5, 100, 45,20,20);//505

				g.setColor(Color.BLACK);
				g.setFont(new Font(Font.SERIF, Font.BOLD, 22));
				if(currentAttack.equals("Magic"))
					g.drawString("MAGIC", 252, 36);//517
				if(currentAttack.equals("Ranged"))
					g.drawString("RANGE", 250, 36);//515

			}
		}
	}








}
