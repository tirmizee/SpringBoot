package com.tirmizee.backend.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.tirmizee.backend.api.permission.data.PermissionCriteriaDTO;
import com.tirmizee.backend.api.permission.data.PermissionDTO;
import com.tirmizee.core.domain.Permission;
import com.tirmizee.core.jdbcrepository.sql.TempQuery;
import com.tirmizee.core.repository.PermissionRepositoryImpl;

@Repository
public class PermissionDaoImpl extends PermissionRepositoryImpl implements PermissionDao {

	@Autowired
	private Map<String, String> queries;
	
	@Override
	public List<Permission> findByUsername(String username) {
		String statement = queries.get("FIND.PERMISSION.BY.USERNAME");
		return getJdbcOps().query(statement, params(username), ROW_MAPPER);
	}

	@Override
	public List<PermissionDTO> findAllByUsername(Integer roleId) {
		RowMapper<PermissionDTO> mapper = BeanPropertyRowMapper.newInstance(PermissionDTO.class);
		return getJdbcOps().query(TempQuery.FIND_PERMISSION_ROLE.toUpperCase(), params(roleId), mapper);
	}

	@Override
	public Page<Permission> findAllByCriteria(PermissionCriteriaDTO search, Pageable pageable) {
		
		List<Object> params = new LinkedList<>();
		
		StringBuilder statement = new StringBuilder()
			.append(" SELECT * FROM ").append(TB_PERMISSION)
			.append(" WHERE ").append(COL_PER_ID).append(" IS NOT NULL ");
		
		if (search.getPerCode() != null) {
			statement.append(" AND ").append(COL_PER_CODE).append(" LIKE ? ");
			params.add("%" + StringUtils.trimToEmpty(search.getPerCode()) + "%");
		}
		
		if (search.getPerName() != null) {
			statement.append(" AND ").append(COL_PER_NAME).append(" LIKE ? ");
			params.add("%" + StringUtils.trimToEmpty(search.getPerName()) + "%");
		}
		
		long total = count(statement.toString(), params.toArray());
		String statementPage = getSqlGenerator().selectAll(statement, pageable);
		List<Permission> content = getJdbcOps().query(statementPage, params.toArray(), ROW_MAPPER);
		return new PageImpl<>(content, pageable, total);
	}

}