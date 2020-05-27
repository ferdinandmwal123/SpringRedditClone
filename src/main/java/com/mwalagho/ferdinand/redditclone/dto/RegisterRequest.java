package com.mwalagho.ferdinand.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest { //Dat Transfer Object

    private String email;
    private String password;
    private String username;

}
