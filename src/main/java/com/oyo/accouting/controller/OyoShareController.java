package com.oyo.accouting.controller;

import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.service.OyoShareService;
import com.oyo.accouting.service.SyncHotelToSapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("oyoShare")
@Controller
public class OyoShareController {

    @Autowired
    private OyoShareService oyoShareService;

    /**
     *
     *
     * @return
     */
    @RequestMapping(value = "oyoShareList")
    public ResponseEntity<List<OyoShareDto>> queryCartList(HttpServletRequest request,OyoShareDto info) {


        List<OyoShareDto> list = this.oyoShareService.queryOyoShare(info);
        return ResponseEntity.ok(list);
    }

}
