package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TBranch;

import java.util.List;

public interface BranchDao {

    TBranch getById(int id);

    int create(int project, String name);

    void setParameter(int branch, String name, String value);

    List<TBranch> findByProject(int project);
}
