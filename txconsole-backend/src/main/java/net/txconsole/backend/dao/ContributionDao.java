package net.txconsole.backend.dao;

import net.txconsole.backend.dao.model.TContribution;
import net.txconsole.backend.dao.model.TContributionDetail;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionInput;

import java.util.List;

public interface ContributionDao {

    int post(int accountId, int branchId, ContributionInput input);

    TContribution getById(int id);

    List<TContribution> findByBranch(int branchId);

    List<TContributionDetail> findDetailsById(int contribution);

    Ack delete(int id);
}
