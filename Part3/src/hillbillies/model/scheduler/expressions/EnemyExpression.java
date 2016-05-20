package hillbillies.model.scheduler.expressions;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Faction;
import hillbillies.model.world.Unit;
import hillbillies.part3.programs.SourceLocation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class EnemyExpression extends UnitExpression {

	public EnemyExpression(SourceLocation sourceLocation) {
		super(sourceLocation);
	}

	@Override
	public Unit evaluate(Task task) {
		Unit unit = task.getAssignedUnit();
		Set<Unit> enemies = new HashSet<Unit>();
		for (Faction faction : task.getWorld().getFactions()) {
			if (unit.getFaction() != faction)
				enemies.addAll(faction.getUnits());
		}
		if (enemies.isEmpty())
			return null;
		Iterator<Unit> ite = enemies.iterator();
		Random rand = new Random();
		int stopIndex = rand.nextInt(enemies.size());
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
			return "any Enemy";
		return "null";
	}

}
