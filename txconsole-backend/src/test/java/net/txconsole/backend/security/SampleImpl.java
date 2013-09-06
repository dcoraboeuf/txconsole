package net.txconsole.backend.security;

import net.txconsole.service.security.ProjectFunction;
import net.txconsole.service.security.ProjectGrant;
import net.txconsole.service.security.ProjectGrantId;

public class SampleImpl implements SampleAPI {

	@Override
	public void no_constraint() {
	}

	@Override
	@ProjectGrant(ProjectFunction.UPDATE)
	public void project_call_missing_param(int project) {
	}

	@Override
	@ProjectGrant(ProjectFunction.UPDATE)
	public void project_call_too_much(@ProjectGrantId int project, @ProjectGrantId int additional) {
	}

	@Override
	@ProjectGrant(ProjectFunction.UPDATE)
	public void project_call_ok(@ProjectGrantId int project, String name) {
	}
}
