package hillbillies.model.scheduler.expressions;

import hillbillies.part3.programs.SourceLocation;

public abstract class BooleanExpression extends Expression<Boolean> {

	public BooleanExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	public BooleanExpression(SourceLocation sourceLocation, Expression<?>[] subExpressions) {
		super(sourceLocation, subExpressions);
	}
}
