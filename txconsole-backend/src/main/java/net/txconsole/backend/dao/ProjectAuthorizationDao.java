package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TProjectAuthorization;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ProjectRole;

import java.util.List;

public interface ProjectAuthorizationDao {

    List<TProjectAuthorization> findByProject(int project);

    List<TProjectAuthorization> findByAccount(int account);

    Ack set(int project, int account, ProjectRole role);
}
