package net.viperfish.journal.ui;

import java.io.Console;
import java.io.PrintWriter;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.viperfish.journal.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.operation.AddEntryOperation;
import net.viperfish.journal.operation.DeleteEntryOperation;
import net.viperfish.journal.operation.EditContentOperation;
import net.viperfish.journal.operation.EditSubjectOperation;
import net.viperfish.journal.operation.GetAllOperation;
import net.viperfish.journal.operation.GetEntryOperation;
import net.viperfish.journal.operation.SearchEntryOperation;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.CompromisedDataException;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;
import net.viperfish.journal.secure.SecureFactoryWrapper;

import org.reflections.Reflections;

public class CommandLineUserInterface implements UserInterface {
	private Scanner in;
	private OperationExecutor e;
	private Console display;
	private PrintWriter out;

	public CommandLineUserInterface() {
		display = System.console();
		in = new Scanner(display.reader());
		out = display.writer();
		e = Configuration.getWorker();
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
		e.submit(new AddEntryOperation(j));
	}

	private void editEntry(long parseLong) {
		GetEntryOperation get = new GetEntryOperation(parseLong);
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
				e.submit(new EditSubjectOperation(parseLong, input));
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
				e.submit(new EditContentOperation(parseLong, content));
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
			SearchEntryOperation ops = new SearchEntryOperation(query);
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
			e.submit(new DeleteEntryOperation(id));
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
			GetEntryOperation get = new GetEntryOperation(id);
			e.submit(get);
			displayEntry(get.getResult());
		}
		if (command[0].equals("listAll")) {
			GetAllOperation ops = new GetAllOperation();
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
		out.print("password:");
		out.flush();
		String password = new String(display.readPassword());
		Configuration.getProperty().setProperty("user.password", password);
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

	private void printDFHelp() {
		out.println("Available DataSourceFactory");
		Set<Class<? extends DataSourceFactory>> result = new Reflections(
				"net.viperfish.journal").getSubTypesOf(DataSourceFactory.class);
		result.remove(SecureFactoryWrapper.class);
		for (Class<? extends DataSourceFactory> i : result) {
			out.println(i.getCanonicalName());
		}
		out.flush();
	}

	private void printIFHelp() {
		out.println("Available Index Factory");
		Set<Class<? extends IndexerFactory>> result = new Reflections(
				"net.viperfish.journal").getSubTypesOf(IndexerFactory.class);
		for (Class<?> i : result) {
			out.println(i.getCanonicalName());
		}
		out.flush();
	}

	private boolean isAsymmetric(String alg) {
		if (alg.equalsIgnoreCase("RSA")) {
			return true;
		}
		if (alg.equalsIgnoreCase("ElGamal")) {
			return true;
		}
		if (alg.contains("IES")) {
			return true;
		}
		return false;
	}

	private Set<String> getAvailableMac(String cipher) {
		Set<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		String alg = cipher.split("/")[0];
		Pattern p = Pattern.compile("(\\w+)?" + "(" + alg + ")(\\w+?)",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Pattern hmac = Pattern.compile("(\\w+)?(hmac)(\\w+?)",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		for (Provider provider : Security.getProviders()) {
			out.println("Provider: " + provider.getName());
			for (Provider.Service service : provider.getServices()) {
				String macAlg = service.getAlgorithm();
				if (service.toString().contains("Mac.")) {
					if (macAlg.contains("PBE") || macAlg.contains("OLD")) {
						continue;
					}
					if (p.matcher(service.toString()).find()
							|| hmac.matcher(service.getAlgorithm()).find()) {
						result.add(service.getAlgorithm());
					}
				}
			}
		}
		return result;
	}

	@Override
	public void setup() {
		out.println("Welcome to setup, enter ? for help");
		String input = new String();
		while (true) {
			input = display
					.readLine("Please Input the Factory for DataSource:(Full Name):");
			if (input.equals("?")) {
				printDFHelp();
				continue;
			}
			Configuration.getProperty().setProperty("DataSourceFactory", input);
			break;
		}
		while (true) {
			input = display
					.readLine("Please Input the Factory for Indexer:(Full Name):");
			if (input.equals("?")) {
				printIFHelp();
				continue;
			}
			Configuration.getProperty().setProperty("IndexerFactory", input);
			break;
		}
		while (true) {
			input = display.readLine("Use Secure Wrapper?[yes/no]:");
			if (input.equals("yes")) {
				String selectedCipher = new String();
				Configuration.getProperty().setProperty("UseSecureWrapper",
						"true");
				while (true) {
					input = display
							.readLine("Enter Encryption Algorithm(alg/mode/padding):");
					if (input.equals("?")) {
						for (String iter : SecureEntryDatabaseWrapper
								.getSupportedEncryption()) {
							out.println(iter);
						}
						out.println("Available Mode:CBC, CFB, GCFB, GOFB, OFB, OpenPGPCFB, PGPCFB, CTR");
						out.println("Available Padding: ISO10126d2PADDING, ISO7816d4PADDING, PKCS7PADDING, PKCS5PADDING, TBCPADDING, X923PADDING, ZEROBYTEPADDING");
						out.flush();
						continue;
					}
					selectedCipher = input;
					Configuration.getProperty().setProperty("EncryptionMethod",
							input);
					break;
				}
				while (true) {
					input = display.readLine("Enter Mac Algorithm:");
					if (input.equals("?")) {
						for (String i : getAvailableMac(selectedCipher)) {
							out.println(i);
						}
						out.flush();
						continue;
					}
					Configuration.getProperty().setProperty("MacMethod", input);
					break;
				}
				break;
			} else if (input.equals("no")) {
				Configuration.getProperty().setProperty("UseSecureWrapper",
						"false");
				Configuration.getProperty().remove("EncryptionMethod");
				Configuration.getProperty().remove("MacMethod");
				break;
			}
		}
	}
}
