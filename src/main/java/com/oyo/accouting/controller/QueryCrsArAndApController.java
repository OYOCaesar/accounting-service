package com.oyo.accouting.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyo.accouting.bean.QueryCrsAccountingDto;
import com.oyo.accouting.job.SyncArAndApJob;
import com.oyo.accouting.service.QueryCrsArAndApService;

import net.sf.json.JSONObject;

//查询CRS中AR和AP数据controller
@RequestMapping("queryCrsArAp")
@Controller
public class QueryCrsArAndApController {
	private static Logger log = LoggerFactory.getLogger(SyncArAndApJob.class);

    @Autowired
    private QueryCrsArAndApService queryCrsArAndApService;

    @RequestMapping(value = "query")
    @ResponseBody
    public JSONObject query(HttpServletRequest request, QueryCrsAccountingDto queryCrsAccountingDto) {
    	JSONObject result = new JSONObject();
    	try {
    		int pageNumber = Integer.parseInt(request.getParameter("page")); //获取当前页码
    		int pageSize = Integer.parseInt(request.getParameter("rows")); //获取每页显示多少行
    		PageHelper.startPage(pageNumber, pageSize);
    		List<QueryCrsAccountingDto> list = queryCrsArAndApService.queryCrsArAndAp(queryCrsAccountingDto);
			PageInfo<QueryCrsAccountingDto> pageInfo = new PageInfo<>(list);
			JSONObject page = JSONObject.fromObject(pageInfo);
			result.put("total", page.getInt("pages"));
			result.put("rows", page.getJSONArray("list"));
		} catch (Exception e) {
			log.error("Query crs ar and ap throwing exception:{}", e);
		}
    	return result;
    }

}