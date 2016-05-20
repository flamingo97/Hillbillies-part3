package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.Expression;
import hillbillies.part3.programs.SourceLocation;

public class AssignmentStatement extends Statement {

	private final String variableName;
	
	public AssignmentStatement(Expression<?> expression, String name, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
		this.variableName = name;
	}

	@Override
	public void execute(Task task) {
		task.addVariable(this, this.getExpression());
	}

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

}
