package net.txconsole.backend;

import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.ContributionService;
import net.txconsole.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final StructureService structureService;
    private final ContributionDao contributionDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public ContributionServiceImpl(StructureService structureService, ContributionDao contributionDao, SecurityUtils securityUtils) {
        this.structureService = structureService;
        this.contributionDao = contributionDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional
    public Ack post(int branchId, ContributionInput input) {
        // Branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getProjectId());
        // Contribution mode
        boolean direct = securityUtils.isGranted(ProjectFunction.CONTRIBUTION_DIRECT, branch.getProjectId());
        // FIXME Direct mode
        if (direct) {
            return Ack.NOK;
        }
        // Staging
        else {
            return stage(branchId, input);
        }
    }

    protected Ack stage(int branchId, ContributionInput input) {
        // Gets the current account ID
        int accountId = securityUtils.getCurrentAccountId();
        // Saves the contribution
        return contributionDao.post(accountId, branchId, input);
    }

}
