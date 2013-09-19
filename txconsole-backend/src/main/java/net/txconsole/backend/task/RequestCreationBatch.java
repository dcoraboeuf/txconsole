package net.txconsole.backend.task;

import net.txconsole.backend.dao.RequestDao;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.RequestService;
import net.txconsole.service.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

// TODO Export as JMX
@Component
public class RequestCreationBatch implements ScheduledService, Runnable {

    private final Logger logger = LoggerFactory.getLogger(RequestCreationBatch.class);
    private final TransactionTemplate transactionTemplate;
    private final RequestDao requestDao;
    private final RequestService requestService;
    private final SecurityUtils securityUtils;

    @Autowired
    public RequestCreationBatch(PlatformTransactionManager transactionManager, RequestDao requestDao, RequestService requestService, SecurityUtils securityUtils) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.requestDao = requestDao;
        this.requestService = requestService;
        this.securityUtils = securityUtils;
    }

    @Override
    public void run() {
        logger.debug("[request-creation] Getting the list of created requests...");
        // Gets the list of created requests
        List<Integer> requests = requestDao.findCreated();
        logger.debug("[request-creation] Count of created requests: {}", requests.size());
        // For each of them, launches the creation
        for (final int requestId : requests) {
            logger.debug("[request-creation] Launching creation of request: {}", requestId);
            securityUtils.asAdmin(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return transactionTemplate.execute(new TransactionCallback<Void>() {
                        @Override
                        public Void doInTransaction(TransactionStatus transactionStatus) {
                            try {
                                requestService.launchCreation(requestId);
                            } catch (Exception ex) {
                                // FIXME Error handling for batches (think: log & sending a message)
                                ex.printStackTrace();
                            }
                            return null;
                        }
                    });
                }
                //
            });
        }
        // TODO JDK8 Parallel execution
    }

    @Override
    public Runnable getTask() {
        return this;
    }

    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(2, TimeUnit.MINUTES);
    }
}
