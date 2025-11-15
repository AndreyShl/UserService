package org.example.model.repository;

import org.example.model.entity.PaymentCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
// - JPA provides
// - findById(ID id)
@Repository
public interface PaymentCardsRepository extends JpaRepository<PaymentCard,Integer> {

    List<PaymentCard> findByUserId(Integer userId);

    int countByUserId(Integer userId);

    @Query(value = "SELECT * FROM payment_cards WHERE card_number = :cardNumber", nativeQuery = true)
    Optional<PaymentCard> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM PaymentCard c WHERE c.user = :userId AND c.active = true")
    List<PaymentCard> findActiveCardsByUser(Long userId);

}
