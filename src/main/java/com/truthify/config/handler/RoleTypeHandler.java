package com.truthify.config.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;
import com.truthify.domain.user.Role;

@Component
public class RoleTypeHandler extends BaseTypeHandler<Role> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Role parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.getKey());
	}

	@Override
	public Role getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return key != null ? Role.ofKey(key) : null;
	}

	@Override
	public Role getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return key != null ? Role.ofKey(key) : null;
	}

	@Override
	public Role getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return key != null ? Role.ofKey(key) : null;
	}
}