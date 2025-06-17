package com.nextroom.app.web.repository;

import com.nextroom.app.web.model.Roommate;
import com.nextroom.app.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoommateRepository extends JpaRepository<Roommate, Long> {
    List<Roommate> findByInviter(User inviter);
    List<Roommate> findByInvitee(User invitee);
    boolean existsByInviterAndInvitee(User inviter, User invitee);
}
