package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class EndOfTaskStatement extends Statement {

	public EndOfTaskStatement(SourceLocation sourceLocation) {
		super(null, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		task.terminate();
	}
}
