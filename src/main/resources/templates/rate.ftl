<!DOCTYPE html>

<html lang="en">
<!-- begin::Head -->
<#include "/base/head.ftl">
<!-- end::Head -->

<!-- end::Body -->
<body class="m-page--fluid m--skin- m-content--skin-light2 m-header--fixed m-header--fixed-mobile m-aside-left--enabled m-aside-left--skin-dark m-aside-left--offcanvas m-footer--push m-aside--offcanvas-default">

<!-- begin:: Page -->
<div class="m-grid m-grid--hor m-grid--root m-page">

  <!-- BEGIN: Menu header -->
<#include "/base/menuHeader.ftl">
  <!-- END: Menu header -->

  <!-- begin::Body -->
  <div class="m-grid__item m-grid__item--fluid m-grid m-grid--ver-desktop m-grid--desktop m-body">

    <!-- begin::Left_aside -->
  <#include "/base/left_aside_menu.ftl">
    <!-- end::Left_aside -->
    <div class="m-grid__item m-grid__item--fluid m-wrapper">

      <!-- END: Subheader -->
      <div class="m-content">
        <div class="m-portlet m-portlet--mobile">
          <div class="m-portlet__head">
            <div class="m-portlet__head-caption">
              <div class="m-portlet__head-title">
                <h3 class="m-portlet__head-text">
                  费率表
                  <small>

                  </small>
                </h3>
              </div>
            </div>
            <div class="m-portlet__head-tools">
              <ul class="m-portlet__nav">
                <li class="m-portlet__nav-item">

                </li>
              </ul>
            </div>
          </div>
          <div class="m-portlet__body">
            <!--begin: Search Form -->
            <form class="m-form m-form--fit m--margin-bottom-20">
              <div class="row m--margin-bottom-10">

                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>状态:</label>
                      <select class="form-control m-input" data-col-index="0">
                          <option value="">Select</option>
                          <option value="On Hold">On Hold</option>
                          <option value="Active">Active</option>
                          <option value="Live">Live</option>
                          <option value="Blocked">Blocked</option>
                      </select>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>城市:</label>
                      <input type="text" class="form-control m-input"
                             data-col-index="1">
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>酒店名称:</label>
                      <input type="text" class="form-control m-input"
                             data-col-index="2">
                  </div>
              </div>

              <div class="m-separator m-separator--md m-separator--dashed"></div>

              <div class="row">
                <div class="col-lg-12">
                  <button class="btn btn-brand m-btn m-btn--icon" id="m_search_btn">
                    <span>
                        <i class="la la-search"></i>
                        <span>查询</span>
                    </span>
                  </button>
                  &nbsp;&nbsp;
                  <button class="btn btn-secondary m-btn m-btn--icon" id="m_reset_btn">
                    <span>
                        <i class="la la-close"></i>
                        <span>重置</span>
                    </span>
                  </button>
                &nbsp;&nbsp;
                <button type="button" class="btn btn-secondary m-btn m-btn--icon" data-toggle="modal" data-target="#m_blockui_4_1_modal">
                    <span>
                        <i class="la"></i>
                        <span>导入费率表</span>
                    </span>
                </button>
                &nbsp;&nbsp;
                <button type="button" class="btn btn-secondary m-btn m-btn--icon">
                            <span>
                                <i class="la"></i>
                                <span>存档</span>
                            </span>
                </button>
                &nbsp;&nbsp;
                <button type="button" class="btn btn-secondary m-btn m-btn--icon">
                            <span>
                                <i class="la"></i>
                                <span>下载</span>
                            </span>
                </button>
                </div>
              </div>
            </form>
            <!--begin: Datatable -->
            <div class="m_datatable_exception" id="ajax_data"></div>
            <!--end: Datatable -->
              <!--弹窗-->
              <div class="modal fade show" id="m_blockui_4_1_modal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" style="display: none; padding-right: 17px;">
                  <div class="modal-dialog" role="document">
                      <div class="modal-content">
                          <div class="modal-header">
                              <h5 class="modal-title" id="exampleModalLabel">
                                  导入文件
                              </h5>
                              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                               <span aria-hidden="true">
                                ×
                               </span>
                              </button>
                          </div>
                          <div class="modal-body">
                              <form id="uploadFileForm" action="../fileUpload/upload" method="post" enctype="multipart/form-data">
                                  <div class="form-group">
                                      <label for="recipient-name" class="form-control-label">
                                          选择文件:
                                      </label>
                                      <input type="hidden" name="isTest" value="f">
                                      <input type="file" name="file" id="file_input" class="form-control" accept=".xls,.xlsx">
                                      <input type="submit" style="margin-left: 370px;margin-top:15px;" class="btn btn-secondary">
                                  </div>
                              </form>
                          </div>
                      </div>
                  </div>
              </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- end:: Body -->
</div>
<!-- end:: Page -->

<!-- begin::Scroll Top -->
<#include "/base/scrollTop.ftl">
<!-- end::Scroll Top -->

<!-- begin::baseJs -->
<#include "/base/baseJs.ftl">
<!-- end::baseJs -->
<script src="/js/rate.js" type="text/javascript"></script>
</body>
<script>

    function fileChange(){
        var ele = document.getElementById('file_input');
        if(ele.value == null || ele.value == undefined || ele.value == ''){
            alert("请选择一个文件！");
            return false;
        }else{
            alert(1);
            $("#submitFileForm").click();
        }
    }
    function exportRateExcel(ele) {
        $("#file_input").val("");
        $("#file_input").click();
    }
</script>
<!-- end::Body -->
</html>
