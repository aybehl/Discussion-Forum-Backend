package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q.questionId, q.title, q.createdAt, q.upvotes, q.downvotes, COUNT(a) " +
            "FROM Question q " +
            "LEFT JOIN q.answers a " +
            "WHERE q.isDeleted = false " +
            "GROUP BY q.questionId")
    Page<Object[]> findQuestionsWithAnswerCount(Pageable pageable);

    @Query("SELECT q.questionId, q.title, q.createdAt, q.upvotes, q.downvotes, " +
            "(SELECT COUNT(a) FROM Answer a WHERE a.relatedQuestion = q) AS noOfReplies " +
            "FROM Question q JOIN q.tags t WHERE t.tagId IN :tagIds AND q.isDeleted = false " +
            "GROUP BY q.questionId")
    Page<Object[]> findQuestionsWithAnswerCountByTags(@Param("tagIds") List<Long> tagIds, Pageable pageable);
}
