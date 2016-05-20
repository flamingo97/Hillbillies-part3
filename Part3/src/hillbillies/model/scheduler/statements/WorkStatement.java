package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.PositionExpression;
import hillbillies.part3.programs.SourceLocation;

public class WorkStatement extends Statement {

	public WorkStatement(PositionExpression expression, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		int[] position = (int[]) this.getExpression().evaluate(task);
		if (position == null) {
			task.getAssignedUnit().stopTask();
			return;
		}
		task.getAssignedUnit().startWork(position[0], position[1], position[2]);
	}
	
	public boolean isExecutableByUnit() {
		return true;
	}
}
