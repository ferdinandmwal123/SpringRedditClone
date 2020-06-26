package com.mwalagho.ferdinand.redditclone.mapper;

import com.mwalagho.ferdinand.redditclone.dto.PostRequest;
import com.mwalagho.ferdinand.redditclone.dto.PostResponse;
import com.mwalagho.ferdinand.redditclone.model.Post;
import com.mwalagho.ferdinand.redditclone.model.Subreddit;
import com.mwalagho.ferdinand.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);
}
