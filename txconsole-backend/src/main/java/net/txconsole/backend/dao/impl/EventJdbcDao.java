package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.EventDao;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.Signature;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

@Component
public class EventJdbcDao extends AbstractJdbcDao implements EventDao {

    @Autowired
    public EventJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    public void add(EventCode code, Collection<String> parameters, Signature signature, Map<EventEntity, Integer> entities) {
        int eventId = dbCreate(
                SQL.EVENT_INSERT,
                params("code", code.name())
                        .addValue("parameters", StringUtils.join(parameters, "||||"))
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
}
