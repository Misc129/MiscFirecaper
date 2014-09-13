package miscfirecaper.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;

public class AbilityBook {
	
	
	private static final int TABS_WIDGET = 548;
	private static final int ABILITY_TAB_WIDGET = 123;
	private static final int SPELLBOOK_WIDGET = 275;
	
	public static void openBook(MethodContext ctx){
		if(isOpen(ctx))
			return;
		Component w = ctx.widgets.get(TABS_WIDGET, ABILITY_TAB_WIDGET);
		if(w != null)
			w.click(true);
	}
	
	public static boolean isOpen(MethodContext ctx){
		Component w = Tab.RANGED.getComponent(ctx);
		if(w == null)
			return false;
		return w.isVisible();
	}
	
	public enum Tab{
		MELEE(26),
		RANGED(9),
		MAGIC(41),
		DEFENCE_CONSTITUTION(57);
		
		private int _widgetId;
		
		Tab(int widgetId){
			_widgetId = widgetId;
		}
		
		public Component getComponent(MethodContext ctx){
			return ctx.widgets.get(SPELLBOOK_WIDGET, _widgetId);
		}
	}
	
	public enum SubTab{
		ATTACK(29),//14873,
		STRENGTH(31),// ,14874
		RANGED(2),//
		ABILITIES(42),//
		COMBAT_SPELLS(45),//
		TELEPORT_SPELLS(47),//
		SKILLING_SPELLS(49),//
		DEFENCE(59),//
		CONSTITUTION(60);//
		
		private int _widgetId;
		
		SubTab(int widgetId){
			_widgetId = widgetId;
		}
		
		public Component getComponent(MethodContext ctx){
			return ctx.widgets.get(SPELLBOOK_WIDGET, _widgetId);
		}
	}
	
	public enum AbilitySpell{
		Wrack(Tab.MAGIC, SubTab.ABILITIES, 14231, 1),
		Dragon_Breath(Tab.MAGIC, SubTab.ABILITIES, 14236, 6),
		Chain(Tab.MAGIC, SubTab.ABILITIES, 14232, 4),
		Combust(Tab.MAGIC, SubTab.ABILITIES, 14235, 5),
		Omnipower(Tab.MAGIC, SubTab.ABILITIES, 14242, 12),
		Asphyxiate(Tab.MAGIC, SubTab.ABILITIES, 14237, 7),
		Resonance(Tab.DEFENCE_CONSTITUTION, SubTab.DEFENCE, 14222, 4),
		Rejuvenate(Tab.DEFENCE_CONSTITUTION, SubTab.DEFENCE, 14229, 11),
		Regenerate(Tab.DEFENCE_CONSTITUTION, SubTab.CONSTITUTION, 14267, 1);
		
		public static final int NONSCROLLABLE_WIDGETCHILD_ID = 16;
		
		private Tab _tab;
		private SubTab _subTab;
		
		private int _textureId;
		private int _widgetId;
		
		AbilitySpell(Tab tab, SubTab subTab, int textureId, int widgetId){
			_tab = tab;
			_subTab = subTab;
			_textureId = textureId;
			_widgetId = widgetId;
		}
		
		private boolean isTabOpen(MethodContext ctx){
			Component w = _subTab.getComponent(ctx);
			if(w == null)
				return false;
			return w.isVisible();
		}
		
		private boolean isSubTabOpen(MethodContext ctx){
			Component w = getComponent(ctx);
			if(w == null)
				return false;
			return w.getTextureId() == _textureId;
		}
		
		public int getTextureId(){
			return _textureId;
		}
		
		public Component getComponent(MethodContext ctx){
			Component w = ctx.widgets.get(SPELLBOOK_WIDGET, NONSCROLLABLE_WIDGETCHILD_ID);
			if(w == null)
				return null;
			return w.getChild(_widgetId);
		}
		
		public void openTab(MethodContext ctx){
			openBook(ctx);
			if(!isTabOpen(ctx)){
				Component tab = _tab.getComponent(ctx);
				if(tab == null)
					return;
				tab.click(true);
				ctx.game.sleep(350);
			}
			if(!isSubTabOpen(ctx)){
				Component subTab = _subTab.getComponent(ctx);
				if(subTab == null)
					return;
				subTab.click(true);
				ctx.game.sleep(350);
			}
		}
		
		public void drag(MethodContext ctx, AbilityBar.AbilitySlot slot){
			openTab(ctx);
			Component ability = getComponent(ctx);
			if(ability == null)
				return;
			slot.drag(ctx, ability.getCenterPoint());
		}
		
		public boolean atSlot(MethodContext ctx, AbilityBar.AbilitySlot slot){
			return slot.getTextureId(ctx) == _textureId;
		}
	}
}
