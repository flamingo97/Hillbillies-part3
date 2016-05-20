package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class HerePositionExpression extends PositionExpression {

	public HerePositionExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public int[] evaluate(Task task) {
		return task.getAssignedUnit().getPosition().toIntArray();
	}

	@Override
	public String getString(Task task) {
		return Expression.positionToString(this.evaluate(task));
	}
}
