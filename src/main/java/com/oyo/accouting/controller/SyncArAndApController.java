package com.oyo.accouting.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oyo.accouting.service.SyncArAndApToSapService;

@RequestMapping("arAndAp")
@Controller
public class SyncArAndApController {

    @Autowired
    private SyncArAndApToSapService syncArAndApToSapService;

    @RequestMapping(value = "syncArAndAp", method = RequestMethod.GET)
    public void syncArAndAp(HttpServletRequest request) {
    	syncArAndApToSapService.syncArAndApToSap();
    }

}
