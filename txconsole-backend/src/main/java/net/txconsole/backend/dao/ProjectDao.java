package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TProject;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.JsonConfiguration;

import java.util.List;

public interface ProjectDao {

    List<TProject> findAll();

    TProject getById(int id);

    int create(String name, String fullName, JsonConfiguration configuration);

    Ack delete(int id);
}
