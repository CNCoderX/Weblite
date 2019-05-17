
/*
    App js Sdk
*/

var voice = { localId: '', serverId: '' };



var AppJssdk = function (para){
    var defaultSett = { isDebug: true }
    var sett = $.extend(defaultSett, para);


    return {
        //删除暂存案件
        deleteData(id){
            window.Android.deleteData(id);
        },
        //案件列表
        GetTemporaryCase(){
            if (sett.isDebug == true) {
            var jsonArr=[];
            var json={"checkInType":1,"WxUserId":"oK5iLt5_Ii9fPukldjNDLu-ldbwI","Phone":"18664571187","Description":"8787868","Address":"湖南省长沙市岳麓区南园路234号","Bdx":112.91892659246,"Bdy":28.205390719551,"ImageIds":["683E7C6E-A816-4A3E-9378-1C581C86F1EF"],"VoiceIds":["3232313"],"cTime":"2017-09-28 11:10:28","Id":"a0c7ff40-4133-c898-a96a-59379e08fefa","voiceIdsPath":[{"path":"dasda","recordTimeLength":"0.859"}]};
            json.ImageIdsBig=[];
            json.ImageIdsFmpId=[];
            $(json.ImageIds).each(function(i,o){
                json.ImageIds[i]=ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + o + "&w=80&h=120";
                json.ImageIdsBig.push(ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + o);
                json.ImageIdsFmpId.push(o);
            });
            jsonArr.push(json);
            return jsonArr;
        }

        var aData= window.Android.serachData();
        
        if(typeof(aData)=="undefined"){
            return [];
        }

        
        $(aData).each(function(i,json){
            aData[i]=JSON.parse(json);
            json=aData[i];
            json.ImageIdsBig=[];
            json.ImageIdsFmpId=[];
            $(json.ImageIds).each(function(i,o){
                json.ImageIds[i]=ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + o + "&w=80&h=120";
                json.ImageIdsBig.push(ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + o);
                json.ImageIdsFmpId.push(o);
            });
        })
        
        return aData;

        },
        //暂存案件
        TemporaryCase:function(options){
            console.log(JSON.stringify(options));
            window.Android.insetOrReviseData(JSON.stringify(options));
        },
        //获取定位
        getLocation: function (options) {
            
            if (sett.isDebug == true) {
                if (options.callback)
                    options.callback({ longitude: 112.907097, latitude: 28.202614 });
                return;
            }
            
            lcommon.page.showLoad();
            app.getBDLocation({
                success: function (location) {
                    
                    lcommon.page.hideLoad();

                    if (location) {
                        
                        //location = JSON.parse(location);
                        var longitude = location.longitude;
                        var latitude = location.latitude;
                        
                    }
                    if (options.callback) {
                        options.callback(location);
                    }


                    
                },
                failure: function (errMsg) {
                    alert(errMsg);
                }
            });



        },
        initPortalUser: function (callbaks) { 
            if (sett.isDebug === true) {
                //userInfo = { wxUserId: "oK5iLt5_Ii9fPukldjNDLu-ldbwI", userId: "AD2938A8-04C6-46BD-BF44-775EE4804BB8", userName: "执法局领导", userType: 0 }
                userInfo = { wxUserId: "oK5iLt5_Ii9fPukldjNDLu-ldbwI", userId: "56A90CA0-A14F-45FC-8280-A7A0935271D7", userName: "系统管理员", userType: 0 }
                if (callbaks) {
                    callbaks();
                }
                return;
            }

            userInfo = app.getUserInfo();//this.GetUserInfo();// lcommon.page.GetAndroidUser();
            //userInfo.userName = userInfo.UserName;
            //userInfo.userDeptName = userInfo.UserDeptName;
            //userInfo.userId = userInfo.UserId;
            //userInfo.userType = userInfo.UserType;
            //userInfo.userDept = userInfo.UserDept;
            

            if (callbaks) {
                callbaks();
            }

        },
        initPagePower: function (callbaks, callbakf) {
            if (callbaks)
                callbaks();
            if (callbakf)
                callbakf();
        },
        wxstartrecord: function (callbaks, callbakf) {
            if (callbaks) {
                callbaks();//成功开始录音;
            }
            if (sett.isDebug == true) {

                return;
            }
            window.Android.mediaRecordStart();            
        },
        wxstoprecord: function (callbaks, callbakf) {
            if (sett.isDebug == true) {
                if (callbaks) {
                    callbaks({ serverId: "3232313",path:"dasda" });
                }
                return;
            }
            window.Android.mediaRecordEnd("endRecord");
            window.endRecord = function (data) {
                data = JSON.parse(data);
                if (callbaks) {
                    voice.localId = data.path;
                    voice.path = data.path;
                    callbaks({ serverId: data.sFileID,path:data.path });
                }
            }
        },
        wxplayvoice: function (callbaks, callbakf) {
            if (voice.localId == '') {
                if (callbakf) {
                    callbakf('请先使用 startRecord 接口录制一段声音');
                }
                return;
            }
            window.Android.recordPlay(voice.path, "EndrecordPlay");
            window.EndrecordPlay = function (data) {
                if (callbaks) {
                    callbaks();
                }
            }
        },

        htmlplay: function (callbaks, callbakf) {

            if (voice.path != null && voice.path.length > 0) {
                // var audio = document.createElement("audio");
                // audio.src = voice.path;
                // audio.play();
                $.commonFn.PlaySound();

                return;
            }

            if (voice.localId == '') {
                if (callbakf) {
                    callbakf('请先使用 startRecord 接口录制一段声音');
                }
                return;
            }
            wx.playVoice({
                localId: voice.localId
            });


            wx.onVoicePlayEnd({
                complete: function (res) {
                    if (callbaks) {
                        callbaks();
                    }
                }
            });

        },
        wxvoiceplayend: function (callbacks, callbakf) {
            wx.onVoicePlayEnd({
                complete: function (res) {
                    if (callbacks) {
                        callbacks();
                    }
                }
            });
        },
        wxuploadvoice: function (callbaks, callbakf) {
          
        },
        wxdownloadvoice: function (options, callbaks, callbakf) {
            if (options.serverId == '') {
                if (callbakf) {
                    callbakf('获取serverId失败');
                }
                return;
            }
            wx.downloadVoice({
                serverId: options.serverId,
                success: function (res) {jl 
                    voice.localId = res.localId; 
                    if (callbaks) {
                        callbaks();
                    }
                }
            });
        },
        wxtranslatevoice: function (callbaks, callbakf) {
            window.Android.translatevoice(voice.localId,"translatevoice");
            window.translatevoice = function (data) {
                data = JSON.parse(data);
                callbaks(data.Speech);
            }

        },
        wxhidemenuitems: function (callbaks, callbakf) {
           
        },
        console: function (info) {
            if (window.console) {
                console.log(info);
            }
        },
        /*选择图片*/
        wxchooseimage: function (callbaks) {
            if (sett.isDebug == true) {
                //模拟调用
                var testUrl = "http://xv17156957.iok.la/WebApi/Handlers/ThumbImageHandler.aspx?FID=683E7C6E-A816-4A3E-9378-1C581C86F1EF&w=80&h=120";
                var BigtestUrl = "http://xv17156957.iok.la/WebApi/Handlers/ThumbImageHandler.aspx?FID=683E7C6E-A816-4A3E-9378-1C581C86F1EF";
                if (callbaks)
                    callbaks(["683E7C6E-A816-4A3E-9378-1C581C86F1EF"], [testUrl], [BigtestUrl],new Object());
                return;
            }
            // lcommon.page.showLoad();
            // app.chooseImages({
            //     maxWidth: 1024,
            //     maxHeight: 1024,
            //     maxSize:9,
            //     success: function (scaledImages, sourceImages) {
            //         scaledImages.forEach(function (sourceImage, index) {
            //             //alert("scaled-image-" + index + ":" + sourceImage);
            //
            //             var fileId = new GUID().newGUID().toLowerCase();
            //
            //             var file = app.getFileInfo({
            //                 path: sourceImage
            //             });
            //             file.type = sourceImage.substr(sourceImage.lastIndexOf('.'));
            //             file.id = fileId;
            //
            //             var url = ajaxProxy.ip + "/WebPdaService/Handlers/upload.ashx?fn={0}&sfn={1}&pos={2}&tlen={3}&sdt={4}&pictypessp={5}";
            //             url = url.format(file.name, fileId, 0, file.length, new Date().format(), 99);
            //
            //             app.upload({
            //                 url: url,
            //                 path: sourceImage,
            //                 success: function (code, headers, data) {
            //
            //                     var smallUrl = ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + fileId + "&w=80&h=120";
            //                     var BigtestUrl = ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + fileId;
            //
            //                     callbaks([fileId], [smallUrl], [BigtestUrl], file);
            //                     lcommon.page.hideLoad();
            //                 },
            //                 failure: function (errMsg) {
            //                     alert(errMsg);
            //                 }
            //             })
            //
            //
            //         });
            //
            //
            //
            //     },
            //     failure: function (err) {
            //         lcommon.page.hideLoad();
            //     }
            // })

            var uploadFile = function(imagePath) {
                var file = app.getFileInfo({
                    path: imagePath
                });
                file.type = imagePath.substr(imagePath.lastIndexOf('.'));
                file.id = new GUID().newGUID().toLowerCase();

                var url = ajaxProxy.ip + "/WebPdaService/Handlers/upload.ashx?fn={0}&sfn={1}&pos={2}&tlen={3}&sdt={4}&pictypessp={5}";
                url = url.format(file.name, file.id, 0, file.length, new Date().format(), 99);

                lcommon.page.showLoad();

                app.upload({
                    url: url,
                    path: imagePath,
                    success: function (code, headers, data) {
                        var smallUrl = ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + file.id + "&w=1024&h=1024";
                        var BigtestUrl = ajaxProxy.baseaddress + "/Handlers/ThumbImageHandler.aspx?FID=" + file.id;

                        callbaks([file.id], [smallUrl], [BigtestUrl], file);
                        lcommon.page.hideLoad();
                    },
                    failure: function (errMsg) {
                        lcommon.page.hideLoad();
                    }
                })
            };

            lcommon.page.pickImage(function () {
                app.pickImage({
                    source: 'camera',
                    maxWidth: 1024,
                    maxHeight: 1024,
                    success: function (scaled, source) {
                        uploadFile(scaled);
                    },
                });
            }, function () {
                app.chooseImages({
                    maxSize: 9,
                    maxWidth: 1024,
                    maxHeight: 1024,
                    success: function (scaled, source) {
                        scaled.forEach(function (path) {
                            uploadFile(path);
                        })
                    }
                })
            })
        },
        wxuploadimage: function (localid, callbacks) {
            
        },
        wxuploadimages: function (localids, serverIds) {
            if (localids.length === 0) {
                return;
            } else {
                for (var i = 0; i < localids.length; i++) {
                    serverIds.push(localids[i]);
                }
            }
        },
        wxdownloadimage: function (serverid, callbacks) {
            wx.downloadImage({
                serverId: serverid, // 需要下载的图片的服务器端ID，由uploadImage接口获得
                isShowProgressTips: 1, // 默认为1，显示进度提示
                success: function (res) {
                    if (callbacks)
                        callbacks(res.localId);// 返回图片下载后的本地ID
                }
            });

        },
        wxpreviewimage: function (currentUrl, urls,oimgSrc) {
            if (sett.isDebug == true) {
                window.open(oimgSrc);
                return;
            }
            if (oimgSrc != null) {
                window.Android.preView(oimgSrc);
            }
        },
        /*获取地图位置*/
        wxgetLocation: function (callback) {
            this.getLocation({callback:callback});
        },
        wxopenLocation: function (para) {
            wx.openLocation({
                latitude: para.latitude, // 纬度，浮点数，范围为90 ~ -90 
                longitude: para.longitude, // 经度，浮点数，范围为180 ~ -180。 
                name: para.name, // 位置名 
                address: para.address, // 地址详情说明 
                scale: 1, // 地图缩放级别,整形值,范围从1~28。默认为最大 
                infoUrl: para.infoUrl // 在查看位置界面底部显示的超链接,可点击跳转 
            });
        },
        wxgetnetworktype: function () {
            wx.getNetworkType({
                success: function (res) {
                    var networkType = res.networkType; // 返回网络类型2g，3g，4g，wifi
                }
            });
        },
        /*微信图片上传*/
        wxpcUpload: function (options) {
            var pdom = this;           
            this.wxchooseimage(function (localIds, localFmpurl, bigFmpUrl, file) { 
                var liCount = $(options.uploaderFilesitem).length;
                var height = (parseInt(((liCount + localIds.length + 1) / 4)) + 1) * 100;
                var chooseImageHtml = "";
                var preserverIds = $(options.addImage).data("PreserverIds");
                if (typeof (preserverIds) == "undefined") {
                    preserverIds = [];
                    $(options.addImage).data("PreserverIds", []);
                }

                for (var i = 0; i < localIds.length; i++) {
                    var localid = localIds[i];
                    chooseImageHtml = $("#uploaderFilesTemp").html();
                    
                    chooseImageHtml = chooseImageHtml.replace("{{src}}", localFmpurl[i]).replace("{{src}}", bigFmpUrl[i]);
                    $(options.uploaderFiles).append(chooseImageHtml);
                    preserverIds.push(bigFmpUrl[i]);
                }
                var files=$(options.addImage).data("fileInfo");
                if (files) {
                    files.push(file);
                    $(options.addImage).data("fileInfo", files);
                }
                else
                {
                    $(options.addImage).data("fileInfo", [file]);
                }               


                $(options.addImage).data("PreserverIds", preserverIds);
                

                $(".operate-upimage").height(height);

                $(options.addImage).data("serverIds", preserverIds);
                

                //删除图片
                var voiceFiles = $(options.closeimg);
                for (var i = 0; i < voiceFiles.length; i++) {
                    (function (i) {
                        var voiceFile = voiceFiles[i];

                      

                        $(voiceFile).unbind("click").click(function (event) {
                            var $this = $(this).parent();
                            var imageIdsLs = $(options.addImage).data("serverIds");
                            //alert($this.index());
                            //alert(JSON.stringify(imageIdsLs));
                            imageIdsLs.splice($this.index() - 2, 1);
                            //alert(JSON.stringify(imageIdsLs));
                            $(options.addImage).data("serverIds", imageIdsLs);
                            var files = $(options.addImage).data("fileInfo");
                            files.splice($this.index() - 1, 1);
                            
                            $(options.addImage).data("fileInfo", files);

                            $this.removeClass("animate-zoom-show").addClass("animate-zoom-hide");
                            setTimeout(function () {
                                $this.remove();
                            }, 500);
                            
                        })

                        //voiceFile.addEventListener("click", function (event) {
                            
                        //});
                    })(i)
                }
            });
        },
        /*图片预览*/
        extwxpreviewimage: function (obj) {
            var urls = [];
            var $this = $(obj);
            var imgSrc = $this.attr("osrc");

            
            
            //app.previewImage({ url: imgSrc });
            //return;
            var preData = $(".upload-photo-btn").data("PreserverIds");

            $(preData).each(function (i, o) {
                urls.push(o);
            });
            
            app.previewImages({ current: $(obj).parent().index(), urls: urls });
            //this.wxpreviewimage(imgSrc, urls,imgSrc);
        },
        /*图片直接预览*/
        previewimage: function (imgSrc, imgArr, current) {
            if (imgArr != null && imgArr.length>0) {
                app.previewImages({ current: current, urls: imgArr });
            } else {
                app.previewImages({ current: 0, urls: [imgSrc] });
            }
            //app.previewImage({ url: imgSrc });
        },
        /*图片预览*/
        extwxpreviewimagenew: function (obj) {
            var urls = [];
            var $this = $(obj);
            var imgSrc = $this.data("osrc");
            var attach = $this.data("urls");
            this.wxpreviewimage(imgSrc, attach,imgSrc);
        },
        //文件打印
        printDocument: function (fileUrl) {
            lcommon.page.showLoad();
            
            //app.download({
            //    url: fileUrl,
            //    success: function (code, headers, path) {
                    
            //        lcommon.page.hideLoad();

            //        app.printDocument({
            //            name: 'name',
            //            path: path
            //        });
            //    },
            //    failure: function (errMsg) {
            //        alert("下载文件报错" + errMsg + fileUrl);
            //    }
            //}); 

            app.download({
                url: fileUrl,
                success: function (code, headers, path) {

                    lcommon.page.hideLoad();

                    app.canonPrint({
                        url: path
                    });
                },
                failure: function (errMsg) {
                    lcommon.page.hideLoad();
                    alert("下载文件报错" + errMsg + fileUrl);
                }
            });  
          
        }
        //设置用户信息
        , SetLogin: function (data) {
            var curuser = app.createPreferences("userModel");
            curuser.set("userJson",data);
            curuser.release();
        },
        //获取用户信息
        GetUserInfo: function () {
            var curuser = app.createPreferences("userModel");
            try {
                if (typeof (curuser.get("userJson")) == "undefined") {
                    return null;
                } else {
                    return JSON.parse(curuser.get("userJson"));
                }
            } finally {
                curuser.release();
            }
        }
    }
}