package com.mwalagho.ferdinand.redditclone.repository;

import com.mwalagho.ferdinand.redditclone.model.Post;
import com.mwalagho.ferdinand.redditclone.model.User;
import com.mwalagho.ferdinand.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    //find user by post, order by voteId in descending order, get top one(the latest one)
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);


}
