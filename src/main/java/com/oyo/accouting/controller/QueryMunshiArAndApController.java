package com.oyo.accouting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyo.accouting.bean.PageResult;
import com.oyo.accouting.bean.QueryCrsAccountingDto;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.QueryCrsArAndApService;
import com.oyo.accouting.service.QueryMunshiArAndApService;

import net.sf.json.JSONObject;

//查询Munshi中AR和AP数据controller
@RequestMapping("queryMunshiArAp")
@Controller
public class QueryMunshiArAndApController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private QueryMunshiArAndApService queryMunshiArAndApService;

    @RequestMapping(value = "query")
    @ResponseBody
    public PageResult query(HttpServletRequest request) {
    	PageResult result = new PageResult();
    	try {
    		Map<String,Object> map = new HashMap<String,Object>();
    		int pageNumber = Integer.parseInt(request.getParameter("page")); //获取当前页码
    		int pageSize = Integer.parseInt(request.getParameter("rows")); //获取每页显示多少行
    		//String sortName = request.getParameter("sort"); //排序字段
    		//String sortOrder = request.getParameter("order"); //排序类型，asc(升序),desc(降序)
    		PageHelper.startPage(pageNumber, pageSize);
    		String yearMonth = request.getParameter("yearMonth");
    		String hotelName = request.getParameter("hotelName");
    		map.put("yearMonth", yearMonth);
    		map.put("hotelName", hotelName);
    		List<QueryCrsAccountingDto> list = queryMunshiArAndApService.queryCrsArAndAp(map);
    		result.setRows(list);
			PageInfo<QueryCrsAccountingDto> pageInfo = new PageInfo<>(list);
			result.setTotal(pageInfo.getTotal());
		} catch (Exception e) {
			log.error("Query munshi ar and ap throwing exception:{}", e);
		}
    	return result;
    }

}
