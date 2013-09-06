package net.txconsole.service;

import net.txconsole.core.model.PipelineCreationForm;
import net.txconsole.core.model.PipelineSummary;

import java.util.List;

public interface StructureService {

    List<PipelineSummary> getPipelines();

    PipelineSummary getPipeline(int id);

    PipelineSummary createPipeline(PipelineCreationForm form);

    PipelineSummary getPipelineByName(String name);
}
