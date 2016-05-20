package hillbillies.model.scheduler;

import hillbillies.model.world.Faction;
import hillbillies.model.world.Unit;
import ogp.framework.util.ModelException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import be.kuleuven.cs.som.annotate.Basic;

/**
* A Scheduler class for the game Hillbillies.
* 
* @invar	The scheduler belongs to a certain faction.
* 			| this.getFaction() != null
* 
* @author HF corp.
* @version 1.0
*/
public class Scheduler {
	
	/**
	 * A class to compare tasks using their priority.
	 * 
	 * @author HF corp.
	 * @version 1.0
	 */
	private class TaskComparator implements Comparator<Task>{
		
		/**
		 * Compares two tasks and returns the difference in priorities.
		 * 
		 * @param t1, t2 the tasks to be compared
		 * @return	the difference in priorities
		 * 			| return t1.getPriority() - t2.getPriority()
		 */
		@Override
		public int compare(Task t1, Task t2) {
			return t1.getPriority() - t2.getPriority();
		}

	}
	private Faction faction;
	private TreeSet<Task> tasks;
	
		/**
		 * Constructor for the Scheduler class.
		 * 
		 * @param faction
		 * 			The faction of this scheduler.
		 * @effect	This constructor creates a new scheduler of the given faction.
		 * 			| this(faction)
		 * @post	This scheduler gets a set of tasks, which is ordered using TaskComparator.
		 * 			| tasks = new TreeSet<Task>(new TaskComparator()) 
		 */
	public Scheduler(Faction faction){
		setFaction(faction);
		tasks = new TreeSet<Task>(new TaskComparator());
	}
	
	 /**
	  * Removes the task t from the scheduler.
	  * 
	  * @param t	The task to remove from the scheduler.
	  * @post 		The task is removed from the scheduler.
	  * 			| tasks.remove(t)
	  */
	public void removeTask(Task t){
		//System.out.println("removing task (class Scheduler, method removeTask(Task))");
		this.getTasks().remove(t);
		if (t.beingExecuted()) {
			if (t.getAssignedUnit().getFaction() == this.getFaction()) 
				t.getAssignedUnit().removeTask();
		}
	}
	
	/**
	 * Removes tasks from the scheduler.
	 * 
	 * @param tasks	The tasks to remove from the scheduler.
	 * @effect		The tasks are removed from the scheduler.
	 * 				|for task in tasks
	 * 				| 	removeTask(t)
	 */
	public void removeAllTasks(Collection<Task> tasks) {
		for (Task t : tasks) 
			removeTask(t);
	}
	
	/**
	 * Adds the task t to the scheduler.
	 * 
	 * @param t		The task to add to the scheduler.
	 * @post		The task is added to the scheduler.
	 * 				| tasks.add(t)
	 * @effect 		Sets the world of the scheduler to the world of the scheduler's faction.
	 * 				| t.setWorld(this.getFaction().getWorld())
	 */
	public void addTask(Task t){
		this.getTasks().add(t);
		t.setWorld(this.getFaction().getWorld());
	}
	
	/**
	 * Replaces the original task with the replacement task in the scheduler.
	 * 
	 * @param original		The task to be removed from the scheduler.
	 * @param replacement	The task that will replace the removed task.
	 * @effect				The original task is removed from the scheduler and replaced by the replacement task.
	 * 						| removeTask(original)
	 * 						| addTask(replacement)
	 * 
	 */
	public void replaceTask(Task original, Task replacement){
		removeTask(original);
		addTask(replacement);
	}
	
	/**
	 * Checks whether the scheduler contains a task t. Returns true if this is the case and false when it is not so.
	 * 
	 * @param t		The task t that is checked to be in the scheduler.
	 * @return		If the scheduler contains this task t, return true. If this is not the case, return false.
	 * 				| if t in this.getTasks() then return true
	 * 				| else then return false
	 */
	public boolean containsTask(Task t){
		return this.getTasks().contains(t);
	}
	
	/**
	 * Checks whether the scheduler contains tasks. Returns true if this is the case and false when it is not so.
	 * 
	 * @param tasks 	The tasks that are checked to be in the scheduler.
	 * @return			If the scheduler contains the tasks, return true. If this is not the case, return false.
	 * 					| for t in tasks
	 * 					|	if not scheduler.contains(t) then return false
	 * 					| return true
	 */
	public boolean containsAllTasks(Collection<Task> tasks) {
		for (Task t : tasks) {
			if (!containsTask(t))
				return false;
		}
		return true;
	}
	
	/**
	 * Gives the task t to the given unit. 
	 * 
	 * @param unit	The unit that will be given the task.
	 * @param t		The task that will be given to the unit.
	 * @effect		The unit is assigned task t.
	 * 				| unit.setTask(t)
	 * @effect		The task is assigned to the unit.
	 * 				| t.setAssignedUnit(unit)
	 * 			
	 */
	public void setScheduledForUnit(Unit unit, Task t) {
		unit.setTask(t);
		t.setAssignedUnit(unit);
	}
	
	/**
	 * Stops the task of the given unit.
	 * 
	 * @param unit	
	 * @effect		If the unit has a task, the task is stopped. If the unit does not have a task, return.
	 * 				| unit.stopTask()
	 */
	public void removeTaskFromUnit(Unit unit) {
		unit.stopTask();
	}
	
	
	/**
	 * cond should always be a method that returns a boolean.
	 * @param cond
	 * @return
	 * @throws ModelException
	 */
	//TODO Dees is echt ni oke, zie boek p 495 JOEPIE GEFIKST nog doc
	public Set<Task> getTasksByCondition(Method cond) throws ModelException {
		if (cond.getReturnType() != boolean.class)
			throw new ModelException();
		if (cond.getParameterCount() != 0)
			throw new ModelException();
		
		Set<Task> result = new HashSet<Task>();
		for (Task t : this.getTasks()) {
			try {
				if ((boolean) cond.invoke(t))
					result.add(t);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * Returns the highest priority task that is not being executed, otherwise returns null.
	 * 
	 * @return		Returns the task of the scheduler that has the highest priority and that is not being executed.
	 * 				Returns null if the scheduler has no tasks (or has only tasks that are already being executed). 
	 * 				| if this.getTasks().isEmpty() the return null
	 * 				| Iterator<Task> ite = getPriorityIterator();
	 * 				| while ite.hasNext() do
	 * 				|	Task t = ite.next()
	 * 				|	if not t.beingExecuted() then return t
	 * 				| return null
	 */
	public Task getHighestPriorityTask(){
		if (this.getTasks().isEmpty())
			return null;
		Iterator<Task> ite = getPriorityIterator();
		while (ite.hasNext()) {
			Task t = ite.next();
			if (!t.beingExecuted())
				return t;
		}
		return null;
	}
	
	/**
	 * Return an iterator returning all tasks in descending priority.
	 * 
	 * @return	an iterator returning all tasks in descending priority
	 * 			| this.getTasks().descenddingIterator()
	 */
	public Iterator<Task> getPriorityIterator() {
		return this.getTasks().descendingIterator();
	}
	
	/**
	 * @return the faction
	 */
	@Basic
	public Faction getFaction() {
		return faction;
	}
	/**
	 * @param faction the faction to set
	 */
	@Basic
	public void setFaction(Faction faction) {
		this.faction = faction;
	}
	/**
	 * @return the tasks
	 */
	@Basic
	public TreeSet<Task> getTasks() {
		return tasks;
	}
}
