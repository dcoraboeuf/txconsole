package net.txconsole.service;

import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionInput;

public interface ContributionService {

    Ack post(int branchId, ContributionInput input);

}
