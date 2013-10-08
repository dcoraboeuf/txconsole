package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.ContributionDao;
import net.txconsole.backend.dao.model.TContribution;
import net.txconsole.backend.dao.model.TContributionDetail;
import net.txconsole.core.model.ContributionEntry;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.support.TimeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ContributionJdbcDao extends AbstractJdbcDao implements ContributionDao {

    private final RowMapper<TContribution> contributionRowMapper = new RowMapper<TContribution>() {
        @Override
        public TContribution mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TContribution(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getString("message"),
                    rs.getInt("account"),
                    SQLUtils.getDateTime(rs, "timestamp")
            );
        }
    };

    @Autowired
    public ContributionJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional
    public int post(int accountId, int branchId, ContributionInput input) {
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
                            .addValue("oldValue", entry.getOldValue())
                            .addValue("newValue", entry.getNewValue())
            );
        }
        // OK
        return contributionId;
    }

    @Override
    @Transactional(readOnly = true)
    public TContribution getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.CONTRIBUTION_BY_ID,
                params("id", id),
                contributionRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TContribution> findByBranch(int branchId) {
        return getNamedParameterJdbcTemplate().query(
                SQL.CONTRIBUTION_BY_BRANCH,
                params("branch", branchId),
                contributionRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TContributionDetail> findDetailsById(int contribution) {
        return getNamedParameterJdbcTemplate().query(
                SQL.CONTRIBUTION_ENTRY_BY_CONTRIBUTION,
                params("contribution", contribution),
                new RowMapper<TContributionDetail>() {
                    @Override
                    public TContributionDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new TContributionDetail(
                                rs.getInt("id"),
                                rs.getInt("contribution"),
                                rs.getString("bundle"),
                                rs.getString("section"),
                                rs.getString("name"),
                                SQLUtils.toLocale(rs, "locale"),
                                rs.getString("oldValue"),
                                rs.getString("newValue")
                        );
                    }
                }
        );
    }

    @Override
    @Transactional
    public void delete(int id) {
        getNamedParameterJdbcTemplate().update(
                SQL.CONTRIBUTION_DELETE,
                params("id", id)
        );
    }
}
