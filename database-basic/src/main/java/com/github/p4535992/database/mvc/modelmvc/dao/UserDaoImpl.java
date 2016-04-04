package com.github.p4535992.database.mvc.modelmvc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.p4535992.database.mvc.modelmvc.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class UserDaoImpl implements UserDao {

	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	@Override
	public User findByName(String name) {
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
		String sql = "SELECT * FROM users WHERE name=:name";
		//new BeanPropertyRowMapper(Customer.class));
        return namedParameterJdbcTemplate.queryForObject(
                    sql,params,new UserMapper());
	}

	@Override
	public List<User> findAll() {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "SELECT * FROM users";
		return namedParameterJdbcTemplate.query(sql, params, new UserMapper());
	}

	private static final class UserMapper implements RowMapper<User> {

		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("id"));
			user.setName(rs.getString("name"));
			user.setEmail(rs.getString("email"));
			return user;
		}
	}

}