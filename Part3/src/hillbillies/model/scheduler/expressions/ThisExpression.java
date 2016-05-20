package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public class ThisExpression extends UnitExpression {

	public ThisExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public Unit evaluate(Task task) {
		return task.getAssignedUnit();
	}

	@Override
	public String getString(Task task) {
		if (this.evaluate(task) == null)
			return "no assigned unit";
		return "this";
	}

}
