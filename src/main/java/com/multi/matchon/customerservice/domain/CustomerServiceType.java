package com.multi.matchon.customerservice.domain;


public enum CustomerServiceType {

    // 계정
    ACCOUNT("계정"),
    // TEAM / GUEST
    TEAM_GUEST("Team / Guest"),
    // 신고
    REPORT("신고"),
    // 매너온도
    MANNER_TEMPERATURE("매너온도"),
    // 커뮤니티
    COMMUNITY("커뮤니티"),
    // 대회
    EVENT("대회"),

    TUTORIAL("이용가이드");

    private final String label;

    CustomerServiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
