package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.part3.programs.SourceLocation;
import ogp.framework.util.ModelException;

public class IsSolidExpression extends BooleanExpression {

	public IsSolidExpression(SourceLocation sourceLocation,
			PositionExpression[] subExpressions) {
		super(sourceLocation, subExpressions);
	}

	@Override
	public Boolean evaluate(Task task) {
		int[] pos = (int[]) this.getSubExpressions()[0].evaluate(task);
		try {
			return task.getWorld().getCube(pos[0], pos[1], pos[2]).isSolid();
		} catch (ModelException e) {
			System.out.println("Something went wrong in IsSolidExpression");
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
