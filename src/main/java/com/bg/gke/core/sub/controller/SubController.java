package com.bg.gke.core.sub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SubController {
	
	@RequestMapping(value="/clusterInfo")
	public String subPage() {
		return "clusterInfo";
	}
}
