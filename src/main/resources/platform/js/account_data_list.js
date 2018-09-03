/***
 * 对账js
 */
//定义页面的参数变量
var startYearAndMonthQuery = "",endYearAndMonthQuery="",checkInDate="",checkOutDate="",orderNo="",region="",city="",hotelName="";
var myTimer = null;//定时器
var summaryTime = null;//汇总统计执行时间戳
var merchantAccountTime = null;//商户对账执行时间戳
var detailTime = null;//明细执行时间戳
var summaryStatus = 0;//汇总统计导出状态
var merchantStatus = 0;//商户对账执导出状态
var datailsStatus = 0;//明细导出状态
var reconStatus = 0;//生成recon状态
//== Class definition
var Datatable_expRemoteAjaxDemo = function () {

  var datatable_exp = "";
  //== Private functions
  var demo_exp = function () {

    var url = '/queryCrsAccountPeriod/query?startYearAndMonthQuery=' + startYearAndMonthQuery+"&endYearAndMonthQuery="+endYearAndMonthQuery
             +"&checkInDate="+checkInDate+"&checkOutDate="+checkOutDate+"&orderNo="+orderNo+"&region="+region
             +"&city="+city+"&hotelName="+hotelName+"&doTime=0";

    datatable_exp = $('.m_datatable_exception').mDatatable({
      // datasource definition
      data: {
        type: 'remote',
        source: {
          read: {
            // sample GET method
            method: 'GET',//
            url: url,
            map: function (raw) {
              return raw;
            },
          },
        },
        pageSize: 10,
        serverPaging: true,
        serverFiltering: true,
        serverSorting: true,
      },

      // layout definition
      layout: {
        scroll: false,
        footer: false
      },

      // column sorting
      sortable: true,

      pagination: true,

      toolbar: {
        // toolbar items
        items: {
          // pagination
          pagination: {
            // page size select
            pageSizeSelect: [10, 20, 30, 50, 100],
          },
        },
      },

        // 表列定义
        columns:[
            {field:'accountPeriod',title:'账期'},
            {field:'hotelName',title:'酒店名称'},
            {field:'orderNo',title:'订单号'},
            {field:'guestName',title:'客人姓名'},
            {field:'orderChannel',title:'订单渠道'},
            {field:'channelName',title:'渠道名'},
            {field:'checkInDate',title:'入住日期'},
            {field:'checkOutDate',title:'退房日期'},
            {field:'startDateOfAccountPeriod',title:'本期开始日期'},
            {field:'endDateOfAccountPeriod',title:'本期结束日期'},
            {field:'checkInDays',title:'本期入住天数'},
            {field:'roomPrice',title:'房间价格'},
            {field:'currentMonthSettlementTotalAmountCompute',title:'本月应结算总额（计算）'},
            {field:'statusDesc',title:'订单状态'},
            {field:'roomsNumber',title:'已用客房数'},
            {field:'currentMonthRoomsNumber',title:'本月已用间夜数'},
            {field:'orderTotalAmount',title:'订单总额'},
            {field:'currentMonthSettlementTotalAmount',title:'本月应结算总额'},
            {field:'paymentMethod',title:'支付方式'},
            {field:'paymentDetails',title:'支付明细'},
            {field:'paymentType',title:'支付类型（预付/后付费）'},
            {field:'otaId',title:'OTA ID'},
            {field:'city',title:'City'},
            {field:'region',title:'Region'},
            {field:'hotelId',title:'Hotels ID'},
            {field:'currentMonthRatePercent',title:'本月匹配费率'},
            {field:'oyoShare',title:'OYO share'}

        ],
    });

  };
  
  //设置参数值
  function setParamValues() {
	  startYearAndMonthQuery = $("#startYearAndMonthQuery").val();
      endYearAndMonthQuery = $("#endYearAndMonthQuery").val();
      checkInDate = $("#m_datepicker_1").val();
      checkOutDate = $("#m_datepicker_2").val();
      orderNo = $("#orderNo").val();
      region = $("#region").val();
      city = $("#city").val();
      hotelName = $("#hotelName").val();
  }
 
  //查询
  $("#m_search_btn").on("click", function (t) {
      t.preventDefault();
      datatable_exp.destroy();
      
      setParamValues();
      
      if (!startYearAndMonthQuery) {
		  alert("请选择开始账期！");
		  return;
	  }
	  if (!endYearAndMonthQuery) {
		  alert("请选择结束账期！");
		  return;
	  }
      
      demo_exp();
  });
  
  //汇总下载
  $("#m_summary_statistics_btn").on("click", function (t) {
	  t.preventDefault();

	  setParamValues();
	  
	  if (!myTimer) {
		  myTimer = setInterval("myInterval()",1000*60);//单位毫秒，60秒
	  }
	  
	  if (!startYearAndMonthQuery) {
		  alert("请选择开始账期！");
		  return;
	  }
	  if (!endYearAndMonthQuery) {
		  alert("请选择结束账期！");
		  return;
	  }
	  
	  if (startYearAndMonthQuery != endYearAndMonthQuery) {
		  alert("请选择相同账期！");
		  return;
	  }
	  
	  summaryStatus = 1;//汇总统计导出状态
	  
	  summaryTime = new Date().getTime();
	  
	  $.ajax({
		    url:'/queryCrsAccountPeriod/exportSummaryStatistics',
		    type:'POST', //GET
		    async:true,  //或false,是否异步
		    data:{
		    	startYearAndMonthQuery:$("#syncCrs").val(),
		        endYearAndMonthQuery:$("#endYearAndMonthQuery").val(),
		        checkInDate:$("#m_datepicker_1").val(),
		        checkOutDate:$("#m_datepicker_2").val(),
		        orderNo:$("#orderNo").val(),
		        region:$("#region").val(),
		        city:$("#city").val(),
		        hotelName:$("#hotelName").val(),
		        doTime:summaryTime
		    },
		    timeout:3600000,    //超时时间,单位毫秒，1个小时
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		        $("#m_summary_statistics_btn").html("执行中...");
		        $("#m_summary_statistics_btn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		        //alert(textStatus);
		    },
		    complete:function(data) {
		    	if (data && data.responseJSON && data.responseJSON.code) {
		    		if (data.responseJSON.code == 0) {
		    			//location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data.responseJSON.msg;
		    		} else if (data.responseJSON.code == -1) {
		    			//alert(data.responseJSON.msg);
		    		} else {
		    			//alert("汇总下载失败!");
		    		}
		    	} else {
		    		//alert("汇总下载失败!");
		    	}
		    	//$("#m_summary_statistics_btn").attr("disabled",false);
		    	//$("#m_summary_statistics_btn").html("汇总下载");
		    }
	  });

  });
  
  //商户对账单下载
  $("#m_merchant_account_download_btn").on("click", function (t) {
	  t.preventDefault();

	  if (!myTimer) {
		  myTimer = setInterval("myInterval()",1000*60);//单位毫秒，60秒
	  }
	  
	  setParamValues();
	  
	  if (!startYearAndMonthQuery) {
		  alert("请选择开始账期！");
		  return;
	  }
	  if (!endYearAndMonthQuery) {
		  alert("请选择结束账期！");
		  return;
	  }
	  
	  if (startYearAndMonthQuery != endYearAndMonthQuery) {
		  alert("请选择相同账期！");
		  return;
	  }
	  
	  merchantStatus = 1;//商户对账执导出状态
	  
	  merchantAccountTime = new Date().getTime();
	  
	  $.ajax({
		    url:'/queryCrsAccountPeriod/exportMerchantAccount',
		    type:'POST', //GET
		    async:true,  //或false,是否异步
		    data:{
		    	startYearAndMonthQuery:$("#syncCrs").val(),
		        endYearAndMonthQuery:$("#endYearAndMonthQuery").val(),
		        checkInDate:$("#m_datepicker_1").val(),
		        checkOutDate:$("#m_datepicker_2").val(),
		        orderNo:$("#orderNo").val(),
		        region:$("#region").val(),
		        city:$("#city").val(),
		        hotelName:$("#hotelName").val(),
		        doTime:merchantAccountTime
		    },
		    timeout:3600000,    //超时时间,单位毫秒，1个小时
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		        $("#m_merchant_account_download_btn").html("执行中...");
		        $("#m_merchant_account_download_btn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		        //alert(textStatus);
		    },
		    complete:function(data) {
		    	if (data && data.responseJSON && data.responseJSON.code) {
		    		if (data.responseJSON.code == 0) {
		    			//location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data.responseJSON.msg;
		    		} else if (data.responseJSON.code == -1) {
		    			//alert(data.responseJSON.msg);
		    		} else {
		    			//alert("商户对账单下载失败!");
		    		}
		    	} else {
		    		//alert("商户对账单下载失败!");
		    	}
		    	//$("#m_merchant_account_download_btn").attr("disabled",false);
		    	//$("#m_merchant_account_download_btn").html("商户对账单下载");
		    }
	  });

  });
  
  //明细下载
  $("#m_detail_download_btn").on("click", function (t) {
	  t.preventDefault();
	
	  if (!myTimer) {
		  myTimer = setInterval("myInterval()",1000*60);//单位毫秒，60秒
	  }
	  
	  setParamValues();
	  
	  if (!startYearAndMonthQuery) {
		  alert("请选择开始账期！");
		  return;
	  }
	  if (!endYearAndMonthQuery) {
		  alert("请选择结束账期！");
		  return;
	  }
	  
	  if (startYearAndMonthQuery != endYearAndMonthQuery) {
		  alert("请选择相同账期！");
		  return;
	  }
	  
	  datailsStatus = 1;//明细导出状态
	  
	  detailTime = new Date().getTime();
	  
	  $.ajax({
		    url:'/queryCrsAccountPeriod/exportDetails',
		    type:'POST', //GET
		    async:true,  //或false,是否异步
		    data:{
		    	startYearAndMonthQuery:$("#syncCrs").val(),
		        endYearAndMonthQuery:$("#endYearAndMonthQuery").val(),
		        checkInDate:$("#m_datepicker_1").val(),
		        checkOutDate:$("#m_datepicker_2").val(),
		        orderNo:$("#orderNo").val(),
		        region:$("#region").val(),
		        city:$("#city").val(),
		        hotelName:$("#hotelName").val(),
		        doTime:detailTime
		    },
		    timeout:3600000,    //超时时间,单位毫秒，1个小时
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		        $("#m_detail_download_btn").html("执行中...");
		        $("#m_detail_download_btn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		        //alert(textStatus);
		    },
		    complete:function(data) {
		    	if (data && data.responseJSON && data.responseJSON.code) {
		    		if (data.responseJSON.code == 0) {
		    			//location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data.responseJSON.msg;
		    		} else if (data.responseJSON.code == -1) {
		    			//alert(data.responseJSON.msg);
		    		} else {
		    			//alert("明细下载失败!");
		    		}
		    	} else {
		    		//alert("明细下载失败!");
		    	}
		    }
	  });
		
  });
  
  //生成recon数据
  $("#m_generate_recon_btn").on("click", function (t) {
      t.preventDefault();
      
      if (!myTimer) {
		  myTimer = setInterval("myInterval()",1000*60);//单位毫秒，60秒
	  }
      
      setParamValues();
      
      if (!startYearAndMonthQuery) {
		  alert("请选择开始账期！");
		  return;
	  }
	  if (!endYearAndMonthQuery) {
		  alert("请选择结束账期！");
		  return;
	  }
	  
	  if (startYearAndMonthQuery != endYearAndMonthQuery) {
		  alert("请选择相同账期！");
		  return;
	  }
	  
	  reconStatus = 1;//生成recon状态
      
      $.ajax({
		    url:'/queryCrsAccountPeriod/generateRecon',
		    type:'POST', //GET
		    async:true,  //或false,是否异步
		    data:{
		    	startYearAndMonthQuery:$("#syncCrs").val(),
		        endYearAndMonthQuery:$("#endYearAndMonthQuery").val(),
		        checkInDate:$("#m_datepicker_1").val(),
		        checkOutDate:$("#m_datepicker_2").val(),
		        orderNo:$("#orderNo").val(),
		        region:$("#region").val(),
		        city:$("#city").val(),
		        hotelName:$("#hotelName").val()
		    },
		    timeout:3600000,    //超时时间,单位毫秒，1个小时
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		        $("#m_generate_recon_btn").html("执行中...");
		        $("#m_generate_recon_btn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	/*if (data) {
		    		alert(data.msg);
		    	} else {
		    		alert("Generate recon failed!");
		    	}*/
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		    },
		    complete:function(data) {
		    	if (data && data.responseJSON) {
		    		if (data.responseJSON.code) {
		    			//alert(data.responseJSON.msg);
		    			//$("#m_generate_recon_btn").attr("disabled",false);
				    	//$("#m_generate_recon_btn").html("生成recon数据");
		    		} else {
		    			console.log("生成recon数据失败，后台抛异常了，没有返回code!");
		    		}
		    	} else {
		    		console.log("生成recon数据失败，后台抛异常了!");
		    	}
		    }
		});
      
  });
  
  return {
    // public functions
    init: function () {
      demo_exp();
    }
  };
}();

//填充城市下拉框
function fillCitiesSelect() {
    $.ajax({
			type : "POST",
			dataType : "json",
			url : "/city/getCities",
			data : "",
			success : function(data) {
				if (data) {
					$('#city').attr("length", '0');
					for (i = 0; i < data.length; i++) {
						$("#city").append($('<option value=' + data[i].name + '>' + data[i].name + '</option>'));
					}
				}
			}
    });   
}

//填充区域下拉框
function fillZonesSelect() {
    $.ajax({
			type : "POST",
			dataType : "json",
			url : "/city/getZones",
			data : "",
			success : function(data) {
				if (data) {
					$('#region').attr("length", '0');
					for (i = 0; i < data.length; i++) {
						$("#region").append($('<option value="' + data[i].name + '">' + data[i].name + '</option>'));
					}
				}
			}
    });   
}

//补零函数
function prefixInteger(num, n) {
   return (Array(n).join(0) + num).slice(-n);
}

//定时器
function myInterval() {
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "/queryCrsAccountPeriod/accountPeriodTimer",
		data : "",
		success : function(data) {
			if (data && data.length > 0) {
				for (i = 0; i < data.length; i++) {
					if (data[i].functionName == "exportSummaryStatistics") { 
						if ((data[i].status == -1 || data[i].status == 1) && summaryStatus == 1 && summaryTime && data[i].createtime == summaryTime) {//执行失败或成功，给出提示，并点亮按钮
							if (data[i].status == 1) {
								location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data[i].exportFileName;
							} else {
								alert("Export Summary Statistics failed!");
							}
							$("#m_summary_statistics_btn").attr("disabled",false);
					    	$("#m_summary_statistics_btn").html("汇总下载");
					    	summaryStatus = 0;//汇总统计导出状态
						};
					}
					if (data[i].functionName == "exportMerchantAccount") {
						if ((data[i].status == -1 || data[i].status == 1) && merchantStatus == 1 && merchantAccountTime && data[i].createtime == merchantAccountTime) {//执行失败或成功，给出提示，并点亮按钮
							if (data[i].status == 1) {
								location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data[i].exportFileName;
							} else {
								alert("Export Merchant Account failed!");
							}
							$("#m_merchant_account_download_btn").attr("disabled",false);
					    	$("#m_merchant_account_download_btn").html("商户对账单下载");
					    	merchantStatus = 0;//商户对账执导出状态
						};
					}
					if (data[i].functionName == "exportDetails") {
						if ((data[i].status == -1 || data[i].status == 1) && datailsStatus == 1 && detailTime && data[i].createtime == detailTime) {//执行失败或成功，给出提示，并点亮按钮
							if (data[i].status == 1) {
								location.href = "/queryCrsAccountPeriod/downloadExcel?fileName=" + data[i].exportFileName;
							} else {
								alert("Export Details failed!");
							}
							$("#m_detail_download_btn").attr("disabled",false);
					    	$("#m_detail_download_btn").html("明细下载");
					    	datailsStatus = 0;//明细导出状态
						};
					}
					if (data[i].functionName == "generateRecon") {
						if ((data[i].status == -1 || data[i].status == 1) && reconStatus == 1) {//执行失败或成功，给出提示，并点亮按钮
							if (data[i].status == 1) {
								alert("Generate Recon successfully.");
							} else {
								alert("Generate Recon failed!");
							}
							$("#m_generate_recon_btn").attr("disabled",false);
					    	$("#m_generate_recon_btn").html("生成recon数据");
					    	reconStatus = 0;//生成recon状态
						};
					}
				}
			}
			
			//如果都执行完，那么关掉定时器
			if (merchantStatus == 0 && merchantStatus == 0 && datailsStatus == 0 && reconStatus == 0) {
				clearInterval(myTimer);
				myTimer = null;
			} else {
				myTimer = setInterval("myInterval()",1000*60);//单位毫秒，60秒
			}
		}
	});   
}

jQuery(document).ready(function () {
	//填充城市下拉框
	fillCitiesSelect();
	//填充区域下拉框
	fillZonesSelect();
	
	$('.calendar').datetimepicker({
        format: 'yyyy-mm',
        autoclose: true,
        todayBtn: false,
        startView: 'year',
        minView:'year',
        maxView:'decade',
        language:'zh-CN',
        clearBtn: true  //添加清除按钮，可选值：true/false
    });
	$('.calendar2').datetimepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: false,
        startView: 'month',
        minView:'month',
        maxView:'decade',
        language:'zh-CN',
        clearBtn: true  //添加清除按钮，可选值：true/false
    });
	
	var myDate = new Date();
	//获取当前年
	var year=myDate.getFullYear();
	//获取当前月
	var month=myDate.getMonth()+1;
	month = prefixInteger(month,2);
	$("#startYearAndMonthQuery").val(year + "-" + month);
	$("#endYearAndMonthQuery").val(year + "-" + month);
	
    Datatable_expRemoteAjaxDemo.init();
    
    //myTimer = setInterval("myInterval()",1000*30);//单位毫秒，60秒
    
});