package net.txconsole.web.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.txconsole.core.model.BranchDashboard;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.web.controller.GUIController;
import net.txconsole.web.controller.UIController;
import net.txconsole.web.controller.UIRequestController;

import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Data
@EqualsAndHashCode(callSuper = false)
public class BranchDashboardResource extends Resource<BranchDashboard> {

    private final Resource<RequestSummary> request;


    public BranchDashboardResource(BranchDashboard branchDashboard) {
        super(branchDashboard);
        withLink(linkTo(methodOn(UIController.class).getBranch(Locale.ENGLISH, branchDashboard.getBranch().getId())).withSelfRel());
        withLink(linkTo(methodOn(GUIController.class).getBranch(Locale.ENGLISH, branchDashboard.getBranch().getId())).withRel(Resource.REL_GUI));
        RequestSummary lastOpenBranch = branchDashboard.getLastOpenRequest();
        if (lastOpenBranch != null) {
            this.request = new Resource<>(lastOpenBranch)
                    .withLink(linkTo(methodOn(UIRequestController.class).getRequest(Locale.ENGLISH, lastOpenBranch.getId())).withSelfRel())
                    .withLink(linkTo(methodOn(GUIController.class).getRequest(Locale.ENGLISH, lastOpenBranch.getId())).withRel(Resource.REL_GUI));
        } else {
            this.request = null;
        }
    }
}
