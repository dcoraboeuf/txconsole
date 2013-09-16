package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.Content;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.RequestStatus;
import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    public void saveDiff(int requestId, TranslationDiff diff) {
        // All entries
        for (TranslationDiffEntry diffEntry : diff.getEntries()) {
            // Request entry
            int requestEntryId = dbCreate(
                    SQL.REQUEST_ENTRY_INSERT,
                    params("request", requestId)
                            .addValue("bundle", diffEntry.getBundle())
                            .addValue("section", diffEntry.getSection())
                            .addValue("name", diffEntry.getKey())
                            .addValue("type", diffEntry.getType().name())
            );
            // Request values
            for (Map.Entry<Locale, Pair<String, String>> entry : diffEntry.getValues().entrySet()) {
                dbCreate(
                        SQL.REQUEST_ENTRY_VALUE_INSERT,
                        params("requestEntry", requestEntryId)
                                .addValue("locale", entry.getKey().toString())
                                .addValue("oldValue", entry.getValue().getLeft())
                                .addValue("newValue", entry.getValue().getRight())
                );
            }
        }
        //To change body of implemented methods use File | Settings | File Templates.
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
}
