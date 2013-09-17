package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.backend.exceptions.RequestNoRequestFileException;
import net.txconsole.core.Content;
import net.txconsole.core.model.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Component
public class RequestJdbcDao extends AbstractJdbcDao implements RequestDao {

    private final RowMapper<TRequest> requestRowMapper = new RowMapper<TRequest>() {
        @Override
        public TRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TRequest(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getString("version"),
                    SQLUtils.getEnum(RequestStatus.class, rs, "status")
            );
        }
    };

    @Autowired
    public RequestJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional
    public int createRequest(int branchId, String version, JsonConfiguration txFileExchangeConfig) {
        return dbCreate(
                SQL.REQUEST_CREATE,
                params("branch", branchId)
                        .addValue("version", version)
                        .addValue("configId", txFileExchangeConfig.getId())
                        .addValue("configNode", jsonToDB(txFileExchangeConfig.getNode()))
                        .addValue("status", RequestStatus.CREATED.name())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TRequest getById(int requestId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.REQUEST_BY_ID,
                params("id", requestId),
                requestRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TRequest> findByBranch(int branchId, int offset, int count) {
        return getNamedParameterJdbcTemplate().query(
                SQL.REQUESTS_BY_BRANCH,
                params("branch", branchId)
                        .addValue("offset", offset)
                        .addValue("count", count),
                requestRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findCreated() {
        return getNamedParameterJdbcTemplate().queryForList(
                SQL.REQUESTS_CREATED,
                params("status", RequestStatus.CREATED.name()),
                Integer.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JsonConfiguration getTxFileExchangeConfiguration(int requestId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.REQUEST_TXFILEEXCHANGE_CONFIG,
                params("id", requestId),
                new RowMapper<JsonConfiguration>() {
                    @Override
                    public JsonConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new JsonConfiguration(
                                rs.getString("TXFILEEXCHANGE_ID"),
                                jsonFromDB(rs, "TXFILEEXCHANGE_CONFIG")
                        );
                    }
                }
        );
    }

    @Override
    @Transactional
    public void saveDiff(final int requestId, final TranslationDiff diff) {
        getJdbcTemplate().execute(new ConnectionCallback<Void>() {
            @Override
            public Void doInConnection(Connection con) throws SQLException, DataAccessException {
                try (
                        PreparedStatement pse = con.prepareStatement(SQL.REQUEST_ENTRY_INSERT, Statement.RETURN_GENERATED_KEYS);
                        PreparedStatement psev = con.prepareStatement(SQL.REQUEST_ENTRY_VALUE_INSERT)
                ) {
                    pse.setInt(1, requestId);
                    // All entries
                    for (TranslationDiffEntry diffEntry : diff.getEntries()) {
                        TranslationDiffType type = diffEntry.getType();
                        // Request entry
                        pse.setString(2, diffEntry.getBundle());
                        pse.setString(3, diffEntry.getSection());
                        pse.setString(4, diffEntry.getKey());
                        pse.setString(5, type.name());
                        pse.executeUpdate();
                        ResultSet psei = pse.getGeneratedKeys();
                        psei.next();
                        int requestEntryId = psei.getInt(1);
                        // Request values
                        psev.setInt(1, requestEntryId);
                        for (TranslationDiffEntryValue v : diffEntry.getValues().values()) {
                            psev.setString(2, v.getLocale().toString());
                            psev.setBoolean(3, v.isToUpdate());
                            psev.setString(4, v.getOldValue());
                            psev.setString(5, v.getNewValue());
                            psev.executeUpdate();
                        }
                    }
                }
                return null;
            }
        });
    }

    @Override
    @Transactional
    public void saveRequestFile(int requestId, Content content) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_SET_REQUEST_FILE,
                params("id", requestId)
                        .addValue("type", content.getType())
                        .addValue("length", content.getBytes().length)
                        .addValue("content", content.getBytes())
        );
    }

    @Override
    @Transactional
    public void setStatus(int requestId, RequestStatus status) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_SET_STATUS,
                params("id", requestId).addValue("status", status.name())
        );
    }

    @Override
    @Transactional
    public void setToVersion(int requestId, String version) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_SET_TOVERSION,
                params("id", requestId).addValue("version", version)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Content getRequestFile(final int requestId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.REQUEST_GET_REQUEST_FILE,
                params("id", requestId),
                new RowMapper<Content>() {
                    @Override
                    public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
                        int length = rs.getInt("REQUEST_FILE_LENGTH");
                        if (rs.wasNull() || length == 0) {
                            throw new RequestNoRequestFileException(requestId);
                        } else {
                            return new Content(
                                    rs.getString("REQUEST_FILE_TYPE"),
                                    rs.getBytes("REQUEST_FILE_CONTENT")
                            );
                        }
                    }
                }
        );
    }

    @Override
    @Transactional
    public void delete(int id) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_DELETE,
                params("id", id)
        );
    }
}
