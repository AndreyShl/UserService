package org.example.service;

import org.example.dto.PaymentCarddto;
import org.example.mapper.PaymentCardMapper;
import org.example.exception.CardLimitExceededException;
import org.example.exception.CardNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.model.entity.PaymentCard;
import org.example.model.entity.User;
import org.example.model.repository.PaymentCardsRepository;
import org.example.model.repository.UsersRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentCardService {

    private final PaymentCardsRepository cardRepository;
    private final UsersRepository userRepository;
    private final PaymentCardMapper mapper;

    public PaymentCardService(PaymentCardsRepository cardRepository,
                              UsersRepository userRepository, PaymentCardMapper mapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(value = "cards", key = "#cardDTO.userId")
    public PaymentCarddto createCard(PaymentCarddto cardDTO) {
        Integer userId = cardDTO.getUserId();

        int count = cardRepository.countByUserId(userId);
        if (count >= 5) {
            throw new CardLimitExceededException(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        PaymentCard card = mapper.toEntity(cardDTO);
        card.setUser(user);
        card.setActive(true);

        return mapper.toDTO(cardRepository.save(card));
    }
    @Cacheable(value = "cards", key = "#id")
    public PaymentCarddto getCardById(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return mapper.toDTO(card);
    }
    @Cacheable(value = "cards", key = "#userId")
    public List<PaymentCarddto> getCardsByUser(Integer userId) {
        return cardRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "cards", key = "#updatedDTO.userId")
    public PaymentCarddto updateCard(Integer id, PaymentCarddto updatedDTO) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        card.setCardNumber(updatedDTO.getCardNumber());
        card.setHolder(updatedDTO.getHolder());
        card.setExpirationDate(updatedDTO.getExpirationDate());
        card.setActive(updatedDTO.getActive());

        return mapper.toDTO(cardRepository.save(card));
    }

    @Transactional
    @CacheEvict(value = "cards", key = "#id")
    public void activateCard(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        card.setActive(true);
    }


    @Transactional
    @CacheEvict(value = "cards", key = "#id")
    public void deactivateCard(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        card.setActive(false);
    }

}