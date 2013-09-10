package net.txconsole.backend.dao.impl;

import net.txconsole.backend.config.Caches;
import net.txconsole.backend.dao.BranchDao;
import net.txconsole.backend.dao.model.TBranch;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BranchJdbcDao extends AbstractJdbcDao implements BranchDao {

    private final RowMapper<TBranch> branchRowMapper = new RowMapper<TBranch>() {
        @Override
        public TBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TBranch(
                    rs.getInt("id"),
                    rs.getInt("project"),
                    rs.getString("name")
            );
        }
    };

    @Autowired
    public BranchJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.BRANCH_ID)
    public TBranch getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BRANCH_BY_ID,
                params("id", id),
                branchRowMapper
        );
    }

    @Override
    @Transactional
    public int create(int project, String name) {
        return dbCreate(
                SQL.BRANCH_CREATE,
                params("project", project).addValue("name", name)
        );
    }

    @Override
    @Transactional
    public void setParameter(int branch, String name, String value) {
        MapSqlParameterSource params = params("branch", branch).addValue("name", name);
        // Deletes any previous value
        getNamedParameterJdbcTemplate().update(
                SQL.BRANCH_PARAMETER_REMOVE,
                params
        );
        // Adds it
        getNamedParameterJdbcTemplate().update(
                SQL.BRANCH_PARAMETER_INSERT,
                params.addValue("value", value)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TBranch> findByProject(int project) {
        return getNamedParameterJdbcTemplate().query(
                SQL.BRANCH_BY_PROJECT,
                params("project", project),
                branchRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getBranchParameters(int branch) {
        final Map<String, String> map = new HashMap<>();
        getNamedParameterJdbcTemplate().query(
                SQL.BRANCH_PARAMETER_BY_BRANCH,
                params("branch", branch),
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet resultSet) throws SQLException {
                        map.put(
                                resultSet.getString("parameter"),
                                resultSet.getString("value")
                        );
                    }
                }
        );
        return map;
    }
}
