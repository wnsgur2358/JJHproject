package com.multi.matchon.common.exception.custom;


public class MatchupRequestLimitExceededException extends RuntimeException {
    public MatchupRequestLimitExceededException(String message) {
        super(message);
    }
}
