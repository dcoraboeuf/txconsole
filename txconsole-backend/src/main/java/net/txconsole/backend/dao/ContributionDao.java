package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TContribution;
import net.txconsole.core.model.ContributionInput;

import java.util.List;

public interface ContributionDao {

    int post(int accountId, int branchId, ContributionInput input);

    TContribution getById(int id);

    List<TContribution> findByBranch(int branchId);
}
