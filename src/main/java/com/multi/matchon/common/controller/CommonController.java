package com.multi.matchon.common.controller;

import com.multi.matchon.common.domain.BoardType;
import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.SportsTypeDto;
import com.multi.matchon.common.service.CommonService;
import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.internal.resource.S3Resource;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommonController {



    private final CommonService commonService;



    @GetMapping("/sports-types")
    public ResponseEntity<List<SportsTypeDto>> findAllSportsType(){
        List<SportsTypeDto> sportsTypeDtos = commonService.findAllSportsType();

        return ResponseEntity.ok(sportsTypeDtos);
    }


}
