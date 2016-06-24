package lisp.expression;

/**
 * @author Shawn Davies
 */
public final class LispSymbolExpression extends LispIdentityExpression<String> {
	public LispSymbolExpression(String left) {
		super(left);
	}
}
