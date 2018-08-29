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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
    			
    			XSSFCell ownerPayCell = sheet1.getRow(12).getCell(2);
    			ownerPayCell.setCellValue(eachList.stream().filter(q->q.getOyoShare() != null).map(AccountPeriodDto::getOyoShare).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP).toString());// //6. 本月业主应支付OYO金额
    			
    			if (null != deductionsList && !deductionsList.isEmpty() && 
    					deductionsList.stream().anyMatch(q->q.getHotelId().equals(eachList.get(0).getHotelId()))) {
    				DeductionsDto deductions = deductionsList.stream().filter(q->q.getHotelId().equals(eachList.get(0).getHotelId())).collect(Collectors.toList()).get(0);
    				if (null != deductions) {
    					//设置附表中的扣除费用
            			XSSFSheet sheetAppendix = workBook.getSheet("附表");
                        
            			XSSFCell tempMemPromotionFeeCell = sheetAppendix.getRow(3).getCell(2);
            			tempMemPromotionFeeCell.setCellValue(null != deductions.getTempMemPromotionFee() ? deductions.getTempMemPromotionFee().doubleValue() : 0.00);//tempMemPromotionFee
            			
            			XSSFCell praisePlatformPromotionFeeCell = sheetAppendix.getRow(4).getCell(2);
            			praisePlatformPromotionFeeCell.setCellValue(null != deductions.getPraisePlatformPromotionFee() ? deductions.getPraisePlatformPromotionFee().doubleValue() : 0.00);//praisePlatformPromotionFee
            			
            			XSSFCell flyingPigsPlatformPromotionFeeCell = sheetAppendix.getRow(5).getCell(2);
            			flyingPigsPlatformPromotionFeeCell.setCellValue(null != deductions.getFlyingPigsPlatformPromotionFee() ? deductions.getFlyingPigsPlatformPromotionFee().doubleValue() : 0.00);//flyingPigsPlatformPromotionFee
            			
            			XSSFCell newActivityACell = sheetAppendix.getRow(6).getCell(2);
            			newActivityACell.setCellValue(null != deductions.getNewActivityA() ? deductions.getNewActivityA().doubleValue() : 0.00);//newActivityA
            			
            			XSSFCell newActivityBCell = sheetAppendix.getRow(7).getCell(2);
            			newActivityBCell.setCellValue(null != deductions.getNewActivityB() ? deductions.getNewActivityB().doubleValue() : 0.00);//newActivityBCell
            			
            			XSSFCell newActivityCCell = sheetAppendix.getRow(8).getCell(2);
            			newActivityCCell.setCellValue(null != deductions.getNewActivityC() ? deductions.getNewActivityC().doubleValue() : 0.00);//newActivityCCell
            			
            			XSSFCell totalCell = sheetAppendix.getRow(9).getCell(2);
            			totalCell.setCellValue(deductions.getTempMemPromotionFee().add(deductions.getPraisePlatformPromotionFee())
            					                                                  .add(deductions.getFlyingPigsPlatformPromotionFee())
            					                                                  .add(deductions.getNewActivityA())
            					                                                  .add(deductions.getNewActivityB())
            					                                                  .add(deductions.getNewActivityC()).doubleValue());//求和
            			
            			//这个是月账单sheet
            			XSSFCell oyoShareCell = sheet1.getRow(11).getCell(2);
            			oyoShareCell.setCellValue(null != deductions.getCurrentMonthReceivedOyoCommission() ? deductions.getCurrentMonthReceivedOyoCommission().doubleValue() : 0.00);// 5. 本月已收取的OYO提成
            			
            			//这个是月账单sheet
            			XSSFCell deductionsCell = sheet1.getRow(10).getCell(2);
            			deductionsCell.setCellValue(totalCell.getNumericCellValue());//4. 本月OYO承担的费用
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
    					row.getCell(7).setCellValue(null != entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute() ? entry.getValue().get(i).getCurrentMonthSettlementTotalAmountCompute().doubleValue() : 0.00);//营业收入
    					row.getCell(8).setCellValue(entry.getValue().get(i).getCheckInDate());//入住时间，格式：yyyy-MM-dd,查询显示字段
    					row.getCell(9).setCellValue(entry.getValue().get(i).getCheckOutDate());//离店时间，格式：yyyy-MM-dd,查询显示字段
    					row.getCell(10).setCellValue(entry.getValue().get(i).getCurrentMonthRoomsNumber().intValue());//本月已用间夜数
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
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
    		String fileName = "汇总" + queryAccountPeriodDto.getStartYearAndMonthQuery() + ".xlsx";
    		//读取模块文件
			inStream = this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/summaryStatistics.xlsx");
			workBook = new XSSFWorkbook(inStream);
			XSSFSheet sheet = workBook.getSheet("Sheet1");
			
			Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
			sheet.shiftRows(1, 1 + hotelGroupMap.size(), 1, true, false); // 第1个参数是指要开始插入的行，第2个参数是结尾行数
			int count = 0;
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
    			Integer uniqueCode = entry.getKey();//uniqueCode
    			List<AccountPeriodDto> eachList = entry.getValue();//每家酒店订单列表
    			
    			//已用客房数
    			Integer roomsNumberSum = eachList.stream().filter(q->q.getRoomsNumber() != null).map(AccountPeriodDto::getRoomsNumber).reduce(Integer::sum).orElse(0);
    			//本月已用间夜数统计
    			Integer currentMonthRoomsNumberSum = eachList.stream().filter(q->q.getCurrentMonthRoomsNumber() != null).map(AccountPeriodDto::getCurrentMonthRoomsNumber).reduce(Integer::sum).orElse(0);
    			//订单总额
    			BigDecimal orderTotalAmountSum = eachList.stream().filter(q->q.getOrderTotalAmount() != null).map(AccountPeriodDto::getOrderTotalAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
    			//本期入住天数统计
    			Integer checkInDaysSum = eachList.stream().filter(q->q.getCheckInDays() != null).map(AccountPeriodDto::getCheckInDays).reduce(Integer::sum).orElse(0);
    			// 本月应结算总额（计算）,=房价*天数
    			BigDecimal currentMonthSettlementTotalAmountComputeSum = eachList.stream().filter(q->q.getCurrentMonthSettlementTotalAmountCompute() != null).map(AccountPeriodDto::getCurrentMonthSettlementTotalAmountCompute).reduce(BigDecimal.ZERO,BigDecimal::add);
    			BigDecimal currentMonthRoomPriceSum = currentMonthSettlementTotalAmountComputeSum.divide(new BigDecimal(checkInDaysSum),2,BigDecimal.ROUND_HALF_UP);
    			//本月OYO提佣额
    			BigDecimal currentMonthOyoShareAmountSum = eachList.stream().filter(q->q.getCurrentMonthOyoShareAmount() != null).map(AccountPeriodDto::getCurrentMonthOyoShareAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
    			//OYO share
    			BigDecimal oyoShareSum = eachList.stream().filter(q->q.getOyoShare() != null).map(AccountPeriodDto::getOyoShare).reduce(BigDecimal.ZERO,BigDecimal::add);
    			
    			XSSFRow creRow = sheet.createRow(1 + count);
    			creRow.setRowStyle(sheet.getRow(2).getRowStyle());
				for (int j = 0; j <= 26; j++) {
					creRow.createCell(j).setCellStyle(sheet.getRow(1).getCell(j).getCellStyle());
				}
    			
				creRow.getCell(0).setCellValue(eachList.get(0).getOyoId());//oyo id
				creRow.getCell(1).setCellValue(uniqueCode);//unique Code
				creRow.getCell(2).setCellValue(eachList.get(0).getHotelName());//酒店名称
				creRow.getCell(3).setCellValue(eachList.get(0).getCity());//City
				creRow.getCell(4).setCellValue(eachList.get(0).getRegion());//region
				creRow.getCell(5).setCellValue(eachList.get(0).getHotelId());//Hotels ID
				creRow.getCell(6).setCellValue(eachList.get(0).getAccountPeriod());//账期，如：201807
				creRow.getCell(7).setCellValue(null != roomsNumberSum ? roomsNumberSum.intValue() : 0);//已用客房数
				creRow.getCell(8).setCellValue(null != currentMonthRoomsNumberSum ? currentMonthRoomsNumberSum.intValue() : 0);//本月已用间夜数
				creRow.getCell(9).setCellValue(null != orderTotalAmountSum ? orderTotalAmountSum.doubleValue() : 0.00);//订单总额
				creRow.getCell(10).setCellValue(null != checkInDaysSum ? checkInDaysSum.intValue() : 0);// 本期入住天数
				creRow.getCell(11).setCellValue(null != currentMonthRoomPriceSum ? currentMonthRoomPriceSum.doubleValue() : 0.00);// 本月房间价格
				creRow.getCell(12).setCellValue(null != currentMonthSettlementTotalAmountComputeSum ? currentMonthSettlementTotalAmountComputeSum.doubleValue() : 0.00);// 本月应结算总额（计算）,=房价*天数
				creRow.getCell(13).setCellValue(null != currentMonthOyoShareAmountSum ? currentMonthOyoShareAmountSum.doubleValue() : 0.00);//本月OYO提佣额
				creRow.getCell(14).setCellValue(null != eachList.get(0).getCurrentMonthRatePercent() ? eachList.get(0).getCurrentMonthRatePercent().toString() : "");//本月匹配费率
				creRow.getCell(15).setCellValue(null != oyoShareSum ? oyoShareSum.doubleValue() : 0.00);//OYO share
				creRow.getCell(16).setCellValue(null != eachList.get(0).getOwnerGrossShareAmount() ?eachList.get(0).getOwnerGrossShareAmount().doubleValue() : 0.00);//业主毛份额(A)
				creRow.getCell(17).setCellValue(null != eachList.get(0).getDisputeOrderAmount() ? eachList.get(0).getDisputeOrderAmount().doubleValue() : 0.00);//争议订单金额(B)
				creRow.getCell(18).setCellValue(null != eachList.get(0).getOtaExemptionAmount() ? eachList.get(0).getOtaExemptionAmount().doubleValue() : 0.00);//OTA豁免额(C)
				creRow.getCell(19).setCellValue(null != eachList.get(0).getCurrentMonthOwnersNetShareAmount() ? eachList.get(0).getCurrentMonthOwnersNetShareAmount().doubleValue() : 0.00);//当月业主净份额(A+B+C)
				creRow.getCell(20).setCellValue(null != eachList.get(0).getCurrentMonthPayAmont() ? eachList.get(0).getCurrentMonthPayAmont().doubleValue() : 0.00);//当月应付
				creRow.getCell(21).setCellValue(null != eachList.get(0).getHotelChargeAmount() ? eachList.get(0).getHotelChargeAmount().doubleValue() : 0.00);//酒店收取金额(已结算)
				creRow.getCell(22).setCellValue(null != eachList.get(0).getHotelChargeMoreAmount() ? eachList.get(0).getHotelChargeMoreAmount().doubleValue() : 0.00);//酒店多收取金额
				creRow.getCell(23).setCellValue(null != eachList.get(0).getOyoChargeAmount() ? eachList.get(0).getOyoChargeAmount().doubleValue() : 0.00);//OYO收取金额(已结算)
				creRow.getCell(24).setCellValue(null != eachList.get(0).getOyoChargeMoreAmount() ? eachList.get(0).getOyoChargeMoreAmount().doubleValue() : 0.00);//OYO多收取金额
				creRow.getCell(25).setCellValue(null != eachList.get(0).getOtaCommission() ? eachList.get(0).getOtaCommission().doubleValue() : 0.00);//OTA佣金
				creRow.getCell(26).setCellValue(null != eachList.get(0).getOtaCommissionTax() ? eachList.get(0).getOtaCommissionTax().doubleValue() : 0.00);//OYO佣金税额
				count ++;
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
		ByteArrayOutputStream out = null;
		InputStream inputStream = null;
		// 设置压缩流：直接写入response，实现边压缩边下载
		ZipOutputStream zipOutputStream = null;
		DataOutputStream dataOutputStream = null;
		try {
			//设置请求参数
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
			
			String excelFileName = "";//excel文件名
			if (null != list && !list.isEmpty()) {
				LocalDate localDate = LocalDate.now();
				LocalTime now = LocalTime.now().withNano(0);
				String exportExcelTime = localDate.toString() + " " + now.toString();
				excelFileName = "明细-" +  exportExcelTime + ".xlsx";
				//读取模块文件
				inStream = this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/details.xlsx");
				workBook = new XSSFWorkbook(inStream);
				XSSFSheet sheet = workBook.getSheet("Sheet1");
				
				AccountPeriodDto accountPeriodDto = null;
				for (int i = 0;i<list.size();i++) {
					accountPeriodDto = list.get(i);
					XSSFRow creRow = sheet.createRow(1 + i);
	    			creRow.setRowStyle(sheet.getRow(1).getRowStyle());
					for (int j = 0; j <= 41; j++) {
						creRow.createCell(j).setCellStyle(sheet.getRow(1).getCell(j).getCellStyle());
					}
					
					creRow.getCell(0).setCellValue(accountPeriodDto.getOyoId());//oyo id
					creRow.getCell(1).setCellValue(accountPeriodDto.getUniqueCode());//unique Code
					creRow.getCell(2).setCellValue(accountPeriodDto.getHotelName());//酒店名称
					creRow.getCell(3).setCellValue(accountPeriodDto.getAccountPeriod());//账期，如：201807
					creRow.getCell(4).setCellValue(accountPeriodDto.getOrderNo());//订单号
					creRow.getCell(5).setCellValue(accountPeriodDto.getGuestName());//客人姓名
					creRow.getCell(6).setCellValue(accountPeriodDto.getOrderChannel());//订单渠道
					creRow.getCell(7).setCellValue(accountPeriodDto.getChannelName());//渠道名
					creRow.getCell(8).setCellValue(accountPeriodDto.getCheckInDate());//入住日期，格式：yyyy-MM-dd,查询显示字段
					creRow.getCell(9).setCellValue(accountPeriodDto.getCheckOutDate());//退房日期，格式：yyyy-MM-dd,查询显示字段
					creRow.getCell(10).setCellValue(accountPeriodDto.getStatusDesc());//订单状态描述;
					creRow.getCell(11).setCellValue(null != accountPeriodDto.getRoomsNumber() ? accountPeriodDto.getRoomsNumber().intValue() : 0);//已用客房数
					creRow.getCell(12).setCellValue(accountPeriodDto.getCurrentMonthRoomsNumber().intValue());//本月已用间夜数
					creRow.getCell(13).setCellValue(null != accountPeriodDto.getOrderTotalAmount() ? accountPeriodDto.getOrderTotalAmount().doubleValue() : 0.00);//订单总额
					creRow.getCell(14).setCellValue(null != accountPeriodDto.getCurrentMonthSettlementTotalAmount() ? accountPeriodDto.getCurrentMonthSettlementTotalAmount().doubleValue() : 0.00);//本月应结算总额
					creRow.getCell(15).setCellValue(null != accountPeriodDto.getCurrentMonthOyoShareAmount() ? accountPeriodDto.getCurrentMonthOyoShareAmount().doubleValue() : 0.00);//本月OYO提佣额
					creRow.getCell(16).setCellValue(null != accountPeriodDto.getOwnerGrossShareAmount() ?accountPeriodDto.getOwnerGrossShareAmount().doubleValue() : 0.00);//业主毛份额(A)
					creRow.getCell(17).setCellValue(null != accountPeriodDto.getDisputeOrderAmount() ? accountPeriodDto.getDisputeOrderAmount().doubleValue() : 0.00);//争议订单金额(B)
					creRow.getCell(18).setCellValue(null != accountPeriodDto.getOtaExemptionAmount() ? accountPeriodDto.getOtaExemptionAmount().doubleValue() : 0.00);//OTA豁免额(C)
					creRow.getCell(19).setCellValue(null != accountPeriodDto.getCurrentMonthOwnersNetShareAmount() ? accountPeriodDto.getCurrentMonthOwnersNetShareAmount().doubleValue() : 0.00);//当月业主净份额(A+B+C)
					creRow.getCell(20).setCellValue(null != accountPeriodDto.getCurrentMonthPayAmont() ? accountPeriodDto.getCurrentMonthPayAmont().doubleValue() : 0.00);//当月应付
					creRow.getCell(21).setCellValue(null != accountPeriodDto.getHotelChargeAmount() ? accountPeriodDto.getHotelChargeAmount().doubleValue() : 0.00);//酒店收取金额(已结算)
					creRow.getCell(22).setCellValue(null != accountPeriodDto.getHotelChargeMoreAmount() ? accountPeriodDto.getHotelChargeMoreAmount().doubleValue() : 0.00);//酒店多收取金额
					creRow.getCell(23).setCellValue(null != accountPeriodDto.getOyoChargeAmount() ? accountPeriodDto.getOyoChargeAmount().doubleValue() : 0.00);//OYO收取金额(已结算)
					creRow.getCell(24).setCellValue(null != accountPeriodDto.getOyoChargeMoreAmount() ? accountPeriodDto.getOyoChargeMoreAmount().doubleValue() : 0.00);//OYO多收取金额
					creRow.getCell(25).setCellValue(accountPeriodDto.getPaymentMethod());//支付方式
					creRow.getCell(26).setCellValue(accountPeriodDto.getPaymentDetails());//支付明细
					creRow.getCell(27).setCellValue(accountPeriodDto.getPaymentType());//支付类型（预付/后付费）
					creRow.getCell(28).setCellValue(null != accountPeriodDto.getOtaCommission() ? accountPeriodDto.getOtaCommission().doubleValue() : 0.00);//OTA佣金
					creRow.getCell(29).setCellValue(null != accountPeriodDto.getOtaCommissionTax() ? accountPeriodDto.getOtaCommissionTax().doubleValue() : 0.00);//OYO佣金税额
					creRow.getCell(30).setCellValue(accountPeriodDto.getOtaId());//OTA ID
					creRow.getCell(31).setCellValue(accountPeriodDto.getCity());//City
					creRow.getCell(32).setCellValue(accountPeriodDto.getCityCh());//城市
					creRow.getCell(33).setCellValue(accountPeriodDto.getRegion());//region
					creRow.getCell(34).setCellValue(accountPeriodDto.getHotelId());//Hotels ID
					creRow.getCell(35).setCellValue(null != accountPeriodDto.getCurrentMonthRatePercent() ? accountPeriodDto.getCurrentMonthRatePercent().toString() : "");//本月匹配费率
					creRow.getCell(36).setCellValue(null != accountPeriodDto.getOyoShare() ? accountPeriodDto.getOyoShare().doubleValue() : 0.00);//OYO share
					creRow.getCell(37).setCellValue(accountPeriodDto.getStartDateOfAccountPeriod());//本账期开始日期
					creRow.getCell(38).setCellValue(accountPeriodDto.getEndDateOfAccountPeriod());//本账期结束日期
					creRow.getCell(39).setCellValue(null != accountPeriodDto.getCheckInDays() ? accountPeriodDto.getCheckInDays().intValue() : 0);// 本期入住天数
					creRow.getCell(40).setCellValue(null != accountPeriodDto.getRoomPrice() ? accountPeriodDto.getRoomPrice().doubleValue() : 0.00);// 房间价格
					creRow.getCell(41).setCellValue(null != accountPeriodDto.getCurrentMonthSettlementTotalAmountCompute() ? accountPeriodDto.getCurrentMonthSettlementTotalAmountCompute().doubleValue() : 0.00);// 本月应结算总额（计算）,=房价*天数*已用房间
				}
				
				out = new ByteArrayOutputStream();//定义字节数组，为了将excel数据写入
				workBook.write(out);
				byte[] bytes = out.toByteArray();//将excel数据变成byte[]
				inputStream = new ByteArrayInputStream(bytes);//excel stream文件
				
				zipOutputStream.putNextEntry(new ZipEntry(excelFileName));
				dataOutputStream = new DataOutputStream(zipOutputStream);
				IOUtils.copy(inputStream, dataOutputStream);//将excel放入zip文件中
				
			}
			
		} catch (Exception e) {
			log.error("Export Details throwing exception:{}", e);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					log.error("Export Details close out throwing exception:{}", e);
				}
			}
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("Export Details close inputStream throwing exception:{}", e);
				}
			}
			if (null != workBook) {
				try {
					workBook.close();
				} catch (IOException e) {
					log.error("Export Details close workBook throwing exception:{}", e);
				}
			}
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
