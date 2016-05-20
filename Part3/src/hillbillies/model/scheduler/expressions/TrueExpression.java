package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class TrueExpression extends BooleanExpression {

	public TrueExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public Boolean evaluate(Task task) {
		return true;
	}

	@Override
	public String getString(Task task) {
		return "true";
	}

}
