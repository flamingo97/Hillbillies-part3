package hillbillies.model.world;

import hillbillies.model.scheduler.Scheduler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ogp.framework.util.ModelException;
import be.kuleuven.cs.som.annotate.Basic;

/**
 * A class to work with factions for the game Hillbillies.
 * 
 * @invar	This faction's world is not null and the world has this faction as a faction.
 * @invar	This faction has at most World.MAX_UNITS_PER_FACTION amount of units.
 * 
 * @author HF corp.
 * @version 1.0
 */
public class Faction {

	private World world;
	private Set<Unit> units;
	private Scheduler scheduler;
	private boolean terminated;
	
	/**
	 * The constructor for this Faction.
	 * @param world
	 * @effect	new.getWorld() == world
	 */
	public Faction(World world) {
		units = new HashSet<Unit>();
		setWorld(world);
		setScheduler(new Scheduler(this));
		
	}
	
	/**
	 * Advances the time with the given deltaT.
	 * @param deltaT
	 * @effect	All units in this faction advance time with the given deltaT.
	 */
	public void advanceTime(double deltaT) {
		for (Unit unit : units) {
			try {
				unit.advanceTime(deltaT);
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Terminates the faction.
	 * 
	 * @effect	Terminate the faction and set its scheduler to null.
	 */
	public void terminate() {
		setTerminated(true);
		this.setScheduler(null);
	}
	
	/**
	 * Removes all terminated units from this faction.
	 */
	public void checkTerminated() {
		Iterator<Unit> iterator = units.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isTerminated())
				iterator.remove();
		}
		if (this.getUnits().isEmpty())
			this.terminate();
	}
	
	/**
	 * Adds the given unit to this faction.
	 * @param unit
	 * 			The unit to add to this faction.
	 * @effect	If this faction does not have 50 units yet, add the given unit and set its faction equal to this
	 * 			and its world equal to this.getWorld().
	 */
	public void addUnit(Unit unit){
		if (units.size() < 50) {
			units.add(unit);
			unit.setFaction(this);
			try {
				unit.setWorld(this.getWorld());
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the units in this faction.
	 * @return
	 */
	@Basic
	public Set<Unit> getUnits(){
		return units;
	}
	
	/**
	 * Sets this faction's world equal to the given world.
	 * @param world
	 */
	@Basic
	public void setWorld(World world) {
		if (world != null)
			this.world = world;
	}
	
	/**
	 * Returns this faction's world.
	 * @return
	 */
	@Basic
	public World getWorld() {
		return world;
	}

	/**
	 * @return the scheduler
	 */
	@Basic
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	@Basic
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * @return the terminated
	 */
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * @param terminated the terminated to set
	 */
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
}
