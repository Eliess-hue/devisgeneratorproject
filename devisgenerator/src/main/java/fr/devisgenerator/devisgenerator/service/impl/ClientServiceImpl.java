package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.exception.ClientNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.service.ClientService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final QuoteRepository quoteRepository;

    @Override
    public ClientResponse create(ClientRequest request, AppUser user) {

        // 1. créer client
        Client client = Client.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .user(user)
                .build();

        // 2. sauvegarder
        client = clientRepository.save(client);

        // 3. retourner réponse
        return toClientResponse(client);

    }

    @Override
    public List<ClientResponse> findAll(AppUser user) {

        // 1. récupérer tous les clients du propriétaire connecté
        List<Client> clients = clientRepository.findByUser_Id(user.getId());

        // 2. convertir et retourner chaque Client en ClientResponse
        return clients.stream()
                .map(this::toClientResponse)
                .toList();

    }

    @Override
    public ClientResponse findById(Long id, AppUser user) {

        // 1. récupérer client
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        // 2. vérifier propriétaire
        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        // 3. retourner réponse
        return toClientResponse(client);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request, AppUser user) {

        // 1. récupérer client
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        // 2. vérifier propriétaire
        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        // 3. mettre à jour les données
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());

        // 4. sauvegarder
        client = clientRepository.save(client);

        // 5. retourner réponse
        return toClientResponse(client);
    }

    @Override
    public void delete(Long id, AppUser user) {

        // 1. récupérer client
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        // 2. vérifier propriétaire
        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        // 3. supprimer
        clientRepository.delete(client);
    }

    private ClientResponse toClientResponse(
            Client client
    ) {

        int quoteCount =
                quoteRepository.countByClient_Id(
                        client.getId()
                );

        String lastQuoteNumber =
                quoteRepository
                        .findTopByClient_IdOrderByCreatedAtDesc(
                                client.getId()
                        )
                        .map(Quote::getNumber)
                        .orElse("-");

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                quoteCount,
                lastQuoteNumber
        );
    }

}