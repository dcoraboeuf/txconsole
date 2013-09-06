package net.txconsole.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.txconsole.backend.dao.PipelineDao;
import net.txconsole.backend.dao.model.TPipeline;
import net.txconsole.core.model.PipelineCreationForm;
import net.txconsole.core.model.PipelineSummary;
import net.txconsole.core.security.SecurityRoles;
import net.txconsole.service.StructureService;
import net.txconsole.service.security.AdminGrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StructureServiceImpl implements StructureService {

    private final PipelineDao pipelineDao;
    private final Function<TPipeline, PipelineSummary> pipelineSummaryFunction = new Function<TPipeline, PipelineSummary>() {
        @Override
        public PipelineSummary apply(TPipeline t) {
            return new PipelineSummary(
                    t.getId(),
                    t.getName(),
                    t.getDescription()
            );
        }
    };

    @Autowired
    public StructureServiceImpl(PipelineDao pipelineDao) {
        this.pipelineDao = pipelineDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineSummary> getPipelines() {
        return Lists.transform(
                pipelineDao.findAll(),
                pipelineSummaryFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PipelineSummary getPipeline(int id) {
        return pipelineSummaryFunction.apply(pipelineDao.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PipelineSummary getPipelineByName(String name) {
        return pipelineSummaryFunction.apply(pipelineDao.getByName(name));
    }

    @Override
    @Transactional
    @AdminGrant
    public PipelineSummary createPipeline(PipelineCreationForm form) {
        int id = pipelineDao.create(form.getName(), form.getDescription());
        return getPipeline(id);
    }
}
