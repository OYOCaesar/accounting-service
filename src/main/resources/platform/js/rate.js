//== Class definition
var Datatable_expRemoteAjaxDemo = function () {

  var datatable_exp = "";
  //== Private functions
  var demo_exp = function () {

    var url = '../oyoShare/oyoShareList?city=' + city+"&status="+status+"&hotelName="+hotelName+"&batch="+batch;
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
            {field:'id',title:'id'},
            {field:'hotelId',title:'hotelId'},
            {field:'uniqueCode',title:'uniqueCode'},
            {field:'oyoId',title:'oyoId'},
            {field:'hotelName',title:'hotelName'},
            {field:'city',title:'city'},
            {field:'zoneName',title:'zoneName'},
            {field:'fixedRate',title:'fixedRate'},
            {field:'status',title:'status'},
            {field:'oyoShare',title:'oyoShare'}
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
          status = e[a];
      }
      if (a == 1) {
          e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          city= e[a];
      }
      if (a == 2) {
          e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          hotelName = e[a];
      }
      /**if (a == 3) {
          e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
          batch = e[a];
      }*/
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

var status="",city = "",hotelName="",batch="";

jQuery(document).ready(function () {

  Datatable_expRemoteAjaxDemo.init();
});