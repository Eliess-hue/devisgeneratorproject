package fr.devisgenerator.devisgenerator.service.impl;

import fr.devisgenerator.devisgenerator.dto.request.ClientRequest;
import fr.devisgenerator.devisgenerator.dto.response.ClientResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;
import fr.devisgenerator.devisgenerator.entity.Client;
import fr.devisgenerator.devisgenerator.entity.Quote;
import fr.devisgenerator.devisgenerator.exception.ClientNotFoundException;
import fr.devisgenerator.devisgenerator.repository.ClientRepository;
import fr.devisgenerator.devisgenerator.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private QuoteRepository quoteRepository;

    @InjectMocks
    private ClientServiceImpl clientService;


    @Test
    void createShouldCreateClientSuccessfully() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .username("john")
                .build();

        ClientRequest request = new ClientRequest(
                "ACME",
                "contact@acme.com",
                "0102030405",
                "Paris"
        );

        when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock des appels QuoteRepository dans toClientResponse()
        mockQuoteInformations(null, 0, null);

        // Act
        ClientResponse response = clientService.create(request, user);

        // Assert
        assertEquals("ACME", response.name());
        assertEquals("contact@acme.com", response.email());
        assertEquals(0, response.quoteCount());
        assertEquals("-", response.lastQuoteNumber());

        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void findAllShouldReturnClientsOfUser() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        when(clientRepository.findByUser_Id(1L))
                .thenReturn(List.of(client));

        // Le client a l'id 1L, 3 devis, dernier = "DEV-2024-003"
        mockQuoteInformations(1L, 3, "DEV-2024-003");

        // Act
        List<ClientResponse> responses = clientService.findAll(user);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("ACME", responses.get(0).name());
        assertEquals(3, responses.get(0).quoteCount());
        assertEquals("DEV-2024-003", responses.get(0).lastQuoteNumber());
    }

    @Test
    void findByIdShouldReturnClientWhenFound() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(user)
                .build();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        // Pas de devis pour ce client
        mockQuoteInformations(1L, 0, null);

        // Act
        ClientResponse response = clientService.findById(1L, user);

        // Assert
        assertEquals(1L, response.id());
        assertEquals("ACME", response.name());
        assertEquals(0, response.quoteCount());
        assertEquals("-", response.lastQuoteNumber());
    }

    @Test
    void findByIdShouldThrowClientNotFoundExceptionWhenClientDoesNotExist() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ClientNotFoundException.class,
                () -> clientService.findById(1L, user)
        );
    }

    @Test
    void findByIdShouldThrowAccessDeniedExceptionWhenClientBelongsToAnotherUser() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .username("johnny")
                .build();

        AppUser otherUser = AppUser.builder()
                .id(2L)
                .username("alice")
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("ACME")
                .email("contact@acme.com")
                .user(otherUser)
                .build();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> clientService.findById(1L, user));
    }

    @Test
    void updateShouldUpdateClientSuccessfully() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .name("Old")
                .email("old@test.com")
                .user(user)
                .build();

        ClientRequest request = new ClientRequest(
                "New",
                "new@test.com",
                "0600000000",
                "Lyon"
        );

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2 devis, dernier = "DEV-2025-002"
        mockQuoteInformations(1L, 2, "DEV-2025-002");

        // Act
        ClientResponse response = clientService.update(1L, request, user);

        // Assert
        assertEquals("New", response.name());
        assertEquals("new@test.com", response.email());
        assertEquals(2, response.quoteCount());
    }

    @Test
    void deleteShouldDeleteClientSuccessfully() {

        // Arrange
        AppUser user = AppUser.builder()
                .id(1L)
                .build();

        Client client = Client.builder()
                .id(1L)
                .user(user)
                .build();

        when(clientRepository.findById(1L))
                .thenReturn(Optional.of(client));

        // Act
        clientService.delete(1L, user);

        // Assert
        verify(clientRepository, times(1))
                .delete(client);
    }

    private void mockQuoteInformations(
            Long clientId,
            int quoteCount,
            String lastQuoteNumber
    ) {
        // Si clientId est null, on utilise any() pour matcher n'importe quel id
        if (clientId == null) {
            when(quoteRepository.countByClient_Id(any()))
                    .thenReturn(quoteCount);

            if (lastQuoteNumber == null) {
                when(quoteRepository
                        .findTopByClient_IdOrderByCreatedAtDesc(any()))
                        .thenReturn(Optional.empty());
            } else {
                Quote quote = Quote.builder()
                        .number(lastQuoteNumber)
                        .build();
                when(quoteRepository
                        .findTopByClient_IdOrderByCreatedAtDesc(any()))
                        .thenReturn(Optional.of(quote));
            }

        } else {
            when(quoteRepository.countByClient_Id(clientId))
                    .thenReturn(quoteCount);

            if (lastQuoteNumber == null) {
                when(quoteRepository
                        .findTopByClient_IdOrderByCreatedAtDesc(clientId))
                        .thenReturn(Optional.empty());
            } else {
                Quote quote = Quote.builder()
                        .number(lastQuoteNumber)
                        .build();
                when(quoteRepository
                        .findTopByClient_IdOrderByCreatedAtDesc(clientId))
                        .thenReturn(Optional.of(quote));
            }
        }
    }

}
