package miscfirecaper.nodes;

import miscfirecaper.MiscFireCaper;
import miscfirecaper.util.Consumables;
import miscfirecaper.util.Task;

import org.powerbot.script.methods.MethodContext;

public class Eat extends Task{
	
	public Eat(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public void execute() {
		if(Consumables.eatFood(ctx, MiscFireCaper.foodId))
			ctx.game.sleep(1500);
	}

	@Override
	public boolean validate() {
		//(Players.getLocal().getHpPercent() < eatPercent)
		return MiscFireCaper.start 
				&& !MiscFireCaper.handleJad 
				&& ctx.players.local().getHealthPercent() < 60
				&& ctx.backpack.select().id(MiscFireCaper.foodId).count() > 0;
	}
}
