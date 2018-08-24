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
                  账期数据列表
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
                      <label>账期:</label>
                      <div class="input-daterange input-group" id="m_datepicker">
                          <input type="text" class="form-control calendar" id="startYearAndMonthQuery" readonly=""
                                 placeholder="Start Time">
                          <div class="input-group-append">
                              <span class="input-group-text"><i class="la la-ellipsis-h"></i></span>
                          </div>
                          <input type="text" class="form-control calendar" id="endYearAndMonthQuery" readonly=""
                                 placeholder="End Time">
                      </div>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>入住日期:</label>
                      <div class="input-daterange input-group" id="m_datepicker2">
                          <input type="text" class="form-control m-input calendar2" id="m_datepicker_1" readonly=""
                                 data-col-index="0">
                      </div>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>退房日期:</label>
                      <div class="input-daterange input-group" id="m_datepicker">
                          <input type="text" class="form-control m-input calendar2" id="m_datepicker_2" readonly=""
                                 data-col-index="1">
                      </div>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>订单号:</label>
                      <input type="text" class="form-control m-input" id="orderNo"
                             data-col-index="2">
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>区域:</label>
                      <select class="form-control m-input" data-col-index="3" id="region">
                          <option value="">Select</option>
                      </select>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>城市:</label>
                      <select class="form-control m-input" data-col-index="4" id="city">
                          <option value="">Select</option>
                      </select>
                  </div>
                  <div class="col-lg-3 m--margin-bottom-10-tablet-and-mobile">
                      <label>酒店名称:</label>
                      <input type="text" class="form-control m-input" id="hotelName"
                             data-col-index="5">
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
                    <button class="btn btn-brand m-btn m-btn--icon" id="m_summary_statistics_btn">
                                        <span>
                                            <i class="la"></i>
                                            <span>汇总下载</span>
                                        </span>
                    </button>
                    &nbsp;&nbsp;
                    <button class="btn btn-brand m-btn m-btn--icon" id="m_merchant_account_download_btn">
                                        <span>
                                            <i class="la"></i>
                                            <span>商户对账单下载</span>
                                        </span>
                    </button>
                    &nbsp;&nbsp;
                    <button class="btn btn-brand m-btn m-btn--icon" id="m_detail_download_btn">
                                        <span>
                                            <i class="la"></i>
                                            <span>明细下载</span>
                                        </span>
                    </button>
                    &nbsp;&nbsp;
                    <button class="btn btn-brand m-btn m-btn--icon" id="m_generate_recon_btn">
                                        <span>
                                            <i class="la"></i>
                                            <span>生成recon数据</span>
                                        </span>
                    </button>
                    &nbsp;&nbsp;
                </div>
              </div>
            </form>
            <!--end: Search Form -->

            <!--begin: Datatable -->
            <div class="m_datatable_exception" id="ajax_data"></div>
            <!--end: Datatable -->
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
<script src="/js/account_data_list.js" type="text/javascript"></script>
</body>
<!-- end::Body -->
</html>
