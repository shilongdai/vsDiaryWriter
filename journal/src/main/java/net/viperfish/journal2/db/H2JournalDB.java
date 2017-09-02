package net.viperfish.journal2.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;

public final class H2JournalDB implements JournalDatabase {

	private JdbcConnectionSource conn;
	private Dao<Journal, Long> dao;
	private H2CryptoInfoDB cryptoInfoDB;

	public H2JournalDB(String filePath) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:h2:").append(filePath);
		try {
			conn = new JdbcConnectionSource(sb.toString(), new H2DatabaseType());
			conn.setUsername("journal");
			conn.setPassword("journal");
			dao = DaoManager.createDao(conn, Journal.class);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		cryptoInfoDB = new H2CryptoInfoDB(conn);

	}

	@Override
	public void close() throws Exception {
		conn.close();
	}

	@Override
	public Journal save(Journal data) throws IOException {
		try {
			dao.createOrUpdate(data);
			for (Entry<String, CryptoInfo> c : data.getInfoMapping().entrySet()) {
				c.getValue().setMapKey(c.getKey());
				c.getValue().setJournal(data.getId());
				cryptoInfoDB.save(c.getValue());
			}
			return data;
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void save(Iterable<Journal> data) throws IOException {
		for (Journal j : data) {
			save(j);
		}
	}

	@Override
	public Iterable<Journal> findAll() throws IOException {
		try {
			List<Journal> result = dao.queryForAll();
			for (Journal j : result) {
				j.setInfoMapping(cryptoInfoDB.findbyJournal(j.getId()));
			}
			return result;
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public Iterable<Journal> findAll(Iterable<Long> ids) throws IOException {
		List<Journal> result = new LinkedList<>();
		for (Long id : ids) {
			result.add(findOne(id));
		}
		return result;
	}

	@Override
	public Journal findOne(Long id) throws IOException {
		try {
			Journal j = dao.queryForId(id);
			j.setInfoMapping(cryptoInfoDB.findbyJournal(j.getId()));
			return j;
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void delete(Long id) throws IOException {
		try {
			dao.deleteById(id);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Iterable<Journal> findByTimestampBetween(Date lower, Date upper) throws IOException {
		QueryBuilder<Journal, Long> queryBuild = dao.queryBuilder();
		try {
			queryBuild.where().between("Timestamp", lower, upper);
			List<Journal> result = dao.query(queryBuild.prepare());
			for (Journal j : result) {
				j.setInfoMapping(cryptoInfoDB.findbyJournal(j.getId()));
			}
			return result;
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public Iterable<Journal> findByIdInAndTimestampBetween(Iterable<Long> id, Date lower, Date upper)
			throws IOException {
		QueryBuilder<Journal, Long> queryBuild = dao.queryBuilder();
		try {
			queryBuild.where().between("Timestamp", lower, upper).and().in("Id", id);
			List<Journal> result = dao.query(queryBuild.prepare());
			for (Journal j : result) {
				j.setInfoMapping(cryptoInfoDB.findbyJournal(j.getId()));
			}
			return result;
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
