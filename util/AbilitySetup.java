package miscfirecaper.util;


import miscfirecaper.util.AbilityBar.AbilitySlot;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Npc;

public class AbilitySetup {

	public static Ability[] priorityAbility = {
		Ability.Resonance,
		Ability.Combust,
		Ability.Chain,
		Ability.Wrack
	};
	
	public static Ability[] priorityAbilityAll = {
		Ability.Resonance,
		Ability.Asphyxiate,
		Ability.Combust,
		Ability.Dragon_Breath,
		Ability.Chain,
		Ability.Wrack,
	};

	public static final Ability[] ACTIONBAR_ABILITIES = {
		Ability.Wrack,
		Ability.Dragon_Breath,
		Ability.Chain,
		Ability.Combust,
		Ability.Asphyxiate,
		Ability.Resonance,
		Ability.Rejuvenate
	};

	public static Ability[] prayers = {
		Ability.Magic_Protection,
		Ability.Ranged_Protection
	};

	public enum Ability{
		/*
		 * 
		 */
		
		Wrack(AbilitySlot.ONE, 22, '1', 14231, 0),
		Dragon_Breath(AbilitySlot.TWO, 102, '2', 14236, 0),
		Chain(AbilitySlot.THREE, 70, '3', 14232, 0),
		Combust(AbilitySlot.FOUR, 86, '4', 14235, 0),
		Regenerate(AbilitySlot.FIVE, 20, '5', 14267, 5),
		Asphyxiate(AbilitySlot.SIX, 118, '6', 14237, 50),
		Resonance(AbilitySlot.SEVEN, 67, '7', 14222, 0),
		Rejuvenate(AbilitySlot.EIGHT, 179, '8', 14229, 100),
		Magic_Protection(AbilitySlot.NINE, 215, '9', 14806, 0),//curses id=14806, reg id=14724
		Ranged_Protection(AbilitySlot.TEN, 231, '0', 14809, 0);//curses id=14809, reg id=14725
		
		
		
		private AbilitySlot _slot;
		
		private char _keybind;
		
		private int _id;
		private int _textureId;
		private int _adrenalineReq;
		
		Ability(AbilitySlot slot, int id, char keybind, int textureId, int adrenalineReq){
			_id = id;
			_slot = slot;
			_keybind = keybind;
			_textureId = textureId;
			_adrenalineReq = adrenalineReq;
		}
		
		public AbilitySlot getSlot(){
			return _slot;
		}
		
		public void use(MethodContext ctx){
			_slot.use(ctx);
		}
		
		public boolean isReady(MethodContext ctx){
			return ctx.combatBar.getActionAt(_slot.getIndex()).isReady();
		}
		
		public boolean isOffCooldown(MethodContext ctx){
			Component w = _slot.getWidgetChildCooldown(ctx);
			if(w == null)
				return false;
			int tid = w.getTextureId();
			return tid == 14521 || tid == -1;
		}
		
		public int getAdrenalineReq(){
			return _adrenalineReq;
		}
		
		public char getCorrectKeybind(){
			return _keybind;
		}
		
		public boolean validateSlot(MethodContext ctx){
			//special case, ranged and magic protecion can be curses or regular
			int id = ctx.combatBar.getActionAt(_slot.getIndex()).getId();
			return id == _id;
//			if(this == Magic_Protection){
//				return id == 14806 || id == 14724;
//			}
//			else if(this == Ranged_Protection){
//				return id == 14809 || id == 14725;
//			}
//			return id == _textureId;
		}
		
		public boolean validateKeybind(MethodContext ctx){
			return ctx.combatBar.getActionAt(_slot.getIndex()).getBind().equals(""+_keybind);
			//return _slot.getKeybind(ctx) == ""+_keybind;
		}
	}
	
	public static boolean validateSlots(MethodContext ctx){
		for(Ability a : Ability.values()){
			if(!a.validateSlot(ctx)){
				System.out.println("slot out of place:"+a.getSlot());
				return false;
			}
		}
		System.out.println("correct actionbar");
		return true;
	}
	
	public static boolean validateKeybinds(MethodContext ctx){
		for(Ability a : Ability.values()){
			if(!a.validateKeybind(ctx)){
				System.out.println("keybind wrong:"+a.getSlot());
				return false;
			}
		}
		System.out.println("correct keybinds");
		return true;
	}
	
	public static void useRotation(MethodContext ctx, Npc target){
		if(target == null)
			return;
//		if(ctx.players.local().getHealthPercent() < 75)
//			Ability.Rejuvenate.use(ctx);
//		for(Ability ab : priorityAbilityAll){
//			ab.use(ctx);
//			ctx.game.sleep(300);
//		}
		if(AbilityBar.getAdrenaline(ctx) == 100
				&& AbilitySetup.Ability.Rejuvenate.isReady(ctx)
				&& ctx.players.local().getHealthPercent() < 65){
			AbilitySetup.Ability.Rejuvenate.use(ctx);
			ctx.game.sleep(100);
			return;
		}
		
		if(target.getLocation().distanceTo(ctx.players.local()) < 1.5 
				&& AbilitySetup.Ability.Dragon_Breath.isReady(ctx)){
			AbilitySetup.Ability.Dragon_Breath.use(ctx);
			ctx.game.sleep(100);
			return;
		}
		
		if(!AbilitySetup.Ability.Rejuvenate.isOffCooldown(ctx)
				&& AbilitySetup.Ability.Asphyxiate.isReady(ctx)){
			AbilitySetup.Ability.Asphyxiate.use(ctx);
			ctx.game.sleep(100);
			return;
		}

		for(Ability ability : priorityAbility){
			if(ability.isReady(ctx)){
				ability.use(ctx);
				ctx.game.sleep(800);
				return;
			}
		}
	}
}
