package com.multi.matchon.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadedFile {
    private String savedFileName;
    private String originalFileName;
}

