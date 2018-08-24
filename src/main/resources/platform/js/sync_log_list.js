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
            url: 'http://localhost:8001/syncLog/querySyncLoglist?batch=' + batch_inp+"&sourceId="+sourceId_inp+"&type="+type_inp+"&status="+status_inp,
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
            {field:'id', title:'id'},
            {field:'sourceId',title:'sourceId'},
            {field:'type',title:'同步数据类型'},
            {field:'status',title:'同步状态'},
            {field:'message',title:'同步结果'},
            {field:'batch',title:'同步批次'},
            {field:'jsonData',title:'同步的数据',width:500}
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
          batch_inp = e[a];
      }
      if (a == 1) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
        sourceId_inp = e[a];
      }
      if (a == 2) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            type_inp = e[a];
      }
        if (a == 3) {
            e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
            status_inp = e[a];
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
var batch_inp = "",sourceId_inp="",type_inp="",status_inp="";

jQuery(document).ready(function () {
  Datatable_expRemoteAjaxDemo.init();
});