package com.es.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.es.common.controller.BaseController;

@Controller
public class LoginMgrController extends BaseController {

	@RequestMapping(value="/index")
	public String index(){
		return "index";
	}
	
}
