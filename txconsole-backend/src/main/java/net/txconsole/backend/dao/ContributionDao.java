package net.txconsole.backend.dao;

import net.txconsole.core.model.ContributionInput;

public interface ContributionDao {

    int post(int accountId, int branchId, ContributionInput input);

}
