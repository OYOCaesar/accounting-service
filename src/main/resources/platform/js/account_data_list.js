//== Class definition
var Datatable_expRemoteAjaxDemo = function () {

  var datatable_exp = "";
  //== Private functions
  var demo_exp = function () {

    var url = 'http://localhost:8001/syncHotel/querySyncHotelList?cardcode=' + cardcode+"&cardname="+cardname+"&uCrsid="+uCrsid+"&batch="+batch;

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
            {field:'id',title:'账期'},
            {field:'cardcode',title:'酒店名称'},
            {field:'cardname',title:'订单号'},
            {field:'valid',title:'客人姓名'},
            {field:'cntctPrsn',title:'订单渠道'},
            {field:'licTradNum',title:'渠道名'},
            {field:'ucrsid',title:'入住日期'},
            {field:'contacts',title:'退房日期'},
            {field:'address',title:'本期开始日期'},
            {field:'address',title:'本期结束日期'},
            {field:'batch',title:'本期入住天数'},
            {field:'batch',title:'房间价格'},
            {field:'batch',title:'本月应结算总额（计算）'},
            {field:'batch',title:'订单状态'},
            {field:'batch',title:'已用客房数'},
            {field:'batch',title:'本月已用间夜数'},
            {field:'batch',title:'订单总额'},
            {field:'batch',title:'本月应结算总额'},
            {field:'batch',title:'支付方式'},
            {field:'batch',title:'支付明细'},
            {field:'batch',title:'支付类型（预付/后付费）'},
            {field:'batch',title:'OTA ID'},
            {field:'batch',title:'City'},
            {field:'batch',title:'Region'},
            {field:'batch',title:'Hotels ID'},
            {field:'batch',title:'本月匹配费率'},
            {field:'batch',title:'OYO share'}

        ],
    });

  };

  $("#m_search_btn").on("click", function (t) {
    t.preventDefault();
    datatable_exp.destroy();
    var e = {};


    $(".m-input").each(function () {
      var a = $(this).data("col-index");

      if (a == 0) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          cardcode = e[a];
      }
      if (a == 1) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          cardname = e[a];
      }
      if (a == 2) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          uCrsid = e[a];
      }
        if (a == 3) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            batch = e[a];
        }
    });
    demo_exp();
  });

  return {
    // public functions
    init: function () {
      demo_exp();
    }
  };
}();

var cardcode = "",cardname="",uCrsid="",batch="";

jQuery(document).ready(function () {

  Datatable_expRemoteAjaxDemo.init();
});