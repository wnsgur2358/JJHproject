package com.multi.matchon.matchup.service;


import com.multi.matchon.common.auth.dto.CustomUser;
import com.multi.matchon.common.domain.Attachment;
import com.multi.matchon.common.domain.BoardType;
import com.multi.matchon.common.domain.SportsTypeName;
import com.multi.matchon.common.dto.res.PageResponseDto;
import com.multi.matchon.common.exception.custom.CustomException;
import com.multi.matchon.common.repository.AttachmentRepository;
import com.multi.matchon.common.repository.SportsTypeRepository;

import com.multi.matchon.common.util.AwsS3Utils;
import com.multi.matchon.matchup.domain.MatchupBoard;
import com.multi.matchon.matchup.domain.MatchupRequest;
import com.multi.matchon.matchup.dto.req.ReqMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardDto;
import com.multi.matchon.matchup.dto.res.ResMatchupBoardListDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestDto;
import com.multi.matchon.matchup.dto.res.ResMatchupRequestListDto;
import com.multi.matchon.matchup.repository.MatchupBoardRepository;
import com.multi.matchon.matchup.repository.MatchupRequestRepository;
import com.multi.matchon.member.domain.Member;
import com.sun.jdi.request.DuplicateRequestException;
import io.awspring.cloud.s3.S3Resource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class MatchupService{

    @Value("${spring.cloud.aws.s3.base-url}")
    private String S3_URL;
    private String FILE_DIR = "attachments/";
    private String FILE_URL;
    @PostConstruct
    public void init(){
        this.FILE_URL = S3_URL;
    }

    private final AttachmentRepository attachmentRepository;

    private final AwsS3Utils awsS3Utils;

    @Transactional
    public void insertFile(MultipartFile multipartFile, MatchupBoard matchupBoard){
        String fileName = UUID.randomUUID().toString().replace("-","");
        awsS3Utils.saveFile(FILE_DIR, fileName, multipartFile);
        Attachment attachment = Attachment.builder()
                .boardType(BoardType.MATCHUP_BOARD)
                .boardNumber(matchupBoard.getId())
                .fileOrder(0)
                .originalName(multipartFile.getOriginalFilename())
                .savedName(fileName+multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".")))
                .savePath(FILE_DIR)
                .build();
        attachmentRepository.save(attachment);
    }
    @Transactional
    public void updateFile(MultipartFile multipartFile, MatchupBoard findMatchupBoard){
        String fileName = UUID.randomUUID().toString().replace("-","");

        List<Attachment> findAttachments = attachmentRepository.findAllByBoardTypeAndBoardNumber(BoardType.MATCHUP_BOARD, findMatchupBoard.getId());

        if(findAttachments.isEmpty())
            throw new CustomException("Matchup "+BoardType.MATCHUP_BOARD+"타입, "+findMatchupBoard.getId()+"번에는 첨부파일이 없습니다.");

        String savedName = findAttachments.get(0).getSavedName();

        awsS3Utils.deleteFile(FILE_DIR, savedName.substring(0,savedName.indexOf(".")));

        findAttachments.get(0).update(multipartFile.getOriginalFilename(), fileName+multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".")), FILE_DIR);

        awsS3Utils.saveFile(FILE_DIR, fileName, multipartFile);
    }


//    public void findBoardListTest() {
//        Pageable pageRequest1 = PageRequest.of(0,4);
//        Pageable pageRequest2 = PageRequest.of(1,4);
//        Pageable pageRequest3 = PageRequest.of(2,4);
//        Pageable pageRequest4 = PageRequest.of(3,4);
//        Pageable pageRequest5 = PageRequest.of(4,4);
//
//        List<ResMatchupBoardListDto> resMatchupBoardDtos = matchupBoardRepository.findBoardListTest();
//        Page<ResMatchupBoardListDto> resMatchupBoardDtos1 = matchupBoardRepository.findBoardListTest2(pageRequest1);
//        Page<ResMatchupBoardListDto> resMatchupBoardDtos2 = matchupBoardRepository.findBoardListTest2(pageRequest2);
//        Page<ResMatchupBoardListDto> resMatchupBoardDtos3 = matchupBoardRepository.findBoardListTest2(pageRequest3);
//        Page<ResMatchupBoardListDto> resMatchupBoardDtos4 = matchupBoardRepository.findBoardListTest2(pageRequest4);
//        Page<ResMatchupBoardListDto> resMatchupBoardDtos5 = matchupBoardRepository.findBoardListTest2(pageRequest5);
//
//
//
//
//        System.out.println("resMatchupBoardDtos = " + resMatchupBoardDtos);
//        System.out.println("resMatchupBoardDtos1 = " + resMatchupBoardDtos1);
//        System.out.println("resMatchupBoardDtos2 = " + resMatchupBoardDtos2);
//        System.out.println("resMatchupBoardDtos3 = " + resMatchupBoardDtos3);
//        System.out.println("resMatchupBoardDtos4 = " + resMatchupBoardDtos4);
//        System.out.println("resMatchupBoardDtos5 = " + resMatchupBoardDtos5);
//
//        PageResponseDto<ResMatchupBoardListDto> pageResponseDtos = PageResponseDto.<ResMatchupBoardListDto>builder()
//                .items(resMatchupBoardDtos1.getContent())
//                .pageInfo(PageResponseDto.PageInfoDto.builder()
//                        .page(resMatchupBoardDtos1.getNumber())
//                        .size(resMatchupBoardDtos1.getNumberOfElements())
//                        .totalElements(resMatchupBoardDtos1.getTotalElements())
//                        .totalPages(resMatchupBoardDtos1.getTotalPages())
//                        .isFirst(resMatchupBoardDtos1.isFirst())
//                        .isLast(resMatchupBoardDtos1.isLast())
//                        .build())
//                .build();
//
//        System.out.println();
//    }


    public S3Resource findAttachmentFile(String savedName) throws IOException {
        S3Resource resource = awsS3Utils.downloadFile(FILE_DIR, savedName);
        return resource;
    }

    public String findAttachmentURL(String savedName) throws IOException {
        String presignedUrl = awsS3Utils.createPresignedGetUrl(FILE_DIR, savedName);

        return presignedUrl;
    }





}















































