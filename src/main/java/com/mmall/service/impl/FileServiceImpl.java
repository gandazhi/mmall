package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.service.IFileService;
import com.mmall.util.QiniuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path, String userId) {

        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = new Date().getTime() + "_" + userId + "." + fileExtensionName;
        logger.info("开始上传文件，上传的文件名{}，上传路径{}，新文件名{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            String localPath = path + "/" + uploadFileName;

            //将上传的文件放到七牛云中
            boolean upload = QiniuUtil.uploadForQiniu(Const.Zone.EAST_CHINA, localPath, uploadFileName);
            if (!upload){
                return "error";
            }
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传图片失败", e);
            return null;
        }

        return targetFile.getName();
    }
}
