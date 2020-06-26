package com.mwalagho.ferdinand.redditclone.repository;

import com.mwalagho.ferdinand.redditclone.model.Comment;
import com.mwalagho.ferdinand.redditclone.model.Post;
import com.mwalagho.ferdinand.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
