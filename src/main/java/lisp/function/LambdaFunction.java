package lisp.function;


import lisp.Lisp;
import lisp.LispEnvironment;

import java.util.List;

/**
 * @author Shawn Davies
 */
public final class LambdaFunction implements Function {

	private final LispEnvironment environment;
	private final List<Object> parameters;
	private final List<Object> body;

	LambdaFunction(LispEnvironment environment, List<Object> parameters, List<Object> body) {
		this.environment = environment;
		this.parameters = parameters;
		this.body = body;
	}

	public static LambdaFunction of(LispEnvironment environment, List<Object> parameters, List<Object> body) {
		return new LambdaFunction(environment, parameters, body);
	}

	@Override
	public final Object evaluate(Object x) {
		if (environment.containsKey(x)) {
			return environment.get(x);
		}
		if (x instanceof List) {
			LispEnvironment env = new LispEnvironment(environment);
			for (int i = 0; i < parameters.size(); i++) {
				env.put(parameters.get(i).toString(), ((List) x).get(i));
			}
			//System.out.println(toString()+" where "+env.toString().substring(1));
			return env.evaluate(body);
		}
		return environment.getOuterEnvironment().evaluate(body);
	}

	@Override
	public final String toString() {
		return "(-> "+ Lisp.stringify(parameters)+" "+Lisp.stringify(body)+")";
	}
}
