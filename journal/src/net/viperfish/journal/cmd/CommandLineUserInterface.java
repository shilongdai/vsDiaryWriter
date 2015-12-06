package net.viperfish.journal.cmd;

import java.io.Console;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.secure.CompromisedDataException;
import net.viperfish.utils.config.ComponentConfig;
import net.viperfish.utils.config.Configuration;

public class CommandLineUserInterface extends UserInterface {
	private Scanner in;
	private OperationExecutor e;
	private Console display;
	private PrintWriter out;
	private OperationFactory ops;

	public CommandLineUserInterface() {
		display = System.console();
		in = new Scanner(display.reader());
		out = display.writer();
		e = JournalApplication.getWorker();
		ops = JournalApplication.getOperationFactory();
	}

	private String readBlock() {
		out.println("enter \\n, then ., then \\n to end");
		out.flush();
		String content = new String();
		String buffer;
		while (true) {
			buffer = in.nextLine();
			if (buffer.equals(".")) {
				break;
			}
			content += "\n";
			content += buffer;
		}
		return content;
	}

	private void addEntry() {
		Journal j = new Journal();
		out.print("subject:");
		out.flush();
		j.setSubject(in.nextLine());
		out.print("content:");
		out.flush();
		String content = readBlock();
		j.setContent(content);
		e.submit(ops.getAddOperation(j));
	}

	private void editEntry(long parseLong) {
		OperationWithResult<Journal> get = ops.getGetEntryOperation(parseLong);
		e.submit(get);
		Journal j = get.getResult();
		out.println("current subject:" + j.getSubject());
		while (true) {
			out.print("modify?[yes/no]:");
			out.flush();
			String input = in.nextLine();
			if (input.equals("yes")) {
				out.print("new subject:");
				out.flush();
				input = in.nextLine();
				e.submit(ops.getEditSubjectOperation(parseLong, input));
				break;
			} else if (input.equals("no")) {
				break;
			}
		}
		out.println("current content:");
		out.println(j.getContent());
		out.flush();
		while (true) {
			out.print("modify content?[yes/no]:");
			out.flush();
			String input = in.nextLine();
			if (input.equals("yes")) {
				out.println("new content:");
				out.flush();
				String content = readBlock();
				e.submit(ops.getEditContentOperation(parseLong, content));
				break;
			} else if (input.equals("no")) {
				break;
			}
		}
	}

	private void displayCollectionJournal(Collection<Journal> j) {
		out.println("ID/Subject/Date");
		Formatter f = new Formatter();
		for (Journal i : j) {
			f.format("%-4s %-20s %-20s \n", i.getId(), i.getSubject(),
					i.getDate());
		}
		out.println(f.toString());
		out.flush();
		f.close();
	}

	public void executeCommand(String[] command) throws Throwable {
		if (command.length == 0) {
			return;
		}
		if (command[0].equals("addEntry")) {
			addEntry();
		}
		if (command[0].equals("search")) {
			if (command.length == 1) {
				out.println("search query string");
				out.flush();
				return;
			}
			String query = new String();
			query += command[1];
			for (int i = 2; i < command.length; ++i) {
				query += " ";
				query += command[i];
			}
			OperationWithResult<Set<Journal>> ops = this.ops
					.getSearchOperation(query);
			e.submit(ops);
			displayCollectionJournal(ops.getResult());
		}
		if (command[0].equals("editEntry")) {
			if (command.length == 1) {
				out.println("editEntry [id]");
				out.flush();
				return;
			}
			Long id = null;
			try {
				id = Long.parseLong(command[1]);
			} catch (NumberFormatException e) {
				out.println("id is a number");
				out.flush();
				return;
			}

			editEntry(id);
		}
		if (command[0].equals("deleteEntry")) {
			if (command.length == 1) {
				out.println("deleteEntry [id]");
				out.flush();
				return;
			}
			Long id = null;
			try {
				id = Long.parseLong(command[1]);
			} catch (NumberFormatException e) {
				out.println("id is a number");
				out.flush();
				return;
			}
			e.submit(ops.getDeleteOperation(id));
		}
		if (command[0].equals("getEntry")) {
			if (command.length != 2) {
				out.println("getEntry [id]");
				out.flush();
				return;
			}
			Long id = null;
			try {
				id = Long.parseLong(command[1]);
			} catch (NumberFormatException e) {
				out.println("id is a number");
				out.flush();
				return;
			}
			OperationWithResult<Journal> get = ops.getGetEntryOperation(id);
			e.submit(get);
			displayEntry(get.getResult());
		}
		if (command[0].equals("listAll")) {
			OperationWithResult<List<Journal>> ops = this.ops
					.getListAllOperation();
			e.submit(ops);
			displayCollectionJournal(ops.getResult());
		}
		if (command[0].equals("?")) {
			out.println("addEntry: to add a entry");
			out.println("editEntry [id]: edit a entry with id of [id]");
			out.println("deleteEntry [id]: delete a entry with id of [id]");
			out.println("getEntry [id]: to view a entry with id of [id]");
			out.println("search [query-p1 query-p2 ...]: to search with query");
			out.println("listAll: to list all entries");
			out.flush();
		}
		if (e.hasException()) {
			throw e.getNextError();
		}
	}

	private void displayEntry(Journal result) {
		out.println("subject:" + result.getSubject());
		out.println("Date:" + result.getDate());
		out.println("content:");
		out.println(result.getContent());
		out.flush();

	}

	@Override
	public void run() {
		out.println("welcome to the journal stub userinterface, input ? for help");
		while (true) {
			String input = new String();
			input = display.readLine("command:");
			if (input.matches("quit")) {
				return;
			}
			String[] command = input.split(" ");
			try {
				executeCommand(command);
			} catch (CompromisedDataException e) {
				System.err
						.println("Compromised or Corrupted Data Detected, please mannully discard compromised entry");
			} catch (Throwable e) {
				System.err.println("Encountered critical error:" + e + ":"
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void getRequiredConf() {
		Iterable<ComponentConfig> all = Configuration.allComponent();
		for (ComponentConfig i : all) {
			Iterable<String> required = i.requiredConfig();
			out.println("Configuring for:" + i.getUnitName());
			out.flush();
			String preference;
			for (String iter : required) {
				while (true) {
					preference = display.readLine("Input value for %s:", iter);
					if (preference.equals("options")) {
						for (String option : i.getOptions(iter)) {
							out.println(option);
						}
						out.flush();
						continue;
					}
					i.setProperty(iter, preference);
				}
			}
		}
	}

	private void getOptionalConf() {
		Iterable<ComponentConfig> all = Configuration.allComponent();
		for (ComponentConfig i : all) {
			Iterable<String> optionals = i.optionalConfig();
			out.println("Configuring for:" + i.getUnitName());
			out.flush();
			String preference;
			for (String iter : optionals) {
				while (true) {
					preference = display.readLine("Input value for %s:", iter);
					if (preference.equals("options")) {
						for (String option : i.getOptions(iter)) {
							out.println(option);
						}
						out.flush();
						continue;
					}
					break;
				}
				i.setProperty(iter, preference);
			}
		}
	}

	@Override
	public void setup() {
		String input = new String();
		while (true) {
			out.println("enter options for option");
			out.flush();
			input = display.readLine("Select [expert/simple] setup:");
			if (input.equals("simple")) {
				getRequiredConf();
				break;
			} else if (input.equals("expert")) {
				getRequiredConf();
				getOptionalConf();
				break;
			}
		}
	}

	@Override
	public String promptPassword() {
		String password = new String();
		while (true) {
			out.print("password:");
			out.flush();
			password = new String(display.readPassword());
			if (!isPasswordSet()) {
				setPassword(password);
				break;
			}
			if (authenticate(password)) {
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				return new String();
			}
			out.println("incorrect password, please retry");
			out.flush();
		}
		return password;
	}
}
