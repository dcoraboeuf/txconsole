package net.txconsole.backend.security;

import net.txconsole.service.security.PipelineFunction;
import net.txconsole.service.security.PipelineGrant;
import net.txconsole.service.security.PipelineGrantId;

public class SampleImpl implements SampleAPI {

	@Override
	public void no_constraint() {
	}

	@Override
	@PipelineGrant(PipelineFunction.UPDATE)
	public void pipeline_call_missing_param(int pipeline) {
	}

	@Override
	@PipelineGrant(PipelineFunction.UPDATE)
	public void pipeline_call_too_much(@PipelineGrantId int pipeline, @PipelineGrantId int additional) {
	}

	@Override
	@PipelineGrant(PipelineFunction.UPDATE)
	public void pipeline_call_ok(@PipelineGrantId int pipeline, String name) {
	}
}
