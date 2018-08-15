package com.oyo.accouting.controller;

import com.oyo.accouting.bean.HotelDto;
import com.oyo.accouting.bean.SyncHotel;
import com.oyo.accouting.service.SyncHotelService;
import com.oyo.accouting.service.SyncHotelToSapService;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private SyncHotelService syncHotelService;

    /**
     *
     *
     * @return
     */
    @RequestMapping(value = "syncHotelToSap")
    public ResponseEntity<String> syncHotelToSap(HttpServletRequest request, HotelDto searchHotel) {


        String res = this.syncHotelToSapService.syncHotelToSap(searchHotel);
        return ResponseEntity.ok(res);
    }


    /**
     *
     *
     * @return
     */
    @RequestMapping(value = "querySyncHotelList")
    public ResponseEntity<List<com.oyo.accouting.pojo.SyncHotel>> querySyncHotelList(HttpServletRequest request, com.oyo.accouting.pojo.SyncHotel syncHotel) {


        if(syncHotel!=null && StringUtils.isNotEmpty(syncHotel.getCardname())){
            syncHotel.setCardname("%"+syncHotel.getCardname()+"%");
        }
        List<com.oyo.accouting.pojo.SyncHotel> list = this.syncHotelService.querySyncHotelList(syncHotel);
        return ResponseEntity.ok(list);
    }

}
