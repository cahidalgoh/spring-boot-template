package es.nextdigital.demo.service.impl;

import es.nextdigital.demo.model.*;
import es.nextdigital.demo.repository.AccountRepository;
import es.nextdigital.demo.repository.CardRepository;
import es.nextdigital.demo.repository.TransactionRepository;
import es.nextdigital.demo.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction transfer(String cardNumber, String destinationIban, BigDecimal amount) {
        // 1. Validar tarjeta
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada"));
        if (!card.isActive()) throw new IllegalStateException("La tarjeta no está activada");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Importe inválido");

        // 2. Validar IBAN destino (Solo para bancos en España)
        if (!destinationIban.matches("^ES\\d{22}$")) {
            throw new IllegalArgumentException("IBAN inválido");
        }

        // 3. Validar cuentas
        Account origin = card.getAccount();
        Account destination = accountRepository.findByIban(destinationIban);
        if (destination == null) throw new IllegalArgumentException("Cuenta destino no encontrada");
        if (origin.getIban().equals(destination.getIban()))
            throw new IllegalArgumentException("La cuenta destino no puede ser la misma que la origen");

        // 4. Calcular comisión si es otro banco
        BigDecimal commission = BigDecimal.ZERO;
        if (!origin.getBankId().equals(destination.getBankId())) {
            commission = amount.multiply(BigDecimal.valueOf(0.01)); // 1% de comisión
        }
        BigDecimal totalDebit = amount.add(commission);

        // 5. Validar saldo/crédito según tipo de tarjeta
        if (card.getType() == CardType.DEBIT) {
            if (origin.getBalance().compareTo(totalDebit) < 0)
                throw new IllegalStateException("Saldo insuficiente");
            origin.setBalance(origin.getBalance().subtract(totalDebit));
        } else if (card.getType() == CardType.CREDIT) {
            if (totalDebit.compareTo(card.getCreditAvailable()) > 0)
                throw new IllegalStateException("Límite de crédito superado");
            card.setCreditAvailable(card.getCreditAvailable().subtract(totalDebit));
            cardRepository.save(card);
        }

        // 6. Actualizar saldo destino
        destination.setBalance(destination.getBalance().add(amount));
        accountRepository.save(origin);
        accountRepository.save(destination);

        // 7. Registrar transacciones
        Transaction outTx = new Transaction();
        outTx.setAccount(origin);
        outTx.setType(TransactionType.TRANSFER_OUT);
        outTx.setAmount(amount);
        outTx.setCommission(commission);
        outTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(outTx);

        Transaction inTx = new Transaction();
        inTx.setAccount(destination);
        inTx.setType(TransactionType.TRANSFER_IN);
        inTx.setAmount(amount);
        inTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(inTx);

        return outTx; // devolvemos la transacción de salida como referencia
    }
}
