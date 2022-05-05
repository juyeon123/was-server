package com.bg.gke.core.main.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.bg.gke.base.abstdao.MainDAO;

@Repository("mainGcpDAO")
public class MainGcpDAO extends MainDAO{
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selSqlList() throws Exception {
		return (List<Map<String, Object>>)selectList("cloudsql.sqlList");
	}
}
