package hillbillies.model.scheduler;

import hillbillies.model.scheduler.expressions.Expression;
import hillbillies.model.scheduler.statements.AssignmentStatement;
import hillbillies.model.scheduler.statements.BeginOfTaskStatement;
import hillbillies.model.scheduler.statements.EndOfTaskStatement;
import hillbillies.model.scheduler.statements.Statement;
import hillbillies.model.world.Faction;
import hillbillies.model.world.Unit;
import hillbillies.model.world.World;
import hillbillies.part3.programs.SourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import be.kuleuven.cs.som.annotate.Basic;

/**
 * A Task class for the game Hillbillies.
 * 
 * @invar 	The name of this task is not null.
 * 			| this.getName() != null
 * @invar	The beginStatement of this task is not null.
 * 			| this.begin != null
 * @invar	There is always a statement currently executing.
 * 			| this.currentlyExecuting != null
 * 
 * @author HF corp.
 * @version 1.0
 */
public class Task {
	
	//TODO: miss is activities nog nodig??
	//private ArrayList<Statement> activities;
	private Statement begin;
	private Statement currentlyExecuting;
	private int priority;
	private String name;
	private Unit assignedUnit;
	private World world;
	private int[] selected;
	private boolean terminated;
	
	private Map<String, Expression<?>> variables;
	
	/**
	 * Constructor for the task class.
	 * 
	 * @param priority 	
	 * 				The priority of the task.
	 * @param name	
	 * 				The name of the task.
	 //TODO misschien activities niet nodig
	 *@param activities
	 * 				The list of activities in the task.
	 * @param selected
	 */
	public Task(int priority, String name, ArrayList<Statement> activities, int[] selected){
		//this.activities = activities;
		setPriority(priority);
		setName(name);
		setSelected(selected);
		variables = new HashMap<String, Expression<?>>();
		
		SourceLocation beginLocation = new SourceLocation(-1, 1);
		begin = new BeginOfTaskStatement(beginLocation);
		setCurrentlyExecuting(begin);
		begin.setNextStatement(activities.get(0));
		
		for (int i = 0; i < activities.size() - 1; i++) {
			activities.get(i).setNextStatement(activities.get(i+1));
		}
		
		SourceLocation endLocation = new SourceLocation(-2, 1);
		EndOfTaskStatement endStatement = new EndOfTaskStatement(endLocation);
		activities.get(activities.size() - 1).setNextStatement(endStatement);
		endStatement.setNextStatement(null);
		
		for (Statement activity : activities) {
			Set<Statement> breaks = activity.getBreakStatements();
			for (Statement breakStatement : breaks) {
				System.out.println("Break statement outside of a while loop at line: " + breakStatement.getSourceLocation().getLine());
			}
		}
	}
	
	public void reset() {
		setCurrentlyExecuting(begin);
		
		variables.clear();
		
		setPriority(this.getPriority() - 2);
		
		setAssignedUnit(null);
	}
	
	public void terminate() {
		terminated = true;
		for (Faction faction : this.getWorld().getFactions()) {
			faction.getScheduler().removeTask(this);
		}
	}
	
	public boolean beingExecuted(){
		return assignedUnit != null;
	}

	public void ExecuteNextActivity() {
		while (this.getCurrentlyExecuting().getNextStatement(this) != null) {
			
			setCurrentlyExecuting(this.getCurrentlyExecuting().getNextStatement(this));
			
			if (this.getCurrentlyExecuting().isNonExecutable())
				continue;
			
			this.getCurrentlyExecuting().execute(this);
			if (this.getCurrentlyExecuting().isExecutableByUnit()) 
				return;
		}
	}
	
	public void addVariable(AssignmentStatement stat, Expression<?> value) {
		if (variables.containsKey(stat.getVariableName())) {
			System.out.println("Trying to reassign a value to a variable, line: " + stat.getSourceLocation().getLine());
			return;
		}
		variables.put(stat.getVariableName(), value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Expression<?> getVariable(String name) {
		return variables.get(name);
	}
	
	/**
	 * @return the activities
	 */
	/*@Basic
	public ArrayList<Statement> getActivities() {
		return activities;
	}*/

	/**
	 * @return the priority
	 */
	@Basic
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	@Basic
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the name
	 */
	@Basic
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Basic
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the assignedUnit
	 */
	@Basic
	public Unit getAssignedUnit() {
		return assignedUnit;
	}

	/**
	 * @param assignedUnit the assignedUnit to set
	 */
	@Basic
	public void setAssignedUnit(Unit assignedUnit) {
		this.assignedUnit = assignedUnit;
	}

	/**
	 * @return the selected
	 */
	public int[] getSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(int[] selected) {
		this.selected = selected;
	}

	/**
	 * @return the currentlyExecuting
	 */
	public Statement getCurrentlyExecuting() {
		return currentlyExecuting;
	}

	/**
	 * @param currentlyExecuting the currentlyExecuting to set
	 */
	public void setCurrentlyExecuting(Statement currentlyExecuting) {
		this.currentlyExecuting = currentlyExecuting;
	}

	/**
	 * @return the terminated
	 */
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * @param world the world to set
	 */
	@Basic
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * @return the world
	 */
	@Basic
	public World getWorld() {
		return world;
	}
}
