package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ProjectCreationForm;
import net.txconsole.core.model.ProjectSummary;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.AdminGrant;
import net.txconsole.service.support.TranslationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StructureServiceImpl implements StructureService {

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
    public StructureServiceImpl(TranslationSourceService translationSourceService, ProjectDao projectDao) {
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
        // OK
        return getProject(id);
    }

    @Override
    @Transactional
    @AdminGrant
    public Ack deleteProject(int id) {
        return projectDao.delete(id);
    }
}
