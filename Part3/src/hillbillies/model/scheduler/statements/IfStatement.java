package hillbillies.model.scheduler.statements;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.BooleanExpression;
import hillbillies.part3.programs.SourceLocation;

import java.util.HashSet;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

public class IfStatement extends Statement {

	private final Statement ifBody, elseBody;
	
	public IfStatement(BooleanExpression expression,
			Statement ifBody, Statement elseBody, SourceLocation sourceLocation) {
		super(expression, sourceLocation);
		this.ifBody = ifBody;
		this.elseBody = elseBody;
	}

	@Override
	public boolean isNonExecutable() {
		return true;
	}

	/**
	 * @return the ifBody
	 */
	@Basic
	protected Statement getIfBody() {
		return ifBody;
	}

	/**
	 * @return the elseBody
	 */
	@Basic
	protected Statement getElseBody() {
		return elseBody;
	}
	
	@Override
	public Set<Statement> getBreakStatements() {
		Set<Statement> result = new HashSet<Statement>();
		result.addAll(ifBody.getBreakStatements());
		if (elseBody != null)
			result.addAll(elseBody.getBreakStatements());
		return result;
	}
	
	@Override
	public void setNextStatement(Statement next) {
		ifBody.setNextStatement(next);
		if (elseBody != null) 
			elseBody.setNextStatement(next);
		else
			this.nextStatement = next;
	}
	
	@Override
	public Statement getNextStatement(Task task) {
		if ((boolean)(this.getExpression().evaluate(task)))
			return ifBody;
		else {
			if (elseBody != null)
				return elseBody;
			else
				return nextStatement;
		}
	}
}
