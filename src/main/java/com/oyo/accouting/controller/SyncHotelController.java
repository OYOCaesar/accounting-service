package com.oyo.accouting.controller;

import com.oyo.accouting.bean.HotelDto;
import com.oyo.accouting.bean.SyncHotel;
import com.oyo.accouting.service.SyncHotelToSapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

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
    @RequestMapping(value = "getSyncHotel", method = RequestMethod.GET)
    public ResponseEntity<String> queryCartList(HttpServletRequest request, HotelDto searchHotel) {


        String res = this.syncHotelToSapService.syncHotelToSap(searchHotel);
        return ResponseEntity.ok(res);
    }

}
