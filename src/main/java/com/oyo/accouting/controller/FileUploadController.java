package com.oyo.accouting.controller;

import com.oyo.accouting.bean.OyoShareDto;
import com.oyo.accouting.pojo.OyoShare;
import com.oyo.accouting.service.OyoShareService;
import com.oyo.accouting.util.ApnExcelParseTool;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @RequestMapping(value = "upload")
    public String fileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file,String isTest,String validDate) throws IOException {

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
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String batch = sdf.format(new Date());
            for(Object o : apnModelList){
            	OyoShare oyoShare = (OyoShare) o;
                oyoShare.setIsTest(isTest);
                oyoShare.setBatch(batch);
                oyoShare.setZoneName(oyoShare.getZone());
            	if(!StringUtils.isEmpty(oyoShare.getOyoShare())){
            	    double d = Double.valueOf(oyoShare.getOyoShare());
            	    oyoShare.setOyoShare(String.format("%.2f",d));
                }
            	if(!StringUtils.isEmpty(oyoShare.getHotelId())) {
                    int i = oyoShare.getHotelId().indexOf(".");
                    oyoShare.setHotelId(oyoShare.getHotelId().substring(0, i < 0 ? oyoShare.getHotelId().length() : i));
                }
                if(!StringUtils.isEmpty(oyoShare.getUniqueCode())) {
                    int j = oyoShare.getUniqueCode().indexOf(".");
                    oyoShare.setUniqueCode(oyoShare.getUniqueCode().substring(0, j < 0 ? oyoShare.getUniqueCode().length() : j));
                }
                if(!StringUtils.isEmpty(validDate)){
            	    oyoShare.setValidDate(validDate);
                }
                oyoShareList.add(oyoShare);
            }
            try {
                this.oyoShareService.insertOyoShareList(oyoShareList);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "t".equals(isTest)?"redirect:../syncSap/testHotelList":"redirect:../syncSap/rate";
        } else {
            return "redirect:../syncSap/error";
        }
    }


    @RequestMapping(value = "downLoadRateExcel")
    public String downLoadRateExcel(HttpServletRequest request,HttpServletResponse response) throws IOException {

	    String filePath =System.getProperty("user.dir") + "/src/main/resources/excelTemplate/rateTemplate.xlsx";
	    String downFileName =new String("费率表".getBytes("gbk"), "iso8859-1");

	    try {
            OyoShareDto osd = new OyoShareDto();
            osd.setIsTest("f");
            List list = this.oyoShareService.queryOyoShare(osd);
            ApnExcelParseTool.exportExcel(filePath, downFileName, response, list,OyoShareDto.class);
        }catch(Exception e){
	        e.printStackTrace();
        }
        return "";
    }

}
