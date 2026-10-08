package com.zzsd.inolibi.controller;

import com.zzsd.inolibi.utils.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@Tag(name = "临时测试")
public class TempExcelController {
    @PostMapping(value = "/temp/excel-to-csv", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "excel 转 csv（临时）")
    public String excelToCsv(@RequestPart("file") MultipartFile file) {
        String string = ExcelUtils.excelToCsv(file);
        return string;
    }
}