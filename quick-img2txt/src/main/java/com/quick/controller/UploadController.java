package com.quick.controller;

import com.quick.util.DataReturn;
import com.quick.util.FileUtil;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 上传文件
 *
 * @author xujf
 */
@Controller
public class UploadController {
    private final static Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Value("${upload.file.path}")
    private String filePath;
    private static List<String> whitelist = new ArrayList<>();// 文件类型白名单

    static {
        // 文件类型白名单
        whitelist.add("jpg");
        whitelist.add("jpeg");
        whitelist.add("png");
    }

    /**
     * 文件批量上传（重命名）
     *
     * @param request
     * @param pro
     * @return
     * @@author xujf
     */
    @RequestMapping(value = "upload")
    @ResponseBody
    public DataReturn upload(HttpServletRequest request, String pro, @RequestParam(name = "fileSuffix", required = false) String fileSuffix) {
        int beginTime = (int) System.currentTimeMillis();
        // 上传目录
        String realPath = filePath;
        int fileTotalCnt = 0;// 上传的文件个数
        int fileCnt = 0;// 上传成功的文件个数
        Date nowDate = new Date();
        String saveFolder = "/" + pro + "/" + FastDateFormat.getInstance("yyyyMM").format(nowDate) + "/";
        FileUtil.isExistDir(realPath + saveFolder);

        List<String> fileList = new ArrayList<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        String newFileName;
        Random random = new Random();
        String extName;
        while (fileNames.hasNext()) {
            fileTotalCnt++;
            MultipartFile file = multipartRequest.getFile(fileNames.next());
            String fileName = file.getOriginalFilename();
            // 后缀
            if (fileName.lastIndexOf(".") != -1) {// 有后缀
                extName = fileName.substring(fileName.lastIndexOf("."));
                // 检验后缀
                if (!whitelist.contains(extName.substring(1).toLowerCase())) {
                    continue;
                }
            } else {// 无后缀
                extName = whitelist.contains(fileSuffix) ? "." + fileSuffix : "";
            }
            newFileName = FastDateFormat.getInstance("yyyyMMddHHmmssSSS").format(nowDate) + random.nextInt(1000) + extName;
            try {
                FileCopyUtils.copy(file.getBytes(), new File(realPath + saveFolder + newFileName));
                fileList.add(saveFolder + newFileName);
                fileCnt++;
            } catch (IOException e) {
                logger.error("文件上传失败：" + fileName);
                e.printStackTrace();
            }
        }
        String msg = getMsg(beginTime, fileCnt, fileTotalCnt);
        return DataReturn.success(msg, fileList);
    }

    private String getMsg(int beginTime, int fileCnt, int fileTotalCnt) {
        int endTime = (int) System.currentTimeMillis();
        logger.info("上传耗时：" + (endTime - beginTime));
        // 自定义返回描述
        String msg = "上传文件成功";
        if (fileCnt == 0) {
            msg = "上传文件失败";
        } else if (fileCnt != fileTotalCnt) {
            msg = "上传文件部分成功";
        }
        logger.info(msg);
        return msg;
    }

    /**
     * 文件批量上传
     * 根据文件名生成文件夹
     *
     * @param request
     * @param pro
     * @return
     * @@author zsx
     */
    @RequestMapping(value = "uploadForWirelessUpdate")
    @ResponseBody
    public DataReturn uploadForWirelessUpdate(HttpServletRequest request, String pro) {

        String saveFolder = "/" + pro + "/";
        int beginTime = (int) System.currentTimeMillis();

        // 上传目录
        String realPath = filePath;
        int fileTotalCnt = 0;// 上传的文件个数
        int fileCnt = 0;// 上传成功的文件个数
        List<String> fileList = new ArrayList<>();// 成功上传的文件列表
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> fileNames = multipartRequest.getFileNames();
        MultipartFile file;
        String fileName, extName;
        String folder = "";
        String originName = "";
        while (fileNames.hasNext()) {
            fileTotalCnt++;
            file = multipartRequest.getFile(fileNames.next());
            fileName = file.getOriginalFilename();
            // 后缀
            if (fileName.lastIndexOf(".") != -1) {// 有后缀
                extName = fileName.substring(fileName.lastIndexOf("."));
                // 检验后缀
                if (!whitelist.contains(extName.substring(1).toLowerCase())) {
                    continue;
                }
            } else {// 无后缀
                extName = "";
            }
            //根据文件名分文件夹
            if (fileName.indexOf("-") != -1) {
                //文件夹名
                folder = fileName.substring(0, fileName.lastIndexOf("-"));
            } else if (fileName.indexOf("_") != -1) {
                //文件夹名
                folder = fileName.substring(0, fileName.lastIndexOf("_"));
            }
            if (null!=folder) {
                saveFolder = saveFolder + folder + "/";
            }
            if (fileName.indexOf(".") != -1) {
                //无后缀的文件名
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            originName = fileName;
            FileUtil.isExistDir(realPath + saveFolder);
            // 上传文件
            try {
                //不带后缀
                //文件夹名
                if (fileName.indexOf("-") != -1) {
                    fileName = fileName.substring(fileName.lastIndexOf("-") + 1);
                } else if (fileName.indexOf("_") != -1) {
                    fileName = fileName.substring(fileName.lastIndexOf("_") + 1);
                }
                File fileIn = new File(realPath + saveFolder + fileName);
                FileCopyUtils.copy(file.getBytes(), fileIn);
                //带后缀
                File fileOut = new File(realPath + saveFolder + fileName + extName);
                FileCopyUtils.copy(fileIn, fileOut);
                fileList.add(originName);
                fileCnt++;
            } catch (IOException e) {
                logger.error("文件上传失败：" + fileName);
                e.printStackTrace();
            }
        }
        String msg = getMsg(beginTime, fileCnt, fileTotalCnt);
        return DataReturn.success(msg, fileList);
    }
}
