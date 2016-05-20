package hillbillies.model.scheduler.statements;

import hillbillies.part3.programs.SourceLocation;

import java.util.HashSet;
import java.util.Set;

public class BreakStatement extends Statement {

	public BreakStatement(SourceLocation sourceLocation) {
		super(null, sourceLocation);
	}
	
	@Override
	public Set<Statement> getBreakStatements() {
		Set<Statement> result = new HashSet<Statement>();
		result.add(this);
		return result;
	}
}
