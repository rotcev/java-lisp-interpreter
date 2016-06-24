/**
 * @author Shawn Davies
 */
public class Triplet<A, B, C> extends Pair<A, B> {

	protected final C c;

	public Triplet(A a, B b, C c) {
		super(a, b);
		this.c = c;
	}

	public final C getC() {
		return c;
	}

	@Override
	public String toString() {
		return a+", "+b+", "+c;
	}
}
