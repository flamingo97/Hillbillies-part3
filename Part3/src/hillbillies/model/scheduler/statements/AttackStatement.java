package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.UnitExpression;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

public class AttackStatement extends Statement {

	public AttackStatement(UnitExpression expression, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		Unit victim = (Unit) this.getExpression().evaluate(task);
		if (victim == null) {
			task.getAssignedUnit().stopTask();
			return;
		}
		task.getAssignedUnit().attack(victim);
	}
	
	public boolean isExecutableByUnit() {
		return true;
	}
}
