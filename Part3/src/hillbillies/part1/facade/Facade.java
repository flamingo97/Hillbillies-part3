package hillbillies.part1.facade;

import hillbillies.model.world.Unit;
import hillbillies.model.world.Unit.State;
import ogp.framework.util.ModelException;

public class Facade implements IFacade {

	public Facade() {
		
	}
	
	@Override
	public Unit createUnit(String name, int[] initialPosition, int weight, int agility, 
			int strength, int toughness, boolean enableDefaultBehavior) throws ModelException {
		//This no longer works, world == null 
		return new Unit(initialPosition[0], initialPosition[1], initialPosition[2], name, weight, 
				strength, agility, toughness, enableDefaultBehavior);
	}

	@Override
	public double[] getPosition(Unit unit) throws ModelException {
		return unit.getPosition().toArray();
	}

	@Override
	public int[] getCubeCoordinate(Unit unit) throws ModelException {
		return unit.getPosition().toIntArray();
	}

	@Override
	public String getName(Unit unit) throws ModelException {
		return unit.getName();
	}

	@Override
	public void setName(Unit unit, String newName) throws ModelException {
		unit.setName(newName);
	}

	@Override
	public int getWeight(Unit unit) throws ModelException {
		return unit.getWeight();
	}

	@Override
	public void setWeight(Unit unit, int newValue) throws ModelException {
		unit.setWeight(newValue);
	}

	@Override
	public int getStrength(Unit unit) throws ModelException {
		return unit.getStrength();
	}

	@Override
	public void setStrength(Unit unit, int newValue) throws ModelException {
		unit.setStrength(newValue);
	}

	@Override
	public int getAgility(Unit unit) throws ModelException {
		return unit.getAgility();
	}

	@Override
	public void setAgility(Unit unit, int newValue) throws ModelException {
		unit.setAgility(newValue);
	}

	@Override
	public int getToughness(Unit unit) throws ModelException {
		return unit.getToughness();
	}

	@Override
	public void setToughness(Unit unit, int newValue) throws ModelException {
		unit.setToughness(newValue);
	}

	@Override
	public int getMaxHitPoints(Unit unit) throws ModelException {
		return unit.getHealth();
	}

	@Override
	public int getCurrentHitPoints(Unit unit) throws ModelException {
		return (int)(unit.getCurrentHealth());
	}

	@Override
	public int getMaxStaminaPoints(Unit unit) throws ModelException {
		return unit.getStamina();
	}

	@Override
	public int getCurrentStaminaPoints(Unit unit) throws ModelException {
		return (int)(unit.getCurrentStamina());
	}

	@Override
	public void advanceTime(Unit unit, double dt) throws ModelException {
		unit.advanceTime(dt);
	}

	@Override
	public void moveToAdjacent(Unit unit, int dx, int dy, int dz)
			throws ModelException {
		unit.moveToAdjacent(dx, dy, dz);
	}

	@Override
	public double getCurrentSpeed(Unit unit) throws ModelException {
		return unit.getCurrentSpeed();
	}

	@Override
	public boolean isMoving(Unit unit) throws ModelException {
		return (unit.getState() == State.WALKING || unit.isSprinting());
	}

	@Override
	public void startSprinting(Unit unit) throws ModelException {
		unit.startSprint();
	}

	@Override
	public void stopSprinting(Unit unit) throws ModelException {
		unit.stopSprint();
	}

	@Override
	public boolean isSprinting(Unit unit) throws ModelException {
		return (unit.isSprinting());
	}

	@Override
	public double getOrientation(Unit unit) throws ModelException {
		return unit.getOrientation();
	}

	@Override
	public void moveTo(Unit unit, int[] cube) throws ModelException {
		unit.moveTo(cube[0], cube[1], cube[2]);
	}

	@Override
	public void work(Unit unit) throws ModelException {
		int[] pos = unit.getPosition().toIntArray();
		unit.startWork(pos[0], pos[1], pos[2]);
	}

	@Override
	public boolean isWorking(Unit unit) throws ModelException {
		return (unit.getState() == State.WORKING);
	}

	@Override
	public void fight(Unit attacker, Unit defender) throws ModelException {
		attacker.attack(defender);
	}

	@Override
	public boolean isAttacking(Unit unit) throws ModelException {
		return (unit.getState() == State.ATTACKING);
	}

	@Override
	public void rest(Unit unit) throws ModelException {
		unit.startRest();
	}

	@Override
	public boolean isResting(Unit unit) throws ModelException {
		return (unit.getState() == State.RESTING);
	}

	@Override
	public void setDefaultBehaviorEnabled(Unit unit, boolean value)
			throws ModelException {
		unit.setDefaultBehaviorEnabled(value);
	}

	@Override
	public boolean isDefaultBehaviorEnabled(Unit unit) throws ModelException {
		return unit.isDefaultBehaviorEnabled();
	}

}
