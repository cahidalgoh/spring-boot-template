package es.nextdigital.demo.service.impl;

import es.nextdigital.demo.model.*;
import es.nextdigital.demo.repository.ATMRepository;
import es.nextdigital.demo.repository.AccountRepository;
import es.nextdigital.demo.repository.CardRepository;
import es.nextdigital.demo.repository.TransactionRepository;
import es.nextdigital.demo.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ATMRepository atmRepository;

    @Override
    @Transactional
    public Transaction deposit(String cardNumber, Long atmId, BigDecimal amount) {
        Card card = cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
        if (!card.isActive()) throw new IllegalStateException("La tarjeta no está activada");
        ATM atm = atmRepository.findById(atmId).orElseThrow();
        if (!atm.getBankId().equals(card.getBankId()))
            throw new IllegalArgumentException("Ingresos solo en cajeros del mismo banco");
        // if (!atm.isSupportsDeposit()) throw new IllegalArgumentException("El cajero no admite ingresos");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Importe inválido");
        Account account = card.getAccount();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);
        return tx;
    }
}
