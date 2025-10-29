package com.multi.matchon.community.domain;


public enum Category {
    ANNOUNCEMENT("공지사항"),
    FREEBOARD("자유게시판"),
    INFORMATION("정보게시판"),
    FOOTBALL_TALK("국내/해외 축구");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


