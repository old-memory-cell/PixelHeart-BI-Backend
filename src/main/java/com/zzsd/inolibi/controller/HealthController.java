package com.zzsd.inolibi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 */
@RestController
@Tag(name = "健康检查" , description = "健康检查")
public class HealthController {


    /**
     * 健康检查
     * @return ok
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public String health(){
        return "ok";
    }
}
