package fr.devisgenerator.devisgenerator.repository;

import java.util.Optional;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

}