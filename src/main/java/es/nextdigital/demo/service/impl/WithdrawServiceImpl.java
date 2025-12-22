package es.nextdigital.demo.service.impl;

import es.nextdigital.demo.model.*;
import es.nextdigital.demo.repository.ATMRepository;
import es.nextdigital.demo.repository.AccountRepository;
import es.nextdigital.demo.repository.CardRepository;
import es.nextdigital.demo.repository.TransactionRepository;
import es.nextdigital.demo.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ATMRepository atmRepository;

    private static final BigDecimal INTERBANK_FEE_RATE = BigDecimal.valueOf(0.02);


    @Override
    @Transactional
    public Transaction withdraw(String cardNumber, Long atmId, BigDecimal amount) {

        Card card = cardRepository.findByCardNumber(cardNumber);

        if (card == null) {
            throw new IllegalArgumentException("Tarjeta no encontrada");
        }

        ATM atm = atmRepository.findById(atmId).orElseThrow();
        Account account = card.getAccount();

        if (!card.isActive()) {
            throw new IllegalStateException("La tarjeta no está activada");
        }
        if (amount.compareTo(card.getWithdrawLimit()) > 0) {
            throw new IllegalArgumentException("El importe supera el límite de la tarjeta");
        }

        BigDecimal fee = new BigDecimal("0.00");
        if (!atm.getBankId().equals(card.getBankId())) {
            // Redondeo de comisiones: Para evitar decimales infinitos,
            fee = amount.multiply(INTERBANK_FEE_RATE).setScale(2, RoundingMode.HALF_UP); //  2% comisión interbancaria
        }

        if (card.getType() == CardType.DEBIT) {
            if (account.getBalance().compareTo(amount.add(fee)) < 0) {
                throw new IllegalStateException("Saldo insuficiente");
            }
            account.setBalance(account.getBalance().subtract(amount).subtract(fee));
        } else if (card.getType() == CardType.CREDIT) {
            if (amount.add(fee).compareTo(card.getCreditAvailable()) > 0) {
                throw new IllegalStateException("Límite de crédito superado");
            }

            // Consumir crédito disponible
            card.setCreditAvailable(card.getCreditAvailable().subtract(amount).subtract(fee));
            cardRepository.save(card);
        }

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.WITHDRAWAL);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            Transaction feeTx = new Transaction();
            feeTx.setAccount(account);
            feeTx.setType(TransactionType.FEE);
            feeTx.setAmount(fee);
            feeTx.setTimestamp(LocalDateTime.now());
            transactionRepository.save(feeTx);
        }

        accountRepository.save(account);
        return tx;
    }
}
