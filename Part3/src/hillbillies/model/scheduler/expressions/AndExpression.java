package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class AndExpression extends BooleanExpression {

	public AndExpression(SourceLocation sourceLocation,
			BooleanExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	@Override
	public Boolean evaluate(Task task) {
		return (boolean)this.getSubExpressions()[0].evaluate(task) && 
				(boolean)this.getSubExpressions()[1].evaluate(task);
	}

	@Override
	public String getString(Task task) {
		Expression<?> e1 = this.getSubExpressions()[0];
		Expression<?> e2 = this.getSubExpressions()[1];
		return "(" + e1.getString(task) + " and " + e2.getString(task) + ")";
	}
}
