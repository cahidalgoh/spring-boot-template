package es.nextdigital.demo.repository;

import es.nextdigital.demo.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ATMRepository extends JpaRepository<ATM, Long> {
}
