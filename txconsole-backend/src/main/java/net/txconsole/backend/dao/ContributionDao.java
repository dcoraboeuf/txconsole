package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TContribution;
import net.txconsole.core.model.ContributionInput;

public interface ContributionDao {

    int post(int accountId, int branchId, ContributionInput input);

    TContribution getById(int id);
}
