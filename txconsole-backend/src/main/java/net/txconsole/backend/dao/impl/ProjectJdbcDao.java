package net.txconsole.backend.dao.impl;

import net.txconsole.backend.config.Caches;
import net.txconsole.backend.dao.ProjectDao;
import net.txconsole.backend.dao.model.TProject;
import net.txconsole.backend.exceptions.ProjectAlreadyExistException;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.JsonConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class ProjectJdbcDao extends AbstractJdbcDao implements ProjectDao {

    private final RowMapper<TProject> projectRowMapper = new RowMapper<TProject>() {
        @Override
        public TProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TProject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("fullName"),
                    Arrays.asList(StringUtils.split(rs.getString("languages"), ","))
            );
        }
    };

    @Autowired
    public ProjectJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
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
    public int create(String name, String fullName, List<String> languages, JsonConfiguration configuration) {
        try {
            return dbCreate(
                    SQL.PROJECT_CREATE,
                    params("name", name)
                            .addValue("fullName", fullName)
                            .addValue("languages", StringUtils.join(languages, ","))
                            .addValue("txsource_id", configuration.getId())
                            .addValue("txsource_config", jsonToDB(configuration.getNode()))
            );
        } catch (DuplicateKeyException ex) {
            throw new ProjectAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(Caches.PROJECT_ID),
                    @CacheEvict(value = Caches.PROJECT_LIST, key = "'0'")
            })
    public Ack delete(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.PROJECT_DELETE,
                        params("id", id)
                )
        );
    }
}
