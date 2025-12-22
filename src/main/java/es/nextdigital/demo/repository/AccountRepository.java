package es.nextdigital.demo.repository;

import es.nextdigital.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByIban(String iban);
}
