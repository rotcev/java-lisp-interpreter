package lisp;

/**
 * @author Shawn Davies
 */
public final class Lisp {
    public static String stringify(Object object) {
        return object.toString().replaceAll("\\[", "(").replaceAll("\\]", ")").replaceAll(",", "");
    }
}
