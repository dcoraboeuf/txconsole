package net.txconsole.service;

import org.springframework.scheduling.Trigger;

public interface ScheduledService {

    Runnable getTask();

    Trigger getTrigger();

}
