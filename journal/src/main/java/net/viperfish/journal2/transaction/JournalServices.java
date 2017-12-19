/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;

import javax.lang.model.type.NullType;

import javafx.concurrent.Service;
import net.viperfish.journal2.auth.OpenBSDBCryptAuthManager;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptorChain;
import net.viperfish.journal2.crypt.AEADProccessor;
import net.viperfish.journal2.crypt.CompressionProccessor;
import net.viperfish.journal2.crypt.HMACProcessor;
import net.viperfish.journal2.crypt.StreamCipherProcessor;
import net.viperfish.journal2.db.H2JournalDB;

/**
 *
 * @author shilongdai
 */
public final class JournalServices {

	private JournalServices() {

	}

	private static JournalDatabase db;
	private static JournalEncryptorChain enc;
	private static OpenBSDBCryptAuthManager auth;

	public static void init() throws IOException {
		enc = new JournalEncryptorChain(Paths.get("kdfSalt"));
		enc.addProccessor(new AEADProccessor());
		enc.addProccessor(new CompressionProccessor());
		enc.addProccessor(new HMACProcessor());
		enc.addProccessor(new StreamCipherProcessor());

		auth = new OpenBSDBCryptAuthManager(Paths.get("passwd"));
		if (auth.isSetup()) {
			auth.load();
		}
		auth.addObserver(enc);

		db = new CryptedJournalDatabase(enc, new H2JournalDB("./data"));
	}

	public static boolean isSetup() {
		return auth.isSetup();
	}

	public static void close() throws Exception {
		db.close();
	}

	public static Service<Collection<Journal>> newGetAllService() {
		return new GetAllService(db);
	}

	public static Service<Collection<Journal>> newGetDateRangeService(Date lower, Date upper) {
		return new GetDateRangeService(lower, upper, db);
	}

	public static Service<Journal> newGetJournalService(long id) {
		return new GetJournalService(id, db);
	}

	public static Service<Journal> newPurgeEntryService(long id) {
		return new PurgeEntryService(id, db);
	}

	public static Service<Journal> newRemoveJournalService(long id) {
		return new RemoveJournalService(id, db);
	}

	public static Service<Journal> newSaveJournalService(Journal j) {
		return new SaveJournalService(j, db);
	}

	public static Service<Collection<Journal>> newSearchDateRangeService(String keyword, Date lower, Date upper) {
		return new SearchDateRangeService(db, keyword, lower, upper);
	}

	public static Service<Void> newSetPasswordService(String password) {
		return new SetPasswordService(auth, password);
	}

	public static Service<Boolean> newLoginService(String password) {
		return new LoginService(auth, password);
	}

	public static Service<NullType> newChangePasswordService(String newPass) {
		return new ChangePasswordService(newPass, (CryptedJournalDatabase) db, auth);
	}

}
