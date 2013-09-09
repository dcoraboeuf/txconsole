package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TProject;
import net.txconsole.core.model.Ack;

import java.util.List;

public interface ProjectDao {

    List<TProject> findAll();

    TProject getById(int id);

    int create(String name, String description);

    Ack delete(int id);
}
