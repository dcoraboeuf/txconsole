package net.txconsole.backend.dao.impl;

import net.txconsole.backend.dao.ProjectAuthorizationDao;
import net.txconsole.backend.dao.model.TProjectAuthorization;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ProjectRole;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectAuthorizationJdbcDao extends AbstractJdbcDao implements ProjectAuthorizationDao {

    private final RowMapper<TProjectAuthorization> projectAuthorizationRowMapper = new RowMapper<TProjectAuthorization>() {

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
    public ProjectAuthorizationJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProjectAuthorization> findByProject(int project) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROJECT_AUTHORIZATION_BY_PROJECT,
                params("project", project),
                projectAuthorizationRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProjectAuthorization> findByAccount(int account) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROJECT_AUTHORIZATION_BY_ACCOUNT,
                params("account", account),
                projectAuthorizationRowMapper
        );
    }

    @Override
    @Transactional
    public Ack set(int project, int account, ProjectRole role) {
        MapSqlParameterSource params = params("project", project).addValue("account", account);
        getNamedParameterJdbcTemplate().update(
                SQL.PROJECT_AUTHORIZATION_DELETE,
                params
        );
        getNamedParameterJdbcTemplate().update(
                SQL.PROJECT_AUTHORIZATION_INSERT,
                params.addValue("role", role.name())
        );
        return Ack.OK;
    }

    @Override
    @Transactional
    public Ack unset(int project, int account) {
        MapSqlParameterSource params = params("project", project).addValue("account", account);
        getNamedParameterJdbcTemplate().update(
                SQL.PROJECT_AUTHORIZATION_DELETE,
                params
        );
        return Ack.OK;
    }
}
