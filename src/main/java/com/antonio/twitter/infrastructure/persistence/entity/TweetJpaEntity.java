package com.antonio.twitter.infrastructure.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_tweets")
public class TweetJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

}
