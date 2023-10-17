package com.solovev.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solovev.dto.DTO;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Card with question for category
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name = "Cards",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"question", "answer", "category_id"})})
public class Card implements DTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name = "question",nullable = false)
    private String question;

    @NonNull
    @Column(name = "answer",nullable = false)
    private String answer;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @NonNull
    @Column(name = "creation_date",nullable = false)
    private final LocalDate creationDate = LocalDate.now();

}
