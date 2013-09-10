package net.txconsole.backend.dao.impl;

import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractJdbcDao extends NamedParameterJdbcDaoSupport {

    private final Function<Object, String> quoteFn = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return "'" + o + "'";
        }
    };
    private final ObjectMapper objectMapper;

    public AbstractJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setDataSource(dataSource);
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, Class<T> type) {
        List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, RowMapper<T> rowMapper) {
        List<T> items = getNamedParameterJdbcTemplate().query(sql, criteria, rowMapper);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected Integer getInteger(ResultSet rs, String name) throws SQLException {
        int i = rs.getInt(name);
        if (rs.wasNull()) {
            return null;
        } else {
            return i;
        }
    }

    protected byte[] getImage(String sql, int id) {
        List<byte[]> list = getNamedParameterJdbcTemplate().query(
                sql,
                params("id", id),
                new RowMapper<byte[]>() {
                    @Override
                    public byte[] mapRow(ResultSet rs, int row) throws SQLException, DataAccessException {
                        return rs.getBytes("image");
                    }
                });
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    protected String jsonToDB(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write JSON as string", e);
        }
    }

    protected JsonNode jsonFromDB(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            try {
                return objectMapper.readTree(value);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read JSON from string", e);
            }
        }
    }

    protected int dbCreate(String sql, MapSqlParameterSource params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    protected MapSqlParameterSource params(String name, Object value) {
        return new MapSqlParameterSource(name, value);
    }
}
