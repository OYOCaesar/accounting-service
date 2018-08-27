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
            url: '../syncHotel/querySyncHotelList?cardcode=' + cardcode+"&cardname="+cardname+"&uCrsid="+uCrsid+"&batch="+batch,
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
            {field:'id',title:'id'},
            {field:'cardcode',title:'cardcode'},
            {field:'cardname',title:'cardname'},
            {field:'valid',title:'valid'},
            {field:'cntctPrsn',title:'cntctPrsn'},
            {field:'licTradNum',title:'licTradNum'},
            {field:'ucrsid',title:'uCrsid'},
            {field:'contacts',title:'contacts'},
            {field:'address',title:'address'},
            {field:'batch',title:'批次'}
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