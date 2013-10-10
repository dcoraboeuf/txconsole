package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TBranch;

import java.util.List;
import java.util.Map;

public interface BranchDao {

    TBranch getById(int id);

    int create(int project, String name);

    void setParameter(int branch, String name, String value);

    List<TBranch> findByProject(int project);

    Map<String,String> getBranchParameters(int branch);

    void delete(int id);
}
