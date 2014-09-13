package miscfirecaper;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

public class AttackStyles {

	private static final int TABS_WIDGET = 548;
	private static final int ATTACKSTYLES_TAB_WIDGET = 116;
	
	private static final int ATTACK_WIDGET = 464;
	private static final int RETALIATE_WIDGET = 5;
	
	private static final int RETALIATE_SETTING = 462;
	
	public static void openTab(MethodContext ctx){
		Component w = ctx.widgets.get(TABS_WIDGET, ATTACKSTYLES_TAB_WIDGET);
		if(w != null)
			w.click(true);
	}
	
	public static boolean isRetaliateOn(MethodContext ctx){
		return(ctx.settings.get(RETALIATE_SETTING) == 0);
	}
	
	public static void setRetaliate(MethodContext ctx, boolean on){
		if(on == isRetaliateOn(ctx))
			return;
		openTab(ctx);
		ctx.game.sleep(400);
		Component w = ctx.widgets.get(ATTACK_WIDGET, RETALIATE_WIDGET);
		if(w == null)
			return;
		if(w.getText().contains("On") != on)
			w.click(true);
		//open inv
		ctx.game.sleep(600);
	}

}

