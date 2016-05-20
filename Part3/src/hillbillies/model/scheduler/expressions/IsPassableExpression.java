package hillbillies.model.scheduler.expressions;

import ogp.framework.util.ModelException;
import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;


public class IsPassableExpression extends BooleanExpression {
	
	public IsPassableExpression(SourceLocation sourceLocation,
			PositionExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	//TODO: Is passable not solid of is da walkable?
	@Override
	public Boolean evaluate(Task task) {
		int[] pos = (int[]) this.getSubExpressions()[0].evaluate(task);
		try {
			return !task.getWorld().getCube(pos[0], pos[1], pos[2]).isSolid();
		} catch (ModelException e) {
			System.out.println("Something went wrong in expressionding");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getString(Task task) {
		//Since evaluate always returns a boolean, this returns "true" or "false".
		return String.valueOf(this.evaluate(task));
	}

}
