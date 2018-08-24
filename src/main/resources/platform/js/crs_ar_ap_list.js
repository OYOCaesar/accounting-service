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
            url: '../queryCrsArAp/query?checkInDateStart=' + checkInDateStart+"&checkInDateEnd="+checkInDateEnd+"&checkOutDateStart="+checkOutDateStart+"&checkOutDateEnd="+checkOutDateEnd+"&hotelName="+hotelName+"&status="+status+"&page=1&rows=10",
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
            {field:'rate',title:'rate',width:60}
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
          checkInDateStart = e[a];
      }
      if (a == 1) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          checkInDateEnd = e[a];
      }
      if (a == 2) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          checkOutDateStart = e[a];
      }
        if (a == 3) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            checkOutDateEnd = e[a];
        }
        if (a == 4) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            hotelName = e[a];
        }
        if (a == 5) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            status = e[a];
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

var checkInDateStart = "",checkInDateEnd="",checkOutDateStart="",checkOutDateEnd="",hotelName="",status="";

jQuery(document).ready(function () {

  Datatable_expRemoteAjaxDemo.init();
});