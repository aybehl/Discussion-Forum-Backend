package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t.tagId, t.name, COUNT(q.questionId) " +
            "FROM Tag t LEFT JOIN t.questions q " +
            "GROUP BY t.tagId, t.name")
    List<Object[]> findTagsWithQuestionCount();

    @Query("SELECT t FROM Tag t JOIN t.questions q WHERE q.questionId = :questionId")
    List<Tag> findAllTagsForQuestion(@Param("questionId") Long questionId);
}
