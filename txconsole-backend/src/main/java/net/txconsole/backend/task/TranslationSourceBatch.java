package net.txconsole.backend.task;

import net.txconsole.service.ScheduledService;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TranslationSourceBatch implements ScheduledService, Runnable {

    @Override
    public Runnable getTask() {
        return this;
    }

    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(1, TimeUnit.MINUTES);
    }

    // FIXME Sync the translation maps for all branches
    // TODO Uses a cache per branch ID for the read/write locks
    // TODO Merges with the service used to access translation maps per branch
    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
