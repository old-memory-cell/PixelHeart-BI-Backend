package com.zzsd.inolibi.controller;

import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.utils.AliyunOSSOperator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像上传 controller
 */
@Slf4j
@RestController
@RequestMapping("upload")
@Tag(name = "图像上传")
public class UploadController {

    @Resource
    private AliyunOSSOperator aliyunOSSOperator;

    /**
     * 文件上传
     * @param file 文件
     * @return 上传结果的返回路径
     * @throws Exception 异常
     */
    @PostMapping("/upload")
    @Operation(summary = "图像上传")
    public Result<String> uplode(@RequestPart("file") MultipartFile file) throws Exception {
        log.info("文件上传开始:{}",file);
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传后的url：{}",url);
        return Result.success(url);
    }

}
