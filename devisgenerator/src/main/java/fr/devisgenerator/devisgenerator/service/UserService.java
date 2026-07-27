package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.response.AppUserResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.enums.UserRole;

import java.util.List;

public interface UserService {

    List<AppUserResponse> findAll();

    AppUserResponse changeRole(Long userId, UserRole role);

    AppUserResponse me(AppUser user);

}