package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.ClientFilterRequest;
import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.enums.UserRole;
import fr.devisgenerator.devisgenerator.exception.ClientNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import fr.devisgenerator.devisgenerator.service.ClientService;

import fr.devisgenerator.devisgenerator.specification.ClientSpecification;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final QuoteRepository quoteRepository;

    @Override
    public ClientResponse create(ClientRequest request, AppUser user) {

        // créer client
        Client client = Client.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .user(user)
                .build();

        // sauvegarder
        client = clientRepository.save(client);

        // retourner réponse
        return toClientResponse(client);

    }

    @Override
    public Page<ClientResponse> search(ClientFilterRequest filter, Pageable pageable, AppUser user
    ) {

        Specification<Client> spec =
                Specification.allOf(
                        ClientSpecification.hasSearch(
                                filter.search()
                        )
                );

        if (!isAdmin(user)) {
            spec = spec.and(
                    ClientSpecification.hasUser(
                            user.getId()
                    )
            );
        }

        return clientRepository
                .findAll(spec, pageable)
                .map(this::toClientResponse);

    }


    @Override
    public ClientResponse findById(Long id, AppUser user) {

        Client client = getAccessibleClient(id, user);

        return toClientResponse(client);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request, AppUser user) {

        Client client = getAccessibleClient(id, user);

        // mettre à jour les données
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());

        // sauvegarder
        client = clientRepository.save(client);

        return toClientResponse(client);
    }

    @Override
    public void delete(Long id, AppUser user) {

        Client client = getAccessibleClient(id, user);

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

    private boolean isAdmin(AppUser user) {
        return user.getRole() == UserRole.ROLE_ADMIN;
    }

    private Client getAccessibleClient(Long id, AppUser user) {

        // récupérer client
        Client client = clientRepository.findById(id)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Client " + id + " not found"
                        ));

        // vérifier administrateur & propriétaire
        if (isAdmin(user)) {
            return client;
        }

        if (!client.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Access denied: user "
                            + user.getId()
                            + " attempted to access client "
                            + id
            );
        }

        return client;
    }

}