package lisp;

import lisp.expression.LispIdentityExpression;
import java.util.ArrayList;

/**
 * A list containing functions used during the read stage of a {@link ListProcessor}.
 * @author Shawn Davies
 */
public final class LispTokenList extends ArrayList<LispIdentityExpression> {

	/**
	 * Retrieves the value in this list at the specified index and then removes it.
	 * @param index The index of the element to take.
	 * @return The retrieved element, that is no longer in this list.
	 */
	public final LispIdentityExpression take(int index) {
		LispIdentityExpression expression = get(index);
		remove(index);
		return expression;
	}

	/**
	 * Checks if the current top element is the end of a list.
	 * @return {@code true} if we are at the end of a list.
	 */
	public final boolean isEndOfList() {
		if (isEmpty()) {
			throw new IllegalStateException("Unexpected EOF");
		}
		return eval(0).equals(ListProcessor.LIST_END);
	}

	/**
	 * Evaluates an {@link LispIdentityExpression} at the given index in this list.
	 * @param index The index of the element in this list.
	 * @return The evaluation result of the element at the given index.
	 */
	public final Object eval(int index) {
		return get(index).evaluate();
	}
}
