package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionEntry;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.support.TimeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class ContributionJdbcDao extends AbstractJdbcDao implements ContributionDao {

    @Autowired
    public ContributionJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional
    public Ack post(int accountId, int branchId, ContributionInput input) {
        // Root
        int contributionId = dbCreate(
                SQL.CONTRIBUTION_INSERT,
                params("branch", branchId)
                        .addValue("account", accountId)
                        .addValue("message", input.getMessage())
                        .addValue("timestamp", SQLUtils.toTimestamp(TimeUtils.now()))
        );
        // Entries
        for (ContributionEntry entry : input.getContributions()) {
            dbCreate(
                    SQL.CONTRIBUTION_ENTRY_INSERT,
                    params("contribution", contributionId)
                            .addValue("bundle", entry.getBundle())
                            .addValue("section", entry.getSection())
                            .addValue("name", entry.getKey())
                            .addValue("locale", entry.getLocale().toString())
                            .addValue("value", entry.getValue())
            );
        }
        // OK
        return Ack.OK;
    }
}
