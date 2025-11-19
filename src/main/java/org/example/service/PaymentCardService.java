package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PaymentCarddto;
import org.example.mapper.PaymentCardMapper;
import org.example.exception.CardLimitExceededException;
import org.example.exception.CardNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.model.entity.PaymentCard;
import org.example.model.entity.User;
import org.example.model.repository.PaymentCardsRepository;
import org.example.model.repository.UsersRepository;
import org.example.specification.PaymentCardSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardsRepository cardRepository;
    private final UsersRepository userRepository;
    private final PaymentCardMapper mapper;
    private static final int MAX_CARDS_PER_USER = 5;


    @Transactional
    @Caching(
            put = { @CachePut(value = "cardById", key = "#result.id") },
            evict = { @CacheEvict(value = "cardsByUserId", key = "#cardDTO.userId") }
    )
    public PaymentCarddto createCard(PaymentCarddto cardDTO) {
        Integer userId = cardDTO.getUserId();

        int count = cardRepository.countByUserId(userId);
        if (count >= MAX_CARDS_PER_USER) {
            throw new CardLimitExceededException(userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        PaymentCard card = mapper.toEntity(cardDTO);
        card.setUser(user);
        card.setActive(true);

        return mapper.toDTO(cardRepository.save(card));
    }
    @Cacheable(value = "cardById", key = "#id")
    public PaymentCarddto getCardById(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return mapper.toDTO(card);
    }
    @Cacheable(value = "cardsByUserId", key = "#userId")
    public List<PaymentCarddto> getCardsByUser(Integer userId) {
        return cardRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "cardById",key = "#id")},
            evict = {@CacheEvict(value = "cardsByUserId",key = "#updatedDTO.userId")}
    )
    public PaymentCarddto updateCard(Integer id, PaymentCarddto updatedDTO) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        mapper.updateCardFromDTO(updatedDTO, card);
        PaymentCard updatedCard = cardRepository.save(card);

        return mapper.toDTO(updatedCard);
    }

    public Page<PaymentCarddto> getAllCards(int page, int size, String name, String surname) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<PaymentCard> spec = Specification
                .where(PaymentCardSpecification.userNameContains(name))
                .and(PaymentCardSpecification.userSurnameContains(surname));

        return cardRepository.findAll(spec, pageable)
                .map(mapper::toDTO);
    }

    @Transactional
    @Caching(
            put = {@CachePut(value = "cardById",key = "#id")},
            evict = {@CacheEvict(value = "cardsByUserId",key = "#card.user.id")}
    )
    public void activateCard(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        card.setActive(true);
        cardRepository.save(card);
    }


    @Transactional
    @Caching(
            put = {@CachePut(value = "cardById",key = "#id")},
            evict = {@CacheEvict(value = "cardsByUserId",key = "#card.user.id")}
    )
    public void deactivateCard(Integer id) {
        PaymentCard card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        card.setActive(false);
        cardRepository.save(card);
    }

}