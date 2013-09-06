package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.ProjectAuthorizationDao;
import net.txconsole.backend.dao.model.TProjectAuthorization;
import net.txconsole.core.model.ProjectRole;
import net.txconsole.dao.AbstractJdbcDao;
import net.txconsole.dao.SQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectAuthorizationJdbcDao extends AbstractJdbcDao implements ProjectAuthorizationDao {

    private final RowMapper<TProjectAuthorization> pipelineAuthorizationRowMapper = new RowMapper<TProjectAuthorization>() {

        @Override
        public TProjectAuthorization mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TProjectAuthorization(
                    rs.getInt("project"),
                    rs.getInt("account"),
                    SQLUtils.getEnum(ProjectRole.class, rs, "role")
            );
        }
    };

    @Autowired
    public ProjectAuthorizationJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProjectAuthorization> findByPipeline(int pipeline) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROJECT_AUTHORIZATION_BY_PROJECT,
                params("project", pipeline),
                pipelineAuthorizationRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProjectAuthorization> findByAccount(int account) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROJECT_AUTHORIZATION_BY_ACCOUNT,
                params("account", account),
                pipelineAuthorizationRowMapper
        );
    }
}
