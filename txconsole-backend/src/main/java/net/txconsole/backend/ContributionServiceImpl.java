package net.txconsole.backend;

import net.txconsole.core.model.Ack;
import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.ContributionService;
import net.txconsole.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContributionServiceImpl implements ContributionService {

    private final StructureService structureService;
    private final SecurityUtils securityUtils;

    @Autowired
    public ContributionServiceImpl(StructureService structureService, SecurityUtils securityUtils) {
        this.structureService = structureService;
        this.securityUtils = securityUtils;
    }

    @Override
    public Ack post(int branchId, ContributionInput input) {
        // Branch
        BranchSummary branch = structureService.getBranch(branchId);
        // Checks for authorizations
        securityUtils.checkGrant(ProjectFunction.CONTRIBUTION, branch.getProjectId());
        // Contribution mode
        boolean direct = securityUtils.isGranted(ProjectFunction.CONTRIBUTION_DIRECT, branch.getProjectId());
        // Direct mode
        if (direct) {
            // TODO return save(branchId, input);
            return Ack.NOK;
        }
        // Staging
        else {
            return stage(branchId, input);
        }
    }

    protected Ack stage(int branchId, ContributionInput input) {
        return Ack.NOK;  //To change body of created methods use File | Settings | File Templates.
    }

}
