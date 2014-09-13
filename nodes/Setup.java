package miscfirecaper.nodes;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;

import miscfirecaper.AttackStyles;
import miscfirecaper.GUI;
import miscfirecaper.Wave;
import miscfirecaper.MiscFireCaper;
import miscfirecaper.util.AbilityBar.AbilitySlot;
import miscfirecaper.util.AbilityBook.AbilitySpell;
import miscfirecaper.util.AbilitySetup;
import miscfirecaper.util.AbilitySetup.Ability;
import miscfirecaper.util.Condition;
import miscfirecaper.util.ProtectionPrayer;
import miscfirecaper.util.Task;

public class Setup extends Task{

	private MethodContext ctx;
	
	public Setup(MethodContext arg0) {
		super(arg0);
		ctx = arg0;
	}

	@Override
	public void execute() {
		
		MiscFireCaper.currentWave = 1;
		MiscFireCaper.currentNpc = "?";

		MiscFireCaper.startTime = System.currentTimeMillis();
		MiscFireCaper.prayerBoostPoint = 250;//TODO base off prayer level when powerbot fixes their shit
		MiscFireCaper.jadHp = 100;
		
		if(!MiscFireCaper.guiDone){
			GUI gui = new GUI();
			//Application.launch(JFXGUI.class);
			while(!MiscFireCaper.guiDone && gui.isVisible()){
				sleep(200);
			}
		}
		
		if(!MiscFireCaper.guiDone)
			return;
		
		//ctx.camera.setPitch(3);
		
		AttackStyles.setRetaliate(ctx,false);
		for(Ability a : Ability.values()){
			if(!a.validateSlot(ctx)){
				MiscFireCaper.error = true;
				MiscFireCaper.errorMsg = "Invalid ability, expected "+a+" at slot "+(a.getSlot().getIndex()+1);
				setAbility(a);
				return;
			}
			if(!a.validateKeybind(ctx)){
				MiscFireCaper.error = true;
				MiscFireCaper.errorMsg = "Invalid keybind, expected '"+a.getCorrectKeybind()+"' at slot "+(a.getSlot().getIndex()+1);
				setKeybind(a);
				return;
			}
		}
		
		//Tabs.INVENTORY.open();
		
//		if(!AbilitySetup.validateSlots(ctx) || !AbilitySetup.validateKeybinds(ctx)){
//			execute();
//		}
		
		if(Wave.outsideCave(ctx)){
			MiscFireCaper.enterCave = true;
		}
		else{
			MiscFireCaper.prepWave = true;
			Wave.constructArea(ctx);
		}
		
		MiscFireCaper.start = true;
	}
	
	public void setAbility(Ability ability){
		switch(ability){
		case Wrack:
			AbilitySpell.Wrack.drag(ctx,AbilitySlot.ONE);
			waitForSlot(Ability.Wrack);
			break;
		case Dragon_Breath:
			AbilitySpell.Dragon_Breath.drag(ctx,AbilitySlot.TWO);
			waitForSlot(Ability.Dragon_Breath);
			break;
		case Chain:
			AbilitySpell.Chain.drag(ctx,AbilitySlot.THREE);
			waitForSlot(Ability.Chain);
			break;
		case Combust:
			AbilitySpell.Combust.drag(ctx,AbilitySlot.FOUR);
			waitForSlot(Ability.Combust);
			break;
		case Regenerate:
			AbilitySpell.Regenerate.drag(ctx,AbilitySlot.FIVE);
			waitForSlot(Ability.Regenerate);
			break;
		case Asphyxiate:
			AbilitySpell.Asphyxiate.drag(ctx,AbilitySlot.SIX);
			waitForSlot(Ability.Asphyxiate);
			break;
		case Resonance:
			AbilitySpell.Resonance.drag(ctx,AbilitySlot.SEVEN);
			waitForSlot(Ability.Resonance);
			break;
		case Rejuvenate:
			AbilitySpell.Rejuvenate.drag(ctx,AbilitySlot.EIGHT);
			waitForSlot(Ability.Rejuvenate);
			break;
		case Magic_Protection:
			System.out.println("fix magic prot");
			//Tabs.PRAYER.open();
			ctx.game.sleep(200);
			Component magicIcon = ProtectionPrayer.MAGIC.getWidgetChild(ctx);
			if(magicIcon == null)
				return;
			AbilitySlot.NINE.drag(ctx,magicIcon.getCenterPoint());
			waitForSlot(Ability.Magic_Protection);
			break;
		case Ranged_Protection:
			System.out.println("fix ranged prot");
			//Tabs.PRAYER.open();
			ctx.game.sleep(200);
			Component rangedIcon = ProtectionPrayer.RANGED.getWidgetChild(ctx);
			if(rangedIcon == null)
				return;
			AbilitySlot.TEN.drag(ctx,rangedIcon.getCenterPoint());
			waitForSlot(Ability.Ranged_Protection);
			break;
		
		default:
			break;
		}
	}
	
	public void setKeybind(Ability ability){
		switch(ability){
		case Wrack:
			AbilitySlot.ONE.setKeybind(ctx,'1');
			waitForKeybind(Ability.Wrack);
			break;
		case Dragon_Breath:
			AbilitySlot.TWO.setKeybind(ctx,'2');
			waitForKeybind(Ability.Dragon_Breath);
			break;
		case Chain:
			AbilitySlot.THREE.setKeybind(ctx,'3');
			waitForKeybind(Ability.Chain);
			break;
		case Combust:
			AbilitySlot.FOUR.setKeybind(ctx,'4');
			waitForKeybind(Ability.Combust);
			break;
		case Regenerate:
			AbilitySlot.FIVE.setKeybind(ctx,'5');
			waitForKeybind(Ability.Regenerate);
			break;
		case Asphyxiate:
			AbilitySlot.SIX.setKeybind(ctx,'6');
			waitForKeybind(Ability.Asphyxiate);
			break;
		case Resonance:
			AbilitySlot.SEVEN.setKeybind(ctx,'7');
			waitForKeybind(Ability.Resonance);
			break;
		case Rejuvenate:
			AbilitySlot.EIGHT.setKeybind(ctx,'8');
			waitForKeybind(Ability.Rejuvenate);
			break;
		case Magic_Protection:
			AbilitySlot.NINE.setKeybind(ctx,'9');
			waitForKeybind(Ability.Magic_Protection);
			break;
		case Ranged_Protection:
			AbilitySlot.TEN.setKeybind(ctx,'0');
			waitForKeybind(Ability.Ranged_Protection);
			break;
		default:
			break;
		}
	}

	public void waitForSlot(final Ability ability){
		MiscFireCaper.waitFor(new Condition(){
			@Override
			public boolean accept() {
				return ability.validateSlot(ctx);
			}
		}, 4000);
	}
	
	public void waitForKeybind(final Ability ability){
		MiscFireCaper.waitFor(new Condition(){
			@Override
			public boolean accept() {
				return ability.validateKeybind(ctx);
			}}, 2000);
	}

	@Override
	public boolean validate() {
		return !MiscFireCaper.start;
	}
}