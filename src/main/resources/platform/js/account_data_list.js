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
            method: 'GET',
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
            {field:'statusDes',title:'订单状态'},
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
            {field:'currentMonthRate',title:'本月匹配费率'},
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
      
      demo_exp();
  });
  
  //汇总下载
  $("#m_summary_statistics_btn").on("click", function (t) {
    t.preventDefault();
    datatable_exp.destroy();
    
    setParamValues();
    
    location.href = '/queryCrsAccountPeriod/exportSummaryStatistics?startYearAndMonthQuery=' + startYearAndMonthQuery+'&endYearAndMonthQuery='+endYearAndMonthQuery
					+'&checkInDate='+checkInDate+'&checkOutDate='+checkOutDate+'&orderNo='+orderNo+'&region='+region
					+'&city='+city+'&hotelName='+hotelName;
    demo_exp();
  });
  
  //商户对账单下载
  $("#m_merchant_account_download_btn").on("click", function (t) {
    t.preventDefault();
    datatable_exp.destroy();
    
    setParamValues();
    
    location.href = '/queryCrsAccountPeriod/exportMerchantAccount?startYearAndMonthQuery=' + startYearAndMonthQuery+'&endYearAndMonthQuery='+endYearAndMonthQuery
				   +'&checkInDate='+checkInDate+'&checkOutDate='+checkOutDate+'&orderNo='+orderNo+'&region='+region
				   +'&city='+city+'&hotelName='+hotelName;
    demo_exp();
  });
  
  //明细下载
  $("#m_detail_download_btn").on("click", function (t) {
    t.preventDefault();
    datatable_exp.destroy();
    
    setParamValues();
    
    location.href = '/queryCrsAccountPeriod/exportDetails?startYearAndMonthQuery=' + startYearAndMonthQuery+'&endYearAndMonthQuery='+endYearAndMonthQuery
                   +'&checkInDate='+checkInDate+'&checkOutDate='+checkOutDate+'&orderNo='+orderNo+'&region='+region
	               +'&city='+city+'&hotelName='+hotelName;
    demo_exp();
  });

  return {
    // public functions
    init: function () {
      demo_exp();
    }
  };
}();

jQuery(document).ready(function () {
	$('.calendar').datetimepicker({
        format: 'yyyy-mm',
        autoclose: true,
        todayBtn: true,
        startView: 'year',
        minView:'year',
        maxView:'decade',
        language:'zh-CN',
    });
	$('.calendar2').datetimepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayBtn: true,
        startView: 'month',
        minView:'month',
        maxView:'decade',
        language:'zh-CN',
    });
    Datatable_expRemoteAjaxDemo.init();
});