/***
 * 对账js
 */
//定义页面的参数变量
var startYearAndMonthQuery = "",endYearAndMonthQuery="",checkInDate="",checkOutDate="",orderNo="",region="",city="",hotelName="";
//== Class definition
var Datatable_expRemoteAjaxDemo = function () {

  var datatable_exp = "";
  //== Private functions
  var demo_exp = function () {

    var url = '/queryCrsAccountPeriod/query?startYearAndMonthQuery=' + startYearAndMonthQuery+"&endYearAndMonthQuery="+endYearAndMonthQuery
             +"&checkInDate="+checkInDate+"&checkOutDate="+checkOutDate+"&orderNo="+orderNo+"&region="+region
             +"&city="+city+"&hotelName="+hotelName;

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

	  location.href = '/queryCrsAccountPeriod/exportSummaryStatistics?startYearAndMonthQuery=' + startYearAndMonthQuery
				    + '&endYearAndMonthQuery=' + endYearAndMonthQuery
					+ '&checkInDate=' + checkInDate
					+ '&checkOutDate=' + checkOutDate
					+ '&orderNo=' + orderNo
					+ '&region=' + region
					+ '&city=' + city 
					+ '&hotelName=' + hotelName;
  });
  
  //商户对账单下载
  $("#m_merchant_account_download_btn").on("click", function (t) {
	  t.preventDefault();

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

	  location.href = '/queryCrsAccountPeriod/exportMerchantAccount?startYearAndMonthQuery=' + startYearAndMonthQuery
					+ '&endYearAndMonthQuery=' + endYearAndMonthQuery
					+ '&checkInDate=' + checkInDate
					+ '&checkOutDate=' + checkOutDate
					+ '&orderNo=' + orderNo
					+ '&region=' + region
					+ '&city=' + city 
					+ '&hotelName=' + hotelName;
  });
  
  //明细下载
  $("#m_detail_download_btn").on("click", function (t) {
	  t.preventDefault();
		
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
		
	  location.href = '/queryCrsAccountPeriod/exportDetails?startYearAndMonthQuery=' + startYearAndMonthQuery
				    + '&endYearAndMonthQuery=' + endYearAndMonthQuery
					+ '&checkInDate=' + checkInDate
					+ '&checkOutDate=' + checkOutDate
					+ '&orderNo=' + orderNo
					+ '&region=' + region
					+ '&city=' + city 
					+ '&hotelName=' + hotelName;
  });
  
  //生成recon数据
  $("#m_generate_recon_btn").on("click", function (t) {
      t.preventDefault();
      
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
		        alert(textStatus);
		    },
		    complete:function(data) {
		    	if (data && data.responseJSON && data.responseJSON.msg) {
		    		alert(data.responseJSON.msg);
		    	}
		    	$("#m_generate_recon_btn").attr("disabled",false);
		    	$("#m_generate_recon_btn").html("生成recon数据");
		    	console.log(data);
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

jQuery(document).ready(function () {
	//填充城市下拉框
	fillCitiesSelect();
	//填充区域下拉框
	fillZonesSelect();
	
	$('.calendar').datetimepicker({
        format: 'yyyy-mm',
        autoclose: true,
        todayBtn: true,
        startView: 'year',
        minView:'year',
        maxView:'decade',
        language:'zh-CN',
        clearBtn: true  //添加清除按钮，可选值：true/false
    });
	$('.calendar2').datetimepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
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
});