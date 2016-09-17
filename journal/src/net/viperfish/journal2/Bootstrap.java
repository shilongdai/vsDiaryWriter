package net.viperfish.journal2;

import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.swtGui.GraphicalUserInterface;

public class Bootstrap {

	public Bootstrap() {
	}

	public static void main(String... arguments) {
		ThreadContext.put("id", UUID.randomUUID().toString());
		ThreadContext.put("username", "journalUser");
		AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext(
				ApplicationRootContext.class);
		rootContext.start();
		rootContext.registerShutdownHook();

		GraphicalUserInterface ui = rootContext.getBean(GraphicalUserInterface.class);
		AuthenticationManager manager = rootContext.getBean(AuthenticationManager.class);

		if (!manager.isSetup()) {
			ui.setFirstPassword();
		}
		ui.promptPassword();
		ui.run();
	}

}
