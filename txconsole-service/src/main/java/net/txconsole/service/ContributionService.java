package net.txconsole.service;

import net.sf.jstring.LocalizableMessage;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionDetail;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.model.ContributionSummary;

import java.util.List;

public interface ContributionService {

    LocalizableMessage post(int branchId, ContributionInput input);

    ContributionSummary getContribution(int id);

    List<ContributionDetail> getContributionDetails(int id);

    ContributionSummary blankContribution(int branchId);

    List<ContributionSummary> getContributionList(int branchId);

    Ack deleteContribution(int id);
}
