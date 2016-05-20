package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public class PositionOfExpression extends PositionExpression {

	public PositionOfExpression(SourceLocation sourceLocation,
			UnitExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	@Override
	public int[] evaluate(Task task) {
		return ((Unit) this.getSubExpressions()[0].evaluate(task)).getPosition().toIntArray();
	}

	@Override
	public String getString(Task task) {
		int[] pos = this.evaluate(task);
		return Expression.positionToString(pos);
	}
}
