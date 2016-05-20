package hillbillies.model.scheduler.expressions;

import hillbillies.part3.programs.SourceLocation;

public abstract class PositionExpression extends Expression<int[]> {

	public PositionExpression(SourceLocation sourceLocation,
			Expression<?>[] subExpressions) {
		super(sourceLocation, subExpressions);
	}
	
	public PositionExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}
}
