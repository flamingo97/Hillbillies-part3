package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class AnyExpression extends UnitExpression {

	public AnyExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public Unit evaluate(Task task) {
		Set<Unit> all = new HashSet<Unit>(task.getWorld().getUnits());
		all.remove(task.getAssignedUnit());
		if (all.isEmpty())
			return null;
		Iterator<Unit> ite = all.iterator();
		Random rand = new Random();
		int stopIndex = rand.nextInt(all.size());
		int i = 0;
		while (ite.hasNext()) {
			Unit next = ite.next();
			if (i == stopIndex)
				return next;
			i++;
		}
		//Should never happen
		return null;
	}

	@Override
	public String getString(Task task) {
		if (this.evaluate(task) != null)
			return "any Unit";
		return "null";
	}

}
