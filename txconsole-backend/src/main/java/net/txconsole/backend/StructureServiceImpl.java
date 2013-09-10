package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.backend.exceptions.ConfigIOException;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.EventForm;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.service.EventService;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.AdminGrant;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StructureServiceImpl implements StructureService {

    private final EventService eventService;
    private final TranslationSourceService translationSourceService;
    private final ProjectDao projectDao;
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

    @Autowired
    public StructureServiceImpl(EventService eventService, TranslationSourceService translationSourceService, ProjectDao projectDao) {
        this.eventService = eventService;
        this.translationSourceService = translationSourceService;
        this.projectDao = projectDao;
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
    @Transactional
    @AdminGrant
    public Ack deleteProject(int id) {
        ProjectSummary project = getProject(id);
        eventService.event(EventForm.projectDeleted(project));
        return projectDao.delete(id);
    }
}
