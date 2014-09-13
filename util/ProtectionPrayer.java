package miscfirecaper.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Powers.Effect;
import org.powerbot.script.wrappers.Component;

public enum ProtectionPrayer {
	//ASSUMED
		//magic protection prayer: keybind '-'
		//missles protection prayer: keybind '='
		//melee protection prayer: keybind '3'
	MAGIC(11, 11),
	RANGED(12, 12),
	MELEE(13, 13);
	
	private int _normal_widgetChild;
	private int _curses_widgetChild;
	
	private static final int WIDGET = 271;
	private static final int WIDGET_NORMAL = 9;
	private static final int WIDGET_CURSES = 10;
	
	
	ProtectionPrayer( int normal_widgetChild, int curses_widgetChild){
		_normal_widgetChild = normal_widgetChild;
		_curses_widgetChild = curses_widgetChild;
	}
	
	public Condition untilEnabled = new Condition(){
		@Override
		public boolean accept(){
			//TODO
			return false;
		}
	};
	
	public Component getWidgetChild(MethodContext ctx){
		
		//if(Prayer.isCursesOn()){
			//return ctx.widgets.get(WIDGET, WIDGET_CURSES).getChild(_curses_widgetChild);
		//}
		//else{
			return ctx.widgets.get(WIDGET, WIDGET_NORMAL).getChild(_normal_widgetChild);
		//}
	}
	
	public boolean isActive(MethodContext ctx){
		int icon = ctx.players.local().getPrayerIcon();
		switch(this){
		case MAGIC:
			return icon == 13 || icon == 2;
		case RANGED:
			return icon == 14 || icon == 1;
		case MELEE:
			return icon == 12 || icon == 0;
		}
		return false;
	}
	
	public static int getPoints(MethodContext ctx){
		Component w = ctx.widgets.get(749, 6);
		if(w != null)
			return Integer.parseInt(w.getText());
		else 
			return -1;
	}
	
	public static void deactivate(MethodContext ctx){
		ctx.widgets.get(749,2).click(true);
		ctx.game.sleep(250);
		ctx.widgets.get(749,2).click(true);
	}
}
