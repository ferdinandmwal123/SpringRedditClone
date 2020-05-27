package com.mwalagho.ferdinand.redditclone.repository;

import com.mwalagho.ferdinand.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
