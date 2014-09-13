package miscfirecaper.util;



import java.awt.Point;
import java.awt.event.KeyEvent;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.Menu.Entry;
import org.powerbot.script.wrappers.Component;


public class AbilityBar {
	
	private static final int ACTIONBAR_WIDGET = 1430;
	private static final int CHATBOX_WIDGET = 137;
	private static final int TEXTFIELD_WIDGET = 56;
	
	private static final String CHATBOX_DEFAULT_STRING = "[Press Enter to Chat]";
	
	public enum AbilitySlot{
		ONE(0, 32, 36, 70),
		TWO(1, 72, 73, 75),
		THREE(2, 76, 77, 79),
		FOUR(3, 80, 81, 83),
		FIVE(4, 84, 85, 87),
		SIX(5, 88, 89, 91),
		SEVEN(6, 92, 93, 95),
		EIGHT(7, 96, 97, 99),
		NINE(8, 100, 101, 103),
		TEN(9, 104, 105, 107),
		ELEVEN(10, 108, 109, 111),
		TWELVE(11, 112, 113, 115);
		
		private static final int WIDGET = 640;

		private SkKeyboard keyboard;
		
		private int _index;
		private int _widgetChildTexture;
		private int _widgetChildCooldown;
		private int _widgetChildText;
		
		AbilitySlot(int index, int widgetChildTexture, int widgetChildCooldown, int widgetChildText){
			_index = index;
			_widgetChildTexture = widgetChildTexture;
			_widgetChildCooldown = widgetChildCooldown;
			_widgetChildText = widgetChildText;
		}
	
		public int getIndex(){
			return _index;
		}
		
		public Component getWidgetChildTexture(MethodContext ctx){
			return ctx.widgets.get(WIDGET, _widgetChildTexture);
		}
		
		public Component getWidgetChildCooldown(MethodContext ctx){
			return ctx.widgets.get(WIDGET, _widgetChildCooldown);
		}
		
		public int getTextureId(MethodContext ctx){
			Component w = getWidgetChildTexture(ctx);
			if(w == null)
				return -1;
			return w.getTextureId();
		}
		
		public String getKeybind(MethodContext ctx){
			return ctx.combatBar.getActionAt(_index).getBind();
		}
		
		public boolean use(MethodContext ctx){
//			if(isChatEnabled(ctx)){
//				ctx.keyboard.send(KeyEvent.VK_ENTER);
//				ctx.game.sleep(500);
//			}
			String key = getKeybind(ctx);
			keyboard = new SkKeyboard(ctx);
			return keyboard.key(key, 0);
		}
		
		public void drag(MethodContext ctx, Point p){
			ctx.mouse.move(p);
			ctx.game.sleep(200);
			ctx.mouse.move(getWidgetChildTexture(ctx).getCenterPoint());
		}
		
		public void setKeybind(MethodContext ctx, char key){
			Component w = getWidgetChildTexture(ctx);
			ctx.mouse.click(w.getCenterPoint(), false);
			ctx.game.sleep(500);
			// && ctx.menu.getItems().contains("Customise-keybind")
			if(ctx.menu.isOpen()){
				ctx.menu.click(new Filter<Entry>(){
					@Override
					public boolean accept(Entry arg0) {
						return arg0.action.equals("Customise-keybind");
					}});
				ctx.game.sleep(1000);
			}
			ctx.keyboard.send("" + key);
			
		}
	}
	
	public static boolean isChatEnabled(MethodContext ctx){
		if(ctx.widgets.get(137,85) == null) return false;
		return !ctx.widgets.get(137,85).getText().contains("Press Enter");
	}
	
	public static boolean isMomentumEnabled(MethodContext ctx){
		return ctx.settings.get(3189) != 0;
	}

	public static int getAdrenaline(MethodContext ctx) {
		//return ctx.settings.get(679) / 10;
		return ctx.combatBar.getAdrenaline();
	}
}
