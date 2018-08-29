/***
 * 手工同步js
 */
//执行一个laydate实例
laydate.render({
    elem: '#syncCrs' //指定元素
    ,type: 'month'
});

//执行一个laydate实例
laydate.render({
    elem: '#syncSAP' //指定元素
    ,type: 'month'
});

$(function () {
	//补零函数
	function prefixInteger(num, n) {
       return (Array(n).join(0) + num).slice(-n);
    }
	
	var myDate = new Date();
	//获取当前年
	var year=myDate.getFullYear();
	//获取当前月
	var month=myDate.getMonth()+1;
	month = prefixInteger(month,2);
	
	var lastMonthDate = new Date(myDate.getFullYear(),myDate.getMonth()-1,myDate.getDate());//上个月
	var lastYear = lastMonthDate.getFullYear();
	var lastMonth = lastMonthDate.getMonth()+1;
	lastMonth = prefixInteger(lastMonth,2);
	
	$("#syncMunshi").val(lastYear + "-" + lastMonth);
	$("#syncCrs").val(lastYear + "-" + lastMonth);
	$("#syncSAP").val(lastYear + "-" + lastMonth);
	
	//同步mushi
	$('#syncMunshiBtn').click(function(){
		$.ajax({
		    url:'/syncMunshiAr/syncMunshiAr',
		    type:'POST', //GET
		    async:true,    //或false,是否异步
		    data:{
		    	yearMonth:$("#syncMunshi").val()
		    },
		    timeout:3600000,    //超时时间
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		    	$("#syncMunshiResult").val("");
		        console.log(xhr);
		        $("#syncMunshiBtn").html("Synchronizing...");
		        $("#syncMunshiBtn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	$("#syncMunshiResult").html(data);
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		    },
		    complete:function(data){
		    	$("#syncMunshiResult").html(data.responseText);
		    	$("#syncMunshiBtn").attr("disabled",false);
		    	$("#syncMunshiBtn").html("sync munshi");
		    }
		});
		
    });
	
	//同步crs
	$('#syncCrsBtn').click(function(){
		$.ajax({
		    url:'/syncCrsArAp/syncArApFromCrs',
		    type:'POST', //GET
		    async:true,    //或false,是否异步
		    data:{
		    	yearMonth:$("#syncCrs").val()
		    },
		    timeout:3600000,    //超时时间
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		    	$("#syncCrsResult").val("");
		        console.log(xhr);
		        $("#syncCrsBtn").html("Synchronizing...");
		        $("#syncCrsBtn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	$("#syncCrsResult").html(data);
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		    },
		    complete:function(data){
		    	$("#syncCrsResult").html(data.responseText);
		    	$("#syncCrsBtn").attr("disabled",false);
		    	$("#syncCrsBtn").html("sync CRS");
		    }
		});
		
    });
	
	//同步ar and ap to SAP
    $('#syncSapBtn').click(function(){
    	$.ajax({
		    url:'/syncArAndApAndJournalEntryToSap/syncToSap',
		    type:'POST', //GET
		    async:true,    //或false,是否异步
		    data:{
		    	yearMonth:$("#syncSAP").val().trim(),
		    	hotelId:$("#hotelId").val().trim()
		    },
		    timeout:3600000,    //超时时间
		    dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
		    beforeSend:function(xhr){
		    	$("#syncSapResult").val("");
		        console.log(xhr);
		        $("#syncSapBtn").html("Synchronizing...");
		        $("#syncSapBtn").attr("disabled",true);
		    },
		    success:function(data,textStatus,jqXHR){
		    	$("#syncSapResult").html(data);
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		        console.log(textStatus);
		    },
		    complete:function(data){
		    	$("#syncSapResult").html(data.responseText);
		    	$("#syncSapBtn").attr("disabled",false);
		    	$("#syncSapBtn").html("sync SAP");
		    }
		});
    });

    //同步hotel to SAP
    $('#syncHotelBtn').click(function(){
        $.ajax({
            url:'/syncHotel/syncHotelToSap',
            type:'POST', //GET
            async:true,    //或false,是否异步
            data:{
                id:$("#cardcode").val()
            },
            timeout:3600000,    //超时时间
            dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
            beforeSend:function(xhr){
                $("#syncHotelResult").val("");
                console.log(xhr);
                $("#syncHotelBtn").html("Synchronizing...");
                $("#syncHotelBtn").attr("disabled",true);
            },
            success:function(data,textStatus,jqXHR){
                $("#syncHotelSapResult").html(data);
            },
            error:function(xhr,textStatus){
                console.log(xhr)
                console.log(textStatus);
            },
            complete:function(data){
                $("#syncHotelResult").html(data.responseText);
                $("#syncHotelBtn").attr("disabled",false);
                $("#syncHotelBtn").html("sync SAP");
            }
        });
    });
});