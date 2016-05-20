package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.BooleanExpression;
import hillbillies.part3.programs.SourceLocation;

import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

public class WhileStatement extends Statement{

	private final Statement body;
	
	public WhileStatement(BooleanExpression condition, Statement body, SourceLocation sourceLocation) {
		super(condition, sourceLocation);
		this.body = body;
	}

	@Override
	public boolean isNonExecutable() {
		return true;
	}

	/**
	 * @return the body
	 */
	@Basic
	protected Statement getBody() {
		return body;
	}

	@Override
	public void setNextStatement(Statement next) {
		body.setNextStatement(this);
		this.nextStatement = next;
		Set<Statement> breakStatements = body.getBreakStatements();
		for (Statement breakStatement : breakStatements) 
			breakStatement.setNextStatement(next);
	}
	
	@Override
	public Statement getNextStatement(Task task) {
		if ((boolean)(this.getExpression().evaluate(task)))
			return body;
		else
			return nextStatement;
	}
}
