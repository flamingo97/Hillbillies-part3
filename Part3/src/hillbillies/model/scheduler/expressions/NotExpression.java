package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class NotExpression extends BooleanExpression {

	public NotExpression(SourceLocation sourceLocation,
			BooleanExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	@Override
	public Boolean evaluate(Task task) {
		return ! (boolean)this.getSubExpressions()[0].evaluate(task);
	}

	@Override
	public String getString(Task task) {
		//Since evaluate always returns a boolean, this returns "true" or "false".
		return String.valueOf(this.evaluate(task));
	}
}
