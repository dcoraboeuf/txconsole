package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TPipelineAuthorization;

import java.util.List;

public interface PipelineAuthorizationDao {

    List<TPipelineAuthorization> findByPipeline(int pipeline);

    List<TPipelineAuthorization> findByAccount(int account);
}
