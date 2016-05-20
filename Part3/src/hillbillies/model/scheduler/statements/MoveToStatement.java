package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.PositionExpression;
import hillbillies.part3.programs.SourceLocation;
import ogp.framework.util.ModelException;

public class MoveToStatement extends Statement {

	public MoveToStatement(PositionExpression expression, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		int[] position = (int[]) this.getExpression().evaluate(task);
		if (position == null) {
			System.out.println("Something went wrong (class MoveToStatement, method execute(Task))");
			task.getAssignedUnit().stopTask();
			return;
		}
		try {
			task.getAssignedUnit().moveTo(position[0], position[1], position[2]);
		} catch (ModelException e) {
		}
	}
	
	public boolean isExecutableByUnit() {
		return true;
	}
}
