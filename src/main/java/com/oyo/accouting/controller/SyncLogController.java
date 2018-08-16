package com.oyo.accouting.controller;

import com.oyo.accouting.bean.SyncLogDto;
import com.oyo.accouting.pojo.SyncLog;
import com.oyo.accouting.service.SyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("syncLog")
@Controller
public class SyncLogController {

    @Autowired
    private SyncLogService syncLogService;

    @RequestMapping(value = "querySyncLoglist")
    public ResponseEntity<List> querySyncLog(HttpServletRequest request, SyncLogDto syncLog) {


        List list = this.syncLogService.querySyncLog(syncLog);
        return ResponseEntity.ok(list);
    }

    @RequestMapping(value = "addSyncLog")
    public ResponseEntity<Integer> addSyncLog(HttpServletRequest request, SyncLog syncLog) {


        Integer count = this.syncLogService.insertSyncLog(syncLog);
        return ResponseEntity.ok(count);
    }
}
