package com.antonio.twitter.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.antonio.twitter.domain.enums.Role;
import com.antonio.twitter.domain.enums.UserStatus;
import com.antonio.twitter.domain.valueobject.Biography;
import com.antonio.twitter.domain.valueobject.Password;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;

class UserTest {

    private static User user(Role role, UserStatus status) {
        return User.reconstitute(new UserId(), new Username("user_" + role.name().toLowerCase()), new Biography("bio"), new Password("secret"), role, status);
    }

    private static User activeUser() {
        return user(Role.USER, UserStatus.ACTIVE);
    }

    private static User activeAdmin() {
        return user(Role.ADMIN, UserStatus.ACTIVE);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("cria usuário ativo com papel USER e id gerado")
        void createsActiveUser() {
            User user = User.create(new Username("antonio"), new Biography("dev"), new Password("secret"));

            assertThat(user.getId()).isNotNull();
            assertThat(user.getUsername()).isEqualTo(new Username("antonio"));
            assertThat(user.getBiography()).isEqualTo(new Biography("dev"));
            assertThat(user.getRole()).isEqualTo(Role.USER);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("permite biografia nula")
        void allowsNullBiography() {
            User user = User.create(new Username("antonio"), null, new Password("secret"));

            assertThat(user.getBiography()).isNull();
        }

        @Test
        @DisplayName("gera ids diferentes para cada usuário")
        void generatesDistinctIds() {
            User a = User.create(new Username("antonio"), null, new Password("secret"));
            User b = User.create(new Username("antonio"), null, new Password("secret"));

            assertThat(a.getId()).isNotEqualTo(b.getId());
        }

        @Test
        @DisplayName("rejeita username ou senha nulos")
        void rejectsNullUsernameOrPassword() {
            assertThatThrownBy(() -> User.create(null, null, new Password("secret")))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> User.create(new Username("antonio"), null, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute")
    class Reconstitute {

        @Test
        @DisplayName("preserva todos os campos")
        void preservesFields() {
            UserId id = new UserId();

            User user = User.reconstitute(id, new Username("admin"), null, new Password("pw"), Role.ADMIN, UserStatus.BANNED);

            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getUsername()).isEqualTo(new Username("admin"));
            assertThat(user.getPassword()).isEqualTo(new Password("pw"));
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getStatus()).isEqualTo(UserStatus.BANNED);
        }

        @Test
        @DisplayName("rejeita campos obrigatórios nulos")
        void rejectsNullRequiredFields() {
            Username username = new Username("antonio");
            Password password = new Password("secret");

            assertThatThrownBy(() -> User.reconstitute(null, username, null, password, Role.USER, UserStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> User.reconstitute(new UserId(), username, null, password, null, UserStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> User.reconstitute(new UserId(), username, null, password, Role.USER, null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("deactivate")
    class Deactivate {

        @Test
        @DisplayName("usuário ativo fica inativo")
        void deactivatesActiveUser() {
            User user = activeUser();

            user.deactivate();

            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("usuário banido não pode se desativar para escapar do ban")
        void bannedUserCannotDeactivate() {
            User user = user(Role.USER, UserStatus.BANNED);

            assertThatThrownBy(user::deactivate).isInstanceOf(IllegalStateException.class);
            assertThat(user.getStatus()).isEqualTo(UserStatus.BANNED);
        }
    }

    @Nested
    @DisplayName("ban")
    class Ban {

        @Test
        @DisplayName("admin ativo bane usuário")
        void activeAdminBansUser() {
            User user = activeUser();

            user.ban(activeAdmin());

            assertThat(user.isBanned()).isTrue();
        }

        @Test
        @DisplayName("usuário comum não pode banir")
        void regularUserCannotBan() {
            User target = activeUser();

            assertThatThrownBy(() -> target.ban(activeUser())).isInstanceOf(IllegalStateException.class);
            assertThat(target.isActive()).isTrue();
        }

        @Test
        @DisplayName("admin banido ou inativo não pode banir")
        void nonActiveAdminCannotBan() {
            User target = activeUser();

            assertThatThrownBy(() -> target.ban(user(Role.ADMIN, UserStatus.BANNED)))
                .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> target.ban(user(Role.ADMIN, UserStatus.INACTIVE)))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("admin não pode banir a si mesmo")
        void adminCannotBanThemselves() {
            User admin = activeAdmin();

            assertThatThrownBy(() -> admin.ban(admin)).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("admin não pode ser banido")
        void adminCannotBeBanned() {
            User otherAdmin = activeAdmin();

            assertThatThrownBy(() -> otherAdmin.ban(activeAdmin())).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("rejeita admin nulo")
        void rejectsNullAdmin() {
            assertThatThrownBy(() -> activeUser().ban(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("unban")
    class Unban {

        @Test
        @DisplayName("admin ativo desbane usuário banido")
        void activeAdminUnbansUser() {
            User user = user(Role.USER, UserStatus.BANNED);

            user.unban(activeAdmin());

            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("não reativa usuário que não está banido")
        void cannotUnbanNonBannedUser() {
            User user = user(Role.USER, UserStatus.INACTIVE);

            assertThatThrownBy(() -> user.unban(activeAdmin())).isInstanceOf(IllegalStateException.class);
            assertThat(user.isInactive()).isTrue();
        }

        @Test
        @DisplayName("usuário comum não pode desbanir")
        void regularUserCannotUnban() {
            User user = user(Role.USER, UserStatus.BANNED);

            assertThatThrownBy(() -> user.unban(activeUser())).isInstanceOf(IllegalStateException.class);
            assertThat(user.isBanned()).isTrue();
        }
    }

    @Nested
    @DisplayName("promote")
    class Promote {

        @Test
        @DisplayName("admin ativo promove usuário ativo")
        void activeAdminPromotesUser() {
            User user = activeUser();

            user.promote(activeAdmin());

            assertThat(user.isAdmin()).isTrue();
        }

        @Test
        @DisplayName("usuário banido ou inativo não pode ser promovido")
        void nonActiveUserCannotBePromoted() {
            User banned = user(Role.USER, UserStatus.BANNED);
            User inactive = user(Role.USER, UserStatus.INACTIVE);

            assertThatThrownBy(() -> banned.promote(activeAdmin())).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> inactive.promote(activeAdmin())).isInstanceOf(IllegalStateException.class);
            assertThat(banned.isAdmin()).isFalse();
            assertThat(inactive.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("usuário comum ou admin inativo não pode promover")
        void onlyActiveAdminCanPromote() {
            User user = activeUser();

            assertThatThrownBy(() -> user.promote(activeUser())).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> user.promote(user(Role.ADMIN, UserStatus.INACTIVE)))
                .isInstanceOf(IllegalStateException.class);
            assertThat(user.isAdmin()).isFalse();
        }

        @Test
        @DisplayName("rejeita promotor nulo")
        void rejectsNullPromoter() {
            assertThatThrownBy(() -> activeUser().promote(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ensureCanPost")
    class EnsureCanPost {

        @Test
        @DisplayName("usuário ativo pode postar")
        void activeUserCanPost() {
            assertThatCode(() -> activeUser().ensureCanPost()).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("usuário banido ou inativo não pode postar")
        void nonActiveUserCannotPost() {
            assertThatThrownBy(() -> user(Role.USER, UserStatus.BANNED).ensureCanPost())
                .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> user(Role.USER, UserStatus.INACTIVE).ensureCanPost())
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("perfil")
    class Profile {

        @Test
        @DisplayName("altera username")
        void changesUsername() {
            User user = activeUser();

            user.changeUsername(new Username("novo_nome"));

            assertThat(user.getUsername()).isEqualTo(new Username("novo_nome"));
        }

        @Test
        @DisplayName("rejeita username nulo")
        void rejectsNullUsername() {
            assertThatThrownBy(() -> activeUser().changeUsername(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("atualiza biografia")
        void updatesBiography() {
            User user = activeUser();

            user.updateBiography(new Biography("nova bio"));

            assertThat(user.getBiography()).isEqualTo(new Biography("nova bio"));
        }
    }

    @Nested
    @DisplayName("igualdade")
    class Equality {

        @Test
        @DisplayName("usuários com o mesmo id são iguais, mesmo com dados diferentes")
        void equalById() {
            UserId id = new UserId();
            User a = User.reconstitute(id, new Username("aaa"), null, new Password("x"), Role.USER, UserStatus.ACTIVE);
            User b = User.reconstitute(id, new Username("bbb"), null, new Password("y"), Role.ADMIN, UserStatus.BANNED);

            assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("usuários com ids diferentes não são iguais")
        void differentIds() {
            assertThat(activeUser()).isNotEqualTo(activeUser());
        }
    }
}
