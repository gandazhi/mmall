package com.mmall.util;

import com.google.gson.Gson;
import com.mmall.common.Const;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QiniuUtil {

    public static final String accessKey = "qIB7-LHKTXakYD_Es_IFHJRX1JqTd_bvr37eyrPU";
    public static final String secretKey = "VrUflpV1tWizAIxbD61PfFIxaHz-duTcNfOKJ2M2";
    public static final String bucket = "java";

    private static final Logger logger = LoggerFactory.getLogger(QiniuUtil.class);

    public static boolean uploadForQiniu(int zone, String localPath, String key){
        Configuration cfg;
        switch (zone){
            case Const.Zone.EAST_CHINA:
                cfg = new Configuration(Zone.zone0());
                break;
            case Const.Zone.NORTH_CHINA:
                cfg = new Configuration(Zone.zone1());
                break;
            case Const.Zone.SOUTH_CHINA:
                cfg = new Configuration(Zone.zone2());
                break;
            case Const.Zone.NORTH_AMERICA:
                cfg = new Configuration(Zone.zoneNa0());
                break;
            default:
                return false;
        }
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(localPath, key ,upToken);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            logger.info("七牛云的key:"+putRet.key);
            logger.info("七牛云的hash:"+putRet.hash);
            return true;
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error("上传七牛云错误", r);
            return false;
        }
    }
}
