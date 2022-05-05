package com.bg.gke.core.main.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bg.gke.core.main.dao.MainGcpDAO;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.cloud.container.v1.ClusterManagerSettings;
import com.google.common.collect.Lists;
import com.google.container.v1.Cluster;
import com.google.container.v1.ListClustersResponse;
import com.google.container.v1.ListNodePoolsResponse;
import com.google.container.v1.NodePool;

@Service("mainGcpService")
public class MainGcpServiceImpl implements MainGcpService{

	private Logger log = LoggerFactory.getLogger(MainGcpServiceImpl.class);
	
	@Autowired
	private MainGcpDAO mainGcpDAO;
	
	@Override
	public Map<String, Object> selectGcpInfo() throws Exception {
		List<Map<String, Object>> cluster_list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> node_list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> db_list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> ret_map = new HashMap<String, Object>();
		Map<String, Object> tmp_map = null;
		
		ClusterManagerSettings clusterManagerSettings;
		ClusterManagerClient clusterService = null;
		Credentials credentials;
		
		try {
			// gcp vm에서 실행할 경우 default setting으로 실행해도된다. (자격증명 키파일 따로 등록할 필요없음)
//			clusterService = ClusterManagerClient.create();
			File json = getCredentialsJson();
			
			credentials = GoogleCredentials.fromStream(new FileInputStream(json.getPath())).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			
			clusterManagerSettings = ClusterManagerSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
			
		    clusterService = ClusterManagerClient.create(clusterManagerSettings);
			
		    ListClustersResponse res = clusterService.listClusters("juyeon-pjt-347108", "asia-northeast3-a");
		    
		    if(res != null) {
		    	for(Cluster cluster : res.getClustersList()) {
		    		tmp_map = new HashMap<String, Object>();
		    		
		    		tmp_map.put("name", cluster.getName());
		    		tmp_map.put("vpc", cluster.getNetwork());
		    		tmp_map.put("subnet", cluster.getSubnetwork());
		    		tmp_map.put("zone", cluster.getLocation());
		    		tmp_map.put("version", cluster.getCurrentMasterVersion());
		    		tmp_map.put("curr_node_count", cluster.getCurrentNodeCount());
		    		tmp_map.put("cluster_cidr", cluster.getClusterIpv4Cidr());
		    		tmp_map.put("endpoint", cluster.getEndpoint());
		    		tmp_map.put("status", cluster.getStatus());
		    		
		    		cluster_list.add(tmp_map);
		    		
		    		tmp_map = new HashMap<String, Object>();
		    		
		    		ListNodePoolsResponse node_res = clusterService.listNodePools("juyeon-pjt-347108", "asia-northeast3-a", cluster.getName());
		    		
		    		for(NodePool node : node_res.getNodePoolsList()) {
		    			tmp_map.put("name", cluster.getName());
		    			tmp_map.put("node_name", node.getName());
		    			tmp_map.put("node_init", node.getInitialNodeCount());
		    			tmp_map.put("node_status", node.getStatus());
		    			tmp_map.put("machine_type", node.getConfig().getMachineType());
		    			tmp_map.put("disk_size_gb", node.getConfig().getDiskSizeGb());
		    			tmp_map.put("node_pod_size", node.getPodIpv4CidrSize());
		    			tmp_map.put("node_version", node.getVersion());
		    			tmp_map.put("node_max", node.getMaxPodsConstraint().getMaxPodsPerNode());
		    			
		    			node_list.add(tmp_map);
		    		}
		    		
		    	}
		    }
		    
		    // 임시 주석
//		    db_list = mainGcpDAO.selSqlList();
		    
		    log.info("GCP ClusterManagerClient interface has been initialized.");
		    
		    ret_map.put("cluster", cluster_list);
		    ret_map.put("node", node_list);
		    ret_map.put("db", db_list);
		    
		    if(json.exists()) {
		    	if(json.delete()) {
			    	log.info("delete key file.");
			    }else {
			    	log.info("delete key file Failed..!");
			    }
		    }
		} catch (Exception e) {
		  log.error("Unable to initialize GCP ClusterManagerClient interface, Kubernetes cluster data will not be available.", e);
		}
		
		return ret_map;
	}

	@SuppressWarnings("unchecked")
	private File getCredentialsJson() {
		JSONObject json_obj = new JSONObject();
		
		File json_key = null;
		FileWriter fw = null;
		
		String json_str = "";
		
		try {
			json_key = new File("juyeon-pjt-8e54f59ed91f.json");
			 
			 if(json_key.createNewFile()) {
				 log.error("create file success.");
				 
				 json_obj.put("type", "service_account");
				 json_obj.put("project_id", "juyeon-pjt-347108");
				 json_obj.put("private_key_id", "4faa3f49b26250d288a400c81673ab83a77a4497");
				 json_obj.put("private_key", "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCb3g/f7jdklKV6\n465q82wOrO9PXWqf/FmlbPJefyEG0ZYMKEyr+aAHcc6lH4WpLD1lvuaBitcHMZdD\nitjWC+ou+sDq1hjrCXoqynMty8eD6ddATbDxAheVgLbDQKsisrs76BXB8wRqDRHd\n+tWWCePTc429VLtBtvZouAZ4fIWVpdac91R7eoEBKrhuZTDwkYVlYfE/f0+lIlfA\nOVO6wC7WLsTgeUZOuxoEP1Qd5bgweXcZTJMLUcRPkf2fMNmkCL5TElnfB17dlxlB\nzGRXHTn/VtesJ7YeSk4i7L4RTYLp8sLb5bH2VNKbueIR1F9CSlcp1iWaidEu/JlR\nrlbEOmT7AgMBAAECggEAA6F+RHXqoWqIvkwsLr6dCJEOAcXIGP/ThVh8bJWHr6MM\nXK5H7c3r5MLJkL5sc3ELmDzyQhfVFgxQLz2sH4/Cxv6rE1sSsD06ffOwWQgcjZ2/\nqtG7WGDAdJq9VN7nj/BuDyFnrqGRO4rpZf65V7Mw2YJYFtdVYROUFSewRS8+iap8\nWGRofkQ5tD5S7eSiDKeINooVrz10lTMax/yvYzP2KGGLXWUYXcE01BEtcXtK4dyb\nHctJIlHtafYa3NktdxubyHxa1Wm7R1PEcoYqTIsMt0FZMLADtzFWWz2ALVtdeNLO\nlM2o14GuLfDYv0QQWIoaTOCX6JPmHieCgRFst/V0YQKBgQDQG4LNtVBTEknhKt5n\nZT92pvjfNBrQPdQbPTI0GITxTGQEnohMmN6PTDZxLKctLIqnLH8gEmJZ0S5LteT9\nlPdXx4y+164obgT2Pl0XoANnO98AEoJA7ZFpV7i0MA86xZl3F5qkC021TlIyMnNn\nOn5IiSN42B6gjvA6eDYj4X2/oQKBgQC/vN7Aj7B3eDjiV7wGpG+nH9v1kR/Y2ear\nDQdlaSisn0UlaF9g1wDwswbrn5zLbf9knw8+KzgqWQD31UlpVhJRMmV81foydhpc\ngWvtezaxsJeqB+cl3O3c7GGwBdokCI/GUP25y2oy8BH3gSVQIh7sBwR+o/SpVvya\n7b9uH4TPGwKBgAqK2j2BiegrbQKzIUErp+Ni4gisow8Zkr0uhHSPKWzv6cEyJsV6\nmQu5WKizEmT/dKazYl1FkSXrquS6+ja+bSlNOpwQYQo7SJUQ47ZmrMV2Cv398gtH\n/a3Qe9Lk/GCF0hhuYa8dw3oD1bYlSCNamzshd2KP0D1iAFl17YgzzCMBAoGBAKLQ\nnArCOWRJt2wkeirdyV4ORLbzRCrdZe3MQ4KO2JY1LP9F8PAHwumh7Hd68ycfweZM\n1yHAi5ISQCszd4I2L5m5hMqL3UiqV1pEIETqAxjCTxd4XIUfZPDb3VwerA07I/OQ\nZNQ3cnfMukUE6o1wW/erQdrUGPcPQbPBvXOi2iEPAoGAWPFEPC7j5YuC4HTw8O0H\ncp/IuSHc8ViXpIfqzK+VutK+YCHyYuBWs0Og1S6cFNoaU1mYnqXFGATTvHaHpeK5\ns0iaIGEarx7vdWeALPrjF8gsrjeRLetYFeNvRnt3iGe0VuOMbKyWSvKwnJ5uavJU\nlAmhjij6vR/TawfYpXS36Jc=\n-----END PRIVATE KEY-----\n");
				 json_obj.put("client_email", "sdk-juyeon@juyeon-pjt-347108.iam.gserviceaccount.com");
				 json_obj.put("client_id", "110055118878309991403");
				 json_obj.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
				 json_obj.put("token_uri", "https://oauth2.googleapis.com/token");
				 json_obj.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
				 json_obj.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/sdk-juyeon%40juyeon-pjt-347108.iam.gserviceaccount.com");
				 
				 json_str = json_obj.toJSONString();
				 
				 log.error("=== Write JSON Key File. ===");
				 fw = new FileWriter(json_key); 
				 fw.write(json_str);
			 }else {
				 log.error("The file already exists.");
			 }
		}catch(Exception e) {
			log.error("getCredentialsJson Error : ", e);
		}finally {
			if(fw != null) {
				try {
					fw.flush();
					fw.close();
				} catch (IOException e) {
					log.error("IOException Error : ", e);
				}
			}
		}
		
		return json_key;
	}

	@Override
	public Map<String, Object> selectGcpClusterInfo() throws Exception {
		List<Map<String, Object>> cluster_list = new ArrayList<Map<String,Object>>();
		Map<String, Object> ret_map = new HashMap<String, Object>();
		Map<String, Object> tmp_map = null;
		
		ClusterManagerSettings clusterManagerSettings;
		ClusterManagerClient clusterService = null;
		Credentials credentials;
		
		try {
			// gcp vm에서 실행할 경우 default setting으로 실행해도된다. (자격증명 키파일 따로 등록할 필요없음)
//			clusterService = ClusterManagerClient.create();
			File json = getCredentialsJson();
			
			credentials = GoogleCredentials.fromStream(new FileInputStream(json.getPath())).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			
			clusterManagerSettings = ClusterManagerSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
			
		    clusterService = ClusterManagerClient.create(clusterManagerSettings);
			
		    ListClustersResponse res = clusterService.listClusters("juyeon-pjt-347108", "asia-northeast3-a");
		    
		    if(res != null) {
		    	for(Cluster cluster : res.getClustersList()) {
		    		tmp_map = new HashMap<String, Object>();
		    		
		    		tmp_map.put("name", cluster.getName());
		    		tmp_map.put("vpc", cluster.getNetwork());
		    		tmp_map.put("subnet", cluster.getSubnetwork());
		    		tmp_map.put("zone", cluster.getLocation());
		    		tmp_map.put("version", cluster.getCurrentMasterVersion());
		    		tmp_map.put("curr_node_count", cluster.getCurrentNodeCount());
		    		tmp_map.put("cluster_cidr", cluster.getClusterIpv4Cidr());
		    		tmp_map.put("endpoint", cluster.getEndpoint());
		    		tmp_map.put("status", cluster.getStatus());
		    		
		    		cluster_list.add(tmp_map);
		    	}
		    }
		    
		    // 임시 주석
//		    db_list = mainGcpDAO.selSqlList();
		    
		    log.info("GCP ClusterManagerClient interface has been initialized.");
		    
		    ret_map.put("cluster", cluster_list);
		    
		    if(json.exists()) {
		    	if(json.delete()) {
			    	log.info("delete key file.");
			    }else {
			    	log.info("delete key file Failed..!");
			    }
		    }
		} catch (Exception e) {
		  log.error("Unable to initialize GCP ClusterManagerClient interface, Kubernetes cluster data will not be available.", e);
		}
		
		return ret_map;
	}

	@Override
	public Map<String, Object> selectGcpDbInfo() throws Exception {
		List<Map<String, Object>> db_list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> ret_map = new HashMap<String, Object>();
		
		try {
		    // 임시 주석
//		    db_list = mainGcpDAO.selSqlList();
		    
		    ret_map.put("db", db_list);
		    
		} catch (Exception e) {
		  log.error("Unable to initialize GCP ClusterManagerClient interface, Kubernetes cluster data will not be available.", e);
		}
		
		return ret_map;
	}
	
}
