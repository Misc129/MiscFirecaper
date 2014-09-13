package miscfirecaper.nodes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import miscfirecaper.MiscFireCaper;
import miscfirecaper.util.Task;

import org.powerbot.script.methods.Environment;
import org.powerbot.script.methods.MethodContext;

public class Finish extends Task{

	private MethodContext ctx;
	
	public Finish(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.finish;
	}

	@Override
	public void execute() {
		if(MiscFireCaper.ENTRANCE_TILE.getLocation().distanceTo(ctx.players.local()) > 5)
			ctx.movement.stepTowards(MiscFireCaper.ENTRANCE_TILE);
		//Tabs.INVENTORY.open();
		if(ctx.backpack.select().id(MiscFireCaper.FIRE_CAPE_ID).count() > 0 && !MiscFireCaper.dataSubmitted){
			submitData(Environment.getDisplayName());
			MiscFireCaper.dataSubmitted = true;
		}
	}

	private static void submitData(String username){
		try {
			//http://miscbots.freezoy.com/updater_fc.php?username=Misc&firecapes=1
			URL submit = new URL("http://miscbots.freezoy.com/updater_fc.php?" +
					"username="+username
					+"&firecapes="+1);
			URLConnection con = submit.openConnection();
			con.setRequestProperty("User-Agent", "| customuseragent |");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			final BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			rd.close();
			System.out.println("data submitted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
