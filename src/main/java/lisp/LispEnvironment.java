package lisp;

import com.diffplug.common.base.Errors;
import com.google.common.collect.ImmutableList;
import expression.Expression;
import lisp.expression.ConditionalExpression;
import lisp.expression.LispSymbolExpression;
import lisp.function.CurriedFunction;
import lisp.function.Function;
import lisp.function.LambdaFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Shawn Davies
 */
public final class LispEnvironment extends HashMap<Object, Object> implements Function {

	/**
	 * Syntax for defining an environment value.
	 */
	private static final String DEFINE = "=>";
	/**
	 * Syntax for sequential execution.
	 */
	private static final String SEQUENTIAL = ">>";
	/**
	 * Syntax which will calculate the product of a list.
	 */
	private static final String PRODUCT = "*";
	/**
	 * Syntax which will calculate the square of a number
	 */
	private static final String SQUARE = "**";
	/**
	 * Syntax which will calculate the sum of a list or a group of lists.
	 * (+ 1 2 3 4 5)
	 * = (+ (' 1 2 3 4 5))
	 * = (+ (.. 1 5))
	 * = (lst (>> (=> a (.. 1 5)) (+ a)))
	 */
	private static final String SUM = "+";
	/**
	 * Syntax which will calculate the difference of a list or a group of lists.
	 */
	private static final String DIFFERENCE = "-";
	/**
	 * Syntax for the mathematical constant PI.
	 */
	private static final String PI = "pi";
	/**
	 * Syntax for an expression that doesn't get evaluated but just returns itself.
	 */
	private static final String IDENTITY = "\'";
	/**
	 * Syntax for printing text.
	 */
	private static final String PRINT = "print";
	/**
	 * Syntax for retrieving the first element in a list.
	 */
	private static final String FIRST = "fst";
	/**
	 * Syntax for retrieving the last element in a list.
	 */
	private static final String LAST = "lst";
	/**
	 * Syntax for defining a lambda expression.
	 */
	private static final String LAMBDA = "->";
	/**
	 * Syntax for printing an environment.
	 */
	private static final String ENVIRONMENT = "env";
	/**
	 * Syntax for constructing a new list from an original list with elements appended to the end.
	 */
	private static final String APPEND = "++";
	/**
	 * Syntax for constructing a new list from elements.
	 */
	private static final String CONSTRUCT = ":";
	/**
	 * The empty list i.e ().
	 */
	public static final ImmutableList<Object> EMPTY_LIST = ImmutableList.of();

	private static final LispEnvironment GLOBAL = new LispEnvironment();
	private static final String IF = "?";
	private static final String CURRY = "curry";
	private static final String ANONYMOUS = "!";
	private static final String BITWISE_AND = "&";

	/**
	 * The environment in which this environment resides.
	 * The global environment has itself as an outer environment.
	 */
	private LispEnvironment outerEnvironment;

	public LispEnvironment(LispEnvironment outerEnvironment) {
		this.outerEnvironment = outerEnvironment;
	}

	public LispEnvironment() {
		this(GLOBAL);
	}

	@Override
	public final Object put(Object key, Object value) {
		if (containsKey(key)) {
			throw new IllegalStateException("An expression with a key of " + key + " is already defined. It's current value is: " + get(key));
		}
		return super.put(key, value);
	}

	@Override
	public final Object get(Object key) {

		if (key.equals("")) {
			return "";
		}

		if (key == EMPTY_LIST) {
			return EMPTY_LIST;
		}

		if (key instanceof Function) {
			return evaluate(key);
		}

		if (key instanceof List) {
			return evaluate(key);
		}

		Object value = super.get(key);

		if (value == null) {
			value = super.get(key.toString());
		}

		if (value != null) {
			return value;
		}

		if (outerEnvironment != null) {
			Object outer = outerEnvironment.get(key);

			if (outer != null) {
				return outer;
			}
		}
		throw new IllegalStateException("Could not find defined expression with key of " + key + " in any environment.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Object evaluate(Object x) {
		if (x instanceof LispSymbolExpression) {
			return get(((LispSymbolExpression) x).evaluate());
		}

		if (x instanceof Expression) {
			return evaluate(((Expression) x).evaluate());
		}

		if (!(x instanceof List)) {
			return x;
		}

		List expression = (List) x;

		if (expression.isEmpty()) {
			return EMPTY_LIST;
		}

		Object function = expression.get(0);

		if (function instanceof Number) {
			return x;
		}

		String functionString = function.toString();

		if (functionString.equals(BITWISE_AND)) {
			Number a = (Number) evaluate(expression.get(1));
			Number b = (Number) evaluate(expression.get(2));
			return a.intValue() & b.intValue();
		}

		if (functionString.equals("~")) {
			Object a = expression.get(1);
			Object b = expression.get(2);

			Object evalA = evaluate(a);
			Object evalB = evaluate(b);
			if (evalB instanceof List) {
				List<Object> result = new ArrayList<>();
				result.add(evalA);
				result.addAll((List<Object>) evalB);
				return result;
			}
			return ImmutableList.of(evalA, evalB);
		}

		if (functionString.equals("rest")) {
			List<Object> list = (List) evaluate(expression.get(1));
			return evaluate(list.subList(1, list.size()));
		}

		if (functionString.equals(PRINT)) {
			return getArguments(expression);
		}

		if (functionString.equals(">") || functionString.equals("<") || functionString.equals(">=") || functionString.equals("<=") || functionString.equals("/=") || functionString.equals("=")) {
			return ConditionalExpression.of(this, functionString, expression.get(1), expression.get(2));
		}

		if (functionString.equals(IF)) {

			Object expr = evaluate(expression.get(1));
			if (expr instanceof Boolean) {
				return expr;
			}

			ConditionalExpression conditional = (ConditionalExpression) expr;
			return conditional.evaluate() ? evaluate(expression.get(2)) : evaluate(expression.get(3));
		}

		if (functionString.equals(ENVIRONMENT)) {
			StringBuilder builder = new StringBuilder();
			for (Entry<Object, Object> entries : entrySet()) {
				builder.append("\n").append(entries.getKey().toString()).append(" = ").append(entries.getValue().toString());
			}
			return builder.toString();
		}

		if (functionString.equals(DEFINE)) {
			LispSymbolExpression name = (LispSymbolExpression) expression.get(1);
			put(name.evaluate(), evaluate(expression.get(2)));
			return get(DEFINE);
		}

		if (functionString.equals(CONSTRUCT)) {
			return construct(expression);
		}

		if (functionString.equals(APPEND)) {
			return append(expression);
		}

		if (functionString.equals(CURRY)) {
			return CurriedFunction.of(this, getArguments(expression));
		}

		if (functionString.equals(ANONYMOUS)) {
			return LambdaFunction.of(this, ImmutableList.of(new LispSymbolExpression("#")), (List) expression.get(1));
		}

		if (functionString.equals(LAMBDA)) {
			return LambdaFunction.of(this, (List) expression.get(1), (List) expression.get(2));
		}

		Object functionValue = get(function);

		if (functionValue == EMPTY_LIST) {
			return EMPTY_LIST;
		}

		if (functionValue instanceof List) {
			return evaluate(functionValue);
		}

		if (functionValue instanceof Number) {
			return functionValue;
		}

		List<Object> arguments = (List<Object>) getArguments(expression).stream().map(this::evaluate).collect(toList());
		if (functionValue instanceof Function) {
			return evaluate(((Function) functionValue).evaluate(arguments));
		}
		return Errors.log().getWithDefault(() -> ((Method) functionValue).invoke(this, arguments), expression);
	}

	private static Method reflect(String name) {
		return Errors.log().getWithDefault(() -> LispEnvironment.class.getMethod(name, List.class), null);
	}

	@SuppressWarnings({"unused", "unchecked"})
	public final<T> List<Object> append(List<T> expression) {
		List<Object> original = (List) evaluate(expression.get(1));

		List<Object> newList = new ArrayList(original);
		newList.addAll(expression.subList(2, expression.size()).stream().map(this::evaluate).collect(toList()));
		return newList;
	}

	@SuppressWarnings("unused")
	public final <T> List<Object> construct(List<T> expression) {
		return getArguments(expression).stream().map(this::evaluate).collect(toList());
	}

	public final <T> List<T> getArguments(List<T> expression) {
		return expression.subList(1, expression.size());
	}

	@SuppressWarnings("unused")
	public final <T> void print(List<T> objects) {
		objects.forEach(System.out::println);
	}

	@SuppressWarnings("unused")
	public final <T> List<T> identity(List<T> list) {
		return list;
	}

	@SuppressWarnings("unused")
	public final <T> List<T> begin(List<T> arguments) {
		return new ArrayList<>(arguments);
	}

	@SuppressWarnings("unused")
	public final <T> Object define(List<T> params) {
		return put(params.get(0).toString(), params.get(1).toString());
	}

	@SuppressWarnings({"unused", "unchecked"})
	public final <T> Number sum(List<T> nums) {
		int sum = 0;
		for (Object object : nums) {
			if (object instanceof List) {
				sum += sum((List) object).floatValue();
			} else {
				sum += ((Number) evaluate(object)).floatValue();
			}
		}
		return sum;
	}

	@SuppressWarnings("unused")
	public final <T> Object first(List<T> objects) {
		Object object = objects.get(0);

		if (object instanceof List) {
			return first((List) object);
		}
		return object;
	}

	@SuppressWarnings("unused")
	public final <T> Object last(List<T> objects) {
		Object object = objects.get(objects.size() - 1);

		if (object instanceof List) {
			return last((List) object);
		}
		return object;
	}

	@SuppressWarnings("unused")
	public final Number difference(List<Number> nums) {
		return nums.stream().reduce(0, (a, b) -> -a.floatValue() - b.floatValue());
	}

	@SuppressWarnings("unused")
	public final Number square(List<Number> nums) {
		return product(ImmutableList.of(nums.get(0), nums.get(0)));
	}

	@SuppressWarnings("unused")
	public final <T> Number product(List<T> nums) {
		int product = 1;
		for (Object object : nums) {
			if (object instanceof List) {
				product *= product((List) object).floatValue();
			} else {
				product *= ((Number) evaluate(object)).floatValue();
			}
		}
		return product;
	}

	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Object, Object> entries : entrySet()) {
			builder.append("\n").append(entries.getKey().toString()).append(" = ").append(entries.getValue().toString());
		}
		return builder.toString();
	}

	public final LispEnvironment getOuterEnvironment() {
		return outerEnvironment;
	}

	static {
		Errors.log().run(() -> {
			GLOBAL.put(SUM, reflect("sum"));
			GLOBAL.put(DIFFERENCE, reflect("difference"));
			GLOBAL.put(PRODUCT, reflect("product"));
			GLOBAL.put(SEQUENTIAL, reflect("begin"));
			GLOBAL.put(DEFINE, reflect("define"));
			GLOBAL.put(PRINT, reflect("print"));
			GLOBAL.put(IDENTITY, reflect("identity"));
			GLOBAL.put(SQUARE, reflect("square"));
			GLOBAL.put(FIRST, reflect("first"));
			GLOBAL.put(LAST, reflect("last"));
			GLOBAL.put(CONSTRUCT, reflect("construct"));
			GLOBAL.put(APPEND, reflect("append"));
			GLOBAL.put(PI, Math.PI);
			GLOBAL.put(ENVIRONMENT, GLOBAL.toString());
		});
	}
}
