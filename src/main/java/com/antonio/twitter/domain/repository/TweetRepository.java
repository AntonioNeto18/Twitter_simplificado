package com.antonio.twitter.domain.repository;

import java.util.List;

import com.antonio.twitter.domain.entity.Tweet;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;

public interface TweetRepository extends TwitterRepository<Tweet, TweetId> {
    List<Tweet> findByAuthorId(UserId userId);
}
