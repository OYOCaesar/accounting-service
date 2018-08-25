package com.oyo.accouting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oyo.accouting.bean.CitiesDto;
import com.oyo.accouting.bean.ZonesDto;
import com.oyo.accouting.service.CrsCityService;

@RequestMapping("city")
@Controller
public class CrsCityController {

    @Autowired
    private CrsCityService crsCityService;

    /**
     * @return 获取所有中国城市
     */
    @RequestMapping(value = "getCities")
    @ResponseBody
    public ResponseEntity<Object> queryAllChinaCities() {
    	List<CitiesDto> list = this.crsCityService.queryAllChinaCities();
        return ResponseEntity.ok(list);
    }
    
    /**
     * @return 获取所有中国区域
     */
    @RequestMapping(value = "getZones")
    @ResponseBody
    public ResponseEntity<Object> queryAllChinaZones() {
    	List<ZonesDto> list = this.crsCityService.queryAllChinaZones();
        return ResponseEntity.ok(list);
    }

}
