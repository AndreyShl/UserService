package org.example.unitTest;


import org.example.dto.PaymentCarddto;
import org.example.mapper.PaymentCardMapper;
import org.example.exception.CardLimitExceededException;
import org.example.exception.CardNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.model.entity.PaymentCard;
import org.example.model.entity.User;
import org.example.model.repository.PaymentCardsRepository;
import org.example.model.repository.UsersRepository;
import org.example.service.PaymentCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentCardServiceTest {

    @Mock
    private PaymentCardsRepository cardRepository;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PaymentCardMapper mapper;

    @InjectMocks
    private PaymentCardService cardService;

    private User user;
    private PaymentCard card;
    private PaymentCarddto cardDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setName("Andrei");

        cardDTO = new PaymentCarddto();
        cardDTO.setId(1);
        cardDTO.setUserId(user.getId());
        cardDTO.setCardNumber("1234567812345678");
        cardDTO.setHolder("Andrei");
        cardDTO.setExpirationDate(LocalDateTime.now().plusYears(2));
        cardDTO.setActive(true);


        card = new PaymentCard();
        card.setId(1);
        card.setCardNumber(cardDTO.getCardNumber());
        card.setHolder(cardDTO.getHolder());
        card.setExpirationDate(cardDTO.getExpirationDate());
        card.setUser(user);
        card.setActive(cardDTO.getActive());
    }

    @Test
    void createCard_success() {
        when(cardRepository.countByUserId(user.getId())).thenReturn(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toEntity(cardDTO)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(mapper.toDTO(card)).thenReturn(cardDTO);

        PaymentCarddto result = cardService.createCard(cardDTO);

        assertEquals(cardDTO.getId(), result.getId());
        assertTrue(result.getActive());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void createCard_exceedsLimit() {
        when(cardRepository.countByUserId(user.getId())).thenReturn(5);

        assertThrows(CardLimitExceededException.class, () -> cardService.createCard(cardDTO));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void createCard_userNotFound() {
        when(cardRepository.countByUserId(user.getId())).thenReturn(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.createCard(cardDTO));
    }

    @Test
    void getCardById_success() {
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));
        when(mapper.toDTO(card)).thenReturn(cardDTO);

        PaymentCarddto result = cardService.getCardById(1);

        assertEquals(cardDTO.getId(), result.getId());
    }

    @Test
    void getCardById_notFound() {
        when(cardRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1));
    }

    @Test
    void getCardsByUser_success() {
        when(cardRepository.findByUserId(user.getId())).thenReturn(List.of(card));
        when(mapper.toDTO(card)).thenReturn(cardDTO);

        List<PaymentCarddto> cards = cardService.getCardsByUser(user.getId());

        assertEquals(1, cards.size());
        assertEquals(cardDTO.getId(), cards.get(0).getId());
    }

    @Test
    void updateCard_success() {
        PaymentCarddto updatedDTO = new PaymentCarddto();
        updatedDTO.setId(1);
        updatedDTO.setUserId(user.getId());
        updatedDTO.setCardNumber("9999888877776666");
        updatedDTO.setHolder("Andrei Updated");
        updatedDTO.setExpirationDate(cardDTO.getExpirationDate());
        updatedDTO.setActive(false);

        when(cardRepository.findById(1)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(mapper.toDTO(card)).thenReturn(updatedDTO);

        PaymentCarddto result = cardService.updateCard(1, updatedDTO);

        assertEquals("9999888877776666", result.getCardNumber());
        assertFalse(result.getActive());
    }

    @Test
    void updateCard_notFound() {
        when(cardRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.updateCard(1, cardDTO));
    }

    @Test
    void activateCard_success() {
        card.setActive(false);
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        cardService.activateCard(1);

        assertTrue(card.isActive());
    }

    @Test
    void deactivateCard_success() {
        card.setActive(true);
        when(cardRepository.findById(1)).thenReturn(Optional.of(card));

        cardService.deactivateCard(1);

        assertFalse(card.isActive());
    }
}
