package lisp.expression;

import expression.IdentityExpression;

/**
 * @author Shawn Davies
 */
public class LispIdentityExpression<T> extends IdentityExpression<T> {

	public LispIdentityExpression(T left) {
		super(left);
	}

	@Override
	public String toString() {
		return left.toString();
	}
}
