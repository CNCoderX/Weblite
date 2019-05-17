/*jshint esversion: 6 */
"use strict";

const app = (function () {

    function invokeNativeFunc(_interface, _function) {
        var android = window[_interface];
        if (arguments.length > 2) {
            var args = Array.from(arguments).slice(2);
            return android[_function].apply(android, args);
        } else {
            return android[_function].apply(android);
        }
    }

    var callbacks = new Map();

    window['invokeCallback'] = function (pointer) {
        var callback = callbacks.get(pointer);
        if (callback) {
            if (arguments.length > 1) {
                var args = Array.from(arguments).slice(1);
                callback.apply(app, args);
            } else {
                callback.apply(app);
            }
        }
    };

    window['removeCallback'] = function (pointer) {
        callbacks.delete(pointer);
    };

    function callback(_function) {
        if (_function) {
            var pointer = invokeNativeFunc('android_js_func', 'createJsFunction');
            if (pointer !== -1) {
                callbacks.set(pointer, _function);
            }
            return pointer;
        }
        return -1;
    }

    function App() {
    }

    (function (_interface) {
        /**
         * 预览图片
         * @param url {string} 图片链接或图片的本地路径
         */
        App.prototype.previewImage = function ({url = null}) {
            invokeNativeFunc(_interface, "previewImage", url);
        };

        /**
         * 预览多张图片
         * @param current {number} 当前图片的序号
         * @param urls {string[]} 图片链接或图片的本地路径
         */
        App.prototype.previewImages = function ({current = 0, urls = null}) {
            if (urls) {
                urls = JSON.stringify(urls);
            }
            invokeNativeFunc(_interface, "previewImages", current, urls);
        };

        /**
         * 从本地相册选择图片或使用相机拍照
         * @param source {string} 图片来源，有效值 gallery/camera
         * @param maxWidth {number} 输出图片的最大宽度
         * @param maxHeight {number} 输出图片的最大高度
         * @param success {function} 成功则返回图片的本地文件路径
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.pickImage = function ({source = 'gallery', maxWidth = 0, maxHeight = 0, success = null, failure = null}) {
            var options = {};
            if (source) {
                options.source = source;
            }
            if (maxWidth) {
                options.maxWidth = maxWidth;
            }
            if (maxHeight) {
                options.maxHeight = maxHeight;
            }
            invokeNativeFunc(_interface, "pickImage", JSON.stringify(options), callback(success), callback(failure));
        };

        /**
         * 从本地相册选择图片，支持多张
         * @param maxSize {int} 图片的数量
         * @param maxWidth {number} 输出图片的最大宽度
         * @param maxHeight {number} 输出图片的最大高度
         * @param success {function} 成功则返回图片的本地文件路径
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.chooseImages = function ({maxSize = 1, maxWidth = 0, maxHeight = 0, success = null, failure = null}) {
            var options = {};
            if (maxSize) {
                options.maxSize = maxSize;
            }
            if (maxWidth) {
                options.maxWidth = maxWidth;
            }
            if (maxHeight) {
                options.maxHeight = maxHeight;
            }
            var _success = null;
            if (success) {
                _success = function (scaledImages, sourceImages) {
                    var _scaledImages = JSON.parse(scaledImages);
                    var _sourceImages = JSON.parse(sourceImages);
                    success(_scaledImages, _sourceImages);
                };
            }
            invokeNativeFunc(_interface, "chooseImages", JSON.stringify(options), callback(_success), callback(failure));
        };


        /**
         * 录音器
         * @param pointer
         * @constructor
         */
        function AudioRecorder(pointer) {
            this.pointer = pointer;
        }

        /**
         * 准备录音
         */
        AudioRecorder.prototype.prepare = function () {
            invokeNativeFunc(_interface, "AudioRecorder_prepare", this.pointer);
        };

        /**
         * 开始录音
         */
        AudioRecorder.prototype.start = function () {
            invokeNativeFunc(_interface, "AudioRecorder_start", this.pointer);
        };

        /**
         * 继续录音
         */
        AudioRecorder.prototype.resume = function () {
            invokeNativeFunc(_interface, "AudioRecorder_resume", this.pointer);
        };

        /**
         * 暂停录音
         */
        AudioRecorder.prototype.pause = function () {
            invokeNativeFunc(_interface, "AudioRecorder_pause", this.pointer);
        };

        /**
         * 结束录音
         */
        AudioRecorder.prototype.stop = function () {
            invokeNativeFunc(_interface, "AudioRecorder_stop", this.pointer);
        };

        /**
         * 获取录音器状态码
         * @returns {number}
         */
        AudioRecorder.prototype.getState = function () {
            return invokeNativeFunc(_interface, "AudioRecorder_getState", this.pointer);
        };

        /**
         * 获取录音文件路径
         * @returns {string}
         */
        AudioRecorder.prototype.getAudioPath = function () {
            return invokeNativeFunc(_interface, "AudioRecorder_getPath", this.pointer);
        };

        /**
         * 释放对象
         */
        AudioRecorder.prototype.release = function () {
            invokeNativeFunc(_interface, "AudioRecorder_release", this.pointer);
        };

        /**
         * 创建录音器实例
         * @param format {string} 音频格式，有效值 wav/mp3
         * @param sampleRate {number} 采样率，有效值 8000/16000/44100
         * @param channels {number} 录音通道数，有效值 1/2
         * @param bitPerSample {number} 每个样本的位数，有效值 8/16
         * @param error {function}
         * @returns {AudioRecorder}
         */
        App.prototype.createAudioRecorder = function ({
                                                          format = 'mp3',
                                                          sampleRate = 44100,
                                                          channels = 1,
                                                          bitPerSample = 16,
                                                          error
                                                      }) {
            var options = {};
            options.sampleRate = sampleRate;
            options.channels = channels;
            options.bitPerSample = bitPerSample;
            var pointer = invokeNativeFunc(_interface, "AudioRecorder_create", format, JSON.stringify(options), callback(error));
            if (pointer === -1) {
                return null;
            } else {
                return new AudioRecorder(pointer);
            }
        };

        /**
         * 音频播放器
         * @param pointer
         * @constructor
         */
        function AudioPlayer(pointer) {
            this.pointer = pointer;
        }

        /**
         * 准备播放
         * @param url {string} 音频链接或音频本地路径
         */
        AudioPlayer.prototype.prepare = function ({url = null}) {
            invokeNativeFunc(_interface, "AudioPlayer_prepare", this.pointer, url);
        };

        /**
         * 开始播放
         */
        AudioPlayer.prototype.start = function () {
            invokeNativeFunc(_interface, "AudioPlayer_start", this.pointer);
        };

        /**
         * 继续播放
         */
        AudioPlayer.prototype.resume = function () {
            invokeNativeFunc(_interface, "AudioPlayer_resume", this.pointer);
        };

        /**
         * 暂停播放
         */
        AudioPlayer.prototype.pause = function () {
            invokeNativeFunc(_interface, "AudioPlayer_pause", this.pointer);
        };

        /**
         * 停止播放
         */
        AudioPlayer.prototype.stop = function () {
            invokeNativeFunc(_interface, "AudioPlayer_stop", this.pointer);
        };

        /**
         * 跳转到指定位置
         * @param msec {number} 单位ms
         */
        AudioPlayer.prototype.seekTo = function (msec = 0) {
            invokeNativeFunc(_interface, "AudioPlayer_seekTo", this.pointer, msec);
        };

        /**
         * 重置播放器，回到IDLE状态
         */
        AudioPlayer.prototype.reset = function () {
            invokeNativeFunc(_interface, "AudioPlayer_reset", this.pointer);
        };

        /**
         * 获取播放器状态码
         * @returns {number}
         */
        AudioPlayer.prototype.getState = function () {
            return invokeNativeFunc(_interface, "AudioPlayer_getState", this.pointer);
        };

        /**
         * 获取音频时长，单位ms
         * @returns {number}
         */
        AudioPlayer.prototype.getDuration = function () {
            return invokeNativeFunc(_interface, "AudioPlayer_getDuration", this.pointer);
        };

        /**
         * 获取当前播放的位置，单位ms
         * @returns {number}
         */
        AudioPlayer.prototype.getPosition = function () {
            return invokeNativeFunc(_interface, "AudioPlayer_getPosition", this.pointer);
        };

        /**
         * 释放对象
         */
        AudioPlayer.prototype.release = function () {
            invokeNativeFunc(_interface, "AudioPlayer_release", this.pointer);
        };

        /**
         * 创建音频播放器实例，支持aac/mp3/ogg/pcm格式
         * @param loop {boolean} 是否循环播放，默认 false
         * @param volume {number} 音量，范围 0~1
         * @param complete {function} 播放完成的回调函数
         * @param error {function} 播放出错的回调函数
         * @returns {AudioPlayer}
         */
        App.prototype.createAudioPlayer = function ({loop = false, volume = 1, complete = null, error = null}) {
            var options = {};
            options.loop = loop;
            options.volume = volume;
            var pointer = invokeNativeFunc(_interface, "AudioPlayer_create", JSON.stringify(options), callback(complete), callback(error));
            if (pointer === -1) {
                return null;
            } else {
                return new AudioPlayer(pointer);
            }
        };

        /**
         * 播放音效
         * @param path {string} 音频本地路径
         */
        App.prototype.playSound = function ({path = null}) {
            invokeNativeFunc(_interface, "playSound", path);
        };

        // /**
        //  * 音效播放器
        //  * @param pointer
        //  * @constructor
        //  */
        // function SoundPlayer(pointer) {
        //     this.pointer = pointer;
        // }
        //
        // /**
        //  * 准备播放
        //  * @param path {string} 音频本地路径
        //  */
        // SoundPlayer.prototype.prepare = function ({path = null}) {
        //     invokeNativeFunc(_interface, "SoundPlayer_prepare", this.pointer, path);
        // };
        //
        // /**
        //  * 开始播放
        //  */
        // SoundPlayer.prototype.start = function () {
        //     invokeNativeFunc(_interface, "SoundPlayer_start", this.pointer);
        // };
        //
        // /**
        //  * 继续播放
        //  */
        // SoundPlayer.prototype.resume = function () {
        //     invokeNativeFunc(_interface, "SoundPlayer_resume", this.pointer);
        // };
        //
        // /**
        //  * 暂停播放
        //  */
        // SoundPlayer.prototype.pause = function () {
        //     invokeNativeFunc(_interface, "SoundPlayer_pause", this.pointer);
        // };
        //
        // /**
        //  * 停止播放
        //  */
        // SoundPlayer.prototype.stop = function () {
        //     invokeNativeFunc(_interface, "SoundPlayer_stop", this.pointer);
        // };
        //
        // /**
        //  * 释放对象
        //  */
        // SoundPlayer.prototype.release = function () {
        //     invokeNativeFunc(_interface, "SoundPlayer_release", this.pointer);
        // };
        //
        // /**
        //  * 创建音效播放器实例，支持aac/mp3/ogg/pcm格式
        //  * @param quality {number} 采样率转换器质量
        //  * @param volume {number} 音量，范围 0~1
        //  * @param error {function} 播放出错的回调函数
        //  * @returns {SoundPlayer}
        //  */
        // App.prototype.createSoundPlayer = function ({quality = 0, volume = 1, error = null}) {
        //     var options = {};
        //     options.quality = quality;
        //     options.volume = volume;
        //     var pointer = invokeNativeFunc(_interface, "SoundPlayer_create", JSON.stringify(options), callback(error));
        //     if (pointer === -1) {
        //         return null;
        //     } else {
        //         return new SoundPlayer(pointer);
        //     }
        // };

        /**
         * 播放视频
         * @param url {string} 视频链接或视频的本地路径
         */
        App.prototype.previewVideo = function ({url = null}) {
            invokeNativeFunc(_interface, "previewVideo", url);
        };

        /**
         * 从本地相册选择视频或使用相机拍摄
         * @param source {string} 视频来源，有效值 gallery/camera
         * @param success {function} 成功则返回视频的本地文件路径
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.pickVideo = function ({source = 'gallery', success = null, failure = null}) {
            invokeNativeFunc(_interface, "pickVideo", source, callback(success), callback(failure));
        };
    })("android_media");

    (function (_interface) {
        /**
         * 获取文件信息
         * @param path {string} 本地文件路径
         * @returns {FileInfo}
         */
        App.prototype.getFileInfo = function ({path = null}) {
            var fileInfo = invokeNativeFunc(_interface, "getFileInfo", path);
            if (fileInfo) {
                return JSON.parse(fileInfo);
            }
            return null;
        };

        function FileInfo() {
            this.name = null;
            this.parent = null;
            this.path = null;
            this.isDirectory = null;
            this.lastModified = null;
            this.length = null;
            this.access = null;
        }

        /**
         * 选择文件
         * @param accept 可接受的文件类型
         * @param success {function} 成功则返回本地文件路径
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.chooseFile = function ({accept = "*/*", success = null, failure = null}) {
            invokeNativeFunc(_interface, "chooseFile", accept, callback(success), callback(failure));
        };

        /**
         * 文件或目录是否存在
         * @param path {string} 本地文件或目录的路径
         * @returns {boolean}
         */
        App.prototype.fileExists = function ({path = null}) {
            return invokeNativeFunc(_interface, "fileExists", path);
        };

        /**
         * 创建新文件
         * @param path {string} 新文件的路径
         * @returns {Object}
         */
        App.prototype.createFile = function ({path = null}) {
            var fileInfo = invokeNativeFunc(_interface, "createFile", path);
            if (fileInfo) {
                return JSON.parse(fileInfo);
            }
            return null;
        };

        /**
         * 创建新文件夹
         * @param path {string} 新文件夹的路径
         * @returns {Object}
         */
        App.prototype.createDirectory = function ({path = null}) {
            var fileInfo = invokeNativeFunc(_interface, "createDirectory", path);
            if (fileInfo) {
                return JSON.parse(fileInfo);
            }
            return null;
        };

        /**
         * 删除文件
         * @param path {string} 本地文件的路径
         * @returns {boolean}
         */
        App.prototype.deleteFile = function ({path = null}) {
            return invokeNativeFunc(_interface, "deleteFile", path);
        };

        /**
         * 删除文件夹
         * @param path {string} 本地文件夹的路径
         * @returns {boolean}
         */
        App.prototype.deleteDirectory = function ({path = null}) {
            return invokeNativeFunc(_interface, "deleteDirectory", path);
        };

        /**
         * 列出文件夹下所有文件
         * @param path {string} 本地文件夹的路径
         * @returns {Array}
         */
        App.prototype.listFiles = function ({path = null}) {
            var files = invokeNativeFunc(_interface, "listFiles", path);
            if (files) {
                return JSON.parse(files);
            }
            return null;
        };

        /**
         * 重命名文件或目录
         * @param path {string} 本地文件或目录的路径
         * @param newName {string} 新文件名
         * @returns {boolean}
         */
        App.prototype.renameFile = function ({path = null, newName = null}) {
            return invokeNativeFunc(_interface, "renameFile", path, newName);
        };

        /**
         * 复制文件到指定位置
         * @param src {string} 源文件路径
         * @param dest {string} 目标文件路径
         * @param success {function} 复制成功回调函数
         * @param failure {function} 复制失败回调函数
         */
        App.prototype.copyFile = function ({src = null, dest = null, success = null, failure = null}) {
            invokeNativeFunc(_interface, "copyFile", src, dest, callback(success), callback(failure));
        };

        /**
         * 往本地文件中写入文本内容
         * @param path {string} 本地文件的路径
         * @param text {string} 写入的文本内容
         * @param append {boolean} 是否在文件尾部追加
         * @param success {function} 写入成功回调函数
         * @param failure {function} 写入失败回调函数
         */
        App.prototype.writeStringToFile = function ({path = null, text = null, append = false, success = null, failure = null}) {
            var _text = encodeURIComponent(text);
            invokeNativeFunc(_interface, "writeStringToFile", path, _text, append, callback(success), callback(failure));
        };

        /**
         * 从本地文件中读取文本内容
         * @param path {string} 本地文件的路径
         * @param offset {number} 读取内容的偏移量
         * @param length {number} 读取内容的长度
         * @param success {function} 读取成功回调函数
         * @param failure {function} 读取失败回调函数
         */
        App.prototype.readStringFromFile = function ({path = null, offset = 0, length = 1024, success = null, failure = null}) {
            var _success = null;
            if (success) {
                _success = function(text) {
                    success(decodeURIComponent(text));
                };
            }
            invokeNativeFunc(_interface, "readStringFromFile", path, offset, length, callback(_success), callback(failure));
        };

        /**
         * '/data/data/<application package>/cache/'
         * @returns {string}
         */
        App.prototype.getCacheDir = function () {
            return invokeNativeFunc(_interface, "getCacheDir");
        };

        /**
         * '/data/data/<application package>/files/'
         * @returns {string}
         */
        App.prototype.getFilesDir = function () {
            return invokeNativeFunc(_interface, "getFilesDir");
        };

        /**
         * '<sdcard root>/Android/data/<application package>/cache/'
         * @returns {string}
         */
        App.prototype.getExternalCacheDir = function () {
            return invokeNativeFunc(_interface, "getExternalCacheDir");
        };

        /**
         * '<sdcard root>/Android/data/<application package>/files/<type>/'
         * @param type {string}
         * @returns {string}
         */
        App.prototype.getExternalFilesDir = function ({type = null}) {
            return invokeNativeFunc(_interface, "getExternalFilesDir", type);
        };

        /**
         * '<sdcard root>/<type>/'
         * @param type {string}
         * @returns {string}
         */
        App.prototype.getExternalStorageDir = function ({type = null}) {
            return invokeNativeFunc(_interface, "getExternalStorageDir", type);
        };

        function Preferences(pointer) {
            this.pointer = pointer;
        }

        /**
         * 判断key是否存在
         * @param key {string} 键
         * @returns {boolean}
         */
        Preferences.prototype.contains = function (key = null) {
            return invokeNativeFunc(_interface, "Preferences_contains", this.pointer, key);
        };

        /**
         * 通过key获取value
         * @param key {string} 键
         * @returns {string}
         */
        Preferences.prototype.get = function (key = null) {
            return invokeNativeFunc(_interface, "Preferences_get", this.pointer, key);
        };

        /**
         * 设置key-value
         * @param key {string} 键
         * @param value {string} 值
         * @returns {boolean}
         */
        Preferences.prototype.set = function (key = null, value = null) {
            return invokeNativeFunc(_interface, "Preferences_set", this.pointer, key, value);
        };

        /**
         * 移除key
         * @param key {string} 键
         * @returns {boolean}
         */
        Preferences.prototype.remove = function (key = null) {
            return invokeNativeFunc(_interface, "Preferences_remove", this.pointer, key);
        };

        /**
         * 清除
         * @returns {boolean}
         */
        Preferences.prototype.clear = function () {
            return invokeNativeFunc(_interface, "Preferences_clear", this.pointer);
        };

        /**
         * 释放对象
         */
        Preferences.prototype.release = function () {
            invokeNativeFunc(_interface, "Preferences_release", this.pointer);
        };

        /**
         * 创建Preferences实例
         * @param name pref名字
         * @returns {Preferences}
         */
        App.prototype.createPreferences = function (name = null) {
            var pointer = invokeNativeFunc(_interface, "Preferences_create", name);
            if (pointer === -1) {
                return null;
            } else {
                return new Preferences(pointer);
            }
        };
    })("android_file");

    (function (_interface) {
        /**
         * 发起网络请求
         * @param url {string} 请求地址
         * @param method {string} 默认 GET，有效值 GET/HEAD/POST/PUT/DELETE/PATCH
         * @param data {Object} 请求的参数
         * @param headers {Object} 请求的header
         * @param timeout {number} 超时时长
         * @param success {function} 成功响应的回调函数
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.request = function ({
                                              url = null, method = 'GET', data = null, headers = null, timeout = 0, success = null, failure = null
                                          }) {
            if (data) {
                data = JSON.stringify(data);
            }
            if (headers) {
                headers = JSON.stringify(headers);
            }
            invokeNativeFunc(_interface, "request", url, method, data, headers, timeout, callback(success), callback(failure));
        };

        /**
         * 上传资源
         * @param url {string} 上传资源的 url
         * @param path {string} 要上传文件资源的路径
         * @param data {Object} 请求的参数
         * @param headers {Object} 请求的header
         * @param timeout {number} 超时时长
         * @param success {function} 成功响应的回调函数
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.upload = function ({
                                             url = null, path = null, data = null, headers = null, timeout = 0, success = null, failure = null
                                         }) {
            if (data) {
                data = JSON.stringify(data);
            }
            if (headers) {
                headers = JSON.stringify(headers);
            }
            invokeNativeFunc(_interface, "upload", url, path, data, headers, timeout, callback(success), callback(failure));
        };

        /**
         * 下载资源
         * @param url 下载资源的 url
         * @param dir 下载目录
         * @param headers {Object} 请求的header
         * @param timeout {number} 超时时长
         * @param success {function} 成功响应的回调函数
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.download = function ({
                                               url = null, dir = null, headers = null, timeout = 0, success = null, failure = null
                                           }) {
            if (headers) {
                headers = JSON.stringify(headers);
            }
            invokeNativeFunc(_interface, "download", url, dir, headers, timeout, callback(success), callback(failure));
        };
    })("android_net");

    (function (_interface) {
        /**
         * 获取设备信息
         * @returns {DeviceInfo}
         */
        App.prototype.getDeviceInfo = function () {
            var deviceInfo = invokeNativeFunc(_interface, "getDeviceInfo");
            if (deviceInfo) {
                return JSON.parse(deviceInfo);
            }
            return null;
        };

        function DeviceInfo() {
            /**
             * @type {DeviceVersion}
             */
            this.version = null;
            this.board = null;
            this.bootloader = null;
            this.brand = null;
            this.device = null;
            this.display = null;
            this.fingerprint = null;
            this.hardware = null;
            this.host = null;
            this.id = null;
            this.manufacturer = null;
            this.model = null;
            this.product = null;
            this.tags = null;
            this.type = null;
            this.isEmulator = false;
        }

        function DeviceVersion() {
            this.baseOS = null;
            this.previewSdkInt = 0;
            this.securityPatch = null;
            this.codename = null;
            this.incremental = null;
            this.release = null;
            this.sdkInt = 0;
        }

        /**
         * 获取设备的IMEI号
         * @returns {string}
         */
        App.prototype.getIMEI = function () {
            return invokeNativeFunc(_interface, "getIMEI");
        };

        /**
         * 获取当前的网络类型
         * @returns {number}
         */
        App.prototype.getNetworkType = function () {
            return invokeNativeFunc(_interface, "getNetworkType");
        };

        /**
         * 获取当前的位置
         * @param success {function} 成功则返回位置信息
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.getLocation = function ({success = null, failure = null}) {
            var _success = null;
            if (success) {
                _success = function (location) {
                    var _location = JSON.parse(location);
                    success(_location);
                };
            }
            invokeNativeFunc(_interface, "getLocation", callback(_success), callback(failure));
        };

        /**
         * 拨打电话
         * @param phoneNumber {number} 电话号码
         */
        App.prototype.makePhoneCall = function ({phoneNumber = null}) {
            invokeNativeFunc(_interface, "makePhoneCall", phoneNumber);
        };

        /**
         * 发送短信
         * @param phoneNumber 电话号码
         * @param message 短信内容
         */
        App.prototype.makeSMS = function ({phoneNumber = null, message = null}) {
            invokeNativeFunc(_interface, "makeSMS", phoneNumber, message);
        };

        /**
         * 设置剪切板内容
         * @param data {string} 剪切板内容
         */
        App.prototype.setClipboardData = function ({data = null}) {
            invokeNativeFunc(_interface, "setClipboardData", data);
        };

        /**
         * 获取剪切板内容
         * @returns {string} 剪切板内容
         */
        App.prototype.getClipboardData = function () {
            return invokeNativeFunc(_interface, "getClipboardData");
        };

        /**
         * 截屏
         * @param success {function} 接口调用成功的回调函数
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.captureScreen = function ({success = null, failure = null}) {
            invokeNativeFunc(_interface, "captureScreen", callback(success), callback(failure));
        };

        /**
         * 振动
         * @param duration {number} 振动的时长，单位 ms
         */
        App.prototype.vibrate = function ({duration = 15}) {
            invokeNativeFunc(_interface, "vibrate", duration);
        };

        /**
         * 打印PDF文档
         * @param name 文档的名字
         * @param path 文档的本地路径
         */
        App.prototype.printDocument = function ({name = 'untitle', path = null}) {
            invokeNativeFunc(_interface, "printDocument", name, path);
        };

        /**
         * 打印图片
         * @param name 图片的名字
         * @param path 图片的本地路径
         */
        App.prototype.printPicture = function ({name = 'untitle', path = null}) {
            invokeNativeFunc(_interface, "printPicture", name, path);
        };

        /**
         * 扫码
         * @param type 扫码类型，有效值 BAR_CODE/QR_CODE/DATA_MATRIX/PDF_417
         * @param success {function} 成功则返回扫码结果
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.scanCode = function ({type = 'QR_CODE', success = null, failure = null}) {
            invokeNativeFunc(_interface, "scanCode", type, callback(success), callback(failure));
        };

        /**
         * 开启Wi-Fi
         * @returns {boolean}
         */
        App.prototype.openWifi = function () {
            return invokeNativeFunc(_interface, "openWifi");
        };

        /**
         * 关闭Wi-Fi
         * @returns {*}
         */
        App.prototype.closeWifi = function () {
            return invokeNativeFunc(_interface, "closeWifi");
        };

        /**
         * 获取Wi-Fi列表
         * @returns {WifiInfo[]}
         */
        App.prototype.getWifiList = function () {
            var result = invokeNativeFunc(_interface, "getWifiList");
            if (result) {
                return JSON.parse(result);
            }
            return null;
        };

        /**
         * 连接Wi-Fi
         * @param SSID {string} Wi-Fi设备的ssid
         * @param password {string} Wi-Fi设备的密码
         * @param cipherType {string} Wi-Fi的加密方式， 有效值 wpa/wep/none
         * @returns {boolean}
         */
        App.prototype.connectWifi = function ({SSID = null, password = null, cipherType = 'wpa'}) {
            return invokeNativeFunc(_interface, "connectWifi", SSID, password, cipherType);
        };

        /**
         * 获取已连接中的Wi-Fi信息
         * @returns {WifiInfo}
         */
        App.prototype.getConnectedWifi = function () {
            var result = invokeNativeFunc(_interface, "getConnectedWifi");
            if (result) {
                return JSON.parse(result);
            }
            return null;
        };
        
        function WifiInfo() {
            this.SSID = null;
            this.BSSID = null;
            this.signalLevel = 0;
            this.cipherType = null;
        }
    })("android_device");

    (function (_interface) {
        /**
         * 获取应用包信息
         * @returns {PkgInfo}
         */
        App.prototype.getPackageInfo = function () {
            var pkgInfo = invokeNativeFunc(_interface, "getPackageInfo");
            if (pkgInfo) {
                return JSON.parse(pkgInfo);
            }
            return null;
        };

        function PkgInfo() {
            this.appName = null;
            this.pkgName = null;
            this.versionCode = 0;
            this.versionName = null;
        }

        /**
         * 安装应用
         * @param path {string} apk文件的本地路径
         */
        App.prototype.installApp = function ({path = null}) {
            invokeNativeFunc(_interface, "installApp", path);
        };

        /**
         * 卸载应用
         * @param packageName {string} 应用的包名
         */
        App.prototype.uninstallApp = function ({packageName = null}) {
            invokeNativeFunc(_interface, "uninstallApp", packageName);
        };

        /**
         * 检测应用是否安装
         * @param packageName {string} 应用的包名
         * @returns {boolean}
         */
        App.prototype.appInstalled = function ({packageName = null}) {
            return invokeNativeFunc(_interface, "appInstalled", packageName);
        };

        /**
         * 启动应用
         * @param packageName {string} 应用的包名
         * @param className {string} 启动页的类名
         * @param data {IntentData[]} 需要传递的数据
         * @returns {boolean}
         */
        App.prototype.launchApp = function ({packageName = null, className = null, data = null}) {
            if (data) {
                data = JSON.stringify(data);
            }
            return invokeNativeFunc(_interface, "launchApp", packageName, className, data);
        };

        function IntentData() {
            this.key = null;
            this.value = null;
            this.type = null;
        }
    })("android_app");
    
    (function (_interface) {

        /**
         * 显示toast
         * @param text {string} toast文本
         */
        App.prototype.showToast = function ({text = null}) {
            invokeNativeFunc(_interface, "showToast", text);
        };

        /**
         * 显示通知栏
         * @param id {number} 通知栏ID
         * @param contentTitle {string} 通知栏标题
         * @param contentText {string} 通知栏显示内容
         * @param ticker {string} 通知在第一次到达时在状态栏中显示的文本
         * @param priority {number} 通知优先级
         * @param number {number} 通知集合的数量
         * @param autoCancel {boolean} 当用户点击面板是否让通知自动取消
         * @param ongoing {boolean} 是否为一个正在进行的通知
         * @param smallIcon {string} 通知的小图标，本地文件路径
         * @param largeIcon {string} 通知的大图标，本地文件路径
         * @param category {string} 通知类别
         * @param contentInfo {string} 通知的右侧设置大文本
         * @param sound {string} 通知提醒铃声，本地文件路径
         * @param vibrate {number[]} 使用震动模式
         * @param onlyAlertOnce {boolean} 设置仅提醒一次
         * @param navigateTo {string} 点击通知栏后跳转的html页面
         */
        App.prototype.showNotification = function (
            {
                id = 1,
                contentTitle,
                contentText,
                ticker,
                priority,
                number,
                autoCancel,
                ongoing,
                smallIcon,
                largeIcon,
                category,
                contentInfo,
                sound,
                vibrate,
                onlyAlertOnce,
                navigateTo
            }) {
            var options = {};
            options.id = id;
            if (contentTitle) {
                options.contentTitle = contentTitle;
            }
            if (contentText) {
                options.contentText = contentText;
            }
            if (ticker) {
                options.ticker = ticker;
            }
            if (priority) {
                options.priority = priority;
            }
            if (number) {
                options.number = number;
            }
            if (autoCancel) {
                options.autoCancel = autoCancel;
            }
            if (ongoing) {
                options.ongoing = ongoing;
            }
            if (smallIcon) {
                options.smallIcon = smallIcon;
            }
            if (largeIcon) {
                options.largeIcon = largeIcon;
            }
            if (category) {
                options.category = category;
            }
            if (contentInfo) {
                options.contentInfo = contentInfo;
            }
            if (sound) {
                options.sound = sound;
            }
            if (vibrate) {
                options.vibrate = vibrate;
            }
            if (onlyAlertOnce) {
                options.onlyAlertOnce = onlyAlertOnce;
            }
            if (navigateTo) {
                options.navigateTo = navigateTo;
            }
            options = JSON.stringify(options);
            invokeNativeFunc(_interface, "showNotification", options);
        };

        /**
         * 移除通知栏
         * @param id {number} 通知栏ID
         */
        App.prototype.removeNotification = function ({id = 1}) {
            invokeNativeFunc(_interface, "removeNotification", id);
        };
    })("android_ui");

    (function (_interface) {

        /**
         * 退出应用
         */
        App.prototype.exit = function () {
            invokeNativeFunc(_interface, "exit");
        };

        /**
         * 获取服务器地址
         * @returns {string}
         */
        App.prototype.getHost = function () {
            return invokeNativeFunc(_interface, "getHost");
        };

        /**
         * 获取登录用户的信息
         * @returns {UserInfo}
         */
        App.prototype.getUserInfo = function () {
            var userInfo = invokeNativeFunc(_interface, "getUserInfo");
            if (userInfo) {
                return JSON.parse(userInfo);
            }
            return null;
        };

        function UserInfo() {
            this.userId = null;
            this.userName = null;
            this.mobileNum = null;
            this.userDept = null;
            this.userDeptName = null;
            this.fId = 0;
            this.roleIds = null;
            this.userType = 0;
            this.loginMsg = null;
        }

        /**
         * 百度定位
         * @param success {function} 成功则返回位置信息
         * @param failure {function} 接口调用失败的回调函数
         */
        App.prototype.getBDLocation = function ({success = null, failure = null}) {
            var _success = null;
            if (success) {
                _success = function (location) {
                    var _location = JSON.parse(location);
                    success(_location);
                };
            }
            invokeNativeFunc(_interface, "getBDLocation", callback(_success), callback(failure));
        };

        /**
         * 调用佳能打印服务
         * @param url 文档的链接或本地路径
         * @returns {boolean}
         */
        App.prototype.canonPrint = function ({url = null}) {
            return invokeNativeFunc(_interface, "canonPrint", url);
        };

        /**
         * 注册消息推送Handler
         * @param handler {function} 接收到消息的回调函数
         * @returns {number}
         */
        App.prototype.registerUPushHandler = function (handler = null) {
            return invokeNativeFunc(_interface, "registerUPushHandler", callback(handler));
        };

        /**
         * 注销消息推送Handler
         * @param handler {number}
         */
        App.prototype.unregisterUPushHandler = function (handler = -1) {
            invokeNativeFunc(_interface, "unregisterUPushHandler", handler);
        };

    })("android_extra");

    return new App();
})();











