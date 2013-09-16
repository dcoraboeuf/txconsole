package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.RequestDao;
import net.txconsole.backend.dao.model.TRequest;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.RequestStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
