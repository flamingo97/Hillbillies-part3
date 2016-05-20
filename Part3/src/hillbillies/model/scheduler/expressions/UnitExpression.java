package hillbillies.model.scheduler.expressions;

import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public abstract class UnitExpression extends Expression<Unit> {

	public UnitExpression(SourceLocation sourceLocation,
			Expression<?>[] subExpressions) {
		super(sourceLocation, subExpressions);
	}
	
	public UnitExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}
}
