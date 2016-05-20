package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

//TODO: uuhm ding met welke type heeft die?
public class ReadVariableExpression extends Expression<Object> {

	private final String variableName;
	
	public ReadVariableExpression(String variableName, SourceLocation sourceLocation) {
		super(sourceLocation);
		this.variableName = variableName;
	}

	@Override
	public Object evaluate(Task task) {
		if (task.getVariable(this.getVariableName()).evaluate(task) == null) {
			System.out.println("Trying to use an object before instantiating, line: " + this.getSourceLocation().getLine());
			return null;
		}
		return task.getVariable(this.getVariableName()).evaluate(task);
	}

	@Override
	public String getString(Task task) {
		return task.getVariable(variableName).getString(task);
	}

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

}
