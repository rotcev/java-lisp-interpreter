package lisp;

import com.diffplug.common.base.Errors;
import lisp.expression.LispFloatExpression;
import lisp.expression.LispIdentityExpression;
import lisp.expression.LispIntegerExpression;
import lisp.expression.LispSymbolExpression;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

/**
 * @author Shawn Davies
 */
public final class ListProcessor {

	/**
	 * Represents the start of an expression.
	 */
	public static final String LIST_BEGIN = "(";
	/**
	 * Represents the end of an expression.
	 */
	public static final String LIST_END = ")";
	/**
	 * The pattern used to tokenize input.
	 */
	private static final String SPLIT_PATTERN = "\\s+";
	/**
	 * The index of the first element in a token list.
	 */
	private static final int FIRST_ELEMENT = 0;
	/**
	 * The text to be processed.
	 */
	private final String text;
	/**
	 * The lisp environment for this processor.
	 */
	private final LispEnvironment environment;

	ListProcessor(String text) {
		this.text = text;
		this.environment = new LispEnvironment();
	}


	/**
	 * Parses text into a collection of {@link LispIdentityExpression}s.
	 * @return the tokens.
	 */
	public final LispTokenList tokenize() {
		return stream(text.replace(LIST_BEGIN, spaces(LIST_BEGIN)).replace(LIST_END, spaces(LIST_END)).trim().split(SPLIT_PATTERN)).map(this::parse).collect(toCollection(LispTokenList::new));
	}

	public final Object read() {
		return read(tokenize());
	}

	public final Object read(LispTokenList tokens) {
		if (tokens.size() == 0) {
			throw new IllegalStateException();
		}

		LispIdentityExpression token = tokens.take(FIRST_ELEMENT);
		Object evaluated = token.evaluate();
		if (evaluated.equals(LIST_BEGIN)) {
			List<Object> list = new ArrayList<>();

			while (!tokens.isEndOfList()) {
				list.add(read(tokens));
			}
			tokens.remove(FIRST_ELEMENT);
			return list;
		}
		return token;
	}

	/**
	 * Print's the content of this processor and starts a repl.
	 */
	public final ListProcessor print() {
		System.out.println("Source code:");
		System.out.println(text);
		System.out.println("End of source code\n");
		environment.evaluate(read(tokenize()));
		Scanner scanner = new Scanner(System.in);

		while(true) {
			try {
				if (scanner.hasNext()) {
					ListProcessor processor = of(scanner.nextLine());

					System.out.println("result: "+environment.evaluate(processor.read()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private LispIdentityExpression parse(String s) {
		return Errors.suppress().getWithDefault(() -> new LispIntegerExpression(Integer.parseInt(s)), Errors.suppress().getWithDefault(() -> new LispFloatExpression(Float.parseFloat(s)), new LispSymbolExpression(s)));
	}

	public static ListProcessor of(String text) {
		return new ListProcessor(text);
	}

	public static ListProcessor of(File file) {
		return of(Errors.log().getWithDefault(() -> new String(Files.readAllBytes(Paths.get(file.toURI()))), ""));
	}

	protected static String insert(String str, String within) {
		return str + within + str;
	}

	private static String spaces(String string) {
		return insert(" ", string);
	}

}
