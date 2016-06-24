package expression;

/**
 * An expression that takes three values of types L, M, and R, and then returns a type of T.
 * @author Shawn Davies
 */
public abstract class TriExpression<L, M, R, T> extends BiExpression<L, R, T> {

	/**
	 * The inner-most (middle) type in this expression.
	 */
	protected final M middle;

	public TriExpression(L left, M middle, R right) {
		super(left, right);
		this.middle = middle;
	}

}
