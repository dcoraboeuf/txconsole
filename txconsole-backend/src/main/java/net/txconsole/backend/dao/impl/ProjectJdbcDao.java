package net.txconsole.backend.dao.impl;

import net.txconsole.backend.config.Caches;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.backend.exceptions.ProjectAlreadyExistException;
import net.txconsole.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectJdbcDao extends AbstractJdbcDao implements ProjectDao {

    private final RowMapper<TProject> projectRowMapper = new RowMapper<TProject>() {
        @Override
        public TProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TProject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("fullName")
            );
        }
    };

    @Autowired
    public ProjectJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = Caches.PROJECT_LIST, key = "'0'")
    public List<TProject> findAll() {
        return getJdbcTemplate().query(
                SQL.PROJECT_ALL,
                projectRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.PROJECT_ID)
    public TProject getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROJECT_BY_ID,
                params("id", id),
                projectRowMapper
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.PROJECT_LIST, key = "'0'")
    public int create(String name, String description) {
        try {
            return dbCreate(
                    SQL.PROJECT_CREATE,
                    params("name", name).addValue("fullName", description)
            );
        } catch (DuplicateKeyException ex) {
            throw new ProjectAlreadyExistException(name);
        }
    }
}
