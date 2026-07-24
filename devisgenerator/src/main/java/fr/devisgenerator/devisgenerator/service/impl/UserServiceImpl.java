package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.response.AppUserResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.exception.UserNotFoundException;
import fr.devisgenerator.devisgenerator.repository.AppUserRepository;
import fr.devisgenerator.devisgenerator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    @Override
    public List<AppUserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toAppUserResponse)
                .toList();
    }

    @Override
    public AppUserResponse changeRole(Long userId, UserRole role) {

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User " + userId + " not found"
                        )
                );

        String previousRole = user.getRole();

        user.setRole(role.name());

        user = userRepository.save(user);


        log.info(
                "User {} role changed from {} to {}",
                user.getUsername(),
                previousRole,
                user.getRole()
        );

        return toAppUserResponse(user);
    }

    private AppUserResponse toAppUserResponse(AppUser user) {

        return new AppUserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

}