package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class SelectedExpression extends PositionExpression {

	public SelectedExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public int[] evaluate(Task task) {
		return task.getSelected();
	}

	@Override
	public String getString(Task task) {
		return Expression.positionToString((int[]) this.evaluate(task));
	}

}
