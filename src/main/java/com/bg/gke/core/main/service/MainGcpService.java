package com.bg.gke.core.main.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface MainGcpService {

	public Map<String, Object> selectGcpInfo() throws Exception;
	
	public Map<String, Object> selectGcpClusterInfo() throws Exception;
	
	public Map<String, Object> selectGcpDbInfo() throws Exception;
}
