package com.quick.controller;

import com.quick.img2txt.Img2TxtService;
import com.quick.util.DataReturn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: wangxc
 * @GitHub: https://github.com/vector4wang
 * @CSDN: http://blog.csdn.net/qqhjqs?viewmode=contents
 * @BLOG: http://vector4wang.tk
 * @wxid: BMHJQS
 */
@Controller
public class Img2TxtController {



    private Logger logger = LogManager.getLogger(Img2TxtController.class);

    @Resource
    private Img2TxtService img2TxtService;

    @RequestMapping(value = "/img2txt",method = RequestMethod.GET)
    public String toPage(){
        return "/imgUpload";
    }

    @RequestMapping(value = "/img2img",method = RequestMethod.GET)
    public String toImagePage(){
        return "/img2img";
    }

    @RequestMapping(value = "/transfer/{type}",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<InputStreamResource> imt2txt(@RequestParam("file") MultipartFile file,@PathVariable String type){
        try {
            String originalFilename = file.getOriginalFilename();
            HttpHeaders headers = new HttpHeaders();

            // 支持jpg、png
            if(originalFilename.toLowerCase().endsWith("jpg")||originalFilename.toLowerCase().endsWith("png")||originalFilename.toLowerCase().endsWith("jpeg")){
                File outFile = img2TxtService.save(file.getBytes(), originalFilename,type);
                System.out.println("Path="+outFile.toURI().getPath());
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", outFile.getName()));
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");
                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentLength(outFile.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(new InputStreamResource(new FileInputStream(outFile)));
            }else{
                File error = new File(img2TxtService.getErrorPath());
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", error.getName()));
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");
                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentLength(error.length())
                        .contentType(MediaType.parseMediaType("application/octet-stream"))
                        .body(new InputStreamResource(new FileInputStream(error)));
            }
        } catch (IOException e) {
            logger.error(e);
            return new ResponseEntity("暂不支持的文件格式",HttpStatus.BAD_REQUEST);
        }
//        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/img2img/{type}",method = RequestMethod.POST)
    @ResponseBody
    public DataReturn imt2img(@RequestParam("file") MultipartFile file, @PathVariable String type){
        try {
            String originalFilename = file.getOriginalFilename();
            // 支持jpg、png
            if(originalFilename.toLowerCase().endsWith("jpg")||originalFilename.toLowerCase().endsWith("png")||originalFilename.toLowerCase().endsWith("jpeg")){
                File outFile = img2TxtService.save(file.getBytes(), originalFilename,type);
                return DataReturn.success("ok", outFile.getName());
            }else{
                return DataReturn.error("不支持的格式");
            }
        } catch (IOException e) {
            logger.error(e);
            return DataReturn.error("不支持的格式");
        }
    }
}
