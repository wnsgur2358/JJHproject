package com.multi.matchon.community.repository;

import com.multi.matchon.community.domain.Board;
import com.multi.matchon.community.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByCategory(Category category, Pageable pageable);
    List<Board> findByCategoryAndPinnedTrueOrderByCreatedDateDesc(Category category);

}





