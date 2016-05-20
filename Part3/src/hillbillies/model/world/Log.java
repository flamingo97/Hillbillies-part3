package hillbillies.model.world;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * A Log class for the game Hillbillies, this is a special case of a carryable.
 * 
 * @author HF corp.
 * @version 1.0
 */

public class Log extends Carryable {
	
	/**
	 * Constructor for the Log class.
	 * 
	 * @param position
	 * 			The position of this Log.
	 */
	public Log(Vector position) {
		super(position);
	}

	/**
	 * @return the CarryableType (log or boulder)
	 */
	@Override @Basic
	public CarryableType getCarryableType() {
		return CarryableType.LOG;
	}

}
