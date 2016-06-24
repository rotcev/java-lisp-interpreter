package lisp.expression;


import expression.Expression;
import lisp.LispEnvironment;

/**
 * @author Shawn Davies
 */
public final class ConditionalExpression implements Expression<Boolean> {

	private final String type;
	private final Object a;
	private final Object b;
	private final LispEnvironment environment;

	ConditionalExpression(LispEnvironment environment, String type, Object a, Object b) {
		this.environment = environment;
		this.type = type;
		this.a = a;
		this.b = b;
	}

	public static ConditionalExpression of(LispEnvironment environment, String type, Object a, Object b) {
		return new ConditionalExpression(environment, type, a, b);
	}

	@Override
	public final Boolean evaluate() {

		Object evalA = environment.evaluate(a);
		Object evalB = environment.evaluate(b);

		if (evalA == LispEnvironment.EMPTY_LIST) {
			return evalB == LispEnvironment.EMPTY_LIST;
		}

		if (evalB == LispEnvironment.EMPTY_LIST) {
			return evalA == LispEnvironment.EMPTY_LIST;
		}

		if (type.equals(">")) {
			Number numA = (Number) evalA;
			Number numB = (Number) evalB;
			return numA.intValue() > numB.intValue();
		}

		if (type.equals("<")) {
			Number numA = (Number) evalA;
			Number numB = (Number) evalB;
			return numA.intValue() < numB.intValue();
		}

		if (type.equals(">=")) {
			Number numA = (Number) evalA;
			Number numB = (Number) evalB;
			return numA.intValue() >= numB.intValue();
		}

		if (type.equals("<=")) {
			Number numA = (Number) evalA;
			Number numB = (Number) evalB;
			return numA.intValue() <= numB.intValue();
		}

		if (type.equals("/=")) {
			return !evalA.equals(evalB);
		}

		if (type.equals("=")) {
			return evalA.equals(evalB);
		}
		throw new IllegalStateException("Unknown conditional type "+type);
	}

	@Override
	public final String toString() {
		return evaluate().toString();
	}

	public final Object getB() {
		return b;
	}

	public final Object getA() {
		return a;
	}

	public final String getType() {
		return type;
	}
}
