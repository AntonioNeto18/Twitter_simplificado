package com.antonio.twitter.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.antonio.twitter.domain.entity.Tweet;
import com.antonio.twitter.domain.valueobject.Content;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.infrastructure.persistence.entity.TweetJpaEntity;

@Component
public class TweetMapper {

    public TweetJpaEntity toJpa(Tweet tweet) {
        TweetJpaEntity jpaEntity = new TweetJpaEntity();
        jpaEntity.setId(tweet.getId().value());
        jpaEntity.setContent(tweet.getContent().value());
        jpaEntity.setAuthorId(tweet.getAuthorId().value());

        return jpaEntity;
    }

    public Tweet toDomain(TweetJpaEntity jpaEntity) {
        return Tweet.reconstitute(
            new TweetId(jpaEntity.getId()),
            new Content(jpaEntity.getContent()),
            new UserId(jpaEntity.getAuthorId())
        );
    }

}
