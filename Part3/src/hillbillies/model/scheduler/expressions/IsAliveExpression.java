package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public class IsAliveExpression extends BooleanExpression {

	public IsAliveExpression(SourceLocation sourceLocation,
			UnitExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	@Override
	public Boolean evaluate(Task task) {
		Unit otherUnit = (Unit) this.getSubExpressions()[0].evaluate(task);
		return !otherUnit.isTerminated();
	}

	@Override
	public String getString(Task task) {
		//Since evaluate always returns a boolean, this returns "true" or "false".
		return String.valueOf(this.evaluate(task));
	}

}
