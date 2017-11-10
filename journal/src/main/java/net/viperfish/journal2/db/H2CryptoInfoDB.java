package net.viperfish.journal2.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.viperfish.journal2.core.CryptoInfo;

final class H2CryptoInfoDB implements AutoCloseable {

    private Dao<CryptoInfo, Long> passwordEntryDAO;
    private JdbcConnectionSource con;

    public H2CryptoInfoDB(JdbcConnectionSource con) {
        this.con = con;
        try {
            passwordEntryDAO = DaoManager.createDao(con, CryptoInfo.class);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }

    public CryptoInfo save(CryptoInfo data) throws IOException {
        try {
            passwordEntryDAO.createOrUpdate(data);
            return data;
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public void save(Iterable<CryptoInfo> data) throws IOException {
        for (CryptoInfo c : data) {
            try {
                passwordEntryDAO.createOrUpdate(c);
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    public Iterable<CryptoInfo> findAll() throws IOException {
        try {
            return passwordEntryDAO.queryForAll();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public Map<String, CryptoInfo> findbyJournal(long journal) throws IOException {
        Map<String, CryptoInfo> result = new HashMap<>();
        QueryBuilder<CryptoInfo, Long> queryBuilder = passwordEntryDAO.queryBuilder();
        try {
            queryBuilder.where().eq("Journal", journal);
            PreparedQuery<CryptoInfo> query = queryBuilder.prepare();
            for (CryptoInfo i : passwordEntryDAO.query(query)) {
                result.put(i.getMapKey(), i);
            }
            return result;
        } catch (SQLException e) {
            throw new IOException(e);
        }

    }

    public void deleteByJournal(Long journal) throws IOException {
        try {
            DeleteBuilder<CryptoInfo, Long> deleteBuilder = passwordEntryDAO.deleteBuilder();
            deleteBuilder.where().eq("Journal", journal);
            deleteBuilder.delete();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public void clear() throws SQLException {
        TableUtils.clearTable(con, CryptoInfo.class);
    }

}
