package net.txconsole.web.resource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.txconsole.core.model.BranchDashboard;
import net.txconsole.core.model.ProjectDashboard;
import net.txconsole.web.controller.GUIController;
import net.txconsole.web.controller.UIController;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectDashboardResource extends Resource<ProjectDashboard> {

    private final List<BranchDashboardResource> branches;

    public ProjectDashboardResource(ProjectDashboard o) {
        super(o);
        withLink(linkTo(methodOn(UIController.class).getProject(Locale.ENGLISH, o.getProject().getId())).withSelfRel());
        withLink(linkTo(methodOn(GUIController.class).getProject(Locale.ENGLISH, o.getProject().getId())).withRel(Resource.REL_GUI));
        this.branches = Lists.transform(
                o.getBranches(),
                new Function<BranchDashboard, BranchDashboardResource>() {
                    @Override
                    public BranchDashboardResource apply(BranchDashboard b) {
                        return new BranchDashboardResource(b);
                    }
                }
        );
    }
}
