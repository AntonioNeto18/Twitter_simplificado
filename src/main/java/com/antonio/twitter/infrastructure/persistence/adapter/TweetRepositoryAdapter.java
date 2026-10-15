package com.antonio.twitter.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.antonio.twitter.domain.entity.Tweet;
import com.antonio.twitter.domain.repository.TweetRepository;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.infrastructure.persistence.mapper.TweetMapper;
import com.antonio.twitter.infrastructure.persistence.repository.TweetJpaRepository;

@Repository
public class TweetRepositoryAdapter implements TweetRepository {

    private final TweetJpaRepository tweetJpaRepository;
    private final TweetMapper tweetMapper;

    public TweetRepositoryAdapter(TweetJpaRepository tweetJpaRepository, TweetMapper tweetMapper) {
        this.tweetJpaRepository = tweetJpaRepository;
        this.tweetMapper = tweetMapper;
    }

    @Override
    public Tweet save(Tweet entity) {
        return tweetMapper.toDomain(tweetJpaRepository.save(tweetMapper.toJpa(entity)));
    }

    @Override
    public List<Tweet> findAll() {
        return tweetJpaRepository.findAll().stream()
                .map(tweetMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Tweet> findById(TweetId id) {
        return tweetJpaRepository.findById(id.value()).map(tweetMapper::toDomain);
    }

    @Override
    public boolean existsById(TweetId id) {
        return tweetJpaRepository.existsById(id.value());
    }

    @Override
    public void delete(Tweet entity) {
        tweetJpaRepository.delete(tweetMapper.toJpa(entity));
    }

    @Override
    public List<Tweet> findByAuthorId(UserId userId) {
        return tweetJpaRepository.findByAuthorId(userId.value()).stream()
                .map(tweetMapper::toDomain)
                .toList();
    }

}
