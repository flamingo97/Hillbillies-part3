package hillbillies.model.world;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * An enum to work with cubetypes.
 * 
 * @author HF corp.
 * @version 1.0
 */
public enum CubeType {
	AIR(0, false), ROCK(1, true), WOOD(2, true), WORKSHOP(3, false);
	
	private final int typeInt;
	private final boolean solid;
	
	/**
	 * Constructor for CubeType.
	 * @param typeInt
	 * 			The typeInt of this CubeType.
	 * @param solid
	 * 			Whether or not this CubeType is solid.
	 */
	private CubeType(int typeInt, boolean solid) {
		this.typeInt = typeInt;
		this.solid = solid;
	}
	
	/**
	 * Returns the typeInt of this CubeType.
	 * @return
	 */
	@Basic
	public int getTypeInt() {
		return typeInt;
	}
	
	/**
	 * Returns the solid of this CubeType.
	 * @return
	 */
	@Basic
	public boolean isSolid() {
		return solid;
	}
}
