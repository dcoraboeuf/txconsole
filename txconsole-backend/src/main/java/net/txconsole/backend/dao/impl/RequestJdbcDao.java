package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.*;
import net.txconsole.core.support.SimpleMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Locale;

@Component
public class RequestJdbcDao extends AbstractJdbcDao implements RequestDao {

    private final RowMapper<TRequest> requestRowMapper = new RowMapper<TRequest>() {
        @Override
        public TRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TRequest(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getString("version"),
                    rs.getString("toVersion"),
                    rs.getString("mergeVersion"),
                    SQLUtils.getEnum(RequestStatus.class, rs, "status"),
                    SQLUtils.getMessage(rs, "message_code", "message_parameters")
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
                            psev.setBoolean(3, v.isEditable());
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
    public void setStatus(int requestId, RequestStatus status) {
        setStatus(requestId, status, null);
    }

    @Override
    @Transactional
    public void setStatus(int requestId, RequestStatus status, SimpleMessage message) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_SET_STATUS,
                params("id", requestId)
                        .addValue("status", status.name())
                        .addValue("messageCode", SQLUtils.getMessageCode(message))
                        .addValue("messageParameters", SQLUtils.getMessageParameters(message))
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
    @Transactional
    public void delete(int id) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_DELETE,
                params("id", id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDiff loadDiff(int id) {
        // Builder
        TranslationDiffBuilder builder = TranslationDiffBuilder.create();
        // Gets all the entries (and only them)
        getNamedParameterJdbcTemplate().query(
                SQL.REQUEST_ENTRY_BY_REQUEST,
                params("request", id),
                new TranslationDiffEntryBuilderRowMapper(builder)
        );
        // OK
        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDiffEntry getRequestEntryDetails(int entryId) {
        // Builder
        TranslationDiffBuilder builder = TranslationDiffBuilder.create();
        // Gets the entry
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.REQUEST_ENTRY_BY_ID,
                params("id", entryId),
                new TranslationDiffEntryBuilderRowMapper(builder)
        ).build();
    }

    @Override
    @Transactional(readOnly = true)
    public int getBranchIdForRequestEntry(int entryId) {
        return getFirstItem(
                SQL.REQUEST_ENTRY_BRANCH,
                params("entryId", entryId),
                Integer.class
        );
    }

    @Override
    @Transactional
    public TranslationDiffEntryValue addValue(int entryId, Locale locale, String value) {
        int id = dbCreate(
                SQL.REQUEST_ENTRY_NEW_VALUE,
                params("entryId", entryId)
                        .addValue("locale", locale.toString())
                        .addValue("value", value)
        );
        return new TranslationDiffEntryValue(
                id,
                locale,
                true,
                null,
                value
        );
    }

    @Override
    @Transactional
    public void editValue(int entryValueId, String value) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_ENTRY_EDIT_VALUE,
                params("id", entryValueId).addValue("value", value)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findRequestEntryId(int requestId, String bundleName, String sectionName, String keyName) {
        return getFirstItem(
                SQL.REQUEST_ENTRY_BY_KEY_IDENTIFIER,
                params("requestId", requestId)
                        .addValue("bundle", bundleName)
                        .addValue("section", sectionName)
                        .addValue("key", keyName),
                Integer.class
        );
    }

    @Override
    @Transactional
    public void setMergeVersion(int requestId, String version) {
        getNamedParameterJdbcTemplate().update(
                SQL.REQUEST_SET_MERGEVERSION,
                params("id", requestId).addValue("version", version)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getLastVersion(int branchId) {
        return getFirstItem(
                SQL.REQUEST_LAST_VERSION,
                params("branch", branchId),
                String.class
        );
    }

    protected class TranslationDiffEntryBuilderRowMapper implements RowMapper<TranslationDiffEntryBuilder> {

        private final TranslationDiffBuilder builder;

        public TranslationDiffEntryBuilderRowMapper(TranslationDiffBuilder builder) {
            this.builder = builder;
        }

        @Override
        public TranslationDiffEntryBuilder mapRow(ResultSet rs, int rowNum) throws SQLException {
            int entryId = rs.getInt("id");
            String bundle = rs.getString("bundle");
            String section = rs.getString("section");
            String key = rs.getString("name");
            TranslationDiffType type = SQLUtils.getEnum(TranslationDiffType.class, rs, "type");
            // Adds the entry
            final TranslationDiffEntryBuilder entry = builder.entry(entryId, bundle, section, key, type);
            // Gets all values
            getNamedParameterJdbcTemplate().query(
                    SQL.REQUEST_ENTRY_VALUE_BY_REQUEST_ENTRY,
                    params("entryId", entryId),
                    new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            int entryValueId = rs.getInt("id");
                            Locale locale = SQLUtils.toLocale(rs, "locale");
                            boolean editable = rs.getBoolean("editable");
                            String oldValue = rs.getString("oldValue");
                            String newValue = rs.getString("newValue");
                            entry.withDiff(entryValueId, locale, editable, oldValue, newValue);
                        }
                    }
            );
            // OK
            return entry;
        }
    }
}
