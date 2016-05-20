package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;
import be.kuleuven.cs.som.annotate.Basic;

public abstract class Expression<T> {
	
	private final SourceLocation sourceLocation;
	private final Expression<?>[] subExpressions;
	
	//private Task task;
	
	public Expression(SourceLocation sourceLocation, Expression<?>[] subExpressions) {
		this.sourceLocation = sourceLocation;
		this.subExpressions = subExpressions;
	}
	
	/**
	 * Used for non-composed expressions
	 * @param sourceLocation
	 */
	public Expression(SourceLocation sourceLocation) {
		this.sourceLocation = sourceLocation;
		this.subExpressions = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract T evaluate(Task task);

	public abstract String getString(Task task);
	
	protected static String positionToString(int[] pos) {
		return "(" + pos[0] + ", " + pos[1] + ", " + pos[2] + ")";
	}
	
	/**
	 * @return the sourceLocation
	 */
	@Basic
	public SourceLocation getSourceLocation() {
		return sourceLocation;
	}
	
	/**
	 * @return the subExpressions
	 */
	@Basic
	protected Expression<?>[] getSubExpressions() {
		return subExpressions;
	}
}
