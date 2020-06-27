package com.mwalagho.ferdinand.redditclone.service;

import com.mwalagho.ferdinand.redditclone.dto.PostRequest;
import com.mwalagho.ferdinand.redditclone.dto.PostResponse;
import com.mwalagho.ferdinand.redditclone.exceptions.PostNotFoundException;
import com.mwalagho.ferdinand.redditclone.exceptions.SubredditNotFoundException;
import com.mwalagho.ferdinand.redditclone.mapper.PostMapper;
import com.mwalagho.ferdinand.redditclone.model.Post;
import com.mwalagho.ferdinand.redditclone.model.Subreddit;
import com.mwalagho.ferdinand.redditclone.model.User;
import com.mwalagho.ferdinand.redditclone.repository.PostRepository;
import com.mwalagho.ferdinand.redditclone.repository.SubredditRepository;
import com.mwalagho.ferdinand.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }
}
