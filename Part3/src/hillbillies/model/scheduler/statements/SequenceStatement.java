package hillbillies.model.scheduler.statements;

import hillbillies.part3.programs.SourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

public class SequenceStatement extends Statement {
	
	private final List<Statement> statements;
	
	public SequenceStatement(List<Statement> statements, SourceLocation sourceLocation) {
		super(null, sourceLocation);
		this.statements = statements;
	}

	@Override
	public boolean isNonExecutable() {
		return true;
	}

	/**
	 * @return the statements
	 */
	@Basic
	protected List<Statement> getStatements() {
		return statements;
	}
	
	public Set<Statement> getBreakStatements() {
		Set<Statement> result = new HashSet<Statement>();
		for (Statement statement : this.getStatements())
			result.addAll(statement.getBreakStatements());
		return result;
	}
	
	@Override
	public void setNextStatement(Statement next) {
		if (this.getStatements().isEmpty()) {
			nextStatement = next;
		} else {
			nextStatement = this.getStatements().get(0);
			for (int i = 0; i < this.getStatements().size() - 1; i++) {
				this.getStatements().get(i).setNextStatement(this.getStatements().get(i+1));
			}
			this.getStatements().get(this.getStatements().size() - 1).setNextStatement(next);
		}
	}
}
