import lisp.ListProcessor;

import java.io.File;

/**
 * @author Shawn Davies
 */
public final class Application {

	public static void main(String[] args) throws Exception {
		ListProcessor processor = ListProcessor.of(new File("./scripts/main.nan"));
		processor.print();
	}
}
