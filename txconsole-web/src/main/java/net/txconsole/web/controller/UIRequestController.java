package net.txconsole.web.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.sf.jstring.Strings;
import net.txconsole.core.Content;
import net.txconsole.core.model.*;
import net.txconsole.core.security.ProjectFunction;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.RequestService;
import net.txconsole.service.StructureService;
import net.txconsole.web.resource.Resource;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.GUIEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/ui")
public class UIRequestController extends AbstractUIController implements UIRequest {

    private final UI ui;
    private final RequestService requestService;
    private final StructureService structureService;
    private final GUIEventService guiEventService;
    private final SecurityUtils securityUtils;
    /**
     * Gets the resource for a request configuration data
     */
    private final Function<Locale, Function<RequestConfigurationData, Resource<RequestConfigurationData>>> requestConfigurationDataResourceFn =
            new Function<Locale, Function<RequestConfigurationData, Resource<RequestConfigurationData>>>() {
                @Override
                public Function<RequestConfigurationData, Resource<RequestConfigurationData>> apply(final Locale locale) {
                    return new Function<RequestConfigurationData, Resource<RequestConfigurationData>>() {
                        @Override
                        public Resource<RequestConfigurationData> apply(RequestConfigurationData o) {
                            return new Resource<>(o)
                                    .withLink(linkTo(methodOn(UIController.class).getBranch(locale, o.getBranch().getId())).withRel("branch"))
                                    .withLink(linkTo(methodOn(GUIController.class).getBranch(locale, o.getBranch().getId())).withRel("branch-gui"))
                                    .withLink(linkTo(methodOn(UIController.class).getProject(locale, o.getProject().getId())).withRel("project"))
                                    .withLink(linkTo(methodOn(GUIController.class).getProject(locale, o.getProject().getId())).withRel("project-gui"));
                        }
                    };
                }
            };
    /**
     * Gets the resource for a request summary
     */
    private final Function<Locale, Function<RequestSummary, Resource<RequestSummary>>> requestSummaryResourceFn = new Function<Locale, Function<RequestSummary, Resource<RequestSummary>>>() {
        @Override
        public Function<RequestSummary, Resource<RequestSummary>> apply(final Locale locale) {
            return new Function<RequestSummary, Resource<RequestSummary>>() {
                @Override
                public Resource<RequestSummary> apply(RequestSummary o) {
                    BranchSummary branch = structureService.getBranch(o.getBranchId());
                    return new Resource<>(o)
                            .withLink(linkTo(methodOn(UIController.class).getBranch(locale, o.getBranchId())).withRel("branch"))
                            .withLink(linkTo(methodOn(GUIController.class).getBranch(locale, o.getBranchId())).withRel("branch-gui"))
                            .withLink(linkTo(methodOn(UIRequestController.class).getRequest(locale, o.getId())).withSelfRel())
                            .withLink(linkTo(methodOn(GUIController.class).getRequest(locale, o.getId())).withRel("gui"))
                                    // ACL
                            .withAction(ProjectFunction.REQUEST_UPLOAD, o.getStatus() == RequestStatus.REQUEST_EXPORTED && securityUtils.isGranted(ProjectFunction.REQUEST_UPLOAD, branch.getProjectId()))
                            .withAction(ProjectFunction.REQUEST_MERGE, o.getStatus() == RequestStatus.REQUEST_EXPORTED && securityUtils.isGranted(ProjectFunction.REQUEST_MERGE, branch.getProjectId()))
                            .withAction(ProjectFunction.REQUEST_EDIT, o.getStatus() == RequestStatus.REQUEST_EXPORTED && securityUtils.isGranted(ProjectFunction.REQUEST_EDIT, branch.getProjectId()))
                            .withAction(ProjectFunction.REQUEST_DELETE, securityUtils.isGranted(ProjectFunction.REQUEST_DELETE, branch.getProjectId()))
                                    // Events
                            .withEvent(guiEventService.getResourceEvent(locale, EventEntity.REQUEST, o.getId(), EventCode.REQUEST_CREATED));
                }
            };
        }
    };

    /**
     * Request summary resource with events and ACL
     */

    @Autowired
    public UIRequestController(ErrorHandler errorHandler, Strings strings, UI ui, RequestService requestService, StructureService structureService, GUIEventService guiEventService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.ui = ui;
        this.requestService = requestService;
        this.structureService = structureService;
        this.guiEventService = guiEventService;
        this.securityUtils = securityUtils;
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
    Resource<RequestConfigurationData> getRequestConfigurationData(Locale locale, @PathVariable int branchId) {
        return requestConfigurationDataResourceFn.apply(locale).apply(
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
    Resource<RequestSummary> createRequest(Locale locale, @PathVariable int branchId, @RequestBody RequestCreationForm form) {
        return requestSummaryResourceFn.apply(locale).apply(requestService.createRequest(branchId, form));
    }

    /**
     * Gets the list of requests for a branch
     */
    @RequestMapping(value = "/branch/{branchId}/request", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Resource<RequestSummary>> getRequestsForBranch(
            Locale locale,
            @PathVariable int branchId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        return Lists.transform(
                requestService.getRequestsForBranch(branchId, offset, count),
                requestSummaryResourceFn.apply(locale)
        );
    }

    /**
     * Downloading a request
     */
    @RequestMapping(value = "/request/{requestId}/download", method = RequestMethod.GET)
    public void downloadRequest(Locale locale, @PathVariable int requestId, HttpServletResponse response) throws IOException {
        // Gets the request file
        Content content = requestService.getRequestFile(requestId);
        // Updates the HTTP response
        response.setContentType(content.getType());
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"request-%d.zip\"", requestId));
        response.setContentLength(content.getBytes().length);
        // Gets the output
        ServletOutputStream out = response.getOutputStream();
        // Writes to the output
        out.write(content.getBytes());
        out.flush();
        out.close();
    }

    /**
     * Gets a request by its ID
     */
    @RequestMapping(value = "/request/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<RequestSummary> getRequest(Locale locale, @PathVariable int id) {
        return requestSummaryResourceFn.apply(locale).apply(requestService.getRequest(id));
    }

    /**
     * Uploads response files for a request
     */
    @Override
    @RequestMapping(value = "/request/{id}/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    Resource<RequestControlledView> uploadRequest(Locale locale, @PathVariable int id, @RequestParam Collection<MultipartFile> files) {
        // Performs the upload
        requestService.uploadRequest(id, files);
        // OK
        return getRequestView(locale, id);
    }

    /**
     * Gets the view for a request
     */
    @RequestMapping(value = "/request/{id}/view", method = RequestMethod.GET)
    public
    @ResponseBody
    Resource<RequestControlledView> getRequestView(Locale locale, @PathVariable int id) {
        // Gets the view
        RequestView view = requestService.getRequestView(id);
        // Gets the controls
        List<TranslationDiffControl> controls = requestService.controlRequest(locale, id);
        // Controlled view
        RequestControlledView controlledView = new RequestControlledView(
                view,
                controls
        );
        // Gets the branch
        BranchSummary branch = structureService.getBranch(controlledView.getSummary().getBranchId());
        int projectId = branch.getProjectId();
        // View resource
        Resource<RequestControlledView> r = new Resource<>(controlledView)
                // TODO Links
                // ACL
                .withActions(securityUtils, projectId, ProjectFunction.values());
        // OK
        return r;
    }

    /**
     * Gets the details for a translation request entry
     */
    @RequestMapping(value = "/request/entry/{entryId}", method = RequestMethod.GET)
    public
    @ResponseBody
    RequestControlledEntry getRequestEntryDetails(Locale locale, @PathVariable int entryId) {
        return new RequestControlledEntry(
                requestService.getRequestEntryDetails(entryId),
                requestService.controlRequestEntry(locale, entryId)
        );
    }

    /**
     * Edits an entry
     */
    @RequestMapping(value = "/request/entry/{entryId}", method = RequestMethod.PUT)
    public
    @ResponseBody
    RequestControlledEntryValue editRequestEntry(Locale locale, @PathVariable int entryId, @RequestBody RequestEntryInput input) {
        return requestService.editRequestEntry(locale, entryId, input);
    }

    /**
     * Controls a request
     */
    @RequestMapping(value = "/request/{id}/control", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TranslationDiffControl> controlRequest(Locale locale, @PathVariable int id) {
        return requestService.controlRequest(locale, id);
    }

    /**
     * Deletes a request
     */
    @RequestMapping(value = "/request/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Resource<BranchSummary> deleteRequest(Locale locale, @PathVariable int id) {
        RequestSummary request = requestService.deleteRequest(id);
        return ui.getBranch(locale, request.getBranchId());
    }

}
