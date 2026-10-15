package com.antonio.twitter.domain.entity;

import com.antonio.twitter.domain.valueobject.Content;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Tweet {

    @EqualsAndHashCode.Include
    private final TweetId id;
    private Content content;
    private final UserId authorId;

    public static Tweet reconstitute(TweetId id, Content content, UserId authorId) {
        requireNonNull(id, "Tweet id");
        requireNonNull(content, "Tweet content");
        requireNonNull(authorId, "Tweet author");
        return new Tweet(
            id,
            content,
            authorId
        );
    }

    public static Tweet create(Content content, User author) {
        requireNonNull(content, "Tweet content");
        requireNonNull(author, "Tweet author");
        author.ensureCanPost();
        return reconstitute(
            new TweetId(),
            content,
            author.getId()
        );
    }

    public void editContent(Content newContent, User editor) {
        requireNonNull(newContent, "Tweet content");
        requireNonNull(editor, "Editor");
        if (!this.isAuthoredBy(editor)) {
            throw new IllegalStateException("Only the author can edit the tweet");
        }
        editor.ensureCanPost();
        this.content = newContent;
    }

    public boolean isAuthoredBy(User user) {
        return user != null && this.authorId.equals(user.getId());
    }

    private static void requireNonNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
