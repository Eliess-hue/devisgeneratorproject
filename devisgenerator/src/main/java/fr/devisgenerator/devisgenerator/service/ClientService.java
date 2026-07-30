package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.request.ClientFilterRequest;
import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    ClientResponse create(ClientRequest request, AppUser user);

    Page<ClientResponse> search(ClientFilterRequest filter, Pageable pageable, AppUser user);

    ClientResponse findById(Long id, AppUser user);

    ClientResponse update(Long id, ClientRequest request, AppUser user);

    void delete(Long id, AppUser user);
}