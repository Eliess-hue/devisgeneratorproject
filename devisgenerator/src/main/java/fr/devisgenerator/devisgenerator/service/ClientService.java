package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;

import java.util.List;

public interface ClientService {

    ClientResponse create(ClientRequest request, AppUser user);

    List<ClientResponse> findAll(AppUser user);

    ClientResponse findById(Long id, AppUser user);

    ClientResponse update(Long id, ClientRequest request, AppUser user);

    void delete(Long id, AppUser user);
}