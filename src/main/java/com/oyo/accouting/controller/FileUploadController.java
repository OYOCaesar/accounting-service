package com.oyo.accouting.controller;

import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.pojo.OyoShare;
import com.oyo.accouting.service.OyoShareService;
import com.oyo.accouting.util.ApnExcelParseTool;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * zfguo
 */
@RequestMapping("fileUpload")
@Controller
public class FileUploadController {

    @Autowired
    private OyoShareService oyoShareService;
	
	@RequestMapping(value = "/")
    public String upload() {

	    return "file_upload";
	}

    @RequestMapping(value = "upload",method={RequestMethod.POST,RequestMethod.GET})
    public ModelAndView fileUpload(HttpServletRequest request, Model mode, @RequestParam("file") MultipartFile file,@RequestParam("isTest")String isTest) throws IOException {

	    ModelAndView view = new ModelAndView("file_upload");

        if(!file.isEmpty()) {

            //上传文件路径
            String path = System.getProperty("user.dir") + "/src/main/resources/upload/";
            //上传文件名
            int index = file.getOriginalFilename().indexOf(".");
            String filename = file.getOriginalFilename().substring(0,index) + new Date().getTime()+file.getOriginalFilename().substring(index);

            File targetFile = new File(path, filename);
            File parentFile = targetFile.getParentFile();
            if(!parentFile.exists()) {
                parentFile.mkdirs();
            }
            //将上传文件保存到一个目标文件当中
            file.transferTo(targetFile);
            ApnExcelParseTool.setFilePath(targetFile.getPath());
            Workbook workbook = ApnExcelParseTool.initWorkBook();
            List<Object> apnModelList = null;
            apnModelList = ApnExcelParseTool.parseWorkbook(workbook,OyoShare.class);
            List<OyoShare> oyoShareList = new ArrayList<>();
            for(Object o : apnModelList){
            	OyoShare oyoShare = (OyoShare) o;
                oyoShare.setIsTest(isTest);
            	if(!StringUtils.isEmpty(oyoShare.getOyoShare())){
            	    double d = Double.valueOf(oyoShare.getOyoShare());
            	    oyoShare.setOyoShare(String.format("%.2f",d));
                }
                int i = oyoShare.getHotelId().indexOf(".");
                oyoShare.setHotelId(oyoShare.getHotelId().substring(0,i<0?oyoShare.getHotelId().length():i));
                int j = oyoShare.getUniqueCode().indexOf(".");
                oyoShare.setUniqueCode(oyoShare.getUniqueCode().substring(0,j<0?oyoShare.getUniqueCode().length():j));
                oyoShareList.add(oyoShare);
            }
           // this.oyoShareService.insertOyoShareList(oyoShareList);
            view.addObject("data","success!");
            mode.addAttribute("data","success!");
        } else {
        	view.addObject("data","error!");
            mode.addAttribute("data","error!");
        }
        
        return view;
    }


    public static void main(String[] args){

    }
}
