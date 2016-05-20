package hillbillies.model.world;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * A Boulder class for the game Hillbillies, , this is a special case of a carryable.
 * 
 * @author HF corp.
 * @version 1.0
 */
public class Boulder extends Carryable {
	
	/**
	 * Constructor for the Boulder class.
	 * 
	 * @param position
	 * 			The position of this Boulder.
	 */
	public Boulder(Vector position) {
		super(position);
	}

	
	/**
	 * @return the CarryableType (log or boulder)
	 */
	@Override @Basic
	public CarryableType getCarryableType() {
		return CarryableType.BOULDER;
	}

}
