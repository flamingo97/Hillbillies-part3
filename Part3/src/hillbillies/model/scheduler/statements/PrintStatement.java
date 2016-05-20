package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.Expression;
import hillbillies.part3.programs.SourceLocation;

public class PrintStatement extends Statement {

	public PrintStatement(Expression<?> expression, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
	}

	@Override
	public void execute(Task task) {
		System.out.println(this.getExpression().getString(task));
	}
}
