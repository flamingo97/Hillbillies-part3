package hillbillies.part3.programs;

import hillbillies.model.scheduler.Task;
import hillbillies.model.scheduler.expressions.AndExpression;
import hillbillies.model.scheduler.expressions.AnyExpression;
import hillbillies.model.scheduler.expressions.BooleanExpression;
import hillbillies.model.scheduler.expressions.BoulderPositionExpression;
import hillbillies.model.scheduler.expressions.CarriesItemExpression;
import hillbillies.model.scheduler.expressions.EnemyExpression;
import hillbillies.model.scheduler.expressions.Expression;
import hillbillies.model.scheduler.expressions.FalseExpression;
import hillbillies.model.scheduler.expressions.FriendExpression;
import hillbillies.model.scheduler.expressions.HerePositionExpression;
import hillbillies.model.scheduler.expressions.IsAliveExpression;
import hillbillies.model.scheduler.expressions.IsEnemyExpression;
import hillbillies.model.scheduler.expressions.IsFriendExpression;
import hillbillies.model.scheduler.expressions.IsPassableExpression;
import hillbillies.model.scheduler.expressions.IsSolidExpression;
import hillbillies.model.scheduler.expressions.LiteralPositionExpression;
import hillbillies.model.scheduler.expressions.LogPositionExpression;
import hillbillies.model.scheduler.expressions.NextToPositionExpression;
import hillbillies.model.scheduler.expressions.NotExpression;
import hillbillies.model.scheduler.expressions.OrExpression;
import hillbillies.model.scheduler.expressions.PositionExpression;
import hillbillies.model.scheduler.expressions.PositionOfExpression;
import hillbillies.model.scheduler.expressions.ReadVariableExpression;
import hillbillies.model.scheduler.expressions.SelectedExpression;
import hillbillies.model.scheduler.expressions.ThisExpression;
import hillbillies.model.scheduler.expressions.TrueExpression;
import hillbillies.model.scheduler.expressions.UnitExpression;
import hillbillies.model.scheduler.expressions.WorkshopPositionExpression;
import hillbillies.model.scheduler.statements.AssignmentStatement;
import hillbillies.model.scheduler.statements.AttackStatement;
import hillbillies.model.scheduler.statements.BreakStatement;
import hillbillies.model.scheduler.statements.FollowStatement;
import hillbillies.model.scheduler.statements.IfStatement;
import hillbillies.model.scheduler.statements.MoveToStatement;
import hillbillies.model.scheduler.statements.PrintStatement;
import hillbillies.model.scheduler.statements.SequenceStatement;
import hillbillies.model.scheduler.statements.Statement;
import hillbillies.model.scheduler.statements.WhileStatement;
import hillbillies.model.scheduler.statements.WorkStatement;
import hillbillies.model.world.Unit;

import java.util.ArrayList;
import java.util.List;

public class TaskFactory implements ITaskFactory<Expression<?>, Statement, Task> {

	@Override
	public List<Task> createTasks(String name, int priority,
			Statement activity, List<int[]> selectedCubes) {
		
		List<Task> result = new ArrayList<Task>();
		ArrayList<Statement> a = new ArrayList<Statement>();
		a.add(activity);
		for (int i = 0; i < selectedCubes.size(); i++) {
			result.add(new Task(priority, name, a, selectedCubes.get(i)));
		}
		
		return result;
	}

	@Override
	public Statement createAssignment(String variableName, Expression<?> value,
			SourceLocation sourceLocation) {
		return new AssignmentStatement(value, variableName, sourceLocation);
	}

	@Override
	public Statement createWhile(Expression<?> condition, Statement body,
			SourceLocation sourceLocation) {
		try {
			return new WhileStatement((BooleanExpression) condition, body, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Statement createIf(Expression<?> condition, Statement ifBody,
			Statement elseBody, SourceLocation sourceLocation) {
		try {
			return new IfStatement((BooleanExpression) condition, ifBody, elseBody, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Statement createBreak(SourceLocation sourceLocation) {
		return new BreakStatement(sourceLocation);
	}

	@Override
	public Statement createPrint(Expression<?> value, SourceLocation sourceLocation) {
		return new PrintStatement(value, sourceLocation);
	}

	@Override
	public Statement createSequence(List<Statement> statements,
			SourceLocation sourceLocation) {
		return new SequenceStatement(statements, sourceLocation);
	}

	@Override
	public Statement createMoveTo(Expression<?> position,
			SourceLocation sourceLocation) {
		try {
			return new MoveToStatement((PositionExpression) position, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Statement createWork(Expression<?> position,
			SourceLocation sourceLocation) {
		try {
			return new WorkStatement((PositionExpression) position, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Statement createFollow(Expression<?> unit, SourceLocation sourceLocation) {
		try {
			return new FollowStatement((UnitExpression) unit, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Statement createAttack(Expression<?> unit, SourceLocation sourceLocation) {
		try {
			return new AttackStatement((UnitExpression) unit, sourceLocation);
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<?> createReadVariable(String variableName,
			SourceLocation sourceLocation) {
		return new ReadVariableExpression(variableName, sourceLocation);
	}

	@Override
	public Expression<Boolean> createIsSolid(Expression<?> position,
			SourceLocation sourceLocation) {
		try {
			return new IsSolidExpression(sourceLocation, new PositionExpression[]{(PositionExpression)position});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createIsPassable(Expression<?> position,
			SourceLocation sourceLocation) {
		try {
			return new IsPassableExpression(sourceLocation, new PositionExpression[]{(PositionExpression)position});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createIsFriend(Expression<?> unit,
			SourceLocation sourceLocation) {
		try {
			return new IsFriendExpression(sourceLocation, new UnitExpression[]{(UnitExpression)unit});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createIsEnemy(Expression<?> unit,
			SourceLocation sourceLocation) {
		try {
			return new IsEnemyExpression(sourceLocation, new UnitExpression[]{(UnitExpression)unit});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createIsAlive(Expression<?> unit,
			SourceLocation sourceLocation) {
		try {
			return new IsAliveExpression(sourceLocation, new UnitExpression[]{(UnitExpression)unit});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createCarriesItem(Expression<?> unit,
			SourceLocation sourceLocation) {
		try {
			return new CarriesItemExpression(sourceLocation, new UnitExpression[]{(UnitExpression)unit});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createNot(Expression<?> expression,
			SourceLocation sourceLocation) {
		try {
			return new NotExpression(sourceLocation, new BooleanExpression[]{(BooleanExpression)expression});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createAnd(Expression<?> left, Expression<?> right,
			SourceLocation sourceLocation) {
		try {
			return new AndExpression(sourceLocation, 
					new BooleanExpression[]{(BooleanExpression)left, (BooleanExpression)right});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<Boolean> createOr(Expression<?> left, Expression<?> right,
			SourceLocation sourceLocation) {
		try {
			return new OrExpression(sourceLocation, 
					new BooleanExpression[]{(BooleanExpression)left, (BooleanExpression)right});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<int[]> createHerePosition(SourceLocation sourceLocation) {
		return new HerePositionExpression(sourceLocation);
	}

	@Override
	public Expression<int[]> createLogPosition(SourceLocation sourceLocation) {
		return new LogPositionExpression(sourceLocation);
	}

	@Override
	public Expression<int[]> createBoulderPosition(SourceLocation sourceLocation) {
		return new BoulderPositionExpression(sourceLocation);
	}

	@Override
	public Expression<int[]> createWorkshopPosition(SourceLocation sourceLocation) {
		return new WorkshopPositionExpression(sourceLocation);
	}

	@Override
	public Expression<int[]> createSelectedPosition(SourceLocation sourceLocation) {
		return new SelectedExpression(sourceLocation);
	}

	@Override
	public Expression<int[]> createNextToPosition(Expression<?> position,
			SourceLocation sourceLocation) {
		try {
			return new NextToPositionExpression(sourceLocation, new PositionExpression[]{(PositionExpression)position});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<int[]> createPositionOf(Expression<?> unit,
			SourceLocation sourceLocation) {
		try {
			return new PositionOfExpression(sourceLocation, new UnitExpression[]{(UnitExpression)unit});
		} catch (ClassCastException e) {
			System.out.println("Type safety, line " + sourceLocation.getLine());
			return null;
		}
	}

	@Override
	public Expression<int[]> createLiteralPosition(int x, int y, int z,
			SourceLocation sourceLocation) {
		return new LiteralPositionExpression(sourceLocation, x, y, z);
	}

	@Override
	public Expression<Unit> createThis(SourceLocation sourceLocation) {
		return new ThisExpression(sourceLocation);
	}

	@Override
	public Expression<Unit> createFriend(SourceLocation sourceLocation) {
		return new FriendExpression(sourceLocation);
	}

	@Override
	public Expression<Unit> createEnemy(SourceLocation sourceLocation) {
		return new EnemyExpression(sourceLocation);
	}

	@Override
	public Expression<Unit> createAny(SourceLocation sourceLocation) {
		return new AnyExpression(sourceLocation);
	}

	@Override
	public Expression<Boolean> createTrue(SourceLocation sourceLocation) {
		return new TrueExpression(sourceLocation);
	}

	@Override
	public Expression<Boolean> createFalse(SourceLocation sourceLocation) {
		return new FalseExpression(sourceLocation);
	}
}
