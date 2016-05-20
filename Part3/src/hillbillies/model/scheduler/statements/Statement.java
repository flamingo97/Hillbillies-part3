package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.Expression;
import hillbillies.part3.programs.SourceLocation;

import java.util.Collections;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

public abstract class Statement {
	
	private final SourceLocation sourceLocation; 
	
	private final Expression<?> expression;
	
	protected Statement nextStatement;
	
	public Statement(Expression<?> expression, SourceLocation sourceLocation) {
		this.expression = expression;
		this.sourceLocation = sourceLocation;
	}

	public void execute(Task task) {}
	
	/**
	 * @return the expression
	 */
	@Basic
	public Expression<?> getExpression() {
		return expression;
	}

	/**
	 * @return the sourceLocation
	 */
	public SourceLocation getSourceLocation() {
		return sourceLocation;
	}
	
	public boolean isNonExecutable() {
		return false;
	}
	
	public boolean isExecutableByUnit() {
		return false;
	}

	public Set<Statement> getBreakStatements() {
		return Collections.emptySet();
	}
	
	/**
	 * @return the nextStatement
	 */
	public Statement getNextStatement(Task task) {
		return nextStatement;
	}

	/**
	 * @param nextStatement the nextStatement to set
	 */
	public void setNextStatement(Statement nextStatement) {
		this.nextStatement = nextStatement;
	}
}
