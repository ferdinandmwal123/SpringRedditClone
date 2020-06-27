package com.mwalagho.ferdinand.redditclone.dto;

import com.mwalagho.ferdinand.redditclone.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private VoteType voteType;
    private Long postId;
}
