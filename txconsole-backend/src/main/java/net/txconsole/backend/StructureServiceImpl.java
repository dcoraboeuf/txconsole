package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.core.model.*;
import net.txconsole.service.EventService;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.AdminGrant;
import net.txconsole.service.support.TranslationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    @AdminGrant
    public Ack deleteProject(int id) {
        ProjectSummary project = getProject(id);
        eventService.event(EventForm.projectDeleted(project));
        return projectDao.delete(id);
    }
}
