package com.multi.matchon.customerservice.repository;

import com.multi.matchon.customerservice.domain.Inquiry;
import com.multi.matchon.customerservice.domain.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    @Query("SELECT ia FROM InquiryAnswer ia WHERE ia.inquiry.id = :inquiryId AND ia.isDeleted = false")
    Optional<InquiryAnswer> findActiveAnswerByInquiryId(@Param("inquiryId") Long inquiryId);
}
