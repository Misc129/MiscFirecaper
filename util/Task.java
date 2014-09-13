package miscfirecaper.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

import miscfirecaper.MiscFireCaper;

public abstract class Task extends MethodProvider{

	public MethodContext ctx;
	
	public Task(MethodContext arg0) {
		super(arg0);
		this.ctx = arg0;
	}
	public abstract boolean validate();
	public abstract void execute();
	
}
