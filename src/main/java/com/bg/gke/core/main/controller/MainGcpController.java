package com.bg.gke.core.main.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bg.gke.core.main.service.MainGcpService;

@RestController
public class MainGcpController {

	@Autowired
	private MainGcpService mainGcpService;
	
//	@RequestMapping(value="/")
//	public String mainPage() throws Exception{
//		
//		Map<String, Object> data = mainGcpService.selectGcpInfo();
//		
//		return "index";
//	}
	
//	@RequestMapping(value="/")
//	public ModelAndView mainPage() throws Exception{
//		ModelAndView mav = new ModelAndView("index");
//		Map<String, Object> data = mainGcpService.selectGcpInfo();
//		
//		mav.addObject("cluster_list", data.get("cluster"));
//		mav.addObject("node_list", data.get("node"));
//		mav.addObject("db_list", data.get("db"));
//		
//		return mav;
////		return null;
//	}
	
	@GetMapping(value="/")
	public Map<String, Object> mainPage() throws Exception{
		
		Map<String, Object> data = mainGcpService.selectGcpInfo();
		
		System.out.println("data : " + data);
		return data;
	}
	
	@GetMapping(value="/cluster")
	public Map<String, Object> mainClusterPage() throws Exception{
		
		Map<String, Object> data = mainGcpService.selectGcpClusterInfo();
		
		System.out.println("cluster data : " + data);
		return data;
	}
	
	@GetMapping(value="/db")
	public Map<String, Object> mainDbPage() throws Exception{
		
		Map<String, Object> data = mainGcpService.selectGcpDbInfo();
		
		System.out.println("db data : " + data);
		return data;
	}
	
	@RequestMapping(value="/react")
	public String reactTestPage() throws Exception{
		return "Hello, the time at the server is now " + new Date() + "\n";
	}
}
