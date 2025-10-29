package com.multi.matchon.community.domain;

import lombok.Getter;

@Getter
public enum ReasonType {
    ABUSE("욕설/비방/괴롭힘"),
    ADVERTISEMENT("상업/광고성 글"),
    SPAM("도배성 글"),
    IRRELEVANT("카테고리 취지에 어긋남"),
    ETC("기타");

    private final String label;

    ReasonType(String label) {
        this.label = label;
    }
}
