package com.bg.gke.base.abstdao;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;

public class MainDAO {
	protected Log log = LogFactory.getLog(MainDAO.class);
	
	@Resource(name="sqlSessionTemplate")
	private SqlSessionTemplate sqlSession;
	
	public Object insert(String queryId, Object params){
		Object tmpObj = getLocaleMap(params);
		return sqlSession.insert(queryId, tmpObj);
	}
	
	public Object update(String queryId, Object params){
		Object tmpObj = getLocaleMap(params);
		return sqlSession.update(queryId, tmpObj);
	}
	
	public Object delete(String queryId, Object params){
		Object tmpObj = getLocaleMap(params);
		return sqlSession.delete(queryId, tmpObj);
	}
	
	public Object selectOne(String queryId){
	    Object tmpObj = new HashMap<String, Object>();
	    return selectOne(queryId, tmpObj) ;
	}
	
	public Object selectOne(String queryId, Object params){
	    Object tmpObj = getLocaleMap(params); 
	    return sqlSession.selectOne(queryId, tmpObj);
	}
	
	@SuppressWarnings("rawtypes")
    public List selectList(String queryId){
        return selectList(queryId, false);
    }
	
	@SuppressWarnings("rawtypes")
	public List selectList(String queryId, Object params){
		Object tmpObj = getLocaleMap(params); 
		
		return sqlSession.selectList(queryId,tmpObj) ;
	}
	
	@SuppressWarnings("unchecked")
	public Object getLocaleMap(Object obj) {
		Map<String, Object> retObj = new HashMap<String, Object>();
		
		Locale localeTmp = new Locale("ko", "KR");
		
		if ( obj == null ) {
		    retObj.put("locale", localeTmp);
		} else {
			
			if (obj instanceof HashMap) {
			    retObj = (Map<String, Object>) obj; 
			    retObj.put("locale", localeTmp);
			} else { // hashMap Type이 아닌 경우 그대로 return ;
				
				return obj ; 
			}
		}
		
		return (Object) retObj;
	}
	
	
}
