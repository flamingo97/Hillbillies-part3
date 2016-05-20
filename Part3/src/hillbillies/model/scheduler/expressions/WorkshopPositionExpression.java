package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.CubeType;
import hillbillies.model.world.Unit;
import hillbillies.model.world.Vector;
import hillbillies.part3.programs.SourceLocation;

import java.util.Set;

public class WorkshopPositionExpression extends PositionExpression {

	public WorkshopPositionExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public int[] evaluate(Task task) {
		Unit unit = task.getAssignedUnit();
		Set<int[]> workshops = unit.getWorld().getCubesOfType(CubeType.WORKSHOP);
		double minimumDistance = Double.MAX_VALUE;
		int[] minimumWorkshop = null;
		for (int[] pos : workshops) {
			Vector deltaPos = unit.getPosition().subtract(new Vector(pos[0], pos[1], pos[2]));
			double distance = deltaPos.length();
			if (distance < minimumDistance) {
				minimumDistance = distance;
				minimumWorkshop = pos;
			}
		}
		return minimumWorkshop;
	}

	@Override
	public String getString(Task task) {
		if (this.evaluate(task) != null)
			return Expression.positionToString(this.evaluate(task));
		return "no workshop in world";
	}

}
