package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.EventDao;
import net.txconsole.backend.dao.model.TEvent;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.Signature;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class EventJdbcDao extends AbstractJdbcDao implements EventDao {

    public static final String EVENT_PARAMETER_SEPARATOR = SQLUtils.PARAMETERS_SEPARATOR;
    private final RowMapper<TEvent> eventRowMapper = new RowMapper<TEvent>() {
        @Override
        public TEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            TEvent e = new TEvent(
                    rs.getInt("id"),
                    SQLUtils.getEnum(EventCode.class, rs, "event_code"),
                    Arrays.asList(StringUtils.split(rs.getString("event_parameters"), EVENT_PARAMETER_SEPARATOR)),
                    SQLUtils.getDateTime(rs, "event_timestamp"),
                    getInteger(rs, "account_id"),
                    rs.getString("account_name")
            );
            for (EventEntity eventEntity : EventEntity.values()) {
                Integer eventEntityId = getInteger(rs, eventEntity.name());
                if (eventEntityId != null) {
                    e = e.withEntity(eventEntity, eventEntityId);
                }
            }
            return e;
        }
    };

    @Autowired
    public EventJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional
    public void add(EventCode code, Collection<String> parameters, Signature signature, Map<EventEntity, Integer> entities) {
        int eventId = dbCreate(
                SQL.EVENT_INSERT,
                params("code", code.name())
                        .addValue("parameters", StringUtils.join(parameters, EVENT_PARAMETER_SEPARATOR))
                        .addValue("timestamp", SQLUtils.toTimestamp(signature.getTimestamp()))
                        .addValue("accountId", signature.getAuthorId())
                        .addValue("accountName", signature.getAuthorName())
        );
        // Entities
        for (Map.Entry<EventEntity, Integer> entityEntry : entities.entrySet()) {
            String entityName = entityEntry.getKey().name();
            int entityId = entityEntry.getValue();
            getNamedParameterJdbcTemplate().update(
                    String.format(SQL.EVENT_SET_ENTITY, entityName),
                    params("id", eventId).addValue("entityId", entityId)
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TEvent> findByEntityAndCode(EventEntity entity, int entityId, EventCode eventCode) {
        return getNamedParameterJdbcTemplate().query(
                String.format(SQL.EVENT_BY_ENTITY_AND_CODE, entity.name()),
                params("entityId", entityId).addValue("eventCode", eventCode.name()),
                eventRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TEvent> findByEntity(EventEntity entity, int entityId, int offset, int count) {
        if (entity != null) {
            return getNamedParameterJdbcTemplate().query(
                    String.format(SQL.EVENT_BY_ENTITY, entity.name()),
                    params("entityId", entityId).addValue("offset", offset).addValue("count", count),
                    eventRowMapper
            );
        } else {
            return getNamedParameterJdbcTemplate().query(
                    SQL.EVENTS,
                    params("offset", offset).addValue("count", count),
                    eventRowMapper
            );
        }
    }
}
