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
import java.util.ArrayList;
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


    /**
     *
     *
     * @return
     */
    @RequestMapping(value = "queryBatchData")
    public ResponseEntity<List<OyoShareDto>> queryBatchData(HttpServletRequest request,OyoShareDto info) {


        List<OyoShareDto> list = this.oyoShareService.queryBatchData(info);
        if(list == null || list.size() == 0 || list.get(0) == null){
            list = new ArrayList<>();
            list.add(new OyoShareDto());
        }
        return ResponseEntity.ok(list);
    }

    @RequestMapping(value = "deleteByIds")
    public ResponseEntity<String> deleteByIds(HttpServletRequest request,String ids) {


        int i = this.oyoShareService.deleteByIds(ids);
        return ResponseEntity.ok(i>0?"成功删除"+i+"条记录！":"删除失败！");
    }

}
