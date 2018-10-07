package com.security.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AuthenticationController {

	private static Logger LOG = LogManager.getLogger();
	
    @RequestMapping(value = "**")
    public ResponseEntity<?> register()  {
    	LOG.info("test!!!");
        return ResponseEntity.ok("success");
    }
}
