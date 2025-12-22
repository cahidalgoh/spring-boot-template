package es.nextdigital.demo.repository;

import es.nextdigital.demo.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByCardNumber(String cardNumber);
}
