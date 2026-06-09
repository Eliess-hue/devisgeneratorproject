package fr.devisgenerator.devisgenerator.repository;

import fr.devisgenerator.devisgenerator.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByUser_Id(Long userId);

}
