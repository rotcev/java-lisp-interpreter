package expression;

/**
 * An expression that can evaluate to type T.
 * @author Shawn Davies
 */
public interface Expression<T> {

	/**
	 * Evaluates this expression to type T.
	 * @return A value of type T.
	 */
	default T evaluate() {
		return null;
	}

}
