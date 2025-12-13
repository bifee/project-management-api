package com.bifee.projectmanagement.dto;

import com.bifee.projectmanagement.entity.Email;
import com.bifee.projectmanagement.entity.Password;

public record UserRegisterDTO (
         Email email,
         String name,
         Password password
){
}
