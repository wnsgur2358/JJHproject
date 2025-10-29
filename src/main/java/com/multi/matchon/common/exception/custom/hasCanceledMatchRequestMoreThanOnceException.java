package com.multi.matchon.common.exception.custom;


public class hasCanceledMatchRequestMoreThanOnceException extends RuntimeException{
    public hasCanceledMatchRequestMoreThanOnceException(String message) {
        super(message);
    }
}
