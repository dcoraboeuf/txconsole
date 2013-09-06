package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TProjectAuthorization;

import java.util.List;

public interface ProjectAuthorizationDao {

    List<TProjectAuthorization> findByPipeline(int pipeline);

    List<TProjectAuthorization> findByAccount(int account);
}
