package com.forum.discussion_platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "question_tags")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Question_Tag {
}
