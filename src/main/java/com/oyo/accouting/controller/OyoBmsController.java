package com.oyo.accouting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sbw on 2017/11/30.
 */
@Slf4j
@Controller("template.controller")
@RequestMapping(value = "syncSap/")
public class OyoBmsController {


    @RequestMapping("/index")
    String index() {

        return "sync_hotel_list";
    }

    @RequestMapping("/rate")
    String fileupload() {

        return "rate";
    }

    @RequestMapping("/toSyncSap")
    String toSyncSap() {

        return "syncCrsAndSAP";
    }

    @RequestMapping("/error")
    String error() {
        return "error";
    }

    @RequestMapping("/munshiList")
    String munshiList() {

        return "munshi_ar_list";
    }

    @RequestMapping("/arApList")
    String arApList() {

        return "crs_ar_ap_list";
    }

    @RequestMapping("/syncHotel")
    String syncHotel() {

        return "sync_hotel_list";
    }

    @RequestMapping("/syncLog")
    String syncLog() {

        return "sync_log_list";
    }

    @RequestMapping("/accountDataList")
    String accountDataList() {

        return "account_data_list";
    }

    @RequestMapping("/testHotelList")
    String testHotelList() {

        return "test_hotel_list";
    }
}
