package hillbillies.model.world;

import java.util.Random;

import ogp.framework.util.ModelException;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;

/**
 * A Carryable class for the game Hillbillies.
 * 
 * @invar	The Carryable's position is valid and passable.
 * @invar	The Carryable's weight lies in between 10 and 50.
 * 
 * @author HF corp.
 * @version 1.0
 */
public abstract class Carryable {
	
	private Vector position;
	private int weight;
	private World world;
	private static final Vector FALL_VELOCITY = new Vector(0,0,-3);
	private boolean terminated;
	/**
	 * Variable to signify that the Carryable has been picked up by a Unit.
	 */
	private boolean pickedUp;
	public enum CarryableType{
		LOG, BOULDER
	};
	
	
	/**
	 * Constructor for the Caryable class.
	 * 
	 * @param position
	 * 			The position of this Carryable.
	 * @effect	Sets the weight of this carryable equal to a value between 10 and 50 (inclusive).
	 * @post	| new.isPickedUp() == false
	 * @post	| new.isTerminated() == false
	 */
	@Model
	protected Carryable(Vector position){
		try {
			setPosition(position.add(new Vector(0.5d, 0.5d, 0.5d)));
		} catch (ModelException e) {
			e.printStackTrace();
		}
		Random rand = new Random();
		setWeight(rand.nextInt(41) + 10);
		terminated = false;
		pickedUp = false;
	}
	
	/**
	 * Advances time by the given value of deltaT.
	 * 
	 * @param deltaT
	 * 			Time since the last time advanceTime was called.
	 * @effect	If the cube under the Carryable is not solid, the Carryable falls.
	 * @throws 	ModelException
	 * 			| if (deltaT <= 0 or deltaT > 0.2)
	 */
	public final void advanceTime(double deltaT) throws ModelException{
		if (deltaT <=0 || deltaT >0.2)
			throw new ModelException();
		int[] cubePosition = this.getPosition().toIntArray();
		if (cubePosition[2] != 0) {
			try {
				CubeType cube = world.getCube(cubePosition[0], cubePosition[1], cubePosition[2]-1);
				if (!cube.isSolid())
					fall(deltaT);
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Makes the carryable fall.
	 * @param deltaT
	 */
	private final void fall(double deltaT){
		try {
			setPosition(this.getPosition().add(FALL_VELOCITY.multiply(deltaT)));
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This Carryable is being picked up by a unit.
	 */
	public final void beingPickedUp(){
		pickedUp = true;
	}
	
	/**
	 * This Carryable is dropped by a unit.
	 */
	public final void beingDropped() {
		pickedUp = false;
	}
	
	/**
	 * Terminate this carryable.
	 */
	public final void terminate(){
		terminated = true;
	}
	
	/**
	 * Returns the carryable type of this carryable.
	 * @return
	 */
	public abstract CarryableType getCarryableType();
	
	/**
	 * Returns the position of this carryable.
	 * @return
	 */
	@Basic
	public final Vector getPosition(){
		return position;
	}

	/**
	 * Sets the position of this carryable equal to the given position.
	 * @param position
	 * @throws ModelException
	 * 			If the given position is invalid, throw a ModelException.
	 */
	public final void setPosition(Vector position) throws ModelException{
		if (!World.isValidPosition(position, world))
			throw new ModelException("The carryable object has an invalid position.");
		this.position = position;
	}
	
	/**
	 * Returns the weight of this carryable.
	 * @return
	 */
	@Basic
	public final int getWeight(){
		return weight;
	}
	
	/**
	 * Sets the weight of this unit equal to the given weight.
	 * @param weight
	 */
	@Basic
	public final void setWeight(int weight){
		this.weight = weight;
	}

	/**
	 * @return the terminated
	 */
	@Basic
	public final boolean isTerminated() {
		return terminated;
	}

	/**
	 * @return the pickedUp
	 */
	@Basic
	public final boolean isPickedUp() {
		return pickedUp;
	}

	/**
	 * @return the world
	 */
	@Basic
	public final World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	@Basic
	public final void setWorld(World world) {
		this.world = world;
	}
}
