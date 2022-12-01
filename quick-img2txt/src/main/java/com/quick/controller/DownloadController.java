package com.quick.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import java.io.*;

import org.slf4j.Logger;
/**
 * FTP Created by zsx on 2019/8/20
 */
@Controller
public class DownloadController {
	private final static Logger logger = LoggerFactory.getLogger(DownloadController.class);
	@Value("${upload.file.path}")
	private String filePath ;



	@RequestMapping(value = "downloadFile",method = RequestMethod.GET)
	public void downloadLocal(HttpServletResponse response, @RequestParam(value = "fileName", defaultValue = "0")  String fileName) throws FileNotFoundException {
		//http://127.0.0.1:8001/downloadFile?fileName=11112232.jpeg
		if (null==fileName||"".equals(fileName)) {
			logger.error("文件路径为空");
			return;
		}
		String file=fileName.substring(fileName.lastIndexOf("/")+1);
		// 下载本地文件
		fileName = String.format("%s%s", filePath,  fileName);
		// 设置输出的格式
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
		// 循环取出流中的数据
		byte[] b = new byte[100];
		int len;
		try (InputStream inStream = new FileInputStream(fileName)){
			while ((len = inStream.read(b)) > 0){
				response.getOutputStream().write(b, 0, len);
			}
		} catch (IOException e) {
			logger.error(String.format("下载文件失败-%s:", fileName),e);
		}
	}

}
