package hillbillies.model.world;

import hillbillies.model.world.Carryable.CarryableType;
import hillbillies.model.world.Unit.State;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ogp.framework.util.ModelException;
import be.kuleuven.cs.som.annotate.Basic;

/**
 * A class to work with the world of the Hillbillies.
 * 
 * @invar	This world has at most MAX_FACTIONS amount of factions.
 * @invar	This world has at most MAX_UNITS_PER_WORLD amount of units.
 * 
 * @author HF corp.
 * @version 1.0
 */
public class World {
	
	private static final String[] FIRST_NAMES = {"Bob", "Larry", "Jules", "Eustache", "Werner", "Jeemes o\'Pierre", "Kurt",
		"Bert", "Bart", "Abertha", "Siegfriederique", "Sieglinderina", "Siegmunderino", "FINDUSGOD", "Usnavy", 
		"Wendeeh", "Voldemort", "Boldemort", "Barry", "Eric", "Nikita", "Hans", "Stefaan", "Thomas", "Wimotheus"};
	private static final String[] LAST_NAMES = {"Van Winckel", "Steegmans", "Vaes", "Veys", "Van Riet", "Goemans",
		"Poetter", "Spazzhands"};
	
	private static final int MAX_FACTIONS = 5;
	private static final int MAX_UNITS_PER_FACTION = 50;
	private static final int MAX_UNITS_PER_WORLD = 100;
		
	private double timeTillCaveIn;
	
	//every spawnpoints has 3 coordinates, int[3]
	private List<int[]> spawnPoints;
	private List<int[]> walkables;
	private Map<int[], Set<int[]>> edgeMap;
	private Set<int[]> cubesToRemove;
	private CubeType[][][] cubes;
	private Set<Faction> factions;
	private Set<Carryable> carryables;
	private ConnectedToBorder connected;
	//If terrain changes, use this thing!
	private TerrainChangeListener modelListener;
	
	/**
	 * The constructor for this world.
	 * @param terrainTypes
	 * @param modelListener
	 * @post	connected is a new ConnectedToBorder(sizeX, sizeY, sizeZ).
	 * @post	cubes is a new array constructed from the given terrainTypes.
	 * @effect	createSpawnPoints()
	 * @effect	createWalkables()
	 * @effect	setModelListener(modelListener)
	 * @effect	setTimeTillCaveIn(0)
	 */
	public World(int[][][] terrainTypes, TerrainChangeListener modelListener) {
		int sizeX = terrainTypes.length;
		int sizeY = terrainTypes[0].length;
		int sizeZ = terrainTypes[0][0].length;
		
		factions = new HashSet<Faction>();
		carryables = new HashSet<Carryable>();
		
		connected = new ConnectedToBorder(sizeX, sizeY, sizeZ);

		cubes = new CubeType[sizeX][sizeY][sizeZ];
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					for (CubeType checkType : CubeType.values()) {
						if (checkType.getTypeInt() == terrainTypes[x][y][z]) {
							cubes[x][y][z] = checkType;
							break;
						}
					}
					if (!cubes[x][y][z].isSolid())
						connected.changeSolidToPassable(x, y, z);
				}
			}
		}
		
		walkables = new ArrayList<int[]>();
		edgeMap = new HashMap<int[], Set<int[]>>();
		createWalkables();
		
		spawnPoints = new ArrayList<int[]>();
		createSpawnPoints();
		
		cubesToRemove = new HashSet<int[]>();
		
		setModelListener(modelListener);
		
		setTimeTillCaveIn(0);
	}
	
	/**
	 * Advances the time for this world with the given deltaT.
	 * @param deltaT
	 * @post	timeTillCaveIn += deltaT
	 * @effect	If the timeTillCaveIn is greater than 5.0d set timeTillCaveIn equal to 0 and call checkAllConnected()
	 * @effect	For faction in factions do faction.advanceTime(deltaT)
	 * @effect	For carryable in carryables do carryable.advanceTime(deltaT)
	 * @effect	For faction in factinos do faction.checkTerminated()
	 * @post	Remove the terminated or picked up carryables from the carryable list.
	 * @throws ModelException
	 * 			If the given deltaT is out of bounds, throw a ModelException
	 */
	public void advanceTime(double deltaT) throws ModelException {
		if (deltaT <= 0 || deltaT > 0.2)
			throw new ModelException("DeltaT is out of bounds: " + Double.toString(deltaT));
		
		timeTillCaveIn += deltaT;
		
		//Updating units and carryables.
		for (Faction faction : factions) {
			faction.advanceTime(deltaT);
		}
		for (Carryable carryable : carryables){
			carryable.advanceTime(deltaT);
		}
		//Checking for terminated units and carryables.
		Iterator<Faction> factionIterator = this.getFactions().iterator();
		while (factionIterator.hasNext()) {
			Faction next = factionIterator.next();
			next.checkTerminated();
			if (next.isTerminated())
				factionIterator.remove();
		}
		
		Iterator<Carryable> iterator = carryables.iterator();
		while (iterator.hasNext()) {
			Carryable carryable = iterator.next();
			if (carryable.isTerminated() || carryable.isPickedUp())
				iterator.remove();
		}
		//Updating the gameworld.
		if (!cubesToRemove.isEmpty()) {
			removeCubes();
			cubesToRemove.clear();
		}
		if (timeTillCaveIn >= 5.0d) {
			checkAllConnected();
			timeTillCaveIn = 0.0d;
		}
	}
	
	/**
	 * Checks which cubes should collapse.
	 * @effect	removedCubes is a new Set<int[]>
	 * 			for x, y, z from 0, 0, 0 to sizeX, sizeY, sizeZ do
	 * 				if !isConnected(x, y, z) and getCube(x, y, z).isSolid() then
	 * 					removeCube(x, y, z)
	 * 			if removedCubes is not empty then call cubeRemoved with parameter removedCubes.
	 */
	private void checkAllConnected() {
		for (int x = 0; x < this.getSizeX(); x++) {
			for (int y = 0; y < this.getSizeY(); y++) {
				for (int z = 0; z < this.getSizeZ(); z++) {
					try {
						if (!isConnected(x, y, z) && this.getCube(x, y, z).isSolid()) {
							removeCube(x, y, z);
						}
					} catch (ModelException e) {
					}
				}
			}
		}
	}
	
	/**
	 * Remove the cube on the given coordinates.
	 * @param x
	 * @param y
	 * @param z
	 * @post	cubesToRemove.add(new int[]{x, y, z})
	 */
	public void removeCube(int x, int y,int z){
		cubesToRemove.add(new int[]{x, y, z});
	}
	
	/**
	 * Remove the cubes in cubesToRemove from the game world.
	 * @param removedCubes
	 * 
	 * @effect	Iterate over the cubes in cubesToRemove and remove them. With a 0.25 chance create a
	 * 			log or a boulder at the cube's position (depending on the cubeType).
	 * 			| for int[] c in cubesToRemove
	 * 			|	r = random.nextDouble()
	 * 			|	if CubeType of c is WOOD and r < 0.25 then
	 * 			|		spawnCarryable(c[0], c[1], c[2], CubeType.WOOD)
	 * 			|	if CubeType of c is ROCK and r < 0.25 then
	 * 			|		spawnCarryable(c[0], c[1], c[2], CubeType.ROCK)
	 * 			|	cubes[c[0]][c[1]][c[2]] = CubeType.AIR;
	 *			|	modelListener.notifyTerrainChanged(c[0], c[1], c[2]);
	 *			|	connected.changeSolidToPassable(c[0], c[1], c[2]);
	 * @effect	Recreate the walkables.
	 *			| createWalkables()
	 * @effect	Recreate the spawnPoints.
	 * 			| createSpawnPoints()
	 * @effect	Reset the path of the walking units.
	 * 			| for unit in getUnits() do
	 * 			|	if unit.getState() == State.WALKING then
	 * 			|		unit.setResetPath(true)
	 */
	private void removeCubes() {
		Random rand = new Random();
		double carryableSpawnChance = 0.25;
		for (int[] c : cubesToRemove) {
			double r = rand.nextDouble();
			try {
				if (this.getCube(c[0], c[1], c[2]) == CubeType.WOOD && r < carryableSpawnChance)
					spawnCarryable(c[0], c[1], c[2], CubeType.WOOD);
				else if (this.getCube(c[0], c[1], c[2]) == CubeType.ROCK && r < carryableSpawnChance)
					spawnCarryable(c[0], c[1], c[2], CubeType.ROCK);
			} catch (ModelException e) {
			}
			cubes[c[0]][c[1]][c[2]] = CubeType.AIR;
			modelListener.notifyTerrainChanged(c[0], c[1], c[2]);
			connected.changeSolidToPassable(c[0], c[1], c[2]);
		}
		
		createWalkables();
	 	createSpawnPoints();
		for (Unit unit : getUnits()) {
			if (unit.getState() == State.WALKING) {
				unit.setResetPath(true);
				//List<int[]> pathToEndGoal = unit.getPathToEndGoal();
				/*removeloop : for (int[] rCube : cubesToRemove) {
					for (int[] pathCube : pathToEndGoal) {
						if (pathCube.equals(rCube)) {
							System.out.println("resetting some path");
							int[] endGoal = unit.getEndGoal().toIntArray();
							try {
								unit.moveTo(endGoal[0], endGoal[1], endGoal[2]);
							} catch (ModelException e) {
							}
							break removeloop;
						}
					}
				}*/
			}
		}
	}

	/**
	 * Spawns a carryable at the given location of the given type.
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @effect	if type == WOOD then spawn a log and add it to the carryables.
	 * 			if type == ROCK then spawn a boulder and add it to the carryables.
	 */
	private void spawnCarryable(int x, int y, int z, CubeType type){
		if (type == CubeType.WOOD) {
			Log c = new Log(new Vector(x,y,z)); 
			addCarryable(c);
		}
		else if (type == CubeType.ROCK) {
			Boulder c = new Boulder(new Vector(x,y,z));
			addCarryable(c);
		}
	}
	
	/**
	 * Adds a carryable to the carryables.
	 * @param carryable
	 * @post	carryables contains the given carryable.
	 * @effect	Set the world of the given carryable equal to this.
	 */
	public void addCarryable(Carryable carryable){
		carryables.add(carryable);
		carryable.setWorld(this);
	}
	
	/**
	 * Create possible spawnPoints for units.
	 * 
	 * @effect	for x, y, z from 0, 0, 1 to sizeX, sizeY, sizeZ do
	 * 				if cubes[x][y][0] is passable then add {x, y, 0} to spawnPoints.
	 * 				if cubes[x][y][z] is passable and cubes[x][y][z-1] is solid then
	 * 					add {x, y, z} to spawnPoints.
	 */
	private void createSpawnPoints() {
		spawnPoints.clear();
		for (int x = 0; x < this.getSizeX(); x++) {
			for (int y = 0; y < this.getSizeY(); y++) {
				if (!cubes[x][y][0].isSolid())
					spawnPoints.add(new int[]{x, y, 0});
				for (int z = 1; z < this.getSizeZ(); z++) {
					if (!cubes[x][y][z].isSolid() && cubes[x][y][z-1].isSolid())
						spawnPoints.add(new int[]{x, y, z});
				}
			}
		}
	}
	
	/**
	 * Create the walkable cubes and the edgeMap for this world.
	 * 
	 * @post for x, y, z from 0, 0, 0 to sizeX, sizeY, sizeZ do
	 * 			if the cube on x, y, z is walkable then
	 * 				add the int array {x, y, z} to walkables.
	 * @post for cube1 in walkables do
	 * 			addSet is a new Set<int[]>
	 * 			for cube2 in walkables do
	 * 				if |cube1[0] - cube2[0]| <= 1 and |cube1[1] - cube2[1]| <= 1 and |cube1[2] - cube2[2]| <= 1 then
	 * 					add cube2 to the addSet
	 *			edgeMap.put(cube1, addSet)
	 */
	private void createWalkables() {
		walkables.clear();
		for (int x = 0; x < this.getSizeX(); x++) {
			for (int y = 0; y < this.getSizeY(); y++) {
				for (int z = 0; z < this.getSizeZ(); z++) {
					if (isWalkable(new int[]{x, y, z}))
						walkables.add(new int[]{x, y, z});
				}
			}
		}
		
		Iterator<int[]> ite1 = walkables.iterator();
		while (ite1.hasNext()) {
			int[] cube1 = ite1.next();
			Set<int[]> addSet = new HashSet<int[]>();
			Iterator<int[]> ite2 = walkables.iterator();
			while (ite2.hasNext()) {
				int[] cube2 = ite2.next();
				if (Math.abs(cube1[0] - cube2[0]) <= 1 && Math.abs(cube1[1] - cube2[1]) <= 1 &&
						Math.abs(cube1[2] - cube2[2]) <= 1) {
					addSet.add(cube2);
				}
			}
			edgeMap.put(cube1, addSet);
		}
	}
	
	/**
	 * Returns all cubes of the given type in this world.
	 * @param type
	 * @return
	 */
	public Set<int[]> getCubesOfType(CubeType type) {
		Set<int[]> result = new HashSet<int[]>();
		for (int x = 0; x < this.getSizeX(); x++) {
			for (int y = 0; y < this.getSizeY(); y++) {
				for (int z = 0; z < this.getSizeZ(); z++) {
					try {
						if (this.getCube(x, y, z) == type)
							result.add(new int[]{x, y, z});
					} catch (ModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Return all logs in this world.
	 * @return
	 */
	public Set<Log> getLogs() {
		Set<Log> logs = new HashSet<Log>();
		for (Carryable carryable : carryables) {
			if (carryable.getCarryableType() == CarryableType.LOG)
				logs.add((Log)carryable);
		}
		
		return logs;
	}
	
	/**
	 * Return all boulders in this world.
	 * @return
	 */
	public Set<Boulder> getBoulders() {
		Set<Boulder> boulders = new HashSet<Boulder>();
		for (Carryable carryable : carryables) {
			if (carryable.getCarryableType() == CarryableType.BOULDER)
				boulders.add((Boulder)carryable);
		}
		
		return boulders;
	}
	
	/**
	 * Returns a carryable at the given position if there is one, returns null otherwise.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Carryable getCarryableAt(int x, int y, int z){
		for (Carryable carryable : this.getCarryables()) {
			int[] carPos = carryable.getPosition().toIntArray();
			if (x == carPos[0] && y == carPos[1] && z == carPos[2]) {
				return carryable;
			}
		}
		return null;
	}
	
	/**
	 * Returns a carryable at the given position of the given type if there is one, returns null otherwise.
	 * @param x
	 * @param y
	 * @param z
	 * @param carryabletype
	 * @return
	 */
	public Carryable getCarryableAtPositionOfType(int x, int y, int z, CarryableType carryabletype){
		for (Carryable carryable : this.getCarryables()) {
			int[] carryablePosition = carryable.getPosition().toIntArray();
			if (carryablePosition[0] == x && carryablePosition[1] == y && carryablePosition[2] == z
					&& carryable.getCarryableType() == carryabletype)
				return carryable;
		}
		return null;					
	}
	
	/**
	 * Returns true iff the given cube is connected to the border.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isConnected(int x, int y, int z){
		return connected.isSolidConnectedToBorder(x, y, z);
	}
	
	/**
	 * Adds the given unit to this world as long as there is a place for this unit.
	 * @param unit
	 * @post	if getUnits().size >= MAX_UNITS_PER_WORLD then return.
	 * @post	if factions.size() < MAXFACTIONS then create a new faction and add the given unit to that faction.
	 * @post	if the max number of factions is reached add the unit to the faction with the least members.
	 */
	public void addUnit(Unit unit){
		if (this.getUnits().size() >= MAX_UNITS_PER_WORLD)
			return;
		if (factions.size() < MAX_FACTIONS) {
			Faction fac = new Faction(this);
			fac.addUnit(unit);
			this.getFactions().add(fac);
		}
		else {
			int minValue = MAX_UNITS_PER_FACTION-1;
			for (Faction faction: factions)
				if (faction.getUnits().size() < minValue)
					minValue = faction.getUnits().size();
			for (Faction faction: factions)
				if (faction.getUnits().size() == minValue){
					faction.addUnit(unit);
					break;
				}	
		}
	}
	
	/**
	 * Spawns a new unit in this world and returns it.
	 * @param enableDefaultBehavior
	 * @effect	Creates a random unit on a random cube from spawnPoints and call addUnit to add this unit.
	 * @return
	 */
	public Unit spawnUnit(boolean enableDefaultBehavior) {
		Random rand = new Random();
		try {
			int index = rand.nextInt(spawnPoints.size());
			int[] spawnPosition = spawnPoints.get(index);
			Unit unit = new Unit(spawnPosition[0], spawnPosition[1], spawnPosition[2],  
					FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[rand.nextInt(LAST_NAMES.length)],
					rand.nextInt(75) + 25, rand.nextInt(75) + 25,
					rand.nextInt(75) + 25, rand.nextInt(75) + 25,
					enableDefaultBehavior);
			addUnit(unit);
			return unit;
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns true iff the given position is walkable.
	 */
	public boolean isWalkable(int[] position) {
		try {
			CubeType cube = this.getCube(position[0], position[1], position[2]);
			if (cube.isSolid()) 
				return false;
			if (position[2] == 0)
				return true;
			for (int x = position[0] - 1; x <= position[0] + 1; x++){
				for (int y = position[1] - 1; y <= position[1] + 1; y++){
					for (int z = position[2] - 1; z <= position[2] + 1; z++){
						try {
							CubeType cubeNeighbour = this.getCube(x, y, z);
							if (cubeNeighbour.isSolid())
								return true;
						} catch (ModelException e) {
						}
					}
				}
			}
		} catch (ModelException e) {
		}
		return false;
	}
	
	/**
	 * Checks whether this position is valid. Every coordinate must be in the interval [0, world.getSizeX()), 
	 * [0, world.getSizeY()) or [0, world.getSizeZ()) (for x, y, z respectively).
	 * 
	 * @param position
	 * 			The position to check.
	 * 
	 * @return	Returns true if this position is valid.
	 * 			| if (position.getX()<0 || position.getX() > world.getSizeX()) then result == false
	 * 			| if (position.getY()<0 || position.getY() > world.getSizeY()) then result == false
	 * 			| if (position.getZ()<0 || position.getZ() > world.getSizeZ()) then result == false
	 * 			| else result == true
	 */
	public static boolean isValidPosition(Vector position, World world) {
		if (world != null) {
			if (position.getX()<0 || position.getX() >= world.getSizeX())
				return false;
			if (position.getY()<0 || position.getY() >= world.getSizeY())
				return false;
			if (position.getZ()<0 || position.getZ() >= world.getSizeZ())
				return false;
		} else {
			if (position.getX() < 0 || position.getY() < 0 || position.getZ() < 0)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the cubetype of the cube at the given coordinates.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * @throws ModelException
	 * 			If x, y, z are out of bounds.
	 */
	public CubeType getCube(int x, int y, int z) throws ModelException{
		if (x < this.getSizeX() && y < this.getSizeY() && z < this.getSizeZ() && 0 <= x && 0 <= y && 0 <= z){
			return cubes[x][y][z];
		}
		else 
			throw new ModelException("This cube does not have a valid position.");
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public int getSizeX(){
		return cubes.length;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public int getSizeY(){
		return cubes[0].length;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public int getSizeZ(){
		return cubes[0][0].length;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public TerrainChangeListener getModelListener() {
		return modelListener;
	}
	
	/**
	 * 
	 * @param modelListener
	 */
	@Basic
	public void setModelListener(TerrainChangeListener modelListener) {
		this.modelListener = modelListener;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public Set<Unit> getUnits() {
		Set<Unit> units = new HashSet<Unit>();
		for (Faction faction : factions) 
			units.addAll(faction.getUnits());
		return units;
	}
	
	/**
	 * 
	 * @return
	 */
	@Basic
	public Set<Faction> getFactions() {
		return factions;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Basic
	public int getTerrainType(int x, int y, int z) {
		return cubes[x][y][z].getTypeInt();
	}

	/**
	 * @return the timeTillCaveIn
	 */
	public double getTimeTillCaveIn() {
		return timeTillCaveIn;
	}

	/**
	 * @param timeTillCaveIn the timeTillCaveIn to set
	 */
	public void setTimeTillCaveIn(int timeTillCaveIn) {
		this.timeTillCaveIn = timeTillCaveIn;
	}

	/**
	 * @return the carryables
	 */
	public Set<Carryable> getCarryables() {
		return carryables;
	}

	/**
	 * @return the walkables
	 */
	public List<int[]> getWalkables() {
		return walkables;
	}

	/**
	 * @return the edgeMap
	 */
	public Map<int[], Set<int[]>> getEdgeMap() {
		return edgeMap;
	}

	/**
	 * @return the spawnPoints
	 */
	public List<int[]> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * @return the cubesToRemove
	 */
	public Set<int[]> getCubesToRemove() {
		return cubesToRemove;
	}
}
