package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TPipeline;
import net.txconsole.core.model.PipelineCreationForm;

import java.util.List;

public interface PipelineDao {

    List<TPipeline> findAll();

    TPipeline getById(int id);

    TPipeline getByName(String name);

    int create(String name, String description);
}
