package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;

public class LiteralPositionExpression extends PositionExpression {

	private final int[] position;
	
	public LiteralPositionExpression(SourceLocation sourceLocation, int x, int y, int z) {
		super(sourceLocation);
		this.position = new int[]{x, y, z};
	}

	@Override
	public int[] evaluate(Task task) {
		return position;
	}

	@Override
	public String getString(Task task) {
		return Expression.positionToString(this.getPosition());
	}

	/**
	 * @return the position
	 */
	protected int[] getPosition() {
		return position;
	}

}
