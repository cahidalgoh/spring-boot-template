package es.nextdigital.demo.service.impl;

import es.nextdigital.demo.model.Transaction;
import es.nextdigital.demo.repository.TransactionRepository;
import es.nextdigital.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;


    @Override
    public List<Transaction> getTransactionsByAccountIban(String iban) {
        return transactionRepository.findByAccountIbanOrderByTimestampDesc(iban);
    }
}
