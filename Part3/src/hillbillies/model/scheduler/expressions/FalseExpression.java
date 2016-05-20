package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class FalseExpression extends BooleanExpression {

	public FalseExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public Boolean evaluate(Task task) {
		return false;
	}

	@Override
	public String getString(Task task) {
		return "false";
	}

}
