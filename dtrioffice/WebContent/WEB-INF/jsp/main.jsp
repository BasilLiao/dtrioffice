<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>DTR ERP(延展系統)</title>
<link rel="stylesheet" href="./thirdparty/css/bootstrap-4.4.1.min.css" />
<link rel="stylesheet" href="./thirdparty/css/jquery-ui.min.css" />
<script src="./thirdparty/js/jquery-3.4.1.min.js"></script>
<script src="./thirdparty/js/jquery-ui-1.12.1.min.js"></script>
<script src="./thirdparty/js/bootstrap-4.4.1.min.js"></script>
<script src="./thirdparty/js/jquery-table2excel.js"></script>
<script src="./thirdparty/js/vue-2.6.11.min.js"></script>
<script src="./thirdparty/js/websocket-sockjs.min.js"></script>
<script src="./thirdparty/js/websocket-stomp.min.js"></script>
<script src="./thirdparty/js/jszip-0.8.0.js"></script>
<script src="./thirdparty/js/xlsx-0.8.0.js"></script>

<link rel="icon" type="image/svg" href="./emoji.svg" />
<style type="text/css">
#header_nav, body {
	/*background-image: url("./thirdparty/images/bg.png");*/
	font-family: Microsoft JhengHei;
}

#countDown, #loading {
	z-index: 1030;
	top: 0px;
	left: 0px;
	background: #ffffff61;
	width: 100%;
	height: 100%;
}

#countDown div, #loading div {
	text-align: center;
}
</style>
</head>
<!-- 排版端 -->
<body class="col-md" id="main">
	<div class="m-0 fixed-top">
		<div id="header_nav" class="col-md p-0">
			<div id="header" class="p-0 m-0"></div>
			<div id="nav" class="mt-0 mb-0"></div>
			<div class="alert mb-0 pb-1 pt-0 mb-1 alert-primary fade shadow" role="alert">
				<div id="alert_message"></div>
			</div>
		</div>
	</div>
	<div class="pb-4 pt-4 col-md-12">
		<div class="col-md-12"></div>
	</div>
	<div id="body" class="p-0 col-md-12"></div>
	<div id="footer" class="mt-1 mb-2"></div>
	<!-- 執行中 -->
	<div id="loading" class="d-none fixed-top" onclick="return main.loading(false);">
		<div class="mt-5 pt-5">
			<div>
				<img alt="" src="./thirdparty/images/loading.gif" />
			</div>
			<div>
				<h2>程序進行中....請稍後.</h2>
			</div>
		</div>
	</div>
	<!-- 提醒登出-->
	<div id="countDown" class="d-none fixed-top" onclick="return main.timeOut(false);">
		<div class="mt-5 pt-5">
			<div>
				<img alt="" src="./thirdparty/images/loading.gif" />
			</div>
			<div id="timeOutmsg">
				<h2>程序登出中....請點我繼續.</h2>
			</div>
		</div>
	</div>
	<!--  ${allData}-->
</body>
<script>
  	//各類模型 Vue 位置(需註冊)
    var templateHeader = "";
    var templateNav = "";
    var templateBody = "";
    var templateFooter = "";
    //控制端
    var main = new Vue({
        el: "#main",
    	tempAutoBomPrint : {
        	orderId:"",//製令單號
        	orderEndDate:"",//預計出貨
        	orderBomId:"",//產品ID
        	orderQty:0,//數量
        	orderNo:"",//訂單號
        	orderer:"",//訂購人
        	orderCountry:"",//訂單國家
        	packagePC:"",//Power cord
        	packageBOG:"",//說明書-有客戶訂單則需要有
        	packageL:"",
        	packageBL:"",
        	packageBP:"",
        	packageRP:"",
        	packageOS:"",
        	noteMoc:"",
        	aubomnumber:"",
        	autoStepCheck:false,
        	autoStep1:false,
        	autoStep2:false,
        	autoStep3:false,
        	autoStep4:false,
        },
        data: {
            //每次回傳資料
            allData: ${ allData },
            //登入存入資料
            loginData: "",
            //各類模型 資料
            contentData: "",
            //Ajax 傳輸模組
            ajaxCell: {
                url: "",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "JSON",
                data: "",
            },
            //倒數計時
            countDownId: "",
            countDownSecond: 300
        },
        created: function () {
            //初始化
            console.log(this.allData);
            this.contentData = this.allData["r_content"];
            this.loginData = this.allData["r_content"]["longinInfo"];
            //取得模板
            $("#header").load(this.contentData["template"]["header"]);
            $("#nav").load(this.contentData["template"]["nav"]);
            $("#body").load(this.contentData["template"]["body"]);
            $("#footer").load(this.contentData["template"]["footer"]);
            //啟動計時
            this.timeOut(true);
        },
        methods: {
            ajaxSend: function (url, dataSend) {
                //進入倒數中
                clearInterval(this.countDownId);
                this.countDownSecond = 800;
                main.timeOut(true);
                //進入運作中
                main.loading(true);
                //排除空白
                var data = dataSend.replaceAll("=", "").replaceAll("!", "");
                $.ajax({
                    url: url,
                    type: this.ajaxCell.type,
                    contentType: this.ajaxCell.contentType,
                    dataType: this.ajaxCell.dataType,
                    data: data,
                    success: function (event) {
                        main.allData = event;
                        main.contentData = event.r_content;
                        main.alertshow(true, " 結果->" + event.r_message);
                        //模組 轉跳 畫面
                        console.log(event.r_cellBackName + " : to success");
                        //固定回傳模組
                        if (event.r_cellBackName != null) {
                            switch (event.r_cellBackName) {
                                case "navAfter":
                                    templateNav["navAfter"](event);
                                    break;
                                case "bodyAfter":
                                    templateBody["bodyAfter"](event);
                                    break;
                                case "headerAfter":
                                    templateHeader["headerAfter"](event);
                                    break;
                                case "footerAfter":
                                    templateFooter["footerAfter"](event);
                                    break;
                            }
                        }
                        main.loading(false);
                    },
                    error: function (event) {
                        main.alertshow(true, "Something to fail");
                        console.log("to index fail:" + event);
                        if (event.status == 200) {
                            //沒有session 拒絕時
                            window.location.replace("login.jsp");
                        }
                        main.loading(false);
                    },
                });
            },
            alertshow: function (open, message) {
                if (open) {
                    $("#alert_message").text("資訊: " + message);
                    $(".alert").addClass("show");
                    setTimeout(function () {
                        $(".alert").removeClass("show");
                        $("#alert_message").text("");
                    }, 8000);
                }
            },
            //timeOut 到計時 登出(提醒失效)
            countDown: function () {
                this.countDownSecond -= 1;
                if (this.countDownSecond == 60) {
                    $("#countDown").removeClass("d-none");
                }
                if (this.countDownSecond < 0) {
                    clearInterval(this.countDownId);
                    $("#countDown").addClass("d-none");
                    templateNav.signout();
                }
                $("#nav_timeout").text("T.O.:"+this.countDownSecond+"/s");
                $("#timeOutmsg h2").text("程序登出中....請點我繼續..." + this.countDownSecond);
                //console.log("倒數登出 : "+this.countDownSecond);
            },
            timeOut: function (check) {
                if (check) {
                    //開始計時
                    this.countDownId = setInterval(this.countDown, 1000); //每秒執行一次，賦值
                } else {
                    //啟動計時
                    clearInterval(this.countDownId);
                    this.countDownSecond = 800;
                    $("#countDown").addClass("d-none");
                    //觸發紀錄
                    var url = "./sessionUpdate.do";
                    var data = JSON.stringify({
                        data: {
                            datetime: "",
                            action: "R",
                            whoami: main.loginData.account,
                            cellBackName: "",
                            cellBackOrder: "",
                            content: "",
                        },
                    });
                    main.ajaxSend(url, data);
                }
            },
            //程序運作中~
            loading: function (open) {
                if (open) {
                    $("#loading").removeClass("d-none");
                } else {
                    $("#loading").addClass("d-none");
                }
            },
        }
    });
    </script>
</html>