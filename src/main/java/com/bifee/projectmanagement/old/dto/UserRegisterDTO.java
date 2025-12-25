package com.bifee.projectmanagement.old.dto;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;

public record UserRegisterDTO (
         Email email,
         String name,
         Password password
){
}
