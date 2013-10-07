package net.txconsole.backend.dao;

import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionInput;

public interface ContributionDao {

    Ack post(int accountId, int branchId, ContributionInput input);

}
