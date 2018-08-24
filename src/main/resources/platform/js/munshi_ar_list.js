//== Class definition
var Datatable_expRemoteAjaxDemo = function () {

  var datatable_exp = "";
  //== Private functions
  var demo_exp = function () {

    datatable_exp = $('.m_datatable_exception').mDatatable({
      // datasource definition
      data: {
        type: 'remote',
        source: {
          read: {
            // sample GET method
            method: 'GET',
            url: 'http://accountingservicetest.cn-north-1.eb.amazonaws.com.cn:8001/queryMunshiArAp/query?yearMonth=' + yearMonth+"&hotelName="+hotelName+"&isSync="+isSync+"&page=1&rows=5000",
            map: function (raw) {
              var dataSet = raw.rows;
              return dataSet;
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
            {field:'hotelId',title:'hotel id',width:60},
            {field:'hotelName',title:'hotel name'},
            {field:'arAmount',title:'ar Amount',width:80},
            {field:'apAmount',title:'ap Amount',width:80},
            {field:'rate',title:'rate',width:60},
            {field:'isSync',title:'isSync',width:60}
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
          yearMonth = e[a];
      }
      if (a == 1) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          hotelName = e[a];
      }
      if (a == 2) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          isSync = e[a];
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

var yearMonth = "",hotelName="",isSync="";

jQuery(document).ready(function () {

  Datatable_expRemoteAjaxDemo.init();
});