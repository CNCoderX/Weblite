applicationCache.onupdateready = function () {
    //V1.0 lcommon.page.showDialog2("提示", "本地缓存已被更新,需要刷新页面来获取应用程序最新版本", function() {
    //手动更新本地缓存，只能在onupdateready事件触发时调用
    applicationCache.swapCache();
    location.reload();
    //});
}

window.onerror = function (errorMessage, scriptUri, lineNumber, columnNumber, errorObj) {
    var errMsg = "错误信息：" + errorMessage + "</br>" +
        //"出错文件：" + scriptUri + "</br> " +
        "出错行号：" + lineNumber + "</br>" +
        "出错列号：" + columnNumber + "</br>" +
        "错误详情：" + errorObj + "</br></br>" +
        "请联系图元科技技术支持.";
    //alert(errMsg);
    console.log(errMsg);
}

var _jssdk; 
var checkInType = window.Android == undefined ? 1 : 1;//签入类型，0代到微信，1代表安卓APP
var isShowCaseStandard = true;//是否显示立案标准。

var ajaxProxyInst = {
    Mobile: function() {
        return new AjaxProxy();
    }
}
var lcommon = {
    time: {
        getNowFormatDate: function () {
            var date = new Date();
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var minutes = date.getMinutes();
            if (minutes == "0") {
                minutes = "00";
            }
            else if (minutes < 10) {
                minutes = "0" + minutes;
            }
            var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                    + " " + date.getHours() + seperator2 + minutes
                    + seperator2 + date.getSeconds();
            return currentdate;
        },
        isLeapYear: function (year) {
            return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
        },
        getDay: function (year, month) {
            var isLYear = lcommon.time.isLeapYear();
            var day = 31;
            if (month == 2) {
                day = islpYear == true ? 29 : 28;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30;
            }
            return day;
        },
        getMonthStartTime: function (year, month) {
            return year + "-" + month + "-01 00:00:00";
        },
        getMonthEndTime: function (year, month) {
            var day = lcommon.time.getDay(year, month);
            return year + "-" + month + "-" + day + " 23:59:59";
        },
        /*时间格式装换*/
        removeT: function (val, row) {
            if (val !== null && typeof (val) != "undefined") {
                var data = val.replace("T", " ");
                return data;
            } else {
                {
                    return null;
                }
            }
        },
        removeTnoTime: function (val, row) {
            if (val !== null && typeof (val) != "undefined") {
                var data = val.split("T")[0];
                return data;
            } else {
                {
                    return null;
                }
            }
        },
        /* .jsonToDate(row.CreationTime, "yyyy/MM/dd/");*/
        jsonToDate: function (t, format) { //格式日期 如：jsonToDate(date,"yyyy-MM-dd"):2012-08-14
            if (t == null) return "";
            if (t == "") return t;
            try {
                var obj = {};
                if ("object" == typeof (t)) {
                    obj = t;
                } else {
                    t = t.replace("date", "Date");
                    obj = eval("new " + (t.replace(/\//g, "")));
                }
                return obj.format(format);
            } catch (e) {
                return t;
            }
        }
    },
    page: {
        browser: {
            versions: function () {
                var u = navigator.userAgent, app = navigator.appVersion;
                return {
                    //移动终端浏览器版本信息
                    trident: u.indexOf('Trident') > -1, //IE内核
                    presto: u.indexOf('Presto') > -1, //opera内核
                    webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                    gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                    mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
                    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                    android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器
                    iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
                    iPad: u.indexOf('iPad') > -1, //是否iPad
                    webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
                };
            }(),
            getOriginalUrl: function (url) { //去掉url参数
                return url.indexOf("?") > -1 ? url.substring(0, url.indexOf("?")) : url;
            },
            language: (navigator.browserLanguage || navigator.language).toLowerCase()
        },
        Cookie: {
            set: function (name, value) {
                var argv = arguments;
                var argc = arguments.length;
                var expires = (argc > 2) ? argv[2] : null;
                var path = (argc > 3) ? argv[3] : '/';
                var domain = (argc > 4) ? argv[4] : null;
                var secure = (argc > 5) ? argv[5] : false;
                document.cookie = name + "=" + escape(value) +
                ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) +
                ((path == null) ? "" : ("; path=" + path)) +
                ((domain == null) ? "" : ("; domain=" + domain)) +
                ((secure == true) ? "; secure" : "");
            },
            get: function (name) {
                var arg = name + "=";
                var alen = arg.length;
                var clen = document.cookie.length;
                var i = 0;
                var j = 0;
                while (i < clen) {
                    j = i + alen;
                    if (document.cookie.substring(i, j) == arg)
                        return lcommon.page.Cookie.getCookieVal(j);
                    i = document.cookie.indexOf(" ", i) + 1;
                    if (i == 0)
                        break;
                }
                return null;
            },
            clear: function (name) {
                if (lcommon.page.Cookie.get(name)) {
                    var expdate = new Date();
                    expdate.setTime(expdate.getTime() - (86400 * 1000 * 1));
                    lcommon.page.Cookie.set(name, "", expdate);
                }
            },
            getCookieVal: function (offset) {
                var endstr = document.cookie.indexOf(";", offset);
                if (endstr == -1) {
                    endstr = document.cookie.length;
                }
                return unescape(document.cookie.substring(offset, endstr));
            }
        },
        GetQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return (r[2]);
            return null;
        },
        Ajax: function (type, url, data, success, error) {
            $.ajax({
                type: type,
                url: url,
                data: data,
                success: success,
                error: error
            });
        },
        showDialog: function (fword, importword, type, callbacks) {
            var dialoghtml = "";
            $("#iosDialog2").remove();
            if (type === 2) {
                dialoghtml = "<div class=\"js_dialog\" id=\"iosDialog2\" style=\"display: none;\">" +
                    "<div class=\"weui-mask\"></div>" +
                    "<div class=\"weui-dialog\">" +
                    "<div class=\"weui-dialog__bd\">" + fword + "<span style=\"color: #2d7ed9; font-weight: bolder;\">" + importword + "</span></div>" +
                    "<div class=\"weui-dialog__ft\">" +
                    "<a href=\"javascript:;\" class=\"weui-dialog__btn weui-dialog__btn_primary\">好的</a>" +
                    "</div>" +
                    "</div>" +
                    "</div> ";
            }
            $("body").append(dialoghtml);

            var $iosDialog2 = $('#iosDialog2');
            //var top = ($(window).height() - $iosDialog2.height()) / 2;
            //$iosDialog2.css("top", top);
            $iosDialog2.find(".weui-dialog__btn").click(function () {
                $(this).parents('.js_dialog').fadeOut(200);
                $iosDialog2.remove();
                if (callbacks) {
                    callbacks();
                }
            });
            $iosDialog2.show();
            //$iosDialog2.addClass("animate-dialogShow").show();
            //$iosDialog2.fadeIn(200);
        },
        showDialog2: function (fword, importword, confirmback, cancleback) {
            var dialoghtml = "<div class=\"js_dialog\" id=\"iosDialog1\" style=\"display: none;\">" +
                "<div class=\"weui-mask\"></div>" +
                "<div class=\"weui-dialog\">" +
                "<div class=\"weui-dialog__hd\"><strong class=\"weui-dialog__title\">" + fword + "</strong></div>" +
                "<div class=\"weui-dialog__bd\">" + importword + "</div>" +
                "<div class=\"weui-dialog__ft\">" +
                "<a href=\"javascript:;\" class=\"weui-dialog__btn weui-dialog__btn_default\">取消</a>" +
                "<a href=\"javascript:;\" class=\"weui-dialog__btn weui-dialog__btn_primary\">确定</a>" +
                "</div>" +
                "</div>" +
                "</div>";
            $("body").append(dialoghtml);
            var $iosDialog = $('#iosDialog1');
            $iosDialog.find(".weui-dialog__btn_primary").click(function () { //确定按钮
                $(this).parents('.js_dialog').fadeOut(200);
                $iosDialog.remove();

                if (confirmback) {
                    confirmback();
                }
            });
            $iosDialog.find(".weui-dialog__btn_default").click(function () { //确定按钮
                $(this).parents('.js_dialog').fadeOut(200);
                $iosDialog.remove();
                if (cancleback) {
                    cancleback();
                }
            });
            $iosDialog.fadeIn(200);

        },
        showInputDialog2: function (fileId, wfInsId, actInstId, recallData, confirmback, cancelback) {
            var dialoghtml = "<div class=\"js_dialog\" id=\"iosDialog1\" style=\"display: none;\">" +
                "<div class=\"weui-mask\"></div>" +
                "<div class=\"weui-dialog1\">" +
                "<div class=\"weui-dialog__hd\"><strong class=\"weui-dialog__title\">请输入撤回意见：</strong></div>" +
                "<div class=\"weui-dialog__bd\"><textarea class=\"inputTextArea\"></textarea></div>" +
                "<div class=\"weui-dialog__ft\">" +
                "<a href=\"javascript:;\" class=\"weui-dialog__btn weui-dialog__btn_default\">取消</a>" +
                "<a href=\"javascript:;\" class=\"weui-dialog__btn weui-dialog__btn_primary\">确定</a>" +
                "</div>" +
                "</div>" +
                "</div>";
            $("body").append(dialoghtml);
            if (recallData.length > 0) {
                var recalhtml = "<div class='row'><div class='column'>环节名称</div><div class='column'>接收人</div></div>" +
                    "<div id=\"actionsheetmenu\" class=\"weui-actionsheet__menu\" style=\"max-height: 15em;overflow-y: auto;font-size:10px;\">";
                recallData.forEach(function (value) {
                    var active = value._ID === actInstId ? "weui-cell_active" : "";
                    recalhtml += "<div class=\"weui-actionsheet__cell row " + active + "\" " +
                        "data-info='{\"ActInstId\":" + value._ID + ",\"PreActInstId\":" + value._prevActivityInstanceID + "}' >" +
                        "<div class='column'>" + value._activityName + "</div><div class='column'>" + value._receiveObjectName + "</div>"
                        + "</div>";
                });
                recalhtml += "</div>";
                $("#iosDialog1 .weui-dialog__ft").before(recalhtml);

            }
            var $iosDialog = $('#iosDialog1');
            $(".weui-actionsheet__menu .weui-actionsheet__cell").click(function () {
                var $this = $(this);
                //var width = lcommon.page.getWidthFromWord($this.text(), $this.css("font-size")) + "px";
                if ($this.hasClass("weui-cell_active")) {
                    $this.removeClass("weui-cell_active");
                    //$this.css("padding-left", "0px");
                } else {
                    $this.addClass("weui-cell_active");
                    //$this.css("padding-left", width);
                }

            });
            $iosDialog.find(".weui-dialog__btn_primary").click(function () { //点击确定按钮
                document.removeEventListener("touchmove", bodyScroll);
                var recalDatas = [];
                var backviews = $(".inputTextArea").val();
                $(".weui-actionsheet__menu .weui-cell_active").each(function () {
                    var info = $(this).data("info");
                    var recalData = { FileId: fileId, WorkflowInstanceId: wfInsId, ActInstId: info.ActInstId, PreActInstId: info.PreActInstId };
                    recalDatas.push(recalData);
                });
                if (recalDatas.length < 1) {
                    lcommon.page.showDialog("提示：", "请选则要撤回的环节", 2);
                } else {
                    $(this).parents('.js_dialog').fadeOut(200);
                    $iosDialog.remove();
                    if (confirmback) {
                        confirmback(recalDatas, backviews);
                    }
                }
            });
            $iosDialog.find(".weui-dialog__btn_default").click(function () { //点击取消按钮
                document.removeEventListener("touchmove", bodyScroll);
                $(this).parents('.js_dialog').fadeOut(200);
                $iosDialog.remove();
                if (cancelback) {
                    cancelback();
                }
            });
            $iosDialog.fadeIn(200);
            document.addEventListener('touchmove', bodyScroll);

            function bodyScroll(event) {
                event.preventDefault();
            }
        },
        pickImage: function(cameraCallback, galleryCallback) {
            var dialoghtml =
                '<div id="image-picker" style="display: none;">' +
                '<div class="weui-skin_android" id="weui-android-actionsheet">' +
                '<div class="weui-mask"></div>' +
                '<div class="weui-actionsheet">' +
                '<div class="weui-actionsheet__menu">' +
                '<div class="weui-actionsheet__cell">拍照上传</div>' +
                '<div class="weui-actionsheet__cell">本地上传</div>' +
                '<div class="weui-actionsheet__cell">取消</div>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>';
            $("body").append(dialoghtml);

            var $imagePicker = $('#image-picker');
            $(".weui-actionsheet__cell").click(function () { //确定按钮
                $imagePicker.fadeOut(200);
                $imagePicker.remove();

                var $text = $(this).text();
                if ($text.indexOf('拍照上传') >= 0) {
                    if (cameraCallback) {
                        cameraCallback();
                    }
                } else if ($text.indexOf('本地上传') >= 0) {
                    if (galleryCallback) {
                        galleryCallback();
                    }
                }
            });

            $imagePicker.fadeIn(200);
        },
        showLoad: function (des) {
            var descript = des == undefined || des.length < 1 ? "数据加载中" : des;
            var loadHtml = "<div id=\"loadingToast\" style=\"display:none;\">" +
                "<div class=\"weui-mask_transparent\"></div>" +
                "<div class=\"weui-toast\">" +
                "<i class=\"weui-loading weui-icon_toast\"></i>" +
                "<p class=\"weui-toast__content\">" + descript + "</p>" +
                "</div>" +
                "</div>";
            if ($("#loadingToast").length < 1) {
                $('body').append(loadHtml);
            }
            if ($("#loadingToast").css('display') != 'none') return;
            $("#loadingToast").fadeIn(100);

        },
        hideLoad: function () {
            if ($("#loadingToast").css('display') == 'none') return;
            $("#loadingToast").fadeOut(100);
        },
        showRecordSound: function (option) {
            var $soundblock = $("#soundblcok");
            if ($soundblock.length === 0) {
                var soundblock = "<div id=\"soundblcok\">" +
                    "<div class=\"gm-layer-center\">" +
                    "<div class=\"soundblock\">" +
                    "<div class=\"sound\"><img src=\"../../image/psound.gif\" /></div>" +
                    "<div class=\"font\">录音中...</div>" +
                    "</div>" +
                    "<div id=\"stopsound\" class=\"gm-btt\"><i class=\"icon-wancheng\"></i>完成</div>" +
                    "</div>" +
                    "<div class=\"gm-layer-mask\"></div>" +
                    "</div>";
                $("body").append(soundblock);
            }
            $(".gm-layer-mask").fadeIn(20);
            $(".gm-layer-center").fadeIn(20);
            $("#stopsound").unbind().click(function () {
                $(".gm-layer-mask").fadeOut(20);
                $(".gm-layer-center").fadeOut(20);
                if (option.callBack) {
                    option.callBack();
                }
            });
        },
        getWidthFromWord: function (word, fsize) {
            var pattern = new RegExp("\\((.| )+?\\)", "igm");
            var arr = word.match(pattern) == null ? [] : word.match(pattern);
            var length = word.length - arr.length;
            if (length >= 8) {
                return 0;
            }
            return length * parseFloat(fsize) + 16;
        },
        validateData: function (data, isShowSuccess, callBack) {
            if (data.errcode != 0) {
                if (data.errcode === 1) {
                    lcommon.page.showDialog("错误：", data.errmsg, 2);
                } else if (data.errcode === 2) {
                    lcommon.page.showDialog("异常：", data.errmsg, 2);
                } else if (data.errcode === 6) { //代表返回的是字符串
                    if (callBack) {
                        callBack(data.errmsg);
                    }
                }
                return;
            } else {
                if (isShowSuccess === true) {
                    if (callBack) {
                        lcommon.page.showDialog("提示：", "成功", 2, callBack);
                    } else {
                        lcommon.page.showDialog("提示：", "成功", 2);
                    }

                } else {
                    if (callBack) {
                        callBack();
                    }
                }
            }
        },
        validateData2: function (data, isShowSuccess, sword, callBack) {
            sword = sword == null ? "成功" : sword;
            if (data.IsSuccess === false) {
                lcommon.page.showDialog("错误：", data.Msg, 2);
                return;
            } else {
                if (isShowSuccess === true) {
                    if (callBack) {
                        lcommon.page.showDialog("提示：", sword, 2, callBack);
                    } else {
                        lcommon.page.showDialog("提示：", sword, 2);
                    }

                } else {
                    if (callBack) {
                        callBack();
                    }
                }
            }
        },
        validateDataNew: function (rs, isShowPromptMsg, promptMsg, callBack) { //适合WebApi的BaseOut返回数据验证
            if (promptMsg === null || promptMsg === undefined || promptMsg.length < 1)
                promptMsg = "成功";
            if (rs.IsSuccess === false) {
                lcommon.page.showDialog("错误：", rs.Msg, 2);
            } else {
                if (isShowPromptMsg === true) {
                    lcommon.page.showDialog("提示：", promptMsg, 2, callBack);

                } else {
                    if (callBack) {
                        callBack();
                    }
                }
            }
        },
        validateTelPhone: function (number, callback) {
            var reg = /^1[34578]\d{9}$/;
            if (reg.test(number)) {
                if (callback)
                    callback();
            } else {
                this.showDialog("提示：", "号码有误,请输入正确电话号码", 2);
                return;
            }
        },
        AddTouchEvent: function (callback) {
            var result = [];
            var touchStartX, touchStartY, touchEndX, touchEndY;
            document.addEventListener("touchstart", function (e) {
                touchStartX = e.touches[0].pageX;
                touchStartY = e.touches[0].pageY;

            });
            document.addEventListener("touchend", function (e) {
                touchEndX = e.changedTouches[0].pageX;
                touchEndY = e.changedTouches[0].pageY;
                result.distanceY = touchEndY - touchStartY;
                result.distanceX = touchEndX - touchStartX;
                if (callback) {
                    callback(result);
                }
            });
        },
        GetAndroidUser: function () {
            if (checkInType == 1) {
                var user = JSON.parse(window.Android.getUser());
                user.wxUserId = user.userId;
                return user;
            } else {
                return { "userId": "56A90CA0-A14F-45FC-8280-A7A0935271D7", "userName": "领导通测试", "userType": 2, "userDept": "123" }
            }
        },
        androidBack: function (flag) { 
            if (window.Android) {
                window.Android.closeForm();
            } else {
                flag === 0 ? WeixinJSBridge.call('closeWindow') : window.history.go(-1);
            }
        },
        back: function () { 
            if (checkInType == 1) { 
                if (window.history.length > 1) {
                    window.history.go(-1);
                } else {
                    window.opener = null;
                    window.close();
                } 

            } else { 
                if (window.history.length > 1) {
                    window.history.go(-1);
                } else {
                    window.opener = null;
                    WeixinJSBridge.call('closeWindow');
                } 
            }
        },
        callPhone: function (phoneNumber) {
            if (window.Android) {
                window.Android.callPhone(phoneNumber);
                return false;
            }
            return true;
        },
        sendSms: function (phoneNumber, msg) {
            if (window.Android) {
                window.Android.sendSms(phoneNumber, msg);
                return false;
            }
            return true;
        },
        showQrCode: function (para) {
            var html =
                '<div class="qrcodePop-wrapper">' +
                    '<div class="qrcodePop-cover"></div>' +
                    '<div class="qrcodePop-box">' +
                    '<div class="qrcodePop-box-con">' +
                    '<p class ="title">' + para.Title + '</p>' +
                    '<p class ="notice">' + para.Notice + '</p>' +
                    '<div class="img-box">' +
                    '<img src="' + para.ImgUrl + '" alt="">' +
                    '</div>' +
                    '</div>' +
                    '<i class="icon-circle-close qrcodePop-closeBtn"></i>' +
                    '</div>' +
                    '</div>';
            $("body").append(html);
            setTimeout(function () {
                $(".qrcodePop-wrapper").addClass('active');
            }, 30);

            $(".qrcodePop-cover").off().on('click', function () {
                $(".qrcodePop-wrapper").removeClass('active');
                setTimeout(function () {
                    $(".qrcodePop-wrapper").remove();
                }, 500);
                if (para.callBack) {
                    para.callBack();
                }
            });

            $(".qrcodePop-closeBtn").off().on('click', function () {
                $(".qrcodePop-wrapper").removeClass('active');
                setTimeout(function () {
                    $(".qrcodePop-wrapper").remove();
                }, 500);
                if (para.callBack) {
                    para.callBack();
                }
            });
        }
    },
    record: {
        getItem: function (arrecord, key) {
            for (var i = 0; i < arrecord.length; i++) {
                if (arrecord[i].url == key) {
                    return arrecord[i];
                }
            }
            return null;
        },
        getCurrentUrl: function () {
            return location.href.replace("?isback=true", "").replace("&isback=true", "");
        },

        getSupUrl: function () {
            return document.referrer.replace("?isback=true", "").replace("&isback=true", "");
        },
        refPage: function (url) {
            url = url.indexOf("?") > -1 ? url + "&isback=true" : url + "?isback=true";
            window.location.href = url;
        }
    },
    tool: {
        joinPropertyValue: function (array, propertyName, joinChart) {
            joinChart = joinChart === undefined || joinChart === '' ? ',' : joinChart;
            var res = "";
            for (var i = 0; i < array.length; i++) {
                var obj = array[i];
                for (var pro in obj) {
                    if (pro === propertyName) {
                        res += obj[pro] + joinChart;
                    }
                }
            }
            if (res.length > 1)
                res = res.substring(0, res.length - 1);
            return res;
        },
        hasClass: function (obj, cls) {
            return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
        },
        addClass: function (obj, cls) {
            if (!this.hasClass(obj, cls)) obj.className += " " + cls;
        },
        removeClass: function (obj, cls) {
            if (this.hasClass(obj, cls)) {
                var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
                obj.className = obj.className.replace(reg, ' ');
            }
        },
        toggleClass: function (obj, cls) {
            if (this.hasClass(obj, cls)) {
                removeClass(obj, cls);
            } else {
                addClass(obj, cls);
            }
        },
        formatDate: function (date, addMonth) {
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var year = date.getFullYear();
            if (addMonth < 0 && Math.abs(addMonth) >= month) {
                addMonth += 12;
                year -= 1;
            }
            if (addMonth != undefined) {
                month += addMonth;
            }
            var strDate = date.getDate();

            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {

                strDate = "0" + strDate;

            }

            var currentdate = year + seperator1 + month + seperator1 + strDate

                    + " " + date.getHours() + seperator2 + date.getMinutes()

                    + seperator2 + date.getSeconds();

            return currentdate;
        },
        addDate: function (date, day) {
            var a = new Date(date);
            a = a.valueOf();
            a = a + day * 24 * 60 * 60 * 1000;
            a = new Date(a);
            return this.getDayFirstTime(a);
        },
        getDayFirstTime: function (date) {
            var seperator1 = "-";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                + " 00:00:00";
            return currentdate;
        },
        getAddMonth: function (dateStr) {
            var syear = parseInt(dateStr.substr(0, 4), 10);
            var smonth = parseInt(dateStr.substr(5, 2), 10);
            var date = new Date();
            var cyear = date.getFullYear();
            var cmonth = date.getMonth() + 1;
            return (syear - cyear) * 12 + (smonth - cmonth);
        },
        getWeekFirstDay: function (date) {
            var seperator1 = "-";
            var nowTime = date.getTime();
            var day = date.getDay();
            var oneDayLong = 24 * 60 * 60 * 1000;
            var monday = new Date(nowTime - (day - 1) * oneDayLong);
            var rmonth = monday.getMonth() + 1;
            var rday = monday.getDate();
            if (rmonth >= 1 && rmonth <= 9) {
                rmonth = "0" + rmonth;
            }
            if (rday >= 0 && rday <= 9) {
                rday = "0" + rday;
            }

            return monday.getFullYear() + seperator1 + rmonth + seperator1 + rday
                + " 00:00:00";
        },
        getMonthFirstDay: function (date, addMonth) {
            var seperator1 = "-";
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            if (addMonth < 0 && Math.abs(addMonth) >= month) {
                addMonth += 12;
                year -= 1;
            }
            if (addMonth != undefined) {
                month += addMonth;
            }
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }

            var currentdate = year + seperator1 + month + seperator1 + "01"
                + " 00:00:00";
            return currentdate;
        },
        getMonthLastDay: function (date, addMonth) {
            var seperator1 = "-";
            var cdate = new Date();
            var year = date.getFullYear();
            var month = date.getMonth() + 1;

            if (addMonth < 0 && Math.abs(addMonth) >= month) {
                addMonth += 12;
                year -= 1;
            }
            if (addMonth != undefined) {
                month += addMonth;
            }
            var d = new Date(year, month, 0);
            var day = d.getDate();
            if (cdate.getFullYear() === date.getFullYear() && (cdate.getMonth() + 1) === month) {
                day = cdate.getDate();
            }
            if (day >= 1 && day <= 9) {
                day = "0" + day;
            }
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }

            var currentdate = year + seperator1 + month + seperator1 + day
                + " 23:59:59";
            return currentdate;
        },
        getYearFirstDay: function (date) {
            return date.getFullYear() + "-01-01 00:00:00";
        },
        getUserTypeName: function (typdId) {
            return typdId === 0 ? "坐席员" : typdId === 1 ? "采集员" : "网格员";
        },
        getJsSdk: function () {
            if (_jssdk === undefined) { 
                if (checkInType == 1) {
                    _jssdk = new AppJssdk(); 
                }
                else {
                    _jssdk = new WeChatJssdk();
                }
            } 
            return _jssdk;
        },
        jsonArraySort: function (array, field, reverse) {
            //数组长度小于2 或 没有指定排序字段 或 不是json格式数据
            if (array.length < 2 || !field || typeof array[0] !== "object") return array;
            //数字类型排序
            if (typeof array[0][field] === "number") {
                array.sort(function (x, y) { return x[field] - y[field] });
            }
            //字符串类型排序
            if (typeof array[0][field] === "string") {
                array.sort(function (x, y) { return x[field].localeCompare(y[field]) });
            }
            //倒序
            if (reverse) {
                array.reverse();
            }
            return array; 
        },
        initAttachUrl: function (datas, filePath) {
            for (var i = 0; i < datas.length; i++) {
                var data = datas[i];
                for (var j = 0; j < data.AttachList.length; j++) {
                    var attach = data.AttachList[j];
                    if (attach.Type == 1) {
                        attach.Uri = attach.ThumbUrl = filePath + "mp3.png";
                    }
                }
            } 
        }
    },
    /*表单提交*/
    formsubmit: function (options) {
        var result = this.formvalidation();
        if (result == false) {
            return result;
        }
        options.postdata = this.getformVal();
        if (options.formdata != null) {
            options.postdata = $.extend({}, options.formdata, options.postdata || {});
        }
        options.callback(options.postdata);
    },
    /*获取表单值*/
    getformVal: function () {
        var objs = $("input,textarea,select", $("body"));
        var postdata = {};
        try {
            objs.each(function () {
                var o = $(this);
                if (o.attr("type") == "checkbox" && o.attr("name") != "") {
                    field = o.attr("name");
                    var chk_value = [];
                    $('input[name=' + field + ']:checked').each(function () {
                        chk_value.push($(this).val());
                    });
                    postdata[field] = chk_value.join(",");
                }
                else if (o.attr("type") == "radio" && o.attr("name") != "") {
                    field = o.attr("name");
                    postdata[field] = $("input[name='" + field + "']:checked").val();
                }
                else if (o.attr("type") == "text" || o.attr("type") == "select") {
                    field = o.attr("id");
                    postdata[field] = $("#" + field).val();
                } else if (o.attr("type") != "button" && o.attr("id") != "") {
                    field = o.attr("id");
                    postdata[field] = $("#" + field).val();
                } else if (o.attr("type") == "hidden") {
                    field = o.attr("id");
                    postdata[field] = $("#" + field).val();
                } else if (o.attr("type") == "select") {
                    field = o.attr("id");
                    postdata[field] = $("#" + field).val();
                }

            });

        } catch (e) {
            alert(e);
        }
        return postdata;
    },
    /*表单验证*/
    formvalidation: function () {
        var result = true;
        var ismast = $("input[ismast='1']");
        $(ismast).each(function (i, o) {
            if ($(o).val() == "") {
                $(o).focus();
                lcommon.page.showDialog("提示：", $(o).attr("placeholder"), 2);
                result = false;
                console.log("1.1" + $(o).attr("errtip"))
                return false;
            }
            else if ($(o).attr("match") == "mobile") {
                var pattern = /^1[34578]\d{9}$/;
                if (!pattern.test($(o).val())) {
                    $(o).focus();
                    lcommon.page.showDialog("提示：", $(o).attr("errtip"), 2);
                    result = false;
                    console.log("1" + $(o).attr("errtip"))
                    return false;
                }
            }
            else if ($(o).val() != "" && $(o).attr("match") == "toId") {

                var tId = $("#" + $(o).attr("tId")).val();
                //身份证
                if (tId == "1" && !checkCard($(o).val())) {
                    lcommon.page.showDialog("提示：", $(o).attr("errtip"), 2);
                    result = false;
                    console.log($(o).attr("errtip"))
                    return false;
                }
            }
        });

        if (result) {
            ismast = $("input[type='radio'][ismast='1']");
            $(ismast).each(function (i, o) {
                var val = $('input:radio[name=' + $(o).attr("name") + ']:checked').val();
                if (val == null) {
                    lcommon.page.showDialog("提示：", $(o).attr("tip"), 2);
                    result = false;
                    return false;
                }
            });
        }
        if (result) {
            ismast = $("select[ismast='1']");
            $(ismast).each(function (i, o) {
                if ($(o).val() == "") {
                    lcommon.page.showDialog("提示：", $(o).attr("placeholder"), 2);
                    result = false;
                    return false;
                }
            });
        }



        if (result) {
            ismast = $("input[ismatch='1']");
            $(ismast).each(function (i, o) {
                if ($(o).val() != "" && $(o).attr("match") == "mobile") {
                    var pattern = /^1[34578]\d{9}$/;
                    if (!pattern.test($(o).val())) {
                        $(o).focus();
                        lcommon.page.showDialog("提示：", $(o).attr("errtip"), 2);
                        result = false;
                        console.log("2" + $(o).attr("errtip"))
                        return false;
                    }
                }
                else if ($(o).val() != "" && $(o).attr("match") == "toId") {
                    var tId = $("#" + $(o).attr("tId")).val();
                    //身份证
                    if (tId == "1" && !checkCard($(o).val())) {
                        lcommon.page.showDialog("提示：", $(o).attr("errtip"), 2);
                        result = false;
                        console.log($(o).attr("errtip"))
                        return false;
                    }
                }
            });
        }

        return result;
    }
}



String.prototype.format = function(args) {
    var result = this;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if(args[key]!=undefined){
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    var reg = new RegExp("({[" + i + "]})", "g");//这个在索引大于9时会有问题，谢谢何以笙箫的指出

                    var reg= new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
};
     

Date.prototype.format = function () {
    var year = this.getFullYear(),
        month = this.getMonth() + 1,
        day = this.getDate(),
        hour = this.getHours(),
        min = this.getMinutes(),
        sec = this.getSeconds();
    var result = year + '-' +
        (month < 10 ? '0' + month : month) + '-' +
        (day < 10 ? '0' + day : day) + ' ' +
        (hour < 10 ? '0' + hour : hour) + ':' +
        (min < 10 ? '0' + min : min) + ':' +
        (sec < 10 ? '0' + sec : sec);

    return result;
};


$.fn.extend({
    initForm: function (options) {
        //默认参数  
        var defaults = {
            jsonValue: options,
            isDebug: true   //是否需要调试，这个用于开发阶段，发布阶段请将设置为false，默认为false,true将会把name value打印出来  
        }
        //设置参数  
        var setting = defaults;
        var form = this;
        jsonValue = setting.jsonValue;
        //如果传入的json字符串，将转为json对象  
        if ($.type(setting.jsonValue) === "string") {
            jsonValue = $.parseJSON(jsonValue);
        }
        //如果传入的json对象为空，则不做任何操作  
        if (!$.isEmptyObject(jsonValue)) {
            var debugInfo = "";
            $.each(jsonValue, function (key, value) {
                //是否开启调试，开启将会把name value打印出来  
                if (setting.isDebug) {
                    console.log("name:" + key + "; value:" + value);
                    debugInfo += "name:" + key + "; value:" + value + " || ";
                }
                var formField = form.find("[id='" + key + "']");
                if ($.type(formField[0]) === "undefined") {
                    if (setting.isDebug) {
                        console.log("can not find name:[" + key + "] in form!!!");    //没找到指定name的表单  
                    }
                } else {
                    var fieldTagName = formField[0].tagName.toLowerCase();
                    if (fieldTagName == "input") {
                        if (formField.attr("type") == "radio") {
                            $("input:radio[id='" + key + "'][value='" + value + "']").attr("checked", "checked");
                        } else {
                            formField.val(value);
                        }
                    } else if (fieldTagName == "select") {
                        //do something special  
                        formField.val(value);
                    } else if (fieldTagName == "textarea") {
                        //do something special  
                        formField.val(value);
                    } else {
                        formField.val(value);
                    }

                }
            })
            if (setting.isDebug) {
                console.log(debugInfo);
            }
        }
        return form;    //返回对象，提供链式操作  
    }
});


//身份证检验  
var vcity = {
    11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古",
    21: "辽宁", 22: "吉林", 23: "黑龙江", 31: "上海", 32: "江苏",
    33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南",
    42: "湖北", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆",
    51: "四川", 52: "贵州", 53: "云南", 54: "西藏", 61: "陕西", 62: "甘肃",
    63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外"
};

function checkCard(card) {
    //是否为空  
    if (!card || !isCardNo(card) || !checkProvince(card) || !checkBirthday(card) || !checkParity(card)) {
        return false;
    }
    return true;
};


//检查号码是否符合规范，包括长度，类型  
function isCardNo(card) {
    //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  
    var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
    if (reg.test(card) === false) {
        return false;
    }
    return true;
};

//取身份证前两位,校验省份  
function checkProvince(card) {
    var province = card.substr(0, 2);
    if (vcity[province] == undefined) {
        return false;
    }
    return true;
};

//检查生日是否正确  
function checkBirthday(card) {
    var len = card.length;
    //身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字  
    if (len == '15') {
        var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
        var arr_data = card.match(re_fifteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date('19' + year + '/' + month + '/' + day);
        return verifyBirthday('19' + year, month, day, birthday);
    }
    //身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X  
    if (len == '18') {
        var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
        var arr_data = card.match(re_eighteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date(year + '/' + month + '/' + day);
        return verifyBirthday(year, month, day, birthday);
    }
    return false;
};

//校验日期  
function verifyBirthday(year, month, day, birthday) {
    var now = new Date();
    var now_year = now.getFullYear();
    //年月日是否合理  
    if (birthday.getFullYear() == year && (birthday.getMonth() + 1) == month && birthday.getDate() == day) {
        //判断年份的范围（3岁到100岁之间)  
        var time = now_year - year;
        if (time >= 3 && time <= 100) {
            return true;
        }
        return false;
    }
    return false;
};

//校验位的检测  
function checkParity(card) {
    //15位转18位  
    card = changeFivteenToEighteen(card);
    var len = card.length;
    if (len == '18') {
        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
        var cardTemp = 0, i, valnum;
        for (i = 0; i < 17; i++) {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        valnum = arrCh[cardTemp % 11];
        if (valnum == card.substr(17, 1)) {
            return true;
        }
        return false;
    }
    return false;
};

//15位转18位身份证号  
function changeFivteenToEighteen(card) {
    if (card.length == '15') {
        var arrInt = new array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        var arrCh = new array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
        var cardTemp = 0, i;
        card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
        for (i = 0; i < 17; i++) {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        card += arrCh[cardTemp % 11];
        return card;
    }
    return card;
};
