package hillbillies.model.world;

import hillbillies.model.scheduler.Task;
import hillbillies.model.world.Carryable.CarryableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ogp.framework.util.ModelException;
import ogp.framework.util.Util;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;


/**
 * A Unit class for the game Hillbillies.
 * 
 * @invar	The Unit's position is valid.
 * 			| isValidPosition(this.getPosition())
 * @invar	The name of the Unit is valid.
 * 			| isValidName(this.getName())
 * @invar	Strength, agility, weight and toughness are in valid boundaries.
 * 			| 1 <= this.getStrength() <= 200
 * 			| 1 <= this.getAgility() <= 200
 * 			| 1 <= this.getRealWeight() <= 200
 * 			| 1 <= this.getToughness() <= 200
 * @invar	Weight is less than or equal to (strength + agility) / 2
 * 			| this.getRealWeight() <= (strength + agility) / 2
 * @invar 	This unit's currentHealth and currentStamina are valid.
 * 			| 0 < this.getCurrentHealth() <= this.getHealth()
 * 			| 0 <= this.getCurrentStamina() <= this.getStamina()
 * @invar	The unit has a world.
 * 			| this.getWorld() != null
 * @invar	The unit has a faction.
 * 			| this.getFaction() != null
 * @invar	If this unit is carrying something carryable is not null.
 * 			| !this.getCarrying() || (this.getCarryable() != null)
 * 
 * @author HF corp.
 * @version 3.0
 */
public class Unit {	
	/**
	 * Enum with the states this unit can have.
	 * 
	 * @author HF corp.
	 */
	public enum State {
		NOTHING, WALKING, ATTACKING, DEFENDING, DANCING, WORKING, RESTING, FALLING
	}
	
	/**
	 * Valid characters for the name of this unit.
	 */
	private static final String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\'\" ";
	private static final Vector FALL_VELOCITY = new Vector(0,0,-3);
	
	private World world;
	private Faction faction;
	
	private boolean terminated;
	
	private State state;
	/** 
	 * Variable registering the time left during which this unit may not change states.
	 */
	private double statetime;
	
	private Vector position;
	private String name;
	
	private List<int[]> pathToEndGoal;
	private Vector endGoal, start, currentGoal;
	private Vector velocity;
	private boolean hasEndGoal;
	private boolean sprinting;
	private boolean carrying;
	private Carryable carryable;
	private int[] workPosition;
	
	private int weight, strength, agility, toughness;
	private int experience, experienceToNextLevel;
	private double currentHealth, currentStamina;
	
	/**
	 * Variable registering the time left during which this unit keeps working.
	 */
	private double busytime;
	
	private boolean resetPath;
	
	private boolean hasToRest;
	/**
	 * Counter to know when this unit has to rest (normally every 3 minutes).
	 */
	private double timeToRest;
	
	private double  orientation;
	private boolean defaultBehaviorEnabled; 
	
	private Task task;
	private boolean statementCompleted;
	
	/**
	 * The unit being followed.
	 */
	private Unit followedUnit;
	
	/**
	 * Variable which is true if this Unit is being constructed and false otherwise.
	 */
	private boolean init;
	
	/**
	 * Constructor for the Unit class.
	 * 
	 * @param x
	 * 			The x-coordinate of this Unit.
	 * @param y
	 * 			The y-coordinate of this Unit.
	 * @param z
	 * 			The z-coordinate of this Unit.
	 * @param name
	 * 			The name of this Unit.
	 * @param weight
	 * 			The weight of this Unit.
	 * @param strength
	 * 			The strength of this Unit.
	 * @param agility
	 * 			The agility of this Unit.
	 * @param toughness
	 * 			The toughness of this Unit.
	 * @param defaultBehaviorEnabled
	 * 			Whether or not this unit should have the default behavior.
	 * @throws ModelException 
	 * 			Throws a ModelException if the given position is invalid.
	 * 			| if !isValidPosition(new Vector(x, y, z))
	 * @throws ModelException
	 * 			Throws a ModelException if the given name is invalid.
	 * 			| if !isValidName(name)
	 * @effect	This constructor creates a new unit with the given parameters.
	 * 			| this(new Vector(x, y, z), name, weight, strength, agility, toughness, defaultBehaviorEnabled)
	 */
	@Raw
	public Unit(double x, double y, double z, String name, int weight, int strength, int agility, 
			int toughness, boolean defaultBehaviorEnabled) throws ModelException {
		this(new Vector(x, y, z), name, weight, strength, agility, toughness, defaultBehaviorEnabled);
	}
	
	/**
	 * Constructor for the Unit class.
	 * 
	 * @param position
	 * 			The position of this Unit.
	 * @param name
	 * 			The name of this Unit.
	 * @param weight
	 * 			The weight of this Unit.
	 * @param strength
	 * 			The strength of this Unit.
	 * @param agility
	 * 			The agility of this Unit.
	 * @param toughness
	 * 			The toughness of this Unit. 
	 * @param defaultBehaviorEnabled
	 * 			Whether or not this unit should have the default behavior.
	 * 
	 * @effect	Checks whether weight, strength, agility and toughness are valid and changes them appropriately.
	 * 			| this.weight = weight
	 *			| this.strength = strength
	 *			| this.agility = agility
	 *			| this.toughness = toughness
	 *			| checkValidProperty()
	 * @post	Sets health and stamina.
	 * 			| new.getCurrentHealth() == this.getHealth()
	 * 			| new.getCurrentStamina() == this.getStamina()
	 * @post	The new position equals the given position.
	 * 			| new.getPosition() == position
	 * @post	The new name equals the given name.
	 * 			| new.getName() == name
	 * @post	The orientation equals the default value PI/2.
	 * 			| new.getOrientation() == Math.PI/2
	 * @post	Sets hasToRest to false.
	 * 			| new.getHasToRest() == false
	 * @post	Sets timeToRest to 0.
	 * 			| new.getTimeToRest() == 0
	 * @post	Sets the state equal to State.NOTHING.
	 * 			| new.getState() == State.NOTHING
	 * @post	The statetime equal to -1.
	 * 			| new.getStatetime() == -1
	 * @post	Sets defaultBehavior equal to defaultBehaviorEnabled.
	 * 			| new.getDefaultBehaviorEnabled() == defaultBehaviorEnabled
	 * @post	Sets the amount of experience equal to 0.
	 * 			| new.getExperience() == 0
	 * @post	Sets the amount of experience needed for the next level equal to 10.
	 * 			| new.getExperienceToNextLevel() == 10
	 * @post	Sets resetPath to false.
	 * 			| new.getResetPath() == false
	 * @post	This unit is not terminated.
	 * 			| new.isTerminated() == false
	 * @throws ModelException
	 * 			If the given position is invalid.
	 * 			| if !isValidPosition(position)
	 * @throws ModelException
	 * 			If the given name is invalid.
	 * 			| if !isValidName(name)
	 */
	@Raw
	public Unit(Vector position, String name, int weight, int strength, int agility, 
			int toughness, boolean defaultBehaviorEnabled) throws ModelException {
		init = true;
		
		terminated = false;
		
		setStrength(strength);
		setAgility(agility);
		setWeight(weight);
		setToughness(toughness);
		
		setCurrentHealth(getHealth());
		setCurrentStamina(getStamina());
				
		setPosition(position.add(new Vector(0.5d, 0.5d, 0.5d)));
		
		setName(name);
		
		setOrientation(Math.PI/2);
		
		setHasEndGoal(false);
		
		setResetPath(false);
		pathToEndGoal = new ArrayList<int[]>();
		
		setHasToRest(false);
		setTimeToRest(0);
		
		setState(State.NOTHING);
		setStatetime(-1);
		setDefaultBehaviorEnabled(defaultBehaviorEnabled);
		
		setExperience(0);
		setExperienceToNextLevel(10);
		
		init = false;
	}
	
	/**
	 * Terminates this unit.
	 * @post	Terminated is true.
	 * 			| new.isTerminated() == true
	 * @post	This unit's state is State.NOTHING.
	 * 			| new.getState() == State.NOTHING
	 * @effect	If this unit is carrying something drop it.
	 * 			| if this.isCarrying() then dropCarryable()
	 */
	public void terminate() {
		terminated = true;
		setState(State.NOTHING);
		if (this.isCarrying()) {
			dropCarryable();
		}
		stopTask();	
	}
	
	/**
	 * Advances time by the given value of deltaT.
	 * 
	 * @param deltaT
	 * 			Time since the last time advanceTime was called.
	 * 
	 * @post	timeToRest increments by deltaT.
	 * @post	if timeToRest is greater than 180 set hasToRest to true.
	 * @post 	If this unit has no solid neighbouring cubes, start falling.
	 * 
	 * @effect	If this unit is falling, fall and lose hitpoints. 
	 * @effect	Else if this unit has to rest, start resting.
	 * @effect 	Else if the unit's state is WALKING, walk.
	 * @effect 	Else if the unit's state is WORKING, work.
	 * @effect	Else if the unit's state is RESTING, rest.
	 * @effect 	When the unit is attacking, lower the statetime by deltaT. If the statetime is less than zero, do nothing.
	 * @effect 	When the unit is doing nothing and has a goal, find a path.	
	 * @effect 	When the unit is doing nothing and does not have a goal, set the behavior to default behavior.	
	 * 
	 * @throws 	ModelException
	 * 			|if (deltaT <= 0 or deltaT > 0.2)
	 */
	public void advanceTime(double deltaT) throws ModelException{
		if (deltaT <= 0 || deltaT > 0.2)
			throw new ModelException("deltaT is out of bounds");
		setTimeToRest(timeToRest + deltaT);
		if (this.getTimeToRest() >= 180) 
			setHasToRest(true);
		
		if (!checkNeighbouringSolid() && state != State.FALLING)
			state = State.FALLING;
		if (state == State.FALLING) {
			setCurrentHealth(this.getCurrentHealth() - 10 * FALL_VELOCITY.length() * deltaT);
			fall(deltaT);
		}
		else if (this.isHasToRest()) {
			startRest();
		} else {
			if (this.getState() == State.WALKING) {
				move(deltaT);
			}
			else if (this.getState() == State.WORKING)
				work(deltaT);
			else if (this.getState() == State.RESTING)
				rest(deltaT);
			else if (this.getState() == State.ATTACKING) {
				setStatetime(statetime - deltaT);
				if (this.getStatetime() <= 0)
					setState(State.NOTHING);
			} else if (this.getState() == State.NOTHING && this.isHasEndGoal()) {
				int[] endGoalInt = endGoal.toIntArray();
				moveTo(endGoalInt[0], endGoalInt[1], endGoalInt[2]);
			}
			else if (this.getState() == State.NOTHING && this.isDefaultBehaviorEnabled()) 
				defaultBehavior();
			if (this.getTask() != null && this.isStatementCompleted()) {
				if (this.getTask().isTerminated()) {
					stopTask();
					return;
				}
				this.getTask().ExecuteNextActivity();
				setStatementCompleted(false);
			}
		}
	}
	
	//TODO doc
	/**
	 * Makes this unit execute default behavior. The unit picks the task from its faction's scheduler with the highest
	 * priority that is not already being executed and starts executing it.
	 * If it is not conducting an activity, the unit can either move to a random valid position, rest or work.
	 * 
	 * @effect	This unit moves to a random valid position, works, attacks a nearby enemy unit or rests.
	 * 			|if (
	 * 			|else
	 * 			| 	R = random.nextInt(4)
	 * 			| 	if R == 0 then
	 * 			|		Set<int[]> V = this.getWorld().getWalkables()
	 * 			|		moveTo(random element in V)
	 * 			| 	else if R == 1 then 
	 * 			|		randX = random int in [-1, 1]
	 * 			|		randY = random int in [-1, 1]
	 * 			|		randZ = random int in [-1, 1]
	 * 			|		startWork(cubePosition[0] + randX, cubePosition[1] + randY, cubePosition[2] + randZ)
	 * 			| 	else if R == 2 then
	 * 			|		for unit in getWorld().getUnits() do
	 * 			|			if |unitPos.getX() - this.getPosition().getX()| < 2 
	 *			|					and |unitPos.getY() - this.getPosition().getY()| < 2 
	 *			|					and |unitPos.getZ() - this.getPosition().getZ()| < 2 then
	 *			|				attack(unit)
	 *			| 	else then startRest()
	 */
	private void defaultBehavior(){
		if (!this.getFaction().getScheduler().getTasks().isEmpty() 
				&& (this.getFaction().getScheduler().getHighestPriorityTask() != null)){
			if (this.getTask() == null) {
				if (this.getFaction().getScheduler().getHighestPriorityTask() == null)
					return;
				this.getFaction().getScheduler().setScheduledForUnit(this, 
						this.getFaction().getScheduler().getHighestPriorityTask());
				setStatementCompleted(false);
				this.getTask().ExecuteNextActivity();
			} 
		}
		else {
			Random rand = new Random();
			int R = rand.nextInt(4);
			if (R ==0){
				List<int[]> V = this.getWorld().getWalkables();
				int[] next = V.get(rand.nextInt(V.size()));
				try {
					long startTime = System.currentTimeMillis();
					moveTo(next[0], next[1], next[2]);
					long endTime = System.currentTimeMillis();
					if (endTime - startTime > 150)
						System.out.println("time taken for moveto (default): " + Long.toString(endTime-startTime));
				} catch (ModelException e) {
				}
			}
			else if (R ==1) {
				int randX = rand.nextInt(3) - 1;
				int randY = rand.nextInt(3) - 1;
				int randZ = rand.nextInt(3) - 1;
				int[] cubePosition = this.getPosition().toIntArray();
				startWork(cubePosition[0] + randX, cubePosition[1] + randY, cubePosition[2] + randZ);
			}
			else if (R == 2) {
				Set<Unit> units = this.getWorld().getUnits();
				for (Unit unit : units) {
					Vector unitPos = unit.getPosition();
					if (Math.abs(unitPos.getX() - this.getPosition().getX()) < 2 
							&& Math.abs(unitPos.getY() - this.getPosition().getY()) < 2 
							&& Math.abs(unitPos.getZ() - this.getPosition().getZ()) < 2) {
						attack(unit);
						return;
					}
				}
			}
			else
				startRest();	
		}
	}
	
	//TODO oki?
	/**
	 * Used to stop this unit's task.
	 * For example when a certain cube is not reachable.
	 */
	public void stopTask() {
		if (this.getTask() == null)
			return;
		
		this.getTask().reset();
		setTask(null);
	}
	
	/**
	 * Use when the task is finished.
	 * 
	 * @effect	Sets the unit's task to null.
	 * 			| setTask(null)
	 */
	public void removeTask() {
		setTask(null);
	}

	/**
	 * Makes this unit start moving to the given adjacent cube.
	 * 
	 * @param xdiff
	 * 			The difference in x-coordinate, newX - currentX.
	 * @param ydiff
	 * 			The difference in y-coordinate, newY - currentY.
	 * @param zdiff
	 * 			The difference in z-coordinate, newZ - currentZ.
	 * 
	 * @post	Sets the currentGoal of this unit to the center of the given cube.
	 * 			| new.currentGoal = new Vector(floor(position.getX()) + 0.5, floor(position.getY()) + 0.5,
	 * 			|															floor(position.getZ()) + 0.5).add(diff)
	 * @post	If the currentGoal is not walkable then return.
	 * 			| if not getWorld().isWalkable(this.getCurrentGoal().toIntArray()) then return
	 * @post	Sets the currentSpeed and velocity of this unit.
	 * 			| if zdiff == 1 then new.currentSpeed = 0.5 * speedb
	 * 			| if zdiff == -1 then new.currentSpeed = 1.2 * speedb
	 * 			| if zdiff == 0 then new.currentSpeed = speedb
	 * 			| new.velocity = currentGoal.subtract(position).normalize()
	 * @post	Sets the orientation of this unit to face the walking direction.
	 * 			| new.orientation = Math.atan2(velocity.getY(), velocity.getX());
	 * @post	Sets the state of this unit to WALKING
	 * 			| new.getState() == State.WALKING
	 * @throws 	ModelException
	 * 			Throws a ModelException if the given cube is not adjacent or if the given cube is not a walkable position.
	 * 			| if (xdiff < -1 || xdiff > 1)
	 * 			| if (ydiff < -1 || ydiff > 1)
	 * 			| if (zdiff < -1 || zdiff > 1) 
	 */
	public void moveToAdjacent(int xdiff, int ydiff, int zdiff) throws ModelException {
		if (this.getStatetime() > 0)
			return;
		if (this.getState() != State.NOTHING && this.getState() != State.WORKING && this.getState() != State.WALKING)
			return;
		
		
		if (xdiff < -1 || xdiff > 1) 
			throw new ModelException();
		if (ydiff < -1 || ydiff > 1)
			throw new ModelException();
		if (zdiff < -1 || zdiff > 1)
			throw new ModelException();
		
		Vector diff = new Vector(xdiff, ydiff, zdiff);
		setStart(new Vector(this.getPosition()));
		setCurrentGoal(new Vector(Math.floor(this.getPosition().getX()) + 0.5d, 
				Math.floor(this.getPosition().getY()) + 0.5d,
				Math.floor(this.getPosition().getZ()) + 0.5d).add(diff));
		if (!this.getWorld().isWalkable(this.getCurrentGoal().toIntArray())) {
			setState(State.NOTHING);
			setHasEndGoal(false);
			return;
		}
		
		setVelocity(this.getCurrentGoal().subtract(this.getPosition()));
		if (this.getVelocity().isAlmostEqual(new Vector())) 
			return;
		setVelocity(this.getVelocity().normalize());
		
		setState(State.WALKING);
		setOrientation(Math.atan2(this.getVelocity().getY(), this.getVelocity().getX()));
	}
	
	/**
	 * Makes this unit start moving towards the given goal.
	 * 
	 * @param endGoalx
	 * 			The x-coordinate of the endgoal.
	 * @param endGoaly
	 * 			The y-coordinate of the endgoal.
	 * @param endGoalz
	 * 			The z-coordinate of the endgoal. 
	 * 
	 * @post	Sets the endGoal of this unit equal to the given endGoal.
	 * 			| endGoal = new Vector(endGoalx, endGoaly, endGoalz)
	 * @post	If the given endGoal is not walkable then set the state to NOTHING and return.
	 * 			| if not getWorld().isWalkable(new int[]{endGoalx, endGoaly, endGoalz}) then 
	 * 			|	setState(State.NOTHING)
	 * 			|	setHasEndGoal(false)
	 * 			|	return
	 * @post	Sets hasEndGoal to true.
	 * 			| hasEndGoal = true
	 * 
	 * @effect	| if |endGoalx - cubePosition[0]| <= 1 and |endGoaly - cubePosition[1]|<= 1 
	 * 			|		and |endGoalz - cubePosition[2]|<= 1 then
	 * 			|	moveToAdjacent(endGoalx - cubePosition[0], endGoaly - cubePosition[1], endGoalz - cubePosition[2])
	 * 			| else then
	 * 			|	findpath()
	 * 
	 * @throws ModelException
	 * 			Throws a ModelException if the given goal has an invalid position.
	 * 			| if (!getWorld().isWalkable(new int[]{endGoalx, endGoaly, endGoalz})
	 */
	public void moveTo(int endGoalx, int endGoaly, int endGoalz) throws ModelException{
		if (this.getStatetime() > 0)
			return;

		setEndGoal(new Vector(endGoalx, endGoaly, endGoalz));
		if (!this.getWorld().isWalkable(new int[]{endGoalx, endGoaly, endGoalz})) {
			setState(State.NOTHING);
			setHasEndGoal(false);
			
			if (this.getTask() != null) {
				stopTask();
			}
			
			return;
		}
		setHasEndGoal(true);	
		int dx = endGoalx - this.getPosition().toIntArray()[0];
		int dy = endGoaly - this.getPosition().toIntArray()[1];
		int dz = endGoalz - this.getPosition().toIntArray()[2];
		
		//We are already on the endgoal
		if (dx == 0 && dy == 0 && dz == 0) {
			setHasEndGoal(false);
			setSprinting(false);
			
			if (this.getTask() != null) {
				setStatementCompleted(true);
			}
		}
		else if(Math.abs(dx)<= 1 && Math.abs(dy)<= 1 && Math.abs(dz)<= 1) 
			moveToAdjacent(dx, dy, dz);
		else 
			findPath();
	}

	/**
	 * This unit starts sprinting.
	 * 
	 * @pre		The current stamina of this unit is greater than 0.
	 * 			| currentStamina > 0
	 * @pre 	This unit is walking.
	 * 			| state == State.WALKING
	 * @post	This unit starts sprinting.
	 * 			| new.isSprinting = true
	 */
	public void startSprint(){
		assert(this.getCurrentStamina() > 0);
		assert(this.getState() == State.WALKING);
		setSprinting(true);
	}

	/**
	 * This unit stops sprinting.
	 * 
	 * @pre		This unit is sprinting.
	 * 			| this.isSprinting == true
	 * @post	This unit stops sprinting.
	 * 			| new.isSprinting = false
	 */
	public void stopSprint(){
		assert(this.isSprinting());
		setSprinting(false);
	}

	/**
	 * Moves this unit towards its current goal. 
	 * (This method is only called from advanceTime so no formal documentation is given)
	 * 
	 * @param deltaT
	 * 			The time passed since the last update
	 * @post 	The unit moves towards its current goal. When sprinting this unit moves twice as fast.
	 * @post	If the unit is following another unit, the endgoal is the cube of the followed unit 
	 * 			or one of the cubes surrounding it. If the unit reaches the endgoal, then setStatementCompleted(true)
	 * 			and setFollowedUnit(null).
	 * @post 	If this unit has reached its current goal, the position will be set equal to the currentGoal and
	 * 			one experience point shall be gained. If the unit has an end goal, look for the next cube to 
	 * 			walk to. If resetPath is true then find a new path to the endGoal. If the current task of the unit is
	 * 			not null, setStatementCompleted(true).
	 */
	private void move(double deltaT){
		try {
			setPosition(this.getPosition().add(this.getVelocity().multiply(deltaT*this.getCurrentSpeed())));
		} catch (ModelException e) {
			e.printStackTrace();
		}
		if (this.isSprinting())
			setCurrentStamina(this.getCurrentStamina() - 0.1 * deltaT);
		
		if (this.getFollowedUnit() != null) {
			int[] unitPos = this.getFollowedUnit().getPosition().toIntArray();
			if (Math.abs(this.getPosition().toIntArray()[0] - unitPos[0]) <= 1 &&
					Math.abs(this.getPosition().toIntArray()[1] - unitPos[1]) <= 1 &&
					Math.abs(this.getPosition().toIntArray()[2] - unitPos[0]) <= 1) {
				setHasEndGoal(false);
				setSprinting(false);
				setStatementCompleted(true);
				setFollowedUnit(null);
				this.setState(State.NOTHING);
			} else if (Math.abs(unitPos[0] - this.getEndGoal().toIntArray()[0]) >= 1 ||
					Math.abs(unitPos[1] - this.getEndGoal().toIntArray()[1]) >= 1 ||
					Math.abs(unitPos[2] - this.getEndGoal().toIntArray()[2]) >= 1) {
				try {
					moveTo(unitPos[0], unitPos[1], unitPos[2]);
				} catch (ModelException e) {
					e.printStackTrace();
				}
			}	
			return;
		}
		
		double length = this.getCurrentGoal().subtract(this.getStart()).length();
		double lengthOnTheRoad = this.getPosition().subtract(this.getStart()).length();
		if (lengthOnTheRoad >= length){	
			if (this.isResetPath()) {
				findPath();
				setResetPath(false);
				return;
			}
			try {
				setPosition(this.getCurrentGoal());
				gainExperience(1);
			} catch (ModelException e) {
			}
			this.setState(State.NOTHING);
			if (this.isHasEndGoal()) {
				if (	Math.floor(this.getCurrentGoal().getX()) == Math.floor(this.getEndGoal().getX()) &&
						Math.floor(this.getCurrentGoal().getY()) == Math.floor(this.getEndGoal().getY()) &&
						Math.floor(this.getCurrentGoal().getZ()) == Math.floor(this.getEndGoal().getZ())) {
					
					setHasEndGoal(false);
					setSprinting(false);
					
					if (this.getTask() != null) {
						setStatementCompleted(true);
					}
				} else {
					try {
						int[] nextCube = pathToEndGoal.get(0);
						int[] cubePosition = this.getPosition().toIntArray();
						moveToAdjacent(nextCube[0] - cubePosition[0], nextCube[1] - cubePosition[1], 
								nextCube[2] - cubePosition[2]);
						pathToEndGoal.remove(0);
					} catch (ModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Finds a path to the current goal of this unit.
	 * This is an implementation of Dijkstra's shortest path algorithm.
	 * 
	 * @post	If the end position is not walkable return.
	 * 			| if not getWorld().isWalkable(endPosition) then return
	 * @post	If the current position is equal to the end position return.
	 * 			| if cubePosition[0] == endPosition[0] and cubePosition[1] == endPosition[1] and cubePosition[2] == endPosition[2] then return
	 * @post	If the end position is reachable via a path containing only walkable cubes then pathToEndGoal contains
	 * 			a shortest path towards this end goal.
	 * 			| V := getWorld().getWalkables()
	 * 			| R := emptySet
	 * 			| a := startPosition
	 * 			| z := endPosition
	 * 			| p := Map<int[], int[]>
	 * 			| L(a) := 0
	 * 			| for all v in V L(v) := Double.MAX_VALUE
	 * 			| while R does not contain z do
	 * 			|	pick v in V\R such that L(v) is minimal, if the minimal L(v) is Double.MAX_VALUE then return
	 * 			| 		if (this.getTask() != null) then stopTask()
	 * 			|	R := R.union({v})
	 * 			|	for each (v, v') such that v and v' are neighbours do
	 * 			|		if L(v) + distance(v, v') < L(v') then
	 * 			|			L(v') := L(v) + distance(v, v')
	 * 			|			p(v') := v
	 * 			| pathToEndGoal := (a = v0, v1, ..., vk = z) where p(vi) = v(i-1)
	 * 			| moveToAdjacent(pathToEndGoal.get(0)[0] - startPosition[0], pathToEndGoal.get(0)[1] - startPosition[1], 
	 *			|	pathToEndGoal.get(0)[2] - startPosition[2])
	 *			| pathToEndGoal.remove(0)
	 */
	private void findPath(){
		int[] startPosition = this.getPosition().toIntArray();
		int[] endPosition = this.getEndGoal().toIntArray();
		
		if (!this.getWorld().isWalkable(endPosition))
			return;
		if (startPosition[0] == endPosition[0] && startPosition[1] == endPosition[1] && startPosition[2] == endPosition[2])
			return;
		
		Set<int[]> R = new HashSet<int[]>();
		List<int[]> V = new ArrayList<>(this.getWorld().getWalkables());
		Map<int[], Double> L = new HashMap<int[], Double>();
		Map<int[], int[]> p = new HashMap<int[], int[]>();
		
		Iterator<int[]> ite = V.iterator();
		while (ite.hasNext()) {
			int[] current = ite.next();
			if (current[0] == startPosition[0] && current[1] == startPosition[1] && current[2] == startPosition[2]) {
				startPosition = current;
				break;
			} 
		}
		L.put(startPosition, new Double(0));
		ite = V.iterator();
		while (ite.hasNext()) {
			int[] current = ite.next();
			if (current[0] == endPosition[0] && current[1] == endPosition[1] && current[2] == endPosition[2]) {
				endPosition = current;
				break;
			}
		}
		ite = V.iterator();
		while (ite.hasNext()) {
			int[] current = ite.next();
			if (current != startPosition)
				L.put(current, Double.MAX_VALUE);
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		long startTime = System.currentTimeMillis();
		List<int[]> copyV = new ArrayList<int[]>(V);
		while (!R.contains(endPosition)) {
			ite = copyV.iterator();
			int[] minCube = new int[]{-1, -1, -1};
			Double minValue = Double.MAX_VALUE;
			while (ite.hasNext()) {
				int[] next = ite.next();
				if (L.get(next).compareTo(minValue) < 0) {
					minValue = L.get(next);
				}
			}
			if (minValue.equals(Double.MAX_VALUE)) {
				setState(State.NOTHING);
				setHasEndGoal(false);
				
				if (this.getTask() != null) {
					stopTask();
				}
				
				return;
			}
			ite = copyV.iterator();
			while (ite.hasNext()) {
				int[] next = ite.next();
				if (L.get(next).equals(minValue)) {
					minCube = next;
					break;
				}
			}
			
			R.add(minCube);
			copyV.remove(minCube);
			Set<int[]> neighbourCubes = this.getWorld().getEdgeMap().get(minCube);
			for (int[] neighbour : neighbourCubes) {
				double distance = new Vector(neighbour[0] - minCube[0], neighbour[1] - minCube[1], 
						neighbour[2] - minCube[2]).length();
				if (neighbour[2] - minCube[2] == 1) {
					distance *= 0.5d;
				} else if (neighbour[2] - minCube[2] == -1) {
					distance *= 1.2d;
				} 
				if (Double.sum(L.get(minCube), new Double(distance)) < L.get(neighbour).doubleValue()) {
					L.put(neighbour, Double.sum(L.get(minCube), new Double(distance)));
					p.put(neighbour, minCube);
				}
			}
			/*ite = V.iterator();
			while (ite.hasNext()) {
				int[] next = ite.next();
				if (Math.abs(next[0] - minCube[0]) <= 1 && Math.abs(next[1] - minCube[1]) <= 1 &&
						Math.abs(next[2] - minCube[2]) <= 1){
					double distance = new Vector(next[0] - minCube[0], next[1] - minCube[1], 
							next[2] - minCube[2]).length();
					if (next[2] - minCube[2] == 1) {
						distance *= 0.5d;
					} else if (next[2] - minCube[2] == -1) {
						distance *= 1.2d;
					} 
					if (Double.sum(L.get(minCube), new Double(distance)) < L.get(next).doubleValue()) {
						L.put(next, Double.sum(L.get(minCube), new Double(distance)));
						p.put(next, minCube);
					}
				}
			}*/
		}
		long endTime = System.currentTimeMillis();
		if (endTime - startTime > 150) 
			System.out.println("dikke loop: " + Long.toString(endTime - startTime));
		////////////////////////////////////////////////////////////////////////////
		
		int[] pathCube = endPosition;
		pathToEndGoal.clear();
		while (!pathCube.equals(startPosition)) {
			pathToEndGoal.add(0, pathCube.clone());
			pathCube = p.get(pathCube);
		}
		
		try {
			moveToAdjacent(pathToEndGoal.get(0)[0] - startPosition[0], pathToEndGoal.get(0)[1] - startPosition[1], 
					pathToEndGoal.get(0)[2] - startPosition[2]);
		} catch (ModelException e) {
			e.printStackTrace();
		}
		pathToEndGoal.remove(0);
	}

	/**
	 * Makes this unit attack the given unit.
	 * 
	 * @param victim
	 * 			The victim to attack.
	 * @pre		Statetime is less than or equal to 0.
	 * 			| statetime <= 0
	 * @pre 	The attacker and the victim are in adjacent cubes.
	 * 			|(Math.abs(victim.getPosition().getX() - position.getX())< 2 
				|&& Math.abs(victim.getPosition().getY() - position.getY())< 2 
				|&& Math.abs(victim.getPosition().getZ() - position.getZ())< 2)
	 * @post 	The attacker and the victim face each other. 
	 * 			|new.orientation = Math.atan2((victim.getPosition().getY() - position.getY()),
										(victim.getPosition().getX() - position.getX()))
	 * @post	State of the attacker is ATTACKING. Statetime equals one.
	 * 			|new.state = State.ATTACKING
	 * 			|new.statetime = 1
	 * @effect	The attacker attacks the victim and the victim defends itself.
	 * 			|victim.defend(this)
	 * 	
	 */
	public void attack(Unit victim){
		if (this.getStatetime() > 0)
			return;
		if (this.getFaction() == victim.getFaction())
			return;
		if (Math.abs(victim.getPosition().getX() - this.getPosition().getX()) < 2 
				&& Math.abs(victim.getPosition().getY() - this.getPosition().getY()) < 2 
				&& Math.abs(victim.getPosition().getZ() - this.getPosition().getZ()) < 2){
			
			setOrientation(Math.atan2((victim.getPosition().getY() - this.getPosition().getY()),
										(victim.getPosition().getX() - this.getPosition().getX())));
			victim.setOrientation(Math.atan2((this.getPosition().getY() - victim.getPosition().getY()),
												(this.getPosition().getX() - victim.getPosition().getX())));
			setState(State.ATTACKING);
			setStatetime(1);
			victim.defend(this);
			
			if (this.getTask() != null)
				setStatementCompleted(true);
		}
	}

	/**
	 * Makes the attacked unit defend itself by dodging, blocking, or taking damage.
	 * 
	 * @param attacker
	 * @effect	The unit has 0.2d * (double)agility/(double)attacker.getAgility() chance to dodge this attack.
	 * 			If the unit dodges he loses no health and moves to an adjacent valid cube in the xy-plane.
	 * 			| pDodge = 0.2d * (double)agility/(double)attacker.getAgility()
	 * 			| if (random.nextDouble() < pDodge) then
	 * 			| 	dodge()
	 * 			|	gainExperience(20)
	 * @effect 	If dodging fails this unit has a 0.25d *((double)strength + (double)agility)/((double)attacker.getStrength() + (double)attacker.getAgility())
	 * 			chance to block this attack. If the unit blocks he loses no health.
	 * 			| pBlock = 0.25d *((double)strength + (double)agility)/((double)attacker.getStrength() + (double)attacker.getAgility()) then
	 * 			| else if (random.nextDouble() < pBlock)
	 * 			|	gainExperience(20)
	 * @post	If the unit fails to dodge or block, this unit loses a portion of its currentHealth.
	 * 			| else
	 * 			| 	new.currentHealth = currentHealth - attacker.getStrength()/10
	 * @post	This unit's state is set to NOTHING, this overrides the statetime. Statetime is set to -1.
	 * 			| new.state = State.NOTHING
	 * 			| new.statetime = -1
	 */
	private void defend(Unit attacker){
		double pDodge = 0.2d * (double)this.getAgility()/(double)attacker.getAgility();
		double pBlock = 0.25d *((double)this.getStrength() + (double)this.getAgility())/
				((double)attacker.getStrength() + (double)attacker.getAgility());
		Random rand = new Random();
		if (rand.nextDouble() < pDodge) {
			dodge(rand);
			gainExperience(20);
		}
		else if(rand.nextDouble() < pBlock) {
			gainExperience(20);
		}
		else {
			setCurrentHealth(this.getCurrentHealth() - attacker.getStrength()/10);
		}
		setState(State.NOTHING);
		setStatetime(-1);
	}	

	/**
	 * Moves this unit to an adjacent valid cube in its xy-plane.
	 * 
	 * @post	This unit has moved to an adjacent valid cube in its xy-plane.
	 *			| List<Vector> validPos = new LinkedList<Vector>()
	 *			| for int xd from -1 to 1 do
	 *			|	for int yd from -1 to 1 do
	 *			|		if xd == 0 and yd == 0 then continue
	 *			|		Vector diff = new Vector(xd, yd, 0)
	 *			|		if getWorld().isWalkable(getPosition().add(diff).toIntArray()) then validPos.add(diff)
	 *			| int index  = random.nextInt(validPos.size())
	 *			| int[] jumpPos = validPos.get(index).toIntArray()
	 *			| setPosition(new Vector(cubePosition[0] + jumpPos[0] + random.nextDouble(), 
	 *			|		cubePosition[1] + jumpPos[1] + random.nextDouble(), cubePosition[2]));
	 *
	 */
	private void dodge(Random rand) {
		List<Vector> validPos = new LinkedList<Vector>();
		for(int xd = -1; xd <= 1; xd++) {
			for(int yd = -1; yd <= 1; yd++) {
				if (xd == 0 && yd == 0)
					continue;
				Vector diff = new Vector(xd, yd, 0);
				if (this.getWorld().isWalkable(this.getPosition().add(diff).toIntArray()))
					validPos.add(diff);
			}
		}
		int index = rand.nextInt(validPos.size());
		int[] jumpPos = validPos.get(index).toIntArray();
		double xRand = rand.nextDouble();
		double yRand = rand.nextDouble();
		try {
			int[] cubePosition = this.getPosition().toIntArray();
			setPosition(new Vector(cubePosition[0] + jumpPos[0] + xRand, cubePosition[1] + jumpPos[1] + yRand,
					cubePosition[2]));
		} catch (ModelException e) {
		}
	}
	
	/**
	 * This unit starts working.
	 * 
	 * @pre 	This unit's statetime is greater than 0.
	 * 			| statetime > 0
	 * @post	This unit's state is set to working and its busytime is set to 500 / strength and the workPosition
	 * 			is set to the given position.
	 * 			| if (this.getState() == State.NOTHING or this.getState() == State.WORKING)
	 *			| 		and (|x - cubePosition[0]| <= 1 and |y - cubePosition[1]| <= 1 and |z - cubePosition[2]| <= 1))
	 * 			| new.state = State.WORKING
	 * 			| new.busytime = 500/(double)strength
	 * 			| setWorkPosition(new int[]{x, y, z})
	 */
	public void startWork(int x, int y, int z){
		if (this.getStatetime() > 0)
			return;
		int dx = x - this.getPosition().toIntArray()[0];
		int dy = y - this.getPosition().toIntArray()[1];
		int dz = z - this.getPosition().toIntArray()[2];
		if ((this.getState() == State.NOTHING || this.getState() == State.WORKING)
				&& (Math.abs(dx) <= 1 && Math.abs(dy) <= 1 && Math.abs(dz) <= 1)){	
			setState(State.WORKING);
			setBusytime(500.0d/(double)this.getStrength());
			setWorkPosition(new int[]{x,y,z});
		}
	}
	
	/**
	 * Completes the current work task, earning 10 experience points.
	 * 
	 * @effect	If this unit is carrying something, drop it.
	 * 			| if isCarrying() then 
	 * 			|	dropCarryable()
	 * 			|	gainExperience(10)
	 * @effect	If the previous effect was not executed and there is a workshop at the workPosition and there are a log
	 * 			and boulder present on the workPosition increase this unit's toughness by 5 and this unit's weight
	 * 			by 2.
	 * 			| else if getWorld().getCube(workPosition[0], workPosition[1], workPosition[2] == CubeType.WORKSHOP and
	 * 			|		getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.LOG) != null and
	 * 			|		getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.BOULDER) != null then
	 * 			|	getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.LOG).terminate()
	 * 			|	getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.BOULDER).terminate()
	 * 			|	setToughness(this.getToughness() + 5)
	 * 			|	setWeight(this.getWeight() + 2)
	 * 			|	gainExperience(10);
	 * @effect	If the previous effects were not executed and there is a carryable on the workPosition, pick it up.
	 * 			| else if getWorld().getCarryableAt(workPosition[0], workPosition[1], workPosition[2]) != null then
	 * 			|	pickup(getWorld().getCarryableAt(workPosition[0], workPosition[1], workPosition[2]))
	 * 			|	gainExperience(10)
	 * @effect	If the previous effects were not executed and the workPosition is a WOOD or a ROCK remove that
	 * 			cube from the world.
	 * 			| else if getWorld().getCube(workPosition[0], workPosition[1], workPosition[2]) == CubeType.WOOD 
	 *			|		or getWorld().getCube(workPosition[0], workPosition[1], workPosition[2]) == CubeType.ROCK then
	 *			|	getWorld().removeCube(workPosition[0], workPosition[1], workPosition[2])
	 *			|	gainExperience(10)
	 *@effect	If this unit's task is not null, setStatementCompleted(true) after the work is done.
	 *			|if (this.getTask() != null)
	 *			|	setStatementCompleted(true);
	 */
	private void endWork(){
		int[] workPosition = this.getWorkPosition();
		try{
			if (this.isCarrying()){
				dropCarryable();
				gainExperience(10);
			}
			else if (this.getWorld().getCube(workPosition[0], workPosition[1], workPosition[2]).equals(CubeType.WORKSHOP)
					&& this.getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.LOG) != null
					&& this.getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.BOULDER) != null){
				this.getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.LOG).terminate();
				this.getWorld().getCarryableAtPositionOfType(workPosition[0], workPosition[1], workPosition[2], CarryableType.BOULDER).terminate();
				setToughness(this.getToughness() + 5);
				setWeight(this.getWeight() + 2);
				gainExperience(10);
			}
			else if (this.getWorld().getCarryableAt(workPosition[0], workPosition[1], workPosition[2]) != null){
				pickUp(this.getWorld().getCarryableAt(workPosition[0], workPosition[1], workPosition[2]));
				gainExperience(10);
			}
			else if (this.getWorld().getCube(workPosition[0], workPosition[1], workPosition[2]) == CubeType.WOOD 
					||this.getWorld().getCube(workPosition[0], workPosition[1], workPosition[2]) == CubeType.ROCK){
				this.getWorld().removeCube(workPosition[0], workPosition[1], workPosition[2]);
				gainExperience(10);
			}
		} catch(ModelException e){	
		}
		
		if (this.getTask() != null)
			setStatementCompleted(true);
	}
	
	/**
	 * Makes this unit work.
	 * 
	 * @param deltaT
	 * 			The time passed since the last update.
	 * @post	busytime decreases and if this unit is done working (busytime <= 0) the state is set to State.NOTHING .
	 */
	private void work(double deltaT){
		setBusytime(this.getBusytime() - deltaT);
		if (this.getBusytime() <= 0){
			setState(State.NOTHING);
			endWork();
		}
	}

	/**
	 * This unit starts resting.
	 * 
	 * @pre		This unit's statetime is greater than 0.
	 * 			| statetime > 0
	 * @post	This unit starts resting.
	 * 			| new.state = State.RESTING
	 * @post	This unit's statetime is set to 40/toughness.
	 * 			| new.statetime = 40/(double)toughness
	 * @post	This unit no longer has to rest.
	 * 			| hasToRest = false
	 * @post	This unit's timeToRest is set to 0.
	 * 			| timeToRest = 0
	 */
	public void startRest(){
		if (this.getStatetime() > 0)
			return;
		setState(State.RESTING);
		setStatetime(40/(double)toughness);
		setHasToRest(false);
		setTimeToRest(0);
	}

	/**
	 * The unit rests.
	 * 
	 * @param deltaT
	 * 			The time since the last update.
	 * @post	The statetime gets lowered by deltaT.
	 * @post	currentHealth increases until it reaches its maximum health.
	 * @post	If currentHealth has reached its maximum, currentStamina increases 
	 * 			until it reaches its maximum stamina.
	 * @post 	When currentHealth and currentStamina have reached their maxima,
	 * 			the unit rests during the rest of the statetime. 
	 * 			Afterwards, its state returns to NOTHING.
	 */
	private void rest(double deltaT){
		setStatetime(this.getStatetime() - deltaT);
		if (this.getCurrentHealth() + (double)this.getToughness()/200 *deltaT< this.getHealth())
			setCurrentHealth(this.getCurrentHealth() + (double)this.getToughness()/200 *deltaT);
		else if (this.getCurrentHealth() < this.getHealth())
			setCurrentHealth(this.getHealth());
		else if (this.getCurrentStamina() + (double)this.getToughness()/100 * deltaT < this.getStamina())
			setCurrentStamina(this.getCurrentStamina() + (double)this.getToughness()/100 * deltaT);
		else if (this.getCurrentStamina()< this.getStamina())
			setCurrentStamina(this.getStamina());
		else if (this.getStatetime() < 0)
			setState(State.NOTHING);
	}

	/**
	 * Checks whether this name is valid. The first letter should be a capital letter. The length of the name should be at least 2.
	 * Every character in the name must be a valid char, found in validChars.
	 * 
	 * @param name
	 * 			The name to check.
	 * 
	 * @return  If the given name is less than two characters long, returns false.
	 * 			| if name.length() < 2 then return false
	 * @return  Returns true iff the name is of the following form.
	 * 			| if name.matches([A-Z][validChars]*) then result == true
	 * 			| else then result == false
	 */
	@Raw
	public static boolean isValidName(String name) {
		if (name.length()<2)
			return false;
		Pattern p = Pattern.compile("[A-Z][" + validChars + "]*");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 * Checks whether weight, strength, agility and toughness are valid and changes them appropriately.
	 * 
	 * @post	If strength is less than minValue, sets strength to minValue and if strength is greater than
	 * 			maxValue sets strength to maxValue. minValue and maxValue are equal to 25, 100 if init is true
	 * 			and 1, 200 if init is false.
	 * 			| if init then minValue = 25 and maxValue = 100
	 * 			| else then minValue = 1 and maxValue = 200
	 * 			| if strength < minValue then new.strength = minValue
	 * 			| else if strength > maxValue then new.strength = maxValue
	 * @post	If agility is less than minValue, sets agility to minValue and if agility is greater than
	 * 			maxValue sets agility to maxValue. minValue and maxValue are equal to 25, 100 if init is true
	 * 			and 1, 200 if init is false.
	 * 			| if init then minValue = 25 and maxValue = 100
	 * 			| else then minValue = 1 and maxValue = 200
	 * 			| if agility < minValue then new.agility = minValue
	 * 			| else if agility > maxValue then new.agility = maxValue
	 * @post	If weight is less than minValue, sets weight to minValue and if weight is greater than
	 * 			maxValue sets weight to maxValue. minValue and maxValue are equal to 25, 100 if init is true
	 * 			and 1, 200 if init is false. If weight is less than (strength + agility) / 2, then weight
	 * 			is set equal to (strength + agility) / 2.
	 * 			| if init then minValue = 25 and maxValue = 100
	 * 			| else then minValue = 1 and maxValue = 200
	 * 			| if weight < (strength + agility) / 2 then new.weight = (strength + agility) / 2
	 * 			| if weight < minValue then new.weight = minValue
	 * 			| else if weight > maxValue then new.weight = maxValue
	 * @post	If toughness is less than minValue, sets toughness to minValue and if toughness is greater than
	 * 			maxValue sets toughness to maxValue. minValue and maxValue are equal to 25, 100 if init is true
	 * 			and 1, 200 if init is false.
	 * 			| if init then minValue = 25 and maxValue = 100
	 * 			| else then minValue = 1 and maxValue = 200
	 * 			| if toughness < minValue then new.toughness = minValue
	 * 			| else if toughness > maxValue then new.toughness = maxValue
	 */
	@Raw
	private void checkValidProperty(){
		int minValue = 1;
		int maxValue = 200;
		if (init) {
			minValue = 25;
			maxValue = 100;
		}
			
		if (this.getStrength() < minValue)
			setStrength(minValue);
		else if (this.getStrength() > maxValue)
			setStrength(maxValue);
		
		if (this.getAgility() < minValue)
			this.setAgility(minValue);
		else if (this.getAgility() > maxValue)
			setAgility(maxValue);
		
		if (this.getRealWeight() < (this.getStrength() + this.getAgility())/2)
			setWeight((this.getStrength() + this.getAgility())/2);
		else if (this.getRealWeight() < minValue)
			setWeight(minValue);
		else if (this.getRealWeight() > maxValue)
			setWeight(maxValue);
		
		if (this.getToughness() < minValue)
			setToughness(minValue);
		else if (this.getToughness() > maxValue)
			setToughness(maxValue);	
	}
	
	/**
	 * This unit gains experience equal to the given amount.
	 *
	 * @param add
	 * 			The experience to add.
	 * @post	Increases the experience by add.
	 * 			| experience += add
	 * @effect	If this unit has enough experience to level up, call increaseAttribute.
	 * 			| while experience >= experienceToNextLevel do
	 *			|	increaseAttribute()
	 *			| 	experienceToNextLevel += 10
	 */
	private void gainExperience(int add) {
		experience += add;
		while (experience >= experienceToNextLevel) {
			increaseAttribute();
			experienceToNextLevel += 10;
		}
	}
	
	/**
	 * Increases an attribute of this unit.
	 * 
	 * @post 	Increases the lowest attribute (strength, agility, toughness) of this unit.
	 * 			| if strength <= agility and strength <= toughness then
	 * 			|	setStrength(strength + 1)
	 * 			| else if agility <= toughness and agility <= toughness then
	 * 			|	setAgility(agility + 1)
	 * 			| else 
	 * 			|	setToughness(toughness + 1) 
	 */
	private void increaseAttribute(){
		if (this.getStrength() <= this.getAgility() && this.getStrength() <= this.getToughness())
			setStrength(this.getStrength() + 1);
		else if (this.getAgility() <= this.getToughness() && this.getAgility() <= this.getStrength())
			setAgility(this.getAgility() + 1);
		else
			setToughness(this.getToughness() + 1);
	}
	
	/**
	 * Pick up the given carryable.
	 * 
	 * @param carryable
	 * @effect	If this unit is not carrying something, pick it up.
	 * 			| if !isCarrying() then
	 * 			|	setCarryable(carryable)
	 * 			|	setCarrying(true)
	 * 			|	carryable.beingPickedUp()
	 */
	private void pickUp(Carryable carryable){
		if (! this.isCarrying()){
			setCarryable(carryable);
			setCarrying(true);
			carryable.beingPickedUp();
		}
	}
	
	/**
	 * If this unit is carrying something, drop it.
	 * 
	 * @effect	If this unit is carrying something, drop it.
	 * 			| if isCarrying() then
	 * 			|	setCarrying(false)
	 * 			|	getCarryable().setPosition(getPosition())
	 * 			|	getCarryable().beingDropped()
	 * 			|	getWorld().addCarryable(getCarryable())
	 * 			|	setCarryable(null)
	 */
	private void dropCarryable() {
		if (this.isCarrying()) {
			setCarrying(false);
			try {
				this.getCarryable().setPosition(this.getPosition());
			} catch (ModelException e) {
			}
			this.getCarryable().beingDropped();
			this.getWorld().addCarryable(this.getCarryable());
			this.setCarryable(null);
		}
	}
	
	/**
	 * If this unit has a neighbouring cube that is solid return true, otherwise return false.
	 * @return	If this unit is falling use a raised cubePosition (by +0.5 on z-axis) to check the neighbouring cubes.
	 * 			If the cubePosition has a solid neighbour return true, return false otherwise.
	 * 			| if getState() == State.FALLING then 
	 * 			|	cubePosition = getPosition.add(new Vector(0, 0, 0.5)).toIntArray()
	 * 			| else then
	 * 			|	cubePosition = getPosition.toIntArray()
	 * 			| for x, y, z from cubePosition[0]-1, cubePosition[1]-1, cubePosition[2]-1 to cubePosition[0]+1, cubePosition[1]+1, cubePosition[2]+1 do
	 * 			|	if getWorld().getCube(x, y, z).isSolid()
	 * 			|		result == true
	 * 			| result == false
	 */
	private boolean checkNeighbouringSolid(){
		int[] cubePosition = new int[3];
		if (this.getState() == State.FALLING)
			cubePosition = this.getPosition().add(new Vector(0.0d, 0.0d, 0.5d)).toIntArray();
		else 
			cubePosition = this.getPosition().toIntArray();
		if (cubePosition[2] == 0)
			return true;
		for (int x = cubePosition[0] - 1; x <= cubePosition[0] + 1; x++){
			for (int y = cubePosition[1] - 1; y <= cubePosition[1] + 1; y++){
				for (int z = cubePosition[2] - 1; z <= cubePosition[2] + 1; z++){
					try {
						CubeType cube = world.getCube(x, y, z);
						if (cube.isSolid())
							return true;
					} catch (ModelException e) {
					}
					
				}
			}
		} 
		return false;	
	}
	
	/**
	 * Makes this unit fall.
	 * (This method is only called from advanceTime and so no formal documentation is given)
	 * 
	 * @param deltaT	
	 * 			The passed time since the last update.
	 * @post	If checkNeighbouringSolid() then set the state of this unit to NOTHING.
	 * @effect	Add FALL_VELOCITY.multiply(deltaT) to this unit's position.
	 */
	private void fall(double deltaT){
		try {
			setPosition(this.getPosition().add(FALL_VELOCITY.multiply(deltaT)));
		} catch (ModelException e) {
			e.printStackTrace();
		}
		if (checkNeighbouringSolid())
			state = State.NOTHING;
	}

	
	/**
	 * @return the position
	 */
	@Basic @Raw
	public Vector getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 * @throws ModelException 
	 * 			| if !isValidPosition(position)
	 */
	@Basic @Raw
	public void setPosition(Vector position) throws ModelException {
		if(!World.isValidPosition(position, this.getWorld()))
			throw new ModelException("Trying to set an invalid position.");
		this.position = position;
	}

	/**
	 * @return the name
	 */
	@Basic @Raw
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 * @throws ModelException 
	 * 			| if !isValidName(name)
	 */
	@Basic @Raw
	public void setName(String name) throws ModelException {
		if (!isValidName(name))
			throw new ModelException("Trying to set an invalid name.");	
		this.name = name;
	}

	/**
	 * @return the defaultBehaviourEnabled
	 */
	@Basic @Raw
	public boolean isDefaultBehaviorEnabled() {
		return defaultBehaviorEnabled;
	}

	/**
	 * @param defaultBehaviourEnabled the defaultBehaviourEnabled to set
	 */
	@Basic @Raw
	public void setDefaultBehaviorEnabled(boolean defaultBehaviourEnabled) {
		this.defaultBehaviorEnabled = defaultBehaviourEnabled;
	}

	/**
	 * @return the state
	 */
	@Basic @Raw
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	@Basic @Raw
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the orientation
	 */
	@Basic @Raw
	public double getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	@Basic @Raw
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return if not isCarrying then result == weight
	 * 			else then result == weight + this.getCarryable().getWeight()
	 */
	@Basic @Raw
	public int getWeight() {
		if (!isCarrying())
			return weight;
		return weight + this.getCarryable().getWeight();
	}
	
	/**
	 * @return weight
	 */
	@Basic @Raw
	public int getRealWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 * @effect	Sets the new weight and then calls checkValidProperty()
	 * 			| new.getWeight() = weight
	 * 			| checkValidProperty()
	 */
	@Raw
	public void setWeight(int weight) {
		this.weight = weight;
		checkValidProperty();
	}

	/**
	 * @return the strength
	 */
	@Basic @Raw
	public int getStrength() {
		return strength;
	}

	/**
	 * @param strength the strength to set
	 * @effect	Sets the new strength and then calls checkValidProperty()
	 * 			| new.getStrength() = strength
	 * 			| checkValidProperty()
	 */
	@Raw
	public void setStrength(int strength) {
		this.strength = strength;
		checkValidProperty();
	}

	/**
	 * @return the agility
	 */
	@Basic @Raw
	public int getAgility() {
		return agility;
	}

	/**
	 * @param agility the agility to set
	 * @effect  Sets the new agility and then calls checkValidProperty()
	 * 			| new.getAgility() = agility
	 * 			| checkValidProperty()
	 */
	@Basic @Raw
	public void setAgility(int agility) {
		this.agility = agility;
		checkValidProperty();
	}

	/**
	 * @return the toughness
	 */
	@Basic @Raw
	public int getToughness() {
		return toughness;
	}

	/**
	 * @param toughness the toughness to set
	 * @effect  Sets the new toughness and then calls checkValidProperty()
	 * 			| new.getToughness() = toughness
	 * 			| checkValidProperty()
	 */
	@Basic @Raw
	public void setToughness(int toughness) {
		this.toughness = toughness;
		checkValidProperty();
	}

	/**
	 * @return the health
	 */
	@Basic @Raw
	public int getHealth() {
		return (int) (200.0d*(((double)weight)/100.0d)*(((double)toughness)/100.0d));
	}

	/**
	 * @return the stamina
	 */
	@Basic @Raw
	public int getStamina() {
		return (int)Math.ceil(200.0d*(((double)weight)/100.0d)*(((double)toughness)/100.0d));
	}

	/**
	 * @return the currentHealth
	 */
	@Basic @Raw
	public double getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 * 
	 * @pre		The given currentHealth should be between 0 and this.getHealth()
	 * 			| currentHealth > 0 && currentHealth <= this.getHealth()
	 */
	@Basic @Raw
	public void setCurrentHealth(double currentHealth) {
		assert (currentHealth <= this.getHealth());
		this.currentHealth = currentHealth;
		if (currentHealth < 0)
			terminate();
	}

	/**
	 * @return the currentStamina
	 */
	@Basic @Raw
	public double getCurrentStamina() {
		return currentStamina;
	}

	/**
	 * @param currentStamina the currentStamina to set
	 *
	 * @pre		The given currentStamina should be between 0 and this.getStamina()
	 * 			| currentStamina > 0 && currentStamina <= this.getStamina()
	 */
	@Basic @Raw
	public void setCurrentStamina(double currentStamina) {
		assert (currentStamina > 0 && currentStamina <= this.getStamina());
		this.currentStamina = currentStamina;
	}

	/**
	 * @return the isSprinting
	 * 			| return sprinting && this.getState() == State.WALKING
	 */
	@Basic @Raw
	public boolean isSprinting() {
		return sprinting && this.getState() == State.WALKING;
	}

	/**
	 * @param isSprinting the isSprinting to set
	 */
	@Basic @Raw
	public void setSprinting(boolean isSprinting) {
		this.sprinting = isSprinting;
	}

	
	/**
	 * 
	 * @return 1.5d*(((double)(strength + agility)) / (200.0d * (((double)weight) / 100.0d)))
	 */
	@Basic @Raw
	public double getSpeedb() {
		return 1.5d*(((double)(getStrength() + getAgility())) / (200.0d * (((double)getWeight()) / 100.0d)));
	}

	/**
	 * @return the statetime
	 */
	@Basic @Raw
	public double getStatetime() {
		return statetime;
	}

	/**
	 * @param statetime the statetime to set
	 */
	@Basic @Raw
	public void setStatetime(double statetime) {
		this.statetime = statetime;
	}

	/**
	 * @return the endGoal
	 */
	@Basic @Raw
	public Vector getEndGoal() {
		return new Vector(endGoal);
	}

	/**
	 * @param endGoal the endGoal to set
	 * @throws ModelException 
	 * 			If the given endgoal is invalid.
	 * 			| if !isValidPosition(endGoal)
	 */
	@Basic @Raw
	public void setEndGoal(Vector endGoal) throws ModelException {
		if (!World.isValidPosition(endGoal, this.getWorld()))
			throw new ModelException("Endgoal is an invalid position.");
		this.endGoal = endGoal;
	}

	/**
	 * @return the start
	 */
	@Basic @Raw
	public Vector getStart() {
		return new Vector(start);
	}

	/**
	 * @param start the start to set
	 * @throws ModelException 
	 *			If the given start position is invalid.
	 *			| if !isValidPosition(start)
	 */
	@Basic @Raw
	public void setStart(Vector start) throws ModelException {
		if (!World.isValidPosition(start, this.getWorld()))
			throw new ModelException("Invalid start position.");
		this.start = start;
	}

	/**
	 * @return the currentGoal
	 */
	@Basic @Raw
	public Vector getCurrentGoal() {
		return new Vector(currentGoal);
	}

	/**
	 * @param currentGoal the currentGoal to set
	 * @throws ModelException 
	 *			If the currentGoal is invalid.
	 *			| if !isValidPosition(currentGoal)
	 */
	@Basic @Raw
	public void setCurrentGoal(Vector currentGoal) throws ModelException {
		if (!World.isValidPosition(currentGoal, this.getWorld()))
			throw new ModelException("The given currentGoal is invalid.");
		this.currentGoal = currentGoal;
	}

	/**
	 * @return the hasEndGoal
	 */
	@Basic @Raw
	public boolean isHasEndGoal() {
		return hasEndGoal;
	}

	/**
	 * @param hasEndGoal the hasEndGoal to set
	 */
	@Basic @Raw
	public void setHasEndGoal(boolean hasEndGoal) {
		this.hasEndGoal = hasEndGoal;
	}

	/**
	 * @return the busytime
	 */
	@Basic @Raw
	public double getBusytime() {
		return busytime;
	}

	/**
	 * @param busytime the busytime to set
	 */
	@Basic @Raw
	public void setBusytime(double busytime) {
		this.busytime = busytime;
	}

	/**
	 * @return the hasToRest
	 */
	@Basic @Raw
	public boolean isHasToRest() {
		return hasToRest;
	}

	/**
	 * @param hasToRest the hasToRest to set
	 */
	@Basic @Raw
	public void setHasToRest(boolean hasToRest) {
		this.hasToRest = hasToRest;
	}

	/**
	 * @return the timeToRest
	 */
	@Basic @Raw
	public double getTimeToRest() {
		return timeToRest;
	}

	/**
	 * @param timeToRest the timeToRest to set
	 */
	@Basic @Raw
	public void setTimeToRest(double timeToRest) {
		this.timeToRest = timeToRest;
	}

	/**
	 * @return the velocity
	 */
	@Basic @Raw
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	@Basic @Raw
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Returns the current speed of this unit.
	 * 
	 * @return	If this unit is walking return the current speed.
	 * 			| if getState() == State.WALKING then
	 * 			|	multiplier = 1
	 * 			|	if getVelocity().getZ() <= 0 then
	 * 			|		multiplier = 0.5
	 * 			|	if getVelocity().getZ() >= 0 then
	 * 			|		multiplier = 1.2 
	 * 			|	if isSprinting() then
	 * 			|		return 2 * multiplier * getSpeedb()
	 * 			|	else
	 * 			|		return multiplier * getSpeedb()
	 */
	public double getCurrentSpeed() {
		if (this.getState() != State.WALKING)
			return 0.0d;
		
		double multiplier = 1;
		if (!Util.fuzzyLessThanOrEqualTo(this.getVelocity().getZ(), 0))
			multiplier = 0.5;
		if (!Util.fuzzyGreaterThanOrEqualTo(this.getVelocity().getZ(), 0))
			multiplier = 1.2;
		
		if (this.isSprinting())
			return 2 * multiplier * getSpeedb();
		else
			return multiplier * getSpeedb();
	}

	/**
	 * @return the world
	 */
	@Basic @Raw
	public World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 * @pre world exists
	 * 			| world != null
	 * @post	This unit's world is equal to the given world.
	 * 			| new.getWorld() == world
	 * @throws ModelException
	 * 			If this unit's position is not walkable in the given world.
	 * 			| if !world.isWalkable(this.getPosition().toIntArray())
	 */
	@Raw
	public void setWorld(World world) throws ModelException{
		if(world == null)
			return;
		if (!world.isWalkable(this.getPosition().toIntArray()))
			throw new ModelException("Trying to add a unit to a world with an invalid position");
		this.world = world;
	}
	
	/**
	 * @param faction
	 */
	@Basic @Raw
	public void setFaction(Faction faction){
		this.faction = faction;
	}
	
	/**
	 * @return the faction
	 */
	@Basic @Raw
	public Faction getFaction(){
		return faction;
	}
	
	/**
	 * @return isTerminated
	 */
	@Basic 
	public boolean isTerminated() {
		return terminated;
	}
	
	/**
	 * @return the carryable
	 */
	@Basic 
	public Carryable getCarryable() {
		return carryable;
	}

	/**
	 * @return the carrying
	 */
	@Basic 
	public boolean isCarrying() {
		return carrying;
	}

	/**
	 * @param carrying the carrying to set
	 */
	@Basic 
	public void setCarrying(boolean carrying) {
		this.carrying = carrying;
	}

	/**
	 * @param carryable the carryable to set
	 */
	@Basic 
	public void setCarryable(Carryable carryable) {
		this.carryable = carryable;
	}

	/**
	 * @return the workPosition
	 */
	@Basic 
	public int[] getWorkPosition() {
		return workPosition;
	}

	/**
	 * @param workPosition the workPosition to set
	 */
	@Basic 
	public void setWorkPosition(int[] workPosition) {
		this.workPosition = workPosition;
	}

	/**
	 * @return the resetPath
	 */
	@Basic 
	public boolean isResetPath() {
		return resetPath;
	}

	/**
	 * @param resetPath the resetPath to set
	 */
	@Basic 
	public void setResetPath(boolean resetPath) {
		this.resetPath = resetPath;
	}

	/**
	 * @return the pathToEndGoal
	 */
	@Basic 
	public List<int[]> getPathToEndGoal() {
		return pathToEndGoal;
	}

	/**
	 * @return the experienceToNextLevel
	 */
	@Basic @Raw
	public int getExperienceToNextLevel() {
		return experienceToNextLevel;
	}

	/**
	 * @param experienceToNextLevel the experienceToNextLevel to set
	 */
	@Basic @Raw
	public void setExperienceToNextLevel(int experienceToNextLevel) {
		this.experienceToNextLevel = experienceToNextLevel;
	}

	/**
	 * @return the experience
	 */
	@Basic @Raw
	public int getExperience() {
		return experience;
	}

	/**
	 * @param experience the experience to set
	 */
	@Basic @Raw
	public void setExperience(int experience) {
		this.experience = experience;
	}

	/**
	 * @return the task
	 */
	@Basic
	public Task getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	@Basic
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @return the statementCompleted
	 */
	@Basic
	public boolean isStatementCompleted() {
		return statementCompleted;
	}

	/**
	 * @param statementCompleted the statementCompleted to set
	 */
	@Basic
	public void setStatementCompleted(boolean statementCompleted) {
		this.statementCompleted = statementCompleted;
	}

	/**
	 * @return the followedUnit
	 */
	@Basic
	public Unit getFollowedUnit() {
		return followedUnit;
	}

	/**
	 * @param followedUnit the followedUnit to set
	 */
	@Basic
	public void setFollowedUnit(Unit followedUnit) {
		this.followedUnit = followedUnit;
	}
}