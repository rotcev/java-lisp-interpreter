package expression;

/**
 * An expression that takes two arguments of types L, R respectively and returns type T.
 * @author Shawn Davies
 */
public abstract class BiExpression<L, R, T> extends SingleExpression<L, T> {

	/**
	 * The right-most type (i.e last type in this expression).
	 */
	protected final R right;

	public BiExpression(L left, R right) {
		super(left);
		this.right = right;
	}

}
