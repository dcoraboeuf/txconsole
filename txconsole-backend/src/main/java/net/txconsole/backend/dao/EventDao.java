package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TEvent;
import net.txconsole.core.model.EventCode;
import net.txconsole.core.model.EventEntity;
import net.txconsole.core.model.Signature;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EventDao {

    void add(EventCode code, Collection<String> parameters, Signature signature, Map<EventEntity, Integer> entities);

    List<TEvent> findByEntityAndCode(EventEntity entity, int entityId, EventCode eventCode);
}
