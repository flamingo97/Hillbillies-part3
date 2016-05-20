package hillbillies.model.scheduler.statements;

import hillbillies.part3.programs.SourceLocation;

public class BeginOfTaskStatement extends Statement {

	public BeginOfTaskStatement(SourceLocation sourceLocation) {
		super(null, sourceLocation);
	}
	
	@Override
	public boolean isNonExecutable() {
		return true;
	}
}
