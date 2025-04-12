package com.venuehub.dto;

import java.util.List;

public record CurrentUserDto(String username, List<String> roles, String loggedInAs) {

}
