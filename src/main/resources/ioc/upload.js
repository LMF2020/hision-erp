var ioc={
    tmpFilePool : {
        type : 'org.nutz.filepool.NutFilePool',
        // 临时文件最大个数为 100 个
        args : [ "~/localFileStorage/upload/tmp", 100 ]
    },
    uploadFileContext : {
        type : 'org.nutz.mvc.upload.UploadingContext',
        singleton : false,
        args : [ { refer : 'tmpFilePool' } ],
        fields : {
            // 是否忽略空文件, 默认为 false
            ignoreNull : true,
            // 单个文件最大尺寸(大约的值，单位为字节，即 20971520 为 20M)
            maxFileSize : 20971520,
            // 正则表达式匹配可以支持的文件名
            nameFilter : '^(.+[.])(xls|xlsx)$'
        }
    },
    fileUpload : {
        type : 'org.nutz.mvc.upload.UploadAdaptor',
        singleton : false,
        args : [ { refer : 'uploadFileContext' } ]
    }
};