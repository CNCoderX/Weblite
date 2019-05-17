
function LocalFile(hashcode) {
    this.hashcode = hashcode;
    app.addObject(hashcode, this);
}

LocalFile.prototype.getName = function () {
    return window.android.File_getName(this.hashcode);
};

LocalFile.prototype.getParent = function () {
    return window.android.File_getParent(this.hashcode);
};

LocalFile.prototype.getPath = function () {
    return window.android.File_getPath(this.hashcode);
};

LocalFile.prototype.canRead = function () {
    return window.android.File_canRead(this.hashcode);
};

LocalFile.prototype.canWrite = function () {
    return window.android.File_canWrite(this.hashcode);
};

LocalFile.prototype.exists = function () {
    return window.android.File_exists(this.hashcode);
};

LocalFile.prototype.isDirectory = function () {
    return window.android.File_isDirectory(this.hashcode);
};

LocalFile.prototype.isFile = function () {
    return window.android.File_isFile(this.hashcode);
};

LocalFile.prototype.isHidden = function () {
    return window.android.File_isHidden(this.hashcode);
};

LocalFile.prototype.lastModified = function () {
    return window.android.File_lastModified(this.hashcode);
};

LocalFile.prototype.length = function () {
    return window.android.File_length(this.hashcode);
};

LocalFile.prototype.createNewFile = function () {
    return window.android.File_createNewFile(this.hashcode);
};

LocalFile.prototype.delete = function () {
    return window.android.File_delete(this.hashcode);
};

LocalFile.prototype.deleteOnExit = function () {
    window.android.File_deleteOnExit(this.hashcode);
};

LocalFile.prototype.list = function () {
    return window.android.File_list(this.hashcode);
};

LocalFile.prototype.mkdir = function () {
    return window.android.File_mkdir(this.hashcode);
};

LocalFile.prototype.mkdirs = function () {
    return window.android.File_mkdirs(this.hashcode);
};

LocalFile.prototype.renameTo = function (name) {
    return window.android.File_renameTo(this.hashcode, name);
};

LocalFile.prototype.setReadOnly = function () {
    return window.android.File_setReadOnly(this.hashcode);
};

LocalFile.prototype.setWritable = function (writable) {
    return window.android.File_setWritable(this.hashcode, writable);
};

LocalFile.prototype.setReadable = function (readable) {
    return window.android.File_setReadable(this.hashcode, readable);
};

LocalFile.prototype.setExecutable = function (executable) {
    return window.android.File_setExecutable(this.hashcode, executable);
};

LocalFile.prototype.canExecute = function () {
    return window.android.File_canExecute(this.hashcode);
};

LocalFile.prototype.getTotalSpace = function () {
    return window.android.File_getTotalSpace(this.hashcode);
};

LocalFile.prototype.getFreeSpace = function () {
    return window.android.File_getFreeSpace(this.hashcode);
};

LocalFile.prototype.getUsableSpace = function () {
    return window.android.File_getUsableSpace(this.hashcode);
};

LocalFile.prototype.release = function () {
    app.removeObject(this.hashcode);
    return window.android.File_release(this.hashcode);
};