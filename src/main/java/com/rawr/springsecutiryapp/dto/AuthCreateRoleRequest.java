package com.rawr.springsecutiryapp.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthCreateRoleRequest(
//        El usuario puede tener maximo 3 roles
        @Size(max = 3, message = "The user cannot have more than 3 roles")
        List<String> roleListName
) {
}
