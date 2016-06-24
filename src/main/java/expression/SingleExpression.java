package expression;


/**
 * An expression that takes a single value of type L and returns a type of T.
 * @author Shawn Davies
 */
public abstract class SingleExpression<L, T> implements Expression<T> {

	/**
	 * The left-most (i.e first) value in this expression.
	 */
	protected final L left;

	public SingleExpression(L left) {
		this.left = left;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"("+evaluate()+")";
	}
}
