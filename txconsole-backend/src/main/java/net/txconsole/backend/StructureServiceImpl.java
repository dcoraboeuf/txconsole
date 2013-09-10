package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.BranchDao;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TBranch;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.backend.exceptions.ConfigIOException;
import net.txconsole.backend.exceptions.ProjectParametersNotDefinedByBranchException;
import net.txconsole.backend.exceptions.ProjectParametersNotDefinedException;
import net.txconsole.core.model.*;
import net.txconsole.service.EventService;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.AdminGrant;
import net.txconsole.service.security.ProjectFunction;
import net.txconsole.service.security.ProjectGrant;
import net.txconsole.service.security.ProjectGrantId;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StructureServiceImpl implements StructureService {

    private final EventService eventService;
    private final TranslationSourceService translationSourceService;
    private final ProjectDao projectDao;
    private final BranchDao branchDao;
    /**
     * Project summary from the table
     */
    private final Function<TProject, ProjectSummary> projectSummaryFunction = new Function<TProject, ProjectSummary>() {
        @Override
        public ProjectSummary apply(TProject t) {
            return new ProjectSummary(
                    t.getId(),
                    t.getName(),
                    t.getFullName()
            );
        }
    };
    /**
     * Branch summary from the table
     */
    private final Function<TBranch, BranchSummary> branchSummaryFunction = new Function<TBranch, BranchSummary>() {
        @Override
        public BranchSummary apply(TBranch t) {
            return new BranchSummary(
                    t.getId(),
                    t.getProject(),
                    t.getName()
            );
        }
    };

    @Autowired
    public StructureServiceImpl(EventService eventService, TranslationSourceService translationSourceService, ProjectDao projectDao, BranchDao branchDao) {
        this.eventService = eventService;
        this.translationSourceService = translationSourceService;
        this.projectDao = projectDao;
        this.branchDao = branchDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummary> getProjects() {
        return Lists.transform(
                projectDao.findAll(),
                projectSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectSummary getProject(int id) {
        return projectSummaryFunction.apply(projectDao.getById(id));
    }

    @Override
    @Transactional
    @AdminGrant
    public ProjectSummary createProject(ProjectCreationForm form) {
        // TODO Checks the project name
        // TODO Checks the languages
        // Checks the configuration
        translationSourceService.getConfiguredTranslationSource(form.getTxSourceConfig());
        // Creation
        int id = projectDao.create(form.getName(), form.getFullName(), form.getLanguages(), form.getTxSourceConfig());
        ProjectSummary project = getProject(id);
        // Event
        eventService.event(EventForm.projectCreated(project));
        // OK
        return project;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getProjectParameters(int id) {
        // Gets the project information
        TProject t = projectDao.getById(id);
        // Gets the project configuration
        Configured<Object, TranslationSource<Object>> configuredTranslationSource = translationSourceService.getConfiguredTranslationSource(t.getTxSourceConfig());
        // Gets the configuration as JSON
        String jsonConfiguration;
        try {
            jsonConfiguration = configuredTranslationSource.writeConfigurationAsJsonString();
        } catch (IOException e) {
            throw new ConfigIOException("txsource", configuredTranslationSource.getConfigurable().getId(), e);
        }
        // Extracts the parameters
        List<String> parameters = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$([A-Z_]+)");
        Matcher m = pattern.matcher(jsonConfiguration);
        while (m.find()) {
            parameters.add(m.group(1));
        }
        // OK
        return parameters;
    }

    @Override
    @Transactional(readOnly = true)
    public BranchSummary getBranch(int id) {
        return branchSummaryFunction.apply(branchDao.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchSummary> getProjectBranches(int id) {
        return Lists.transform(
                branchDao.findByProject(id),
                branchSummaryFunction
        );
    }

    @Override
    @Transactional
    @ProjectGrant(ProjectFunction.UPDATE)
    public BranchSummary createBranch(@ProjectGrantId int project, BranchCreationForm form) {
        // TODO Cheks the branch name
        // Gets the project parameters
        List<String> projectParameters = getProjectParameters(project);
        // Gets the input (branch parameters)
        Collection<String> branchParameters = Collections2.transform(
                form.getParameters(),
                ParameterValueForm.nameFn
        );
        // Checks that each project parameter is set by the branch
        List<String> remainingProjectParameters = new ArrayList<>(projectParameters);
        remainingProjectParameters.removeAll(branchParameters);
        if (!remainingProjectParameters.isEmpty()) {
            throw new ProjectParametersNotDefinedByBranchException(remainingProjectParameters);
        }
        // Checks that each branch parameter is defined at project level
        List<String> remainingBranchParameters = new ArrayList<>(branchParameters);
        remainingBranchParameters.removeAll(projectParameters);
        if (!remainingBranchParameters.isEmpty()) {
            throw new ProjectParametersNotDefinedException(remainingBranchParameters);
        }
        // Creates the branch ID
        int branch = branchDao.create(project, form.getName());
        // Adds the parameters
        for (ParameterValueForm parameterValueForm : form.getParameters()) {
            branchDao.setParameter(branch, parameterValueForm.getName(), parameterValueForm.getValue());
        }
        // Loads the summaries (for the event generation)
        BranchSummary branchSummary = getBranch(branch);
        ProjectSummary projectSummary = getProject(project);
        eventService.event(EventForm.branchCreated(branchSummary, projectSummary));
        // OK
        return branchSummary;
    }

    @Override
    @Transactional
    @AdminGrant
    public Ack deleteProject(int id) {
        ProjectSummary project = getProject(id);
        eventService.event(EventForm.projectDeleted(project));
        return projectDao.delete(id);
    }
}
