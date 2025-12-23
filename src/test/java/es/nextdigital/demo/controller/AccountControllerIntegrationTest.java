package es.nextdigital.demo.controller;

import es.nextdigital.demo.model.Account;
import es.nextdigital.demo.model.Transaction;
import es.nextdigital.demo.model.TransactionType;
import es.nextdigital.demo.repository.AccountRepository;
import es.nextdigital.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();

        account = new Account();
        account.setIban("ES111111111111111111111111");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setBankId("BANK1");
        accountRepository.save(account);

        // timestamps distintos para asegurar orden estable
        Transaction deposit = new Transaction();
        deposit.setAccount(account);
        deposit.setType(TransactionType.DEPOSIT);
        deposit.setAmount(BigDecimal.valueOf(500));
        deposit.setCommission(BigDecimal.ZERO);
        deposit.setTimestamp(LocalDateTime.now().minusSeconds(2));
        transactionRepository.save(deposit);

        Transaction withdrawal = new Transaction();
        withdrawal.setAccount(account);
        withdrawal.setType(TransactionType.WITHDRAWAL);
        withdrawal.setAmount(BigDecimal.valueOf(200));
        withdrawal.setCommission(BigDecimal.ZERO);
        withdrawal.setTimestamp(LocalDateTime.now().minusSeconds(1));
        transactionRepository.save(withdrawal);
    }

    @Test
    void testGetTransactionsByAccountIban() throws Exception {
        mockMvc.perform(get("/accounts/" + account.getIban() + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[0].amount").value(200))
                .andExpect(jsonPath("$[1].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(500));
    }

    @Test
    void testGetTransactionsWithCommission() throws Exception {
        // Creamos una cuenta destino en otro banco
        Account destination = new Account();
        destination.setIban("ES222222222222222222222222");
        destination.setBalance(BigDecimal.valueOf(500));
        destination.setBankId("BANK2");
        accountRepository.save(destination);

        // Transferencia saliente con comisión (más reciente)
        Transaction transferOut = new Transaction();
        transferOut.setAccount(account);
        transferOut.setType(TransactionType.TRANSFER_OUT);
        transferOut.setAmount(BigDecimal.valueOf(100));
        transferOut.setCommission(BigDecimal.valueOf(1));
        transferOut.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transferOut);

        // Transferencia entrante en la cuenta destino
        Transaction transferIn = new Transaction();
        transferIn.setAccount(destination);
        transferIn.setType(TransactionType.TRANSFER_IN);
        transferIn.setAmount(BigDecimal.valueOf(100));
        transferIn.setCommission(BigDecimal.ZERO);
        transferIn.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transferIn);

        // Validamos movimientos de la cuenta origen
        mockMvc.perform(get("/accounts/" + account.getIban() + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFER_OUT"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].commission").value(1.0));

        // Validamos movimientos de la cuenta destino
        mockMvc.perform(get("/accounts/" + destination.getIban() + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFER_IN"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].commission").value(0.0));
    }
}
