package com.oyo.accouting.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.DeductionsDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.pojo.Deductions;
import com.oyo.accouting.service.DeductionsService;
import com.oyo.accouting.service.QueryCrsAccountPeriodService;

import net.sf.json.JSONObject;

//查询CRS中账单数据controller
@RequestMapping("queryCrsAccountPeriod")
@Controller
public class QueryCrsAccountPeriodController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private QueryCrsAccountPeriodService queryCrsAccountPeriodService;
    
    @Autowired
    private DeductionsService deductionsService;

    @RequestMapping(value = "query")
    @ResponseBody
    public ResponseEntity<List<AccountPeriodDto>> query(HttpServletRequest request, QueryAccountPeriodDto queryAccountPeriodDto) {
    	List<AccountPeriodDto> list = new ArrayList<AccountPeriodDto>();
    	try {
    		setReuestParams(request, queryAccountPeriodDto);
    		list = queryCrsAccountPeriodService.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
		} catch (Exception e) {
			log.error("Query crs account period throwing exception:{}", e);
		}
    	return ResponseEntity.ok(list);
    }
    
    //商户对账单导出
	@RequestMapping("exportMerchantAccount")
	public void exportMerchantAccount(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		XSSFWorkbook workBook = null;
		InputStream inStream = null;
		// 设置压缩流：直接写入response，实现边压缩边下载
		ZipOutputStream zipOutputStream = null;
		DataOutputStream dataOutputStream = null;
		try {
			
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
    		// 遍历打包下载
    		String zipName = "商户对账" + System.currentTimeMillis() + ".zip";
    		zipName = URLEncoder.encode(zipName,"UTF-8");
    		response.setContentType("APPLICATION/OCTET-STREAM");
    		response.setHeader("Content-Disposition", "attachment; filename=" + zipName);
    		zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			// 设置压缩方式
			zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
			
			ByteArrayOutputStream out = null;
			InputStream inputStream = null;
			String excelFileName = "";//excel文件名
			//查询指定账期的扣除费用列表
			List<DeductionsDto> deductionsList = deductionsService.selectListByAccountPeriod(queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", ""));
			Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
    			List<AccountPeriodDto> eachList = entry.getValue();
    			excelFileName = eachList.get(0).getOyoId() + "-" + entry.getKey() + "-" + queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", "") + "-商户对账单" + ".xlsx";
    			//读取模块文件
    			inStream = this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/merchantAccount.xlsx");
    			workBook = new XSSFWorkbook(inStream);
    			XSSFSheet sheet1 = workBook.getSheet("月账单");
    			XSSFCell oyoIdCell = sheet1.getRow(1).getCell(2);
    			oyoIdCell.setCellValue(eachList.get(0).getOyoId());//oyo id
    			
    			XSSFCell hotelNameCell = sheet1.getRow(2).getCell(2);
    			hotelNameCell.setCellValue(eachList.get(0).getHotelName());// hotel name
    			
    			XSSFCell rateCell = sheet1.getRow(4).getCell(2);
    			rateCell.setCellValue(null != eachList.get(0).getCurrentMonthRatePercent() ? eachList.get(0).getCurrentMonthRatePercent().toString() : "");// rate
    			
    			XSSFCell titleCell = sheet1.getRow(6).getCell(1);
    			titleCell.setCellValue(eachList.get(0).getAccountPeriod().substring(0, 4) + "/" + eachList.get(0).getAccountPeriod().substring(4) + "账单总结");// 2018/07账单总结
    			
    			XSSFCell roomsNightCell = sheet1.getRow(7).getCell(2);
    			roomsNightCell.setCellValue(eachList.stream().filter(q->q.getCurrentMonthRoomsNumber() != null).map(AccountPeriodDto::getCurrentMonthRoomsNumber).reduce(Integer::sum).orElse(0));// 1. 本月双方确认的已售间夜数
    			
    			XSSFCell currentMonthSettlementTotalAmountCell = sheet1.getRow(8).getCell(2);
    			currentMonthSettlementTotalAmountCell.setCellValue(eachList.stream().filter(q->q.getCurrentMonthSettlementTotalAmountCompute() != null).map(AccountPeriodDto::getCurrentMonthSettlementTotalAmountCompute).reduce(BigDecimal.ZERO, BigDecimal::add).toString());// 2. 本月双方确认的营收
    			
    			XSSFCell oyoShareCell = sheet1.getRow(9).getCell(2);
    			oyoShareCell.setCellValue(eachList.stream().filter(q->q.getOyoShare() != null).map(AccountPeriodDto::getOyoShare).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP).toString());// //3. 本月双方确认的OYO的提成
    			
    			XSSFCell ownerPayCell = sheet1.getRow(12).getCell(2);
    			ownerPayCell.setCellValue(eachList.stream().filter(q->q.getOyoShare() != null).map(AccountPeriodDto::getOyoShare).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP).toString());// //6. 本月业主应支付OYO金额
    			
    			if (null != deductionsList && !deductionsList.isEmpty() && 
    					deductionsList.stream().anyMatch(q->q.getHotelId().equals(eachList.get(0).getHotelId()))) {
    				DeductionsDto deductions = deductionsList.stream().filter(q->q.getHotelId().equals(eachList.get(0).getHotelId())).collect(Collectors.toList()).get(0);
    				if (null != deductions) {
    					//设置附表中的扣除费用
            			XSSFSheet sheetAppendix = workBook.getSheet("附表");
            			
            			XSSFCellStyle cellStyle = workBook.createCellStyle();
                        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                        
            			XSSFCell tempMemPromotionFeeCell = sheetAppendix.getRow(3).getCell(2);
            			tempMemPromotionFeeCell.setCellValue(null != deductions.getTempMemPromotionFee() ? deductions.getTempMemPromotionFee().toString() : "");//tempMemPromotionFee
            			//tempMemPromotionFeeCell.setCellStyle(cellStyle);
            			
            			XSSFCell praisePlatformPromotionFeeCell = sheet1.getRow(4).getCell(2);
            			praisePlatformPromotionFeeCell.setCellValue(null != deductions.getPraisePlatformPromotionFee() ? deductions.getPraisePlatformPromotionFee().toString() : "");//praisePlatformPromotionFee
            			//praisePlatformPromotionFeeCell.setCellStyle(cellStyle);
            			
            			XSSFCell flyingPigsPlatformPromotionFeeCell = sheet1.getRow(5).getCell(2);
            			flyingPigsPlatformPromotionFeeCell.setCellValue(null != deductions.getFlyingPigsPlatformPromotionFee() ? deductions.getFlyingPigsPlatformPromotionFee().toString() : "");//flyingPigsPlatformPromotionFee
            			flyingPigsPlatformPromotionFeeCell.setCellStyle(cellStyle);
            			
            			XSSFCell newActivityACell = sheet1.getRow(6).getCell(2);
            			newActivityACell.setCellValue(null != deductions.getNewActivityA() ? deductions.getNewActivityA().toString() : "");//newActivityA
            			//newActivityACell.setCellStyle(cellStyle);
            			
            			XSSFCell newActivityBCell = sheet1.getRow(7).getCell(2);
            			newActivityBCell.setCellValue(null != deductions.getNewActivityB() ? deductions.getNewActivityB().toString() : "");//newActivityBCell
            			//newActivityBCell.setCellStyle(cellStyle);
            			
            			XSSFCell newActivityCCell = sheet1.getRow(8).getCell(2);
            			newActivityCCell.setCellValue(null != deductions.getNewActivityC() ? deductions.getNewActivityC().toString() : "");//newActivityCCell
            			//newActivityCCell.setCellStyle(cellStyle);
    				}
    			}
    			
    			//写CRS明细数据
    			XSSFSheet sheet = workBook.getSheet("CRS明细");
    			try {
    				for (int i = 0; i < entry.getValue().size(); i++) {
    					XSSFRow row = sheet.getRow(2 + i);
    					if (row == null) { 
    						row = sheet.createRow(2 + i);
    						row.setRowStyle(sheet.getRow(2).getRowStyle());
    						for (int j = 0; j <= 22; j++) {
    							row.createCell(j).setCellStyle(sheet.getRow(2).getCell(j).getCellStyle());
							}
    					}
    					//设置数值
    					row.getCell(0).setCellValue(entry.getValue().get(i).getOrderNo());//orderNo
    					row.getCell(1).setCellValue(entry.getValue().get(i).getGuestName());//顾客名字
    					row.getCell(2).setCellValue(entry.getValue().get(i).getBookingGuestName());//预定人名称
    					row.getCell(3).setCellValue(entry.getValue().get(i).getBookingSecondaryGuestName());//预定人名称2
    					row.getCell(4).setCellValue(entry.getValue().get(i).getOrderChannel());//订单来源
    					row.getCell(5).setCellValue(entry.getValue().get(i).getOyoId());//OYO酒店编号
    					row.getCell(6).setCellValue(entry.getValue().get(i).getHotelName());//酒店名称
    					row.getCell(7).setCellValue(null != entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute() ? entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute().toString() : "");//营业收入
    					row.getCell(8).setCellValue(entry.getValue().get(i).getCheckInDate());//入住时间，格式：yyyy-MM-dd,查询显示字段
    					row.getCell(9).setCellValue(entry.getValue().get(i).getCheckOutDate());//离店时间，格式：yyyy-MM-dd,查询显示字段
    					row.getCell(10).setCellValue(entry.getValue().get(i).getCurrentMonthRoomsNumber());//本月已用间夜数
    					row.getCell(11).setCellValue(null != entry.getValue().get(i).getCurrentMonthRatePercent() ? entry.getValue().get(i).getCurrentMonthRatePercent().toString() : "");//费率
    					row.getCell(12).setCellValue(entry.getValue().get(i).getPaymentType());//顾客选择方式
    					row.getCell(13).setCellValue(entry.getValue().get(i).getOtaName());//平台名称
    					row.getCell(14).setCellValue(entry.getValue().get(i).getOtaId());//平台订单号
    					row.getCell(15).setCellValue(entry.getValue().get(i).getCity());//城市
    					row.getCell(16).setCellValue(entry.getValue().get(i).getRegion());//区域
    					row.getCell(17).setCellValue(entry.getValue().get(i).getRevenueCheckResults());//营收核对结果
    					row.getCell(18).setCellValue(entry.getValue().get(i).getReasonsForRevenueDifference());//营收差异原因
    					row.getCell(19).setCellValue(entry.getValue().get(i).getProportions());//提成比例
    					row.getCell(20).setCellValue(entry.getValue().get(i).getPaymentTypeCheckingResult());//顾客选择方式核对结果
    					row.getCell(21).setCellValue(entry.getValue().get(i).getPlatformFeePayableParty());//平台费承担方
    					row.getCell(22).setCellValue(entry.getValue().get(i).getRemarks());//备注
					}
					
					out = new ByteArrayOutputStream();//定义字节数组，为了将excel数据写入
					workBook.write(out);
					byte[] bytes = out.toByteArray();//将excel数据变成byte[]
					inputStream = new ByteArrayInputStream(bytes);//excel stream文件
					
					zipOutputStream.putNextEntry(new ZipEntry(excelFileName));
					dataOutputStream = new DataOutputStream(zipOutputStream);
					IOUtils.copy(inputStream, dataOutputStream);//将excel放入zip文件中
    			} finally {
    				if (null != out) {
    					out.close();
    				}
    				if (null != inputStream) {
    					inputStream.close();
    				}
    				if (null != workBook) {
    					workBook.close();
    				}
    			}
				
    		}
		    
		} catch (Exception e) {
			log.error("Export Merchant Account throwing exception:{}", e);
		} finally {
			try {
				if (null != inStream) {
					inStream.close();
				}
				if (null != dataOutputStream) {
					//flush
					dataOutputStream.flush();
					dataOutputStream.close();
				}
				if (null != zipOutputStream) {
					zipOutputStream.close();
				}
			} catch (Exception e) {
				log.error("Export Merchant Account close generate zip file stream exception：{}：{}", e);
			}
		}
	}
	
	//汇总统计导出
	@RequestMapping("exportSummaryStatistics")
	public void exportSummaryStatistics(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		XSSFWorkbook workBook = null;
		InputStream inStream = null;
		try {
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodStatisticsByCondition(queryAccountPeriodDto);
    		
    		String fileName = "汇总" + System.currentTimeMillis() + ".xlsx";
    		//读取模块文件
			inStream = this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/summaryStatistics.xlsx");
			workBook = new XSSFWorkbook(inStream);
			XSSFSheet sheet = workBook.getSheet("Sheet1");
			
			sheet.shiftRows(1, 1 + list.size(), 1, true, false); // 第1个参数是指要开始插入的行，第2个参数是结尾行数
			for (int i = 0; i < list.size(); i++) {
				XSSFRow creRow = sheet.createRow(1 + i);
				creRow.setRowStyle(sheet.getRow(1).getRowStyle());
				creRow.createCell(0).setCellValue(list.get(i).getOyoId());//oyo id
				creRow.createCell(1).setCellValue(list.get(i).getUniqueCode());//unique Code
				creRow.createCell(2).setCellValue(list.get(i).getHotelName());//酒店名称
				creRow.createCell(3).setCellValue(list.get(i).getAccountPeriod());//账期，如：201807
				creRow.createCell(4).setCellValue(list.get(i).getRoomsNumber());//已用客房数
				creRow.createCell(5).setCellValue(list.get(i).getCurrentMonthRoomsNumber());//本月已用间夜数
				creRow.createCell(6).setCellValue(null != list.get(i).getOrderTotalAmount() ? list.get(i).getOrderTotalAmount().toString() : "");//订单总额
				creRow.createCell(7).setCellValue(null != list.get(i).getCurrentMonthSettlementTotalAmount() ? list.get(i).getCurrentMonthSettlementTotalAmount().toString() : "");//本月应结算总额
				creRow.createCell(8).setCellValue(null != list.get(i).getCurrentMonthOyoShareAmount() ? list.get(i).getCurrentMonthOyoShareAmount().toString() : "");//本月OYO提佣额
				creRow.createCell(9).setCellValue(null != list.get(i).getOwnerGrossShareAmount() ?list.get(i).getOwnerGrossShareAmount().toString() : "");//业主毛份额(A)
				creRow.createCell(10).setCellValue(null != list.get(i).getDisputeOrderAmount() ? list.get(i).getDisputeOrderAmount().toString() : "");//争议订单金额(B)
				creRow.createCell(11).setCellValue(null != list.get(i).getOtaExemptionAmount() ? list.get(i).getOtaExemptionAmount().toString() : "");//OTA豁免额(C)
				creRow.createCell(12).setCellValue(null != list.get(i).getCurrentMonthOwnersNetShareAmount() ? list.get(i).getCurrentMonthOwnersNetShareAmount().toString() : "");//当月业主净份额(A+B+C)
				creRow.createCell(13).setCellValue(null != list.get(i).getCurrentMonthPayAmont() ? list.get(i).getCurrentMonthPayAmont().toString() : "");//当月应付
				creRow.createCell(14).setCellValue(null != list.get(i).getHotelChargeAmount() ? list.get(i).getHotelChargeAmount().toString() : "");//酒店收取金额(已结算)
				creRow.createCell(15).setCellValue(null != list.get(i).getHotelChargeMoreAmount() ? list.get(i).getHotelChargeMoreAmount().toString() : "");//酒店多收取金额
				creRow.createCell(16).setCellValue(null != list.get(i).getOyoChargeAmount() ? list.get(i).getOyoChargeAmount().toString() : "");//OYO收取金额(已结算)
				creRow.createCell(17).setCellValue(null != list.get(i).getOyoChargeMoreAmount() ? list.get(i).getOyoChargeMoreAmount().toString() : "");//OYO多收取金额
				creRow.createCell(18).setCellValue(null != list.get(i).getOtaCommission() ? list.get(i).getOtaCommission().toString() : "");//OTA佣金
				creRow.createCell(19).setCellValue(null != list.get(i).getOtaCommissionTax() ? list.get(i).getOtaCommissionTax().toString() : "");//OYO佣金税额
				creRow.createCell(20).setCellValue(list.get(i).getCity());//City
				creRow.createCell(21).setCellValue(list.get(i).getCityCh());//城市
				creRow.createCell(22).setCellValue(list.get(i).getRegion());//region
				creRow.createCell(23).setCellValue(list.get(i).getHotelId());//Hotels ID
				creRow.createCell(24).setCellValue(null != list.get(i).getCurrentMonthRatePercent() ? list.get(i).getCurrentMonthRatePercent().toString() : "");//本月匹配费率
				creRow.createCell(25).setCellValue(null != list.get(i).getOyoShare() ? list.get(i).getOyoShare().toString() : "");//OYO share
				creRow.createCell(26).setCellValue(list.get(i).getCheckInDays());// 本期入住天数
				creRow.createCell(27).setCellValue(null != list.get(i).getRoomPrice() ? list.get(i).getRoomPrice().toString() : "");// 房间价格
				creRow.createCell(28).setCellValue(null != list.get(i).getCurrentMonthSettlementTotalAmountCompute() ? list.get(i).getCurrentMonthSettlementTotalAmountCompute().toString() : "");// 本月应结算总额（计算）,=房价*天数
			}
			
			// 设置response参数，可以打开下载页面
    		response.setContentType("application/octet-stream");
    		fileName = URLEncoder.encode(fileName,"UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.flushBuffer();
            workBook.write(response.getOutputStream());
		} catch (Exception e) {
			log.error("Export Summary Statistics throwing exception:{}", e);
		} finally {
			if (null != workBook) {
				try {
					workBook.close();
				} catch (IOException e) {
					log.error("workBook close throwing exception:{}", e);
				}
			}
			if (null != inStream) {
				try {
					inStream.close();
				} catch (IOException e) {
					log.error("fis close throwing exception:{}", e);
				}
			}
		}
	}
	
	//明细导出
	@RequestMapping("exportDetails")
	public void exportDetails(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		XSSFWorkbook workBook = null;
		InputStream inStream = null;
		// 设置压缩流：直接写入response，实现边压缩边下载
		ZipOutputStream zipOutputStream = null;
		DataOutputStream dataOutputStream = null;
		try {
			
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
    		// 遍历打包下载
    		String zipName = "明细" + System.currentTimeMillis() + ".zip";
    		zipName = URLEncoder.encode(zipName,"UTF-8");
    		response.setContentType("APPLICATION/OCTET-STREAM");
    		response.setHeader("Content-Disposition", "attachment; filename=" + zipName);
    		zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			// 设置压缩方式
			zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
			
			ByteArrayOutputStream out = null;
			InputStream inputStream = null;
			String excelFileName = "";//excel文件名
    		Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
				excelFileName = entry.getValue().get(0).getOyoId()
						      + "-" + entry.getKey() + "-" + entry.getValue().get(0).getAccountPeriod() + "-明细" + ".xlsx";
				//读取模块文件
				inStream = this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/details.xlsx");
				workBook = new XSSFWorkbook(inStream);
				XSSFSheet sheet = workBook.getSheet("Sheet1");
				
				try {
					for (int i = 0; i < entry.getValue().size(); i++) {
						XSSFRow creRow = sheet.createRow(1 + i);
						creRow.setRowStyle(sheet.getRow(1).getRowStyle());
						creRow.createCell(0).setCellValue(entry.getValue().get(i).getOyoId());//oyo id
						creRow.createCell(1).setCellValue(entry.getValue().get(i).getUniqueCode());//unique Code
						creRow.createCell(2).setCellValue(entry.getValue().get(i).getHotelName());//酒店名称
						creRow.createCell(3).setCellValue(entry.getValue().get(i).getAccountPeriod());//账期，如：201807
						creRow.createCell(4).setCellValue(entry.getValue().get(i).getOrderNo());//订单号
						creRow.createCell(5).setCellValue(entry.getValue().get(i).getGuestName());//客人姓名
						creRow.createCell(6).setCellValue(entry.getValue().get(i).getOrderChannel());//订单渠道
						creRow.createCell(7).setCellValue(entry.getValue().get(i).getChannelName());//渠道名
						creRow.createCell(8).setCellValue(entry.getValue().get(i).getCheckInDate());//入住日期，格式：yyyy-MM-dd,查询显示字段
						creRow.createCell(9).setCellValue(entry.getValue().get(i).getCheckOutDate());//退房日期，格式：yyyy-MM-dd,查询显示字段
						creRow.createCell(10).setCellValue(entry.getValue().get(i).getStatusDesc());//订单状态描述;
						creRow.createCell(11).setCellValue(entry.getValue().get(i).getRoomsNumber());//已用客房数
						creRow.createCell(12).setCellValue(entry.getValue().get(i).getCurrentMonthRoomsNumber());//本月已用间夜数
						creRow.createCell(13).setCellValue(null != entry.getValue().get(i).getOrderTotalAmount() ? entry.getValue().get(i).getOrderTotalAmount().toString() : "");//订单总额
						creRow.createCell(14).setCellValue(null != entry.getValue().get(i).getCurrentMonthSettlementTotalAmount() ? entry.getValue().get(i).getCurrentMonthSettlementTotalAmount().toString() : "");//本月应结算总额
						creRow.createCell(15).setCellValue(null != entry.getValue().get(i).getCurrentMonthOyoShareAmount() ? entry.getValue().get(i).getCurrentMonthOyoShareAmount().toString() : "");//本月OYO提佣额
						creRow.createCell(16).setCellValue(null != entry.getValue().get(i).getOwnerGrossShareAmount() ?entry.getValue().get(i).getOwnerGrossShareAmount().toString() : "");//业主毛份额(A)
						creRow.createCell(17).setCellValue(null != entry.getValue().get(i).getDisputeOrderAmount() ? entry.getValue().get(i).getDisputeOrderAmount().toString() : "");//争议订单金额(B)
						creRow.createCell(18).setCellValue(null != entry.getValue().get(i).getOtaExemptionAmount() ? entry.getValue().get(i).getOtaExemptionAmount().toString() : "");//OTA豁免额(C)
						creRow.createCell(19).setCellValue(null != entry.getValue().get(i).getCurrentMonthOwnersNetShareAmount() ? entry.getValue().get(i).getCurrentMonthOwnersNetShareAmount().toString() : "");//当月业主净份额(A+B+C)
						creRow.createCell(20).setCellValue(null != entry.getValue().get(i).getCurrentMonthPayAmont() ? entry.getValue().get(i).getCurrentMonthPayAmont().toString() : "");//当月应付
						creRow.createCell(21).setCellValue(null != entry.getValue().get(i).getHotelChargeAmount() ? entry.getValue().get(i).getHotelChargeAmount().toString() : "");//酒店收取金额(已结算)
						creRow.createCell(22).setCellValue(null != entry.getValue().get(i).getHotelChargeMoreAmount() ? entry.getValue().get(i).getHotelChargeMoreAmount().toString() : "");//酒店多收取金额
						creRow.createCell(23).setCellValue(null != entry.getValue().get(i).getOyoChargeAmount() ? entry.getValue().get(i).getOyoChargeAmount().toString() : "");//OYO收取金额(已结算)
						creRow.createCell(24).setCellValue(null != entry.getValue().get(i).getOyoChargeMoreAmount() ? entry.getValue().get(i).getOyoChargeMoreAmount().toString() : "");//OYO多收取金额
						creRow.createCell(25).setCellValue(entry.getValue().get(i).getPaymentMethod());//支付方式
						creRow.createCell(26).setCellValue(entry.getValue().get(i).getPaymentDetails());//支付明细
						creRow.createCell(27).setCellValue(entry.getValue().get(i).getPaymentType());//支付类型（预付/后付费）
						creRow.createCell(28).setCellValue(null != entry.getValue().get(i).getOtaCommission() ? entry.getValue().get(i).getOtaCommission().toString() : "");//OTA佣金
						creRow.createCell(29).setCellValue(null != entry.getValue().get(i).getOtaCommissionTax() ? entry.getValue().get(i).getOtaCommissionTax().toString() : "");//OYO佣金税额
						creRow.createCell(30).setCellValue(entry.getValue().get(i).getOtaId());//OTA ID
						creRow.createCell(31).setCellValue(entry.getValue().get(i).getCity());//City
						creRow.createCell(32).setCellValue(entry.getValue().get(i).getCityCh());//城市
						creRow.createCell(33).setCellValue(entry.getValue().get(i).getRegion());//region
						creRow.createCell(34).setCellValue(entry.getValue().get(i).getHotelId());//Hotels ID
						creRow.createCell(35).setCellValue(null != entry.getValue().get(i).getCurrentMonthRatePercent() ? entry.getValue().get(i).getCurrentMonthRatePercent().toString() : "");//本月匹配费率
						creRow.createCell(36).setCellValue(null != entry.getValue().get(i).getOyoShare() ? entry.getValue().get(i).getOyoShare().toString() : "");//OYO share
						creRow.createCell(37).setCellValue(entry.getValue().get(i).getStartDateOfAccountPeriod());//本账期开始日期
						creRow.createCell(38).setCellValue(entry.getValue().get(i).getEndDateOfAccountPeriod());//本账期结束日期
						creRow.createCell(39).setCellValue(entry.getValue().get(i).getCheckInDays());// 本期入住天数
						creRow.createCell(40).setCellValue(null != entry.getValue().get(i).getRoomPrice() ? entry.getValue().get(i).getRoomPrice().toString() : "");// 房间价格
						creRow.createCell(41).setCellValue(null != entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute() ? entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute().toString() : "");// 本月应结算总额（计算）,=房价*天数
					}
					
					out = new ByteArrayOutputStream();//定义字节数组，为了将excel数据写入
					workBook.write(out);
					byte[] bytes = out.toByteArray();//将excel数据变成byte[]
					inputStream = new ByteArrayInputStream(bytes);//excel stream文件
					
					zipOutputStream.putNextEntry(new ZipEntry(excelFileName));
					dataOutputStream = new DataOutputStream(zipOutputStream);
					IOUtils.copy(inputStream, dataOutputStream);//将excel放入zip文件中
				} finally {
					if (null != out) {
						out.close();
					}
					if (null != inputStream) {
						inputStream.close();
					}
					if (null != workBook) {
						workBook.close();
					}
				}
				
    		}
		} catch (Exception e) {
			log.error("Export Details throwing exception:{}", e);
		} finally {
			try {
				if (null != inStream) {
					inStream.close();
				}
				if (null != dataOutputStream) {
					//flush
					dataOutputStream.flush();
					dataOutputStream.close();
				}
				if (null != zipOutputStream) {
					zipOutputStream.close();
				}
			} catch (Exception e) {
				log.error("Export Details close generate zip file stream exception：{}", e);
			}
		} 
	}
	
	//生成recon数据
	@RequestMapping("generateRecon")
	@ResponseBody
	public JSONObject generateRecon(HttpServletRequest request, QueryAccountPeriodDto queryAccountPeriodDto) {
		JSONObject result = new JSONObject();
		try {
			
			queryAccountPeriodDto.setStartYearAndMonthQuery(request.getParameter("startYearAndMonthQuery"));
    		queryAccountPeriodDto.setEndYearAndMonthQuery(request.getParameter("endYearAndMonthQuery"));
    		queryAccountPeriodDto.setCheckInDate(request.getParameter("checkInDate"));
    		queryAccountPeriodDto.setCheckOutDate(request.getParameter("checkOutDate"));
    		queryAccountPeriodDto.setOrderNo(request.getParameter("orderNo"));
    		queryAccountPeriodDto.setRegion(request.getParameter("region"));
    		queryAccountPeriodDto.setCity(request.getParameter("city"));
    		queryAccountPeriodDto.setHotelName(request.getParameter("hotelName"));
    		
			LocalDate localDate = LocalDate.now();
    		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
    		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
    			queryAccountPeriodDto.setStartYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    			queryAccountPeriodDto.setEndYearAndMonthQuery(localDate.getYear() + "-" + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
    		} else {
    			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery());
        		}
        		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
        			queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery());
        		}
    		}
    		String resultStr = queryCrsAccountPeriodService.generateReconData(queryAccountPeriodDto);
    		if (StringUtils.isNotEmpty(resultStr)) {
    			result.put("code", "-1");
    			result.put("msg", resultStr);
    		} else {
    			result.put("code", "0");
    			result.put("msg", "Generate Recon successfully.");
    		}
		} catch (Exception e) {
			result.put("code", "-1");
			result.put("msg", e.getMessage());
			log.error("Generate Recon throwing exception:{}", e);
		}
		return result;
	}
	
	/***
	 * 设置请求参数
	 * @param request 请求
	 * @param queryAccountPeriodDto 查询对象
	 */
	private void setReuestParams(HttpServletRequest request, QueryAccountPeriodDto queryAccountPeriodDto) {
		queryAccountPeriodDto.setStartYearAndMonthQuery(request.getParameter("startYearAndMonthQuery"));
		queryAccountPeriodDto.setEndYearAndMonthQuery(request.getParameter("endYearAndMonthQuery"));
		queryAccountPeriodDto.setCheckInDate(request.getParameter("checkInDate"));
		queryAccountPeriodDto.setCheckOutDate(request.getParameter("checkOutDate"));
		queryAccountPeriodDto.setOrderNo(request.getParameter("orderNo"));
		queryAccountPeriodDto.setRegion(request.getParameter("region"));
		queryAccountPeriodDto.setCity(request.getParameter("city"));
		queryAccountPeriodDto.setHotelName(request.getParameter("hotelName"));
		
		LocalDate localDate = LocalDate.now();
		//如果开始结束账期都为空，那么开始结束账期均为当前月所在的账期
		if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
			queryAccountPeriodDto.setStartYearAndMonthQuery(String.valueOf(localDate.getYear()) + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
			queryAccountPeriodDto.setEndYearAndMonthQuery(String.valueOf(localDate.getYear()) + (localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : localDate.getMonthValue()));
		} else {
			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
				queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery().replaceAll("-", ""));
				queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery().replaceAll("-", ""));
			}
			if (StringUtils.isEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
				queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery().replaceAll("-", ""));
				queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery().replaceAll("-", ""));
			}
			if (StringUtils.isNotEmpty(queryAccountPeriodDto.getStartYearAndMonthQuery()) && StringUtils.isNotEmpty(queryAccountPeriodDto.getEndYearAndMonthQuery())) {
				queryAccountPeriodDto.setStartYearAndMonthQuery(queryAccountPeriodDto.getStartYearAndMonthQuery().replaceAll("-", ""));
				queryAccountPeriodDto.setEndYearAndMonthQuery(queryAccountPeriodDto.getEndYearAndMonthQuery().replaceAll("-", ""));
			}
		}
		
	}

}
