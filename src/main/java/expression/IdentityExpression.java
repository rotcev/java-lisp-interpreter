package expression;

/**
 * An identity expression is an expression that evaluates to itself.
 * @author Shawn Davies
 */
public class IdentityExpression<T> extends SingleExpression<T, T> {
	public IdentityExpression(T left) {
		super(left);
	}

	@Override
	public final T evaluate() {
		return left;
	}

}
