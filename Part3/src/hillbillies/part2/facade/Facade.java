package hillbillies.part2.facade;

import hillbillies.model.world.Boulder;
import hillbillies.model.world.Faction;
import hillbillies.model.world.Log;
import hillbillies.model.world.Unit;
import hillbillies.model.world.World;
import hillbillies.model.world.Unit.State;
import hillbillies.part2.listener.TerrainChangeListener;

import java.util.Set;

import ogp.framework.util.ModelException;

public class Facade implements IFacade {

	@Override
	public Unit createUnit(String name, int[] initialPosition, int weight,
			int agility, int strength, int toughness,
			boolean enableDefaultBehavior) throws ModelException {
		return new Unit(initialPosition[0], initialPosition[1], initialPosition[2], name, weight, strength, agility,
				toughness, enableDefaultBehavior);
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
		int[]pos = unit.getPosition().toIntArray();
		unit.moveTo(pos[0] + dx, pos[1] + dy, pos[2] + dz);

	}

	@Override
	public double getCurrentSpeed(Unit unit) throws ModelException {
		return unit.getCurrentSpeed();
	}

	@Override
	public boolean isMoving(Unit unit) throws ModelException {
		return unit.getState() == State.WALKING;
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
		return unit.isSprinting();
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
		return unit.getState() == State.WORKING;
	}

	@Override
	public void fight(Unit attacker, Unit defender) throws ModelException {
		attacker.attack(defender);
	}

	@Override
	public boolean isAttacking(Unit unit) throws ModelException {
		return unit.getState() == State.ATTACKING;
	}

	@Override
	public void rest(Unit unit) throws ModelException {
		unit.startRest();
	}

	@Override
	public boolean isResting(Unit unit) throws ModelException {
		return unit.getState() == State.RESTING;
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

	@Override
	public World createWorld(int[][][] terrainTypes,
			TerrainChangeListener modelListener) throws ModelException {
		return new World(terrainTypes, modelListener);
	}

	@Override
	public int getNbCubesX(World world) throws ModelException {
		return world.getSizeX();
	}

	@Override
	public int getNbCubesY(World world) throws ModelException {
		return world.getSizeY();
	}

	@Override
	public int getNbCubesZ(World world) throws ModelException {
		return world.getSizeZ();
	}

	@Override
	public void advanceTime(World world, double dt) throws ModelException {
		world.advanceTime(dt);
	}

	@Override
	public int getCubeType(World world, int x, int y, int z)
			throws ModelException {
		return world.getTerrainType(x, y, z);
	}

	@Override
	public void setCubeType(World world, int x, int y, int z, int value)
			throws ModelException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSolidConnectedToBorder(World world, int x, int y, int z)
			throws ModelException {
		return world.isConnected(x, y, z);
	}

	@Override
	public Unit spawnUnit(World world, boolean enableDefaultBehavior)
			throws ModelException {
		return world.spawnUnit(enableDefaultBehavior);
	}

	@Override
	public void addUnit(Unit unit, World world) throws ModelException {
		world.addUnit(unit);
	}

	@Override
	public Set<Unit> getUnits(World world) throws ModelException {
		return world.getUnits();
	}

	@Override
	public boolean isCarryingLog(Unit unit) throws ModelException {
		// TODO Auto-generated method stub
		return unit.isCarrying();
	}

	@Override
	public boolean isCarryingBoulder(Unit unit) throws ModelException {
		// TODO Auto-generated method stub
		return unit.isCarrying();
	}

	@Override
	public boolean isAlive(Unit unit) throws ModelException {
		return !unit.isTerminated();
	}

	@Override
	public int getExperiencePoints(Unit unit) throws ModelException {
		return unit.getExperience();
	}

	@Override
	public void workAt(Unit unit, int x, int y, int z) throws ModelException {
		unit.startWork(x, y, z);
	}

	@Override
	public Faction getFaction(Unit unit) throws ModelException {
		return unit.getFaction();
	}

	@Override
	public Set<Unit> getUnitsOfFaction(Faction faction) throws ModelException {
		return faction.getUnits();
	}

	@Override
	public Set<Faction> getActiveFactions(World world) throws ModelException {
		return world.getFactions();
	}

	@Override
	public double[] getPosition(Boulder boulder) throws ModelException {
		return boulder.getPosition().toArray();
	}

	@Override
	public Set<Boulder> getBoulders(World world) throws ModelException {
		return world.getBoulders();
	}

	@Override
	public double[] getPosition(Log log) throws ModelException {
		return log.getPosition().toArray();
	}

	@Override
	public Set<Log> getLogs(World world) throws ModelException {
		return world.getLogs();
	}
}
