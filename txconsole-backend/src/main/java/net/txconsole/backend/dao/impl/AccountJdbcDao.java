package net.txconsole.backend.dao.impl;

import net.txconsole.backend.config.Caches;
import net.txconsole.backend.dao.AccountDao;
import net.txconsole.backend.dao.model.TAccount;
import net.txconsole.backend.exceptions.AccountAlreadyExistException;
import net.txconsole.core.model.Ack;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Component
public class AccountJdbcDao extends AbstractJdbcDao implements AccountDao {

    private final RowMapper<TAccount> accountRowMapper = new RowMapper<TAccount>() {
        @Override
        public TAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TAccount(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("roleName"),
                    rs.getString("mode"),
                    SQLUtils.toLocale(rs, "locale"));
        }
    };

    @Autowired
    public AccountJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource, objectMapper);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.ACCOUNT)
    public TAccount getByID(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.ACCOUNT,
                params("id", id),
                accountRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TAccount findByNameAndPassword(String name, String password) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.ACCOUNT_AUTHENTICATE,
                    params("user", name).addValue("password", encodePassword(password)),
                    accountRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getRoleByModeAndName(String mode, String name) {
        return getFirstItem(SQL.ACCOUNT_ROLE, params("mode", mode).addValue("user", name), String.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TAccount findByModeAndName(String mode, String name) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.ACCOUNT_BY_NAME,
                    params("user", name).addValue("mode", mode),
                    accountRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TAccount> findAll() {
        return getJdbcTemplate().query(
                SQL.ACCOUNT_LIST,
                accountRowMapper
        );
    }

    @Override
    @Transactional
    public int createAccount(String name, String fullName, String email, String roleName, String mode, String password) {
        try {
            return dbCreate(
                    SQL.ACCOUNT_CREATE,
                    params("name", name)
                            .addValue("fullName", fullName)
                            .addValue("roleName", roleName)
                            .addValue("email", email)
                            .addValue("mode", mode)
                            .addValue("password", encodePassword(password))
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    @CacheEvict(Caches.ACCOUNT)
    public void deleteAccount(int id) {
        getNamedParameterJdbcTemplate().update(
                SQL.ACCOUNT_DELETE,
                params("id", id)
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.ACCOUNT, key = "#id")
    public void updateAccount(int id, String name, String fullName, String email, String roleName) {
        try {
            // Updates the account itself
            getNamedParameterJdbcTemplate().update(
                    SQL.ACCOUNT_UPDATE,
                    params("id", id).addValue("name", name).addValue("fullName", fullName).addValue("email", email).addValue("roleName", roleName)
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    public Ack changePassword(int id, String oldPassword, String newPassword) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.ACCOUNT_CHANGE_PASSWORD,
                        params("id", id)
                                .addValue("oldPassword", encodePassword(oldPassword))
                                .addValue("newPassword", encodePassword(newPassword))
                )
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.ACCOUNT, key = "#id")
    public Ack changeEmail(int id, String password, String email) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.ACCOUNT_CHANGE_EMAIL,
                        params("id", id)
                                .addValue("password", encodePassword(password))
                                .addValue("email", email)
                )
        );
    }

    @Override
    @Transactional
    public Ack resetPassword(int id, String password) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.ACCOUNT_RESET_PASSWORD,
                        params("id", id)
                                .addValue("password", encodePassword(password))
                )
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.ACCOUNT, key = "#id")
    public Ack changeLanguage(int id, Locale lang) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.ACCOUNT_CHANGE_LOCALE,
                        params("id", id)
                                .addValue("locale", lang.toString())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TAccount> getUserAccounts() {
        return getJdbcTemplate().query(
                SQL.ACCOUNT_USER_LIST,
                accountRowMapper
        );
    }

    private String encodePassword(String password) {
        return StringUtils.upperCase(Sha512DigestUtils.shaHex(password));
    }
}
