package com.multi.matchon.matchup.controller;



import com.multi.matchon.common.auth.dto.CustomUser;

import com.multi.matchon.common.dto.res.ApiResponse;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestListDto;
import com.multi.matchon.matchup.service.MatchupService;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Controller
@RequestMapping("/matchup")
@Slf4j
@RequiredArgsConstructor
public class MatchupController {

    private final MatchupService matchupService;

    // 첨부 파일 가져오기
    @GetMapping("/attachment/file")
    public ResponseEntity<S3Resource> getAttachmentFile(@RequestParam("saved-name") String savedName) throws IOException {

        S3Resource resource = matchupService.findAttachmentFile(savedName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(savedName, StandardCharsets.UTF_8)
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // presignedUrl 반환
    @GetMapping("/attachment/presigned-url")
    public ResponseEntity<ApiResponse<String>> getAttachmentUrl(@RequestParam("saved-name") String savedName) throws IOException {

        String resourceUrl = matchupService.findAttachmentURL(savedName);

        return ResponseEntity.ok().body(ApiResponse.ok(resourceUrl));

    }

}
