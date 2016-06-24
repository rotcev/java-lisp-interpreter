package lisp.function;

import lisp.Lisp;
import lisp.LispEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * A curried function is a function that takes an
 * @author Shawn Davies
 */
public final class CurriedFunction implements Function {

	private final LispEnvironment environment;
	private final List<Object> body;

	CurriedFunction(LispEnvironment environment, List<Object> body) {
		this.environment = environment;
		this.body = body;
	}

	public static CurriedFunction of(LispEnvironment environment, List<Object> parameters) {
		return new CurriedFunction(environment, parameters);
	}

	@Override
	public final Object evaluate(Object x) {
		List<Object> parameters = new ArrayList<>(body);
		parameters.add(x);
		return environment.evaluate(parameters);
	}

	@Override
	public final String toString() {
		return Lisp.stringify(body);
	}

}
