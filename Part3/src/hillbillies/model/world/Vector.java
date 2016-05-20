package hillbillies.model.world;

import ogp.framework.util.ModelException;
import ogp.framework.util.Util;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Value;

/**
 * A class to work with 3D vectors.
 * 
 * @author HF corp.
 * @version 1.0
 */
@Value
public class Vector {
	
	/**
	 * Variables registering the coordinates of this vector.
	 */
	private final double x,y,z;
	
	/**
	 * Creates a new vector with the given coordinates.
	 * 
	 * @param x
	 * 			The x-coordinate of this vector.
	 * @param y
	 * 			The y-coordinate of this vector.
	 * @param z
	 * 			The z-coordinate of this vector.
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a new vector equal to the given vector.
	 * @param vector
	 * 			The given vector.
	 * @effect	Sets this vector equal to the given vector.
	 * 			| this(vector.getX(), vector.getY(), vector.getZ())
	 */
	public Vector(Vector vector) {
		this(vector.getX(), vector.getY(), vector.getZ());
	}
	
	/**
	 * Creates a zero-vector.
	 * 
	 * @effect	Sets the coordinates of this vector equal to 0.
	 * 			| this(0, 0, 0)
	 */
	public Vector() {
		this(0.0d, 0.0d, 0.0d);
	}
	
	/**
	 * Returns a vector wich is the sum of this vector and the given vector.
	 * 
	 * @param other
	 * 			The vector to add.
	 * @return
	 * 			| result == new Vector(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ())
	 */
	public Vector add(Vector other) {
		return new Vector(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
	}
	
	/**
	 * Returns a vector wich is the product of this vector and the given scalar.
	 * 
	 * @param scalar
	 * 			The given scalar with wich to multiply.
	 * @return	
	 * 			| result == new Vector(scalar * this.getX(), scalar * this.getY(), scalar*this.getZ())
	 */
	public Vector multiply(double scalar){
		return new Vector(scalar * this.getX(), scalar * this.getY(), scalar*this.getZ());
	}
	
	/**
	 * Returns a vector wich is equal to the given vector subtracted from this vector.
	 * 
	 * @param other
	 * 			The vector to subtract.
	 * @return
	 * 			| result == new Vector(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ())
	 */
	public Vector subtract(Vector other){
		return new Vector(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
	}
	
	/**
	 * Returns the length of this vector.
	 * 
	 * @return
	 * 			| result == Math.sqrt(this.getX()*this.getX() + this.getY()*this.getY() + this.getZ()*this.getZ())
	 */
	public double length() {
		return Math.sqrt(this.getX()*this.getX() + this.getY()*this.getY() + this.getZ()*this.getZ());
	}
	
	/**
	 * Returns a new vector wich is equal to this vector normalized, the length shall be 1.
	 * If this vector has a length of 0, a ModelException is thrown.
	 * 
	 * @return
	 * 			| result == new Vector(this.getX() / l, this.getY() / l, this.getZ() / l)
	 * @throws ModelException 
	 * 			| if (length() == 0)
	 */
	public Vector normalize() throws ModelException {
		double l = this.length();
		if (l == 0) {
			throw new ModelException("Trying to normalize the zero-vector.");
		}
		return new Vector(this.getX() / l, this.getY() / l, this.getZ() / l);
	}
	
	/**
	 * Returns true if this vector is almost equal to the given vector. 
	 * Each component of this vector is has at most a difference of Unit.EPS with the components of the other vector.
	 * 
	 * @param other
	 * 			The vector to compare with.
	 * @return	Returns true if this vector is almost equal to the given vector. 
	 * 			| if (Util.fuzzyEquals(this.getX(), other.getX()) && Util.fuzzyEquals(this.getY(), other.getY()) 
				| && Util.fuzzyEquals(this.getZ(), other.getZ()))
	 * 			|	then return true
	 * 			| else
	 * 			| 	then return false
	 * 			
	 */
	public boolean isAlmostEqual(Vector other) {
		if (Util.fuzzyEquals(this.getX(), other.getX()) && Util.fuzzyEquals(this.getY(), other.getY()) 
				&& Util.fuzzyEquals(this.getZ(), other.getZ()))
			return true;
		return false;
	}
	
	/**
	 * Gives a visual representation of this vector.
	 */
	public void print() {
		System.out.println("x: " + this.getX() + "\ty: " + this.getY() + "\tz: " + this.getZ());
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	@Immutable
	public double getX() {
		return x;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	@Immutable
	public double getY() {
		return y;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	@Immutable
	public double getZ() {
		return z;
	}
	
	/**
	 * Returns a double array with the coordinates of this vector.
	 * 
	 * @return
	 * 			| result == new double[]{this.getX(), this.getY(), this.getZ()}
	 */
	public double[] toArray() {
		return new double[]{this.getX(), this.getY(), this.getZ()};
	}
	
	/**
	 * Returns an int array with the coordinates of this vector rounded down.
	 * 
	 * @return
	 * 			| result == new int[]{(int)this.getX(), (int)this.getY(), (int)this.getZ()}
	 */
	public int[] toIntArray() {
		return new int[]{(int)this.getX(), (int)this.getY(), (int)this.getZ()};
	}
}
