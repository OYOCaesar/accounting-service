//Class definition
var importReportData = function () {
  //== Private functions

  // basic demo
  var dataTable_report = "";
  var import_ReportData = function () {
    var url ='http://accountingservicetest.cn-north-1.eb.amazonaws.com.cn:8001/oyoShare/oyoShareList?isTest=t&hotelName='+hotelName+"&city="+city+"&status="+status;
    dataTable_report = $('#m_datatable_data').mDatatable({
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
      columns: [
        {
          field: 'uniqueCode',
          title: 'UNIQUE_CODE',
          sortable: false,
          textAlign: 'center'
        },
        {
          field: 'oyoId',
          title: 'OYO_ID',
          sortable: false,
          selector: false,
          textAlign: 'center'
        },
        {
          field: 'hotelId',
          title: 'HOTEL_ID',
        },
        {
          field: 'status',
          title: 'STATUS',
          sortable: false,
          selector: false,
          textAlign: 'center'
        },
        {
          field: 'hotelName',
          title: 'HOTEL_NAME',
          sortable: false,
          selector: false,
          textAlign: 'center',
        },
        {
          field: 'city',
          title: 'CITY',
          sortable: false,
          selector: false,
          textAlign: 'center',
        },
        {
          field: 'zoneName',
          title: 'ZONE_NAME',
          sortable: false,
          selector: false,
          textAlign: 'center'
        },
        {
          field: 'testing',
          title: 'Testing',
          type: "date",
          sortable: false,
          selector: false,
          textAlign: 'center',
        }
      ],
    });
  };

  $("#m_search_btn").on("click", function (t) {
    t.preventDefault();
    dataTable_report.destroy();
    var e = {};

    $(".m-input").each(function () {
      var a = $(this).data("col-index");

      if (a == 0) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
        hotelName = e[a];
      }
      if (a == 1) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
        city = e[a];
      }
      if (a == 2) {
        e[a] ? e[a] += "|" + $(this).val() : e[a] = $(this).val();
        status = e[a];
      }

    });

    import_ReportData();
  });

  return {
    init: function () {
      import_ReportData();
    }
  };
}();
var hotelName="",city="",status="";
jQuery(document).ready(function () {
  importReportData.init()
});

