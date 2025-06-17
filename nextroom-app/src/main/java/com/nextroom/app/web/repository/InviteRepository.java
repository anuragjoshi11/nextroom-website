package com.nextroom.app.web.repository;

import com.nextroom.app.web.model.Invite;
import com.nextroom.app.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findBySlug(String slug);
    Optional<Invite> findByUrl(String url);
    boolean existsBySlug(String slug);
    boolean existsByUrl(String url);
    Optional<Invite> findByInvitedBy(User invitedBy);
}
