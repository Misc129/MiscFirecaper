package miscfirecaper.nodes;

import miscfirecaper.MiscFireCaper;
import miscfirecaper.Wave;
import miscfirecaper.util.Task;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.GameObject;

public class EnterCave extends Task{

	private MethodContext ctx;
	
	public EnterCave(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}
	
	@Override
	public void execute() {
		if(!Wave.outsideCave(ctx)){
			sleep(3000);
			Wave.constructArea(ctx);
			MiscFireCaper.prepWave = true;
			MiscFireCaper.enterCave = false;
			return;
		}
//		if(ctx.widgets.get(1184).isValid()){
//			ctx.widgets.get(1184,18).click(true);
//			sleep(1000);
//		}
		GameObject cave = ctx.objects.select().id(MiscFireCaper.CAVE_ENTRANCE_ID).iterator().next();
		System.out.println(cave);
		if(cave.isOnScreen() && cave.interact("Enter"))
			sleep(2500);
		else
			ctx.camera.turnTo(cave);
	}

	@Override
	public boolean validate() {
		return MiscFireCaper.enterCave;
	}
}
