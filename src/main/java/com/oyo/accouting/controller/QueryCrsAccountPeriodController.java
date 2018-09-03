package com.oyo.accouting.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.oyo.accouting.bean.AccountPeriodDto;
import com.oyo.accouting.bean.DeductionsDto;
import com.oyo.accouting.bean.QueryAccountPeriodDto;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.pojo.AccountPeriod;
import com.oyo.accouting.pojo.AccountPeriodTimer;
import com.oyo.accouting.service.AccountPeriodTimerService;
import com.oyo.accouting.service.DeductionsService;
import com.oyo.accouting.service.QueryCrsAccountPeriodService;

import net.sf.json.JSONObject;

//查询CRS中账单数据controller
@RequestMapping("queryCrsAccountPeriod")
@Controller
public class QueryCrsAccountPeriodController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

	//功能名产量
	private static String FUNCTIONNAME_EXPORTSUMMARYSTATISTICS = "exportSummaryStatistics";//汇总统计导出
	private static String FUNCTIONNAME_EXPORTMERCHANTACCOUNT = "exportMerchantAccount";//商户对账单导出
	private static String FUNCTIONNAME_EXPORTDETAILS = "exportDetails";//明细导出
	private static String FUNCTIONNAME_GENERATERECON = "generateRecon";//生成RECON数据
		
    @Autowired
    private QueryCrsAccountPeriodService queryCrsAccountPeriodService;
    
    @Autowired
    private DeductionsService deductionsService;
    
    @Autowired
    private AccountPeriodTimerService accountPeriodTimerService;
    
    //recon和导出定时器
    @RequestMapping(value = "accountPeriodControl")
    @ResponseBody
    public ResponseEntity<List<AccountPeriodTimer>> accountPeriodControl() {
    	List<AccountPeriodTimer> list = new ArrayList<AccountPeriodTimer>();
    	try {
    		List<String> queryList = new ArrayList<String>();
    		queryList.add(FUNCTIONNAME_EXPORTMERCHANTACCOUNT);
    		queryList.add(FUNCTIONNAME_EXPORTSUMMARYSTATISTICS);
    		queryList.add(FUNCTIONNAME_EXPORTDETAILS);
    		queryList.add(FUNCTIONNAME_GENERATERECON);
    		list = accountPeriodTimerService.selectByFunctionNameList(queryList);
		} catch (Exception e) {
			log.error("accountPeriodTimer throwing exception:{}", e);
		}
    	return ResponseEntity.ok(list);
    }
    
    @RequestMapping(value = "query")
    @ResponseBody
    public ResponseEntity<List<AccountPeriodDto>> query(HttpServletRequest request, QueryAccountPeriodDto queryAccountPeriodDto) {
    	List<AccountPeriodDto> list = new ArrayList<AccountPeriodDto>();
    	try {
    		setReuestParams(request, queryAccountPeriodDto);
    		//int pageNumber = Integer.parseInt(request.getParameter("page")); //获取当前页码
    		//int pageSize = Integer.parseInt(request.getParameter("rows")); //获取每页显示多少行
    		//PageHelper.startPage(pageNumber, pageSize);
    		PageHelper.startPage(1, 10000);
    		list = queryCrsAccountPeriodService.queryAccountPeriodByCondition(queryAccountPeriodDto);
    		
		} catch (Exception e) {
			log.error("Query crs account period throwing exception:{}", e);
		}
    	return ResponseEntity.ok(list);
    }
    
    //商户对账单导出
	@RequestMapping("exportMerchantAccount")
	@ResponseBody
	public JSONObject exportMerchantAccount(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		JSONObject result = new JSONObject();
		XSSFWorkbook workBook = null;
		InputStream inStream = null;
		// 设置压缩流：直接写入response，实现边压缩边下载
		ZipOutputStream zipOutputStream = null;
		DataOutputStream dataOutputStream = null;
		OutputStream outputStrem = null;
		AccountPeriodTimer accountPeriodTimerQuery = null;
		try {
			
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
			
			accountPeriodTimerQuery = new AccountPeriodTimer();
			accountPeriodTimerQuery.setFunctionName(FUNCTIONNAME_EXPORTMERCHANTACCOUNT);
			accountPeriodTimerQuery.setCreatetime(queryAccountPeriodDto.getDoTime());
			//先查询当前是否在导出
			List<AccountPeriodTimer> accountPeriodTimerList = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null == accountPeriodTimerList || accountPeriodTimerList.isEmpty()) {
				AccountPeriodTimer accountPeriodTimer = new AccountPeriodTimer();
				accountPeriodTimer.setFunctionName(FUNCTIONNAME_EXPORTMERCHANTACCOUNT);
				accountPeriodTimer.setCreatetime(queryAccountPeriodDto.getDoTime());
				accountPeriodTimer.setStatus(0);//执行中，状态：-1：失败；0：执行中；1：执行完成 
				accountPeriodTimer.setCreatedate(new Date());
				int insertResult = this.accountPeriodTimerService.insertSelective(accountPeriodTimer);
				if (insertResult < 1) {
					result.put("code", "-1");
					result.put("msg", "商户对账单导出失败,请重试!");
					log.info("插入account_period_timer表失败");
					return result;
				}
			} else {
				if (accountPeriodTimerList.get(0).getStatus() == 0) {
					result.put("code", "-1");
					result.put("msg", "正在商户对账单导出,请稍等......");
					return result;
				} else {
					accountPeriodTimerList.get(0).setStatus(0);
					accountPeriodTimerList.get(0).setCreatetime(System.currentTimeMillis());
					accountPeriodTimerList.get(0).setCreatedate(new Date());
					int updateResult = this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerList.get(0));
					if (updateResult < 1) {
						result.put("code", "-1");
						result.put("msg", "商户对账单导出失败,请重试!");
						log.info("更新account_period_timer表失败");
						return result;
					}
				}
			}
			
			long queryStart = System.currentTimeMillis();
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodAllByConditionBatch(queryAccountPeriodDto);
    		long queryEnd = System.currentTimeMillis();
    		log.info("query time:" + (queryEnd - queryStart) / 1000);
    		
    		// 遍历打包下载
    		String zipName = "商户对账-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-" + System.currentTimeMillis() + ".zip";
    		
    		ServletContext servletContext = request.getServletContext();
    		// 获得保存文件的路径
    		String basePath = servletContext.getRealPath("exportExcel") + "/";
    		
    		//先删掉当前年月的商户对账单excel
			delFilesByPath(basePath, "商户对账-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-", ".xlsx");
			
    		//zipName = URLEncoder.encode(zipName,"UTF-8");
    		/*response.setContentType("APPLICATION/OCTET-STREAM");
    		response.setHeader("Content-Disposition", "attachment; filename=" + zipName);
    		zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));*/
    		File file = new File(zipName);
    		if (!file.exists()) {
    			file.createNewFile();
    		}
    		outputStrem = new FileOutputStream(basePath + file);
    		zipOutputStream = new ZipOutputStream(outputStrem);
			// 设置压缩方式
			zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
			
			ByteArrayOutputStream out = null;
			InputStream inputStream = null;
			String excelFileName = "";//excel文件名
			long queryDeductionsStart = System.currentTimeMillis();
			//查询指定账期的扣除费用列表
			List<DeductionsDto> deductionsList = deductionsService.selectListByAccountPeriod(queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", ""));
			long queryDeductionsEnd = System.currentTimeMillis();
    		log.info("query Deductions:" + (queryDeductionsEnd - queryDeductionsStart) / 1000);
    		
    		long insertExcelStart = System.currentTimeMillis();
			Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getUniqueCode));
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
    			List<AccountPeriodDto> eachList = entry.getValue();
    			excelFileName = eachList.get(0).getOyoId() + "-" + entry.getKey() + "-" + queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", "") + "-商户对账单" + ".xlsx";
    			String templateName = "/accountPeriodExcelTemplates/merchantAccount.xlsx";
    			if (StringUtils.isNotEmpty(eachList.get(0).getRegion())) {
    				switch(eachList.get(0).getRegion()) {
		    			case "East China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_EastChina.xlsx";
		    				break;
		    			case "Expansion M1B":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_ExpansionM1B.xlsx";
		    				break;
		    			case "North China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_NorthChina.xlsx";
		    				break;
		    			case "North East China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_NorthEastChina.xlsx";
		    				break;
		    			case "NorthWest China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_NorthWestChina.xlsx";
		    				break;
		    			case "South Central China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_SouthCentralChina.xlsx";
		    				break;
		    			case "South China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_SouthChina.xlsx";
		    				break;
		    			case "SouthWest China":
		    				templateName = "/accountPeriodExcelTemplates/merchantAccount_SouthWestChina.xlsx";
		    				break;
		    			default: 
		    			    templateName = "/accountPeriodExcelTemplates/merchantAccount.xlsx";
	    			}
    			}
    			
    			//读取模块文件
    			inStream = this.getClass().getResourceAsStream(templateName);
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
    			BigDecimal currentMonthSettlementTotalAmount = eachList.stream().filter(q->q.getCurrentMonthSettlementTotalAmountCompute() != null).map(AccountPeriodDto::getCurrentMonthSettlementTotalAmountCompute).reduce(BigDecimal.ZERO, BigDecimal::add);
    			currentMonthSettlementTotalAmountCell.setCellValue(null != currentMonthSettlementTotalAmount ? currentMonthSettlementTotalAmount.doubleValue() : 0.00);// 2. 本月双方确认的营收
    			
    			//3. 本月双方确认的OYO的提成
    			XSSFCell doubleConfirmOyoCell = sheet1.getRow(9).getCell(2);
    			BigDecimal rate = eachList.get(0).getCurrentMonthRate();
    			BigDecimal doubleConfirmOyoValue = new BigDecimal("0.00");
    			if (currentMonthSettlementTotalAmount != null && rate != null && currentMonthSettlementTotalAmount.compareTo(BigDecimal.ZERO) != 0 && rate.compareTo(BigDecimal.ZERO) != 0) {
    				doubleConfirmOyoValue = currentMonthSettlementTotalAmount.multiply(rate).multiply(new BigDecimal("0.01")).setScale(2,BigDecimal.ROUND_HALF_UP);
    			}
    			doubleConfirmOyoCell.setCellValue(null != doubleConfirmOyoValue ? doubleConfirmOyoValue.doubleValue() : 0.00);
    			doubleConfirmOyoCell.setCellFormula(sheet1.getRow(9).getCell(2).getCellFormula());//支持公式
    			
    			XSSFCell ownerPayCell = sheet1.getRow(12).getCell(2);
    			ownerPayCell.setCellValue(sheet1.getRow(9).getCell(2).getNumericCellValue() - sheet1.getRow(10).getCell(2).getNumericCellValue() - sheet1.getRow(11).getCell(2).getNumericCellValue());// //6. 本月业主应支付OYO金额
    			ownerPayCell.setCellFormula(sheet1.getRow(12).getCell(2).getCellFormula());//支持公式
    			
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
            			totalCell.setCellFormula(sheetAppendix.getRow(9).getCell(2).getCellFormula());//支持公式
            			
            			//这个是月账单sheet
            			XSSFCell oyoShareCell = sheet1.getRow(11).getCell(2);
            			oyoShareCell.setCellValue(null != deductions.getCurrentMonthReceivedOyoCommission() ? deductions.getCurrentMonthReceivedOyoCommission().doubleValue() : 0.00);// 5. 本月已收取的OYO提成
            			
            			//这个是月账单sheet
            			XSSFCell deductionsCell = sheet1.getRow(10).getCell(2);
            			deductionsCell.setCellValue(totalCell.getNumericCellValue());//4. 本月OYO承担的费用
            			deductionsCell.setCellFormula(sheet1.getRow(10).getCell(2).getCellFormula());//支持公式
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
    					workBook = null;
    				}
    			}
				
    		}
    		
    		long insertExcelEnd = System.currentTimeMillis();
    		log.info("insert Excel:" + (insertExcelEnd - insertExcelStart) / 1000);
    		
    		result.put("code", 0);
			result.put("msg", zipName);
			
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(1);//执行完成
				accountPeriodTimerListExist.get(0).setExportFileName(zipName);//导出文件名
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
		    
		} catch (Exception e) {
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(-1);//执行失败
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
			result.put("code", -1);
			result.put("msg", "下载失败");
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
				if (null != outputStrem) {
					outputStrem.close();
				}
			} catch (Exception e) {
				log.error("Export Merchant Account close generate zip file stream exception:{}", e);
			}
		}
		return result;
	}
	
	//汇总统计导出
	@RequestMapping("exportSummaryStatistics")
	@ResponseBody
	public JSONObject exportSummaryStatistics(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		JSONObject result = new JSONObject();
		SXSSFWorkbook workBook = null;
		OutputStream out = null;
		AccountPeriodTimer accountPeriodTimerQuery = null;
		try {
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
			
			accountPeriodTimerQuery = new AccountPeriodTimer();
			accountPeriodTimerQuery.setFunctionName(FUNCTIONNAME_EXPORTSUMMARYSTATISTICS);
			accountPeriodTimerQuery.setCreatetime(queryAccountPeriodDto.getDoTime());
			//先查询当前是否在导出
			List<AccountPeriodTimer> accountPeriodTimerList = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null == accountPeriodTimerList || accountPeriodTimerList.isEmpty()) {
				AccountPeriodTimer accountPeriodTimer = new AccountPeriodTimer();
				accountPeriodTimer.setFunctionName(FUNCTIONNAME_EXPORTSUMMARYSTATISTICS);
				accountPeriodTimer.setCreatetime(queryAccountPeriodDto.getDoTime());
				accountPeriodTimer.setStatus(0);//执行中，状态：-1：失败；0：执行中；1：执行完成 
				accountPeriodTimer.setCreatedate(new Date());
				int insertResult = this.accountPeriodTimerService.insertSelective(accountPeriodTimer);
				if (insertResult < 1) {
					result.put("code", "-1");
					result.put("msg", "汇总统计导出失败,请重试!");
					log.info("插入account_period_timer表失败");
					return result;
				}
			} else {
				if (accountPeriodTimerList.get(0).getStatus() == 0) {
					result.put("code", "-1");
					result.put("msg", "正在汇总统计导出,请稍等......");
					return result;
				} else {
					accountPeriodTimerList.get(0).setStatus(0);
					accountPeriodTimerList.get(0).setCreatetime(System.currentTimeMillis());
					accountPeriodTimerList.get(0).setCreatedate(new Date());
					int updateResult = this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerList.get(0));
					if (updateResult < 1) {
						result.put("code", "-1");
						result.put("msg", "汇总统计导出失败,请重试!");
						log.info("更新account_period_timer表失败");
						return result;
					}
				}
			}
			
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodAllByConditionBatch(queryAccountPeriodDto);
    		//查询指定账期的扣除费用列表
			List<DeductionsDto> deductionsList = deductionsService.selectListByAccountPeriod(queryAccountPeriodDto.getStartYearAndMonthQuery().replace("-", ""));
    		
			String fileName = "汇总-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-" + System.currentTimeMillis() + ".xlsx";
			
			ServletContext servletContext = request.getServletContext();
    		// 获得保存文件的路径
    		String basePath = servletContext.getRealPath("exportExcel") + "/";
    		
			//先删掉当前年月的汇总统计excel
			delFilesByPath(basePath, "汇总-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-", ".xlsx");
			
    		//读取模块文件
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/summaryStatistics.xlsx"));
			workBook = new SXSSFWorkbook(xssfWorkbook, 1000);
			Sheet sheet = workBook.getSheet("Sheet1");
			
			// 设置单元格边框
			CellStyle cellStyle = workBook.createCellStyle(); 
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 底部边框
			cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 底部边框颜色
			         
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);  // 左边边框
			cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边边框颜色
			         
			cellStyle.setBorderRight(CellStyle.BORDER_THIN); // 右边边框
			cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());  // 右边边框颜色
			         
			cellStyle.setBorderTop(CellStyle.BORDER_THIN); // 上边边框
			cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());  // 上边边框颜色
			
			Map<Integer,List<AccountPeriodDto>> hotelGroupMap = list.stream().collect(Collectors.groupingBy(AccountPeriodDto::getHotelId));
			int count = 0;
    		for (Map.Entry<Integer, List<AccountPeriodDto>> entry : hotelGroupMap.entrySet()) {
    			//超过1000000条数据，将产生一个新的sheet存在数据
				if (count > 1000000 && (count + 1) % 1000000 == 1) {
					sheet = workBook.createSheet("sheet" + (count / 1000000 + 1));
				}
    			Integer hotelId = entry.getKey();//hotelId
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
    			
    			//扣除费用相关列计算
    			BigDecimal tempMemPromotionFee = new BigDecimal("0.00");
    			BigDecimal praisePlatformPromotion = new BigDecimal("0.00");
    			BigDecimal flyingPigsPlatformPromotion = new BigDecimal("0.00");
    			BigDecimal newActivityA = new BigDecimal("0.00");
    			BigDecimal newActivityB = new BigDecimal("0.00");
    			BigDecimal newActivityC = new BigDecimal("0.00");
    			BigDecimal currentMonthReceivedOyoCommission = new BigDecimal("0.00");
    			
    			if (null != deductionsList && !deductionsList.isEmpty() && 
    					deductionsList.stream().anyMatch(q->q.getHotelId().equals(eachList.get(0).getHotelId()))) {
    				DeductionsDto deductions = deductionsList.stream().filter(q->q.getHotelId().equals(hotelId)).collect(Collectors.toList()).get(0);
    				if (null != deductions) {
    					tempMemPromotionFee = deductions.getTempMemPromotionFee();
    					praisePlatformPromotion = deductions.getPraisePlatformPromotionFee();
    					flyingPigsPlatformPromotion = deductions.getFlyingPigsPlatformPromotionFee();
    					newActivityA = deductions.getNewActivityA();
    					newActivityB = deductions.getNewActivityB();
    					newActivityC = deductions.getNewActivityC();
    					currentMonthReceivedOyoCommission = deductions.getCurrentMonthReceivedOyoCommission();
    					
    				}
    			}
    			
    			Row creRow = sheet.createRow(1 + count);
				for (int j = 0; j <= 33; j++) {
					creRow.createCell(j).setCellStyle(cellStyle);//给单元格设置边框
				}
    			
				creRow.getCell(0).setCellValue(eachList.get(0).getOyoId());//oyo id
				creRow.getCell(1).setCellValue(eachList.get(0).getUniqueCode());//unique Code
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
				//后加的列
				creRow.getCell(27).setCellValue(tempMemPromotionFee != null ? tempMemPromotionFee.doubleValue() : 0.00);//临时会员卡促销费用
				creRow.getCell(28).setCellValue(praisePlatformPromotion != null ? praisePlatformPromotion.doubleValue() : 0.00);//有赞平台促销费用
				creRow.getCell(29).setCellValue(flyingPigsPlatformPromotion != null ? flyingPigsPlatformPromotion.doubleValue() : 0.00);//飞猪平台促销费用
				creRow.getCell(30).setCellValue(newActivityA != null ? newActivityA.doubleValue() : 0.00);//新活动A
				creRow.getCell(31).setCellValue(newActivityB != null ? newActivityB.doubleValue() : 0.00);//新活动B
				creRow.getCell(32).setCellValue(newActivityC != null ? newActivityC.doubleValue() : 0.00);//新活动C
				creRow.getCell(33).setCellValue(currentMonthReceivedOyoCommission != null ? currentMonthReceivedOyoCommission.doubleValue() : 0.00);//本月已收取的OYO提成
				count ++;
    		}
			
    		File file = new File(basePath + fileName);
    		if (!file.exists()) {
    			file.createNewFile();
    		}
    		out = new FileOutputStream(file);
    		workBook.write(out);
    		result.put("code", 0);
			result.put("msg", fileName);
			
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(1);//执行完成
				accountPeriodTimerListExist.get(0).setExportFileName(fileName);//导出文件名
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
		} catch (Exception e) {
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(-1);//执行失败
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
			result.put("code", -1);
			result.put("msg", "下载失败");
			log.error("Export Summary Statistics throwing exception:{}", e);
		} finally {
			if (null != workBook) {
				workBook.dispose();
			}
		}
		return result;
	}
	
	//明细导出
	@RequestMapping("exportDetails")
	@ResponseBody
	public JSONObject exportDetails(HttpServletRequest request, HttpServletResponse response, QueryAccountPeriodDto queryAccountPeriodDto) {
		JSONObject result = new JSONObject();
		SXSSFWorkbook workBook = null;
		OutputStream out = null;
		AccountPeriodTimer accountPeriodTimerQuery = null;
		try {
			//设置请求参数
			setReuestParams(request, queryAccountPeriodDto);
			queryAccountPeriodDto.setPageSize(null);
			
			accountPeriodTimerQuery = new AccountPeriodTimer();
			accountPeriodTimerQuery.setFunctionName(FUNCTIONNAME_EXPORTDETAILS);
			accountPeriodTimerQuery.setCreatetime(queryAccountPeriodDto.getDoTime());
			//先查询当前是否在导出
			List<AccountPeriodTimer> accountPeriodTimerList = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null == accountPeriodTimerList || accountPeriodTimerList.isEmpty()) {
				AccountPeriodTimer accountPeriodTimer = new AccountPeriodTimer();
				accountPeriodTimer.setFunctionName(FUNCTIONNAME_EXPORTDETAILS);
				accountPeriodTimer.setCreatetime(queryAccountPeriodDto.getDoTime());
				accountPeriodTimer.setStatus(0);//执行中，状态：-1：失败；0：执行中；1：执行完成 
				accountPeriodTimer.setCreatedate(new Date());
				int insertResult = this.accountPeriodTimerService.insertSelective(accountPeriodTimer);
				if (insertResult < 1) {
					result.put("code", "-1");
					result.put("msg", "明细导出失败,请重试!");
					log.info("插入account_period_timer表失败");
					return result;
				}
			} else {
				if (accountPeriodTimerList.get(0).getStatus() == 0) {
					result.put("code", "-1");
					result.put("msg", "正在明细导出,请稍等......");
					return result;
				} else {
					accountPeriodTimerList.get(0).setStatus(0);
					accountPeriodTimerList.get(0).setCreatetime(System.currentTimeMillis());
					accountPeriodTimerList.get(0).setCreatedate(new Date());
					int updateResult = this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerList.get(0));
					if (updateResult < 1) {
						result.put("code", "-1");
						result.put("msg", "明细导出失败,请重试!");
						log.info("更新account_period_timer表失败");
						return result;
					}
				}
			}
			
    		List<AccountPeriodDto> list = queryCrsAccountPeriodService.queryAccountPeriodAllByConditionBatch(queryAccountPeriodDto);
			
			String fileName = "明细-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-" + System.currentTimeMillis() + ".xlsx";
			
			ServletContext servletContext = request.getServletContext();
    		// 获得保存文件的路径
    		String basePath = servletContext.getRealPath("exportExcel") + "/";
    		
			//先删掉当前年月的明细excel
			delFilesByPath(basePath, "明细-" + queryAccountPeriodDto.getStartYearAndMonthQuery() + "-", ".xlsx");
			
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(this.getClass().getResourceAsStream("/accountPeriodExcelTemplates/details.xlsx"));
	        workBook = new SXSSFWorkbook(xssfWorkbook, 1000);
	        Sheet sheet = workBook.getSheetAt(0);
	        
	        // 设置单元格边框
 			CellStyle cellStyle = workBook.createCellStyle(); 
 			cellStyle.setBorderBottom(CellStyle.BORDER_THIN); // 底部边框
 			cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 底部边框颜色
 			         
 			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);  // 左边边框
 			cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边边框颜色
 			         
 			cellStyle.setBorderRight(CellStyle.BORDER_THIN); // 右边边框
 			cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());  // 右边边框颜色
 			         
 			cellStyle.setBorderTop(CellStyle.BORDER_THIN); // 上边边框
 			cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());  // 上边边框颜色
			
			if (null != list && !list.isEmpty()) {
				AccountPeriodDto accountPeriodDto = null;
				for (int i = 0;i<list.size();i++) {
					//超过1000000条数据，将产生一个新的sheet存在数据
					if (i > 1000000 && (i + 1) % 1000000 == 1) {
						sheet = workBook.createSheet("sheet" + (i / 1000000 + 1));
					}
					accountPeriodDto = list.get(i);
					Row creRow = sheet.createRow(1 + i);
					for (int j = 0; j <= 41; j++) {
						creRow.createCell(j).setCellStyle(cellStyle);//给单元格设置边框
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
				
			}
			
			File file = new File(basePath + fileName);
    		if (!file.exists()) {
    			file.createNewFile();
    		}
    		out = new FileOutputStream(file);
    		workBook.write(out);
    		result.put("code", 0);
			result.put("msg", fileName);
			
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(1);//执行完成
				accountPeriodTimerListExist.get(0).setExportFileName(fileName);//导出文件名
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
			
		} catch (Exception e) {
			//查询recon的记录
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(-1);//执行失败
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
			result.put("code", -1);
			result.put("msg", "下载失败");
			log.error("Export Details throwing exception:{}", e);
		} finally {
			if (null != workBook) {
				workBook.dispose();
			}
		} 
		return result;
	}
	
	//文件下载相关代码
	@RequestMapping("/downloadExcel")
	public String downloadExcel(HttpServletRequest request, HttpServletResponse response) {
		ServletContext servletContext = request.getServletContext();
		// 获得保存文件的路径
		String basePath = servletContext.getRealPath("exportExcel") + "/";
		
		String fileName = basePath + request.getParameter("fileName");
	    if (StringUtils.isNotEmpty(fileName)) {
            byte[] buffer = new byte[2048];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                response.setContentType("application/force-download");// 设置强制下载不打开
                String excelFileName = request.getParameter("fileName");
                excelFileName = URLEncoder.encode(excelFileName,"UTF-8");
                response.addHeader("Content-Disposition", "attachment;fileName=" + excelFileName);// 设置文件名
                
            	File file = new File(fileName);
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                
                //删除服务器上的文件
                file.delete();
                
                //删除掉该文件在表account_period_timer中的记录
                AccountPeriodTimer accountPeriodTimer = new AccountPeriodTimer();
                String deleteFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                accountPeriodTimer.setExportFileName(deleteFileName);
                List<AccountPeriodTimer> list = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimer);
                if (null != list && !list.isEmpty()) {
                	list.forEach(q->{
                		this.accountPeriodTimerService.deleteByPrimaryKey(q.getId());
                	});
                }
                
            } catch (Exception e) {
                log.error("Download Excel throw exception:{}", e);
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    	log.error("Download Excel close bis throw exception:{}", e);
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    	log.error("Download Excel close fis throw exception:{}", e);
                    }
                }
            }
	    }
	    return null;
	}
	
	// 用以模糊删除头部为startStr,以endStr结尾的文件
	private static boolean delFilesByPath(String path, String startStr, String endStr) {
		boolean b = false;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		File[] tempFile = file.listFiles();
		if (null != tempFile && tempFile.length > 0) {
			for (int i = 0; i < tempFile.length; i++) {
				long createTime = Long.valueOf(tempFile[i].getName().substring(tempFile[i].getName().lastIndexOf("-") + 1, tempFile[i].getName().lastIndexOf(".")));
				if (tempFile[i].getName().startsWith(startStr) && tempFile[i].getName().endsWith(endStr) 
						&& (System.currentTimeMillis() - createTime >= 1000*3600*2)) {//删除2小时之前创建的文件(包括2小时)
					boolean del = deleteFile(path + tempFile[i].getName());
					if (del) {
						log.info("文件" + tempFile[i].getName() + "删除成功");
						b = true;
					} else {
						log.info("文件" + tempFile[i].getName() + "删除失败");
					}
				}
			}
		}
		return b;
	}

	//删除文件
	private static boolean deleteFile(String path) {
		boolean del = false;
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
			del = true;
		}
		return del;
	}
	
	//生成RECON数据
	@RequestMapping("generateRecon")
	@ResponseBody
	public JSONObject generateRecon(HttpServletRequest request, QueryAccountPeriodDto queryAccountPeriodDto) {
		JSONObject result = new JSONObject();
		AccountPeriodTimer accountPeriodTimerQuery = null;
		try {
			accountPeriodTimerQuery = new AccountPeriodTimer();
			accountPeriodTimerQuery.setFunctionName(FUNCTIONNAME_GENERATERECON);
			//先查询当前是否在导出
			List<AccountPeriodTimer> accountPeriodTimerList = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null == accountPeriodTimerList || accountPeriodTimerList.isEmpty()) {
				AccountPeriodTimer accountPeriodTimer = new AccountPeriodTimer();
				accountPeriodTimer.setFunctionName(FUNCTIONNAME_GENERATERECON);
				accountPeriodTimer.setCreatetime(System.currentTimeMillis());
				accountPeriodTimer.setStatus(0);//执行中，状态：-1：失败；0：执行中；1：执行完成 
				accountPeriodTimer.setCreatedate(new Date());
				int insertResult = this.accountPeriodTimerService.insertSelective(accountPeriodTimer);
				if (insertResult < 1) {
					result.put("code", "-1");
					result.put("msg", "生成RECON数据失败,请重试!");
					log.info("插入account_period_timer表失败");
					return result;
				}
			} else {
				if (accountPeriodTimerList.get(0).getStatus() == 0) {
					result.put("code", "-1");
					result.put("msg", "正在生成RECON数据,请稍等......");
					return result;
				} else {
					accountPeriodTimerList.get(0).setStatus(0);
					accountPeriodTimerList.get(0).setCreatetime(System.currentTimeMillis());
					accountPeriodTimerList.get(0).setCreatedate(new Date());
					int updateResult = this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerList.get(0));
					if (updateResult < 1) {
						result.put("code", "-1");
						result.put("msg", "生成RECON数据失败,请重试!");
						log.info("更新account_period_timer表失败");
						return result;
					}
				}
			}
			
			queryAccountPeriodDto.setStartYearAndMonthQuery(request.getParameter("startYearAndMonthQuery"));
    		queryAccountPeriodDto.setEndYearAndMonthQuery(request.getParameter("endYearAndMonthQuery"));
    		/*queryAccountPeriodDto.setCheckInDate(request.getParameter("checkInDate"));
    		queryAccountPeriodDto.setCheckOutDate(request.getParameter("checkOutDate"));
    		queryAccountPeriodDto.setOrderNo(request.getParameter("orderNo"));
    		queryAccountPeriodDto.setRegion(request.getParameter("region"));
    		queryAccountPeriodDto.setCity(request.getParameter("city"));
    		queryAccountPeriodDto.setHotelName(request.getParameter("hotelName"));*/
    		
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
    		queryAccountPeriodDto.setAccountPeriod(queryAccountPeriodDto.getStartYearAndMonthQuery().replaceAll("-", ""));//账期,如：201808
    		
    		//首先:删除指定账期的对账数据
    		int deleteResult = queryCrsAccountPeriodService.deleteAccountPeriodByYearMonth(queryAccountPeriodDto);
    		if (deleteResult < 1) {
    			result.put("code", "-1");
    			result.put("msg", "删除该账期数据失败!");
    			return result;
    		}
    		
    		//其次:从CRS中查询中指定账期的对账数据列表
    		List<AccountPeriod> resultList = queryCrsAccountPeriodService.queryAccountPeriodFromCrs(queryAccountPeriodDto);
    		if (null == resultList || resultList.isEmpty()) {
    			result.put("code", "-1");
    			result.put("msg", "从CRS获取该账期数据失败!");
    			return result;
    		}
    		log.info("本次'" + queryAccountPeriodDto.getAccountPeriod() + "'共需同步：" + resultList.size() + "条数据");
    		//最后:遍历上述对账数据列表,分批插入到对账表accout_period中
    		List<AccountPeriod> failedInsertList = new ArrayList<AccountPeriod>();
    		int times = 3;//错误最多执行三次
    		//调用分批插入方法
    		batchInsertionList(resultList, failedInsertList);
    		
    		//如果失败列表为空，那么数据全部插入成功
    		if (failedInsertList == null || failedInsertList.isEmpty()) {
    			result.put("code", "0");
    			result.put("msg", "Generate Recon successfully.");
    			
    			//查询recon的记录
    			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
				if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
					accountPeriodTimerListExist.get(0).setStatus(1);//执行完成
					this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
				}
    		} else {
    			//循环执行
        		while(times > 0) {
        			resultList = new ArrayList<AccountPeriod>();
        			resultList = failedInsertList;
        			failedInsertList = new ArrayList<AccountPeriod>();//重置错误列表
        			//调用分批插入方法
        			batchInsertionList(resultList, failedInsertList);
            		times--;
        		}
    		}
    		
    		if (failedInsertList != null && !failedInsertList.isEmpty()) {
    			String orderNos = "[";
    			List<String> orderNoList = failedInsertList.stream().map(AccountPeriod::getOrderNo).collect(Collectors.toList());
    			for (int i = 0; i < orderNoList.size(); i++) {
					if (i != orderNoList.size() - 1) {
						orderNos += orderNoList.get(i) + ",";
					} else {
						orderNos += orderNoList.get(i);
					}
				}
    			orderNos += "]";

    			result.put("code", "-1");
    			result.put("msg", "Generate Recon failed,orderNo list is:" + orderNos);
    			
				//查询recon的记录,更新监控表信息
    			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
				if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
					accountPeriodTimerListExist.get(0).setStatus(-1);//执行失败
					this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
				}
    			
    		}
    		
		} catch (Exception e) {
			//查询recon的记录,更新监控表信息
			List<AccountPeriodTimer> accountPeriodTimerListExist = this.accountPeriodTimerService.selectByAccountPeriodTimer(accountPeriodTimerQuery);
			if (null != accountPeriodTimerListExist && !accountPeriodTimerListExist.isEmpty()) {
				accountPeriodTimerListExist.get(0).setStatus(-1);//执行失败
				this.accountPeriodTimerService.updateByPrimaryKeySelective(accountPeriodTimerListExist.get(0));
			}
			result.put("code", "-1");
			result.put("msg", e.getMessage());
			log.error("Generate Recon throwing exception:{}", e);
		}
		return result;
	}

	/***
	 * 分批插入
	 * @param resultList 要插入的列表
	 * @param failedInsertList 失败列表
	 * @throws Exception
	 */
	private void batchInsertionList(List<AccountPeriod> resultList, List<AccountPeriod> failedInsertList)
			throws Exception {
		//每1000条批量插入一次
		int len = (resultList.size() % 1000 == 0 ? resultList.size() / 1000 : ((resultList.size() / 1000) + 1));
		for (int i = 0; i < len; i++) {
			int startIndex = 0;
			int endIndex = 0;
			if (len <= 1) {
				endIndex = resultList.size();
			} else {
				startIndex = i * 1000;
				if (i == len - 1) {
					endIndex = resultList.size();
				} else {
					endIndex = (i + 1) * 1000;
				}
			}
			//批量插入所选账期数据
			List<AccountPeriod> insertReslutList = this.queryCrsAccountPeriodService.batchInsertAccountPeriod(resultList.subList(startIndex, endIndex));
			if (null != insertReslutList) {//如果插入失败，将失败的列表放入failedInsertList，以便后面继续做插入操作。
				failedInsertList.addAll(insertReslutList);
		    }
		}
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
		queryAccountPeriodDto.setDoTime(StringUtils.isNotEmpty(request.getParameter("doTime")) ? Long.valueOf(request.getParameter("doTime")) : null);
		
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
