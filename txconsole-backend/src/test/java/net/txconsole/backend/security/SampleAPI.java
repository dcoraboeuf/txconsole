package net.txconsole.backend.security;

public interface SampleAPI {

    void no_constraint();

	void pipeline_call_missing_param(int pipeline);

	void pipeline_call_too_much(int pipeline, int additional);

	void pipeline_call_ok(int pipeline, String name);

}
