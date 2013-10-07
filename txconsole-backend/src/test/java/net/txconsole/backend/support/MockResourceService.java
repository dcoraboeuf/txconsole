package net.txconsole.backend.support;

import net.txconsole.core.model.BranchSummary;
import net.txconsole.core.model.ContributionSummary;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.core.model.Resource;
import net.txconsole.service.ResourceService;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MockResourceService implements ResourceService {

    @Override
    public Resource<BranchSummary> getBranch(Locale locale, BranchSummary branch) {
        return new Resource<>(branch)
                .withLink(new Link("http://server:port/context/branch/" + branch.getId(), Resource.REL_GUI));
    }

    @Override
    public Resource<ProjectSummary> getProject(Locale locale, ProjectSummary project) {
        return new Resource<>(project)
                .withLink(new Link("http://server:port/context/project/" + project.getId(), Resource.REL_GUI));
    }

    @Override
    public Resource<ContributionSummary> getContribution(Locale locale, ContributionSummary contribution) {
        return new Resource<>(contribution)
                .withLink(new Link("http://server:port/context/contribution/" + contribution.getId(), Resource.REL_GUI));
    }
}
