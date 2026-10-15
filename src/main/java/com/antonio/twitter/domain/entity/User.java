package com.antonio.twitter.domain.entity;

import com.antonio.twitter.domain.enums.Role;
import com.antonio.twitter.domain.enums.UserStatus;
import com.antonio.twitter.domain.valueobject.Biography;
import com.antonio.twitter.domain.valueobject.Password;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class User {

    @EqualsAndHashCode.Include
    private final UserId id;
    private Username username;
    private Biography biography;
    private Password password;
    private Role role;
    private UserStatus status;

    public static User reconstitute(UserId id, Username username, Biography biography, Password password, Role role, UserStatus status) {
        requireNonNull(id, "User id");
        requireNonNull(username, "Username");
        requireNonNull(password, "Password");
        requireNonNull(role, "Role");
        requireNonNull(status, "Status");
        return new User(
            id,
            username,
            biography,
            password,
            role,
            status
        );
    }

    public static User create(Username username, Biography biography, Password password) {
        return reconstitute(
            new UserId(),
            username,
            biography,
            password,
            Role.USER,
            UserStatus.ACTIVE
        );
    }

    public void deactivate() {
        if (this.isBanned()) {
            throw new IllegalStateException("Banned users cannot be deactivated");
        }
        this.status = UserStatus.INACTIVE;
    }

    public void promote(User promoter) {
        requireNonNull(promoter, "Promoter");
        promoter.ensureCanModerate();
        this.ensureCanBePromoted();

        this.role = Role.ADMIN;
    }

    public void ban(User admin) {
        requireNonNull(admin, "Admin");
        admin.ensureCanModerate();
        if (this.equals(admin)) {
            throw new IllegalStateException("Admins cannot ban themselves");
        }
        if (this.isAdmin()) {
            throw new IllegalStateException("Admins cannot be banned");
        }
        this.status = UserStatus.BANNED;
    }

    public void unban(User admin) {
        requireNonNull(admin, "Admin");
        admin.ensureCanModerate();
        if (!this.isBanned()) {
            throw new IllegalStateException("User is not banned");
        }
        this.status = UserStatus.ACTIVE;
    }

    public void ensureCanPost() {
        if (!this.isActive()) {
            throw new IllegalStateException("User is not active and cannot post");
        }
    }

    public void ensureCanModerate() {
        if (!this.isAdmin()) {
            throw new IllegalStateException("User is not an admin and cannot moderate");
        }
        if (!this.isActive()) {
            throw new IllegalStateException("Admin is not active and cannot moderate");
        }
    }

    public void ensureCanBePromoted() {
        if (!this.isActive()) {
            throw new IllegalStateException("User is not active and cannot be promoted");
        }
    }

    public void updateBiography(Biography biography) {
        this.biography = biography;
    }

    public void changeUsername(Username username) {
        requireNonNull(username, "Username");
        this.username = username;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isBanned() {
        return this.status == UserStatus.BANNED;
    }

    public boolean isInactive() {
        return this.status == UserStatus.INACTIVE;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    private static void requireNonNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
