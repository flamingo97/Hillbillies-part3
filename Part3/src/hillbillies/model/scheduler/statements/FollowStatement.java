package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.UnitExpression;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public class FollowStatement extends Statement {

	public FollowStatement(UnitExpression expression, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		task.getAssignedUnit().setFollowedUnit((Unit) this.getExpression().evaluate(task));
	}

	@Override
	public boolean isExecutableByUnit() {
		return true;
	}
}
