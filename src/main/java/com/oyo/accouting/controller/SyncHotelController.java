package com.oyo.accouting.controller;

import com.oyo.accouting.service.SyncHotelToSapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("syncHotel")
@Controller
public class SyncHotelController {

    @Autowired
    private SyncHotelToSapService syncHotelToSapService;

    /**
     *
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List> queryCartList(HttpServletRequest request) {


        List list = this.syncHotelToSapService.syncHotelToSap();
        return ResponseEntity.ok(list);
    }

}
