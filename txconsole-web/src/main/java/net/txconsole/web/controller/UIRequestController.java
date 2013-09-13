package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.model.RequestConfigurationData;
import net.txconsole.core.model.RequestCreationForm;
import net.txconsole.core.model.RequestSummary;
import net.txconsole.service.RequestService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/ui")
public class UIRequestController extends AbstractUIController {

    private final RequestService requestService;
    /**
     * Gets the resource for a request configuration data
     */
    private final Function<RequestConfigurationData, Resource<RequestConfigurationData>> requestConfigurationDataResourceFn =
            new Function<RequestConfigurationData, Resource<RequestConfigurationData>>() {
                @Override
                public Resource<RequestConfigurationData> apply(RequestConfigurationData o) {
                    return new Resource<>(o)
                            .withLink(linkTo(methodOn(UIController.class).getBranch(o.getBranch().getId())).withRel("branch"))
                            .withLink(linkTo(methodOn(GUIController.class).getBranch(o.getBranch().getId())).withRel("branch-gui"))
                            .withLink(linkTo(methodOn(UIController.class).getProject(o.getProject().getId())).withRel("project"))
                            .withLink(linkTo(methodOn(GUIController.class).getProject(o.getProject().getId())).withRel("project-gui"));
                }
            };
    /**
     * Gets the resource for a request summary
     */
    private final Function<RequestSummary, Resource<RequestSummary>> requestSummaryResourceFn = new Function<RequestSummary, Resource<RequestSummary>>() {
        @Override
        public Resource<RequestSummary> apply(RequestSummary o) {
            return new Resource<>(o)
                    .withLink(linkTo(methodOn(UIController.class).getBranch(o.getBranchId())).withRel("branch"))
                    .withLink(linkTo(methodOn(GUIController.class).getBranch(o.getBranchId())).withRel("branch-gui"));
            // TODO Request UI
            // TODO Request GUI
        }
    };

    /**
     * Request summary resource with events and ACL
     */

    @Autowired
    public UIRequestController(ErrorHandler errorHandler, Strings strings, RequestService requestService) {
        super(errorHandler, strings);
        this.requestService = requestService;
    }

    /**
     * Gets the configuration data to create a translation request for a branch.
     *
     * @param branchId ID of the branch
     * @return Configuration data
     */
    @RequestMapping(value = "/branch/{branchId}/request/config", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<RequestConfigurationData> getRequestConfigurationData(@PathVariable int branchId) {
        return requestConfigurationDataResourceFn.apply(
                requestService.getRequestConfigurationData(branchId)
        );
    }

    /**
     * Sends a form for creation of a translation request for a branch
     *
     * @param branchId ID of the branch
     * @param form     Request creation form
     */
    @RequestMapping(value = "/branch/{branchId}/request", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<RequestSummary> createRequest(@PathVariable int branchId, @RequestBody RequestCreationForm form) {
        return requestSummaryResourceFn.apply(requestService.createRequest(branchId, form));
    }

    /**
     * Gets the list of requests for a branch
     */
    @RequestMapping(value = "/branch/{branchId}/request", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<RequestSummary>> getRequestsForBranch(
            @PathVariable int branchId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        return Lists.transform(
                requestService.getRequestsForBranch(branchId, offset, count),
                requestSummaryResourceFn
        );
    }

}