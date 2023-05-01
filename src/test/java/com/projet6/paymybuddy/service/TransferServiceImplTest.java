package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.*;
import com.projet6.paymybuddy.dto.ExternalTransferDto;
import com.projet6.paymybuddy.dto.InternalTransferDto;
import com.projet6.paymybuddy.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TransferServiceImplTest {
    @Autowired
    private TransferService transferService;

    @MockBean
    private TransferDAO transferDao;

    @MockBean
    private InternalTransferDAO internalTransferDao;

    @MockBean
    private BankAccountDAO bankAccountDao;

    @MockBean
    private RelationDAO relationDao;

    @MockBean
    private UserDAO userDao;

    @MockBean
    private ExternalTransferDAO externalTransferDao;

    private User sender;
    private User receiver;
    private Relation relation;
    private BankAccount bankAccount;
    private User user;

    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setBalance(BigDecimal.valueOf(100));
        sender.setId(1);

        receiver = new User();
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setId(2);

        relation = new Relation(sender, receiver);

        user = new User();
        user.setEmail("user@example.com");
        user.setBalance(BigDecimal.valueOf(150));

        bankAccount = new BankAccount();
        bankAccount.setIban("1234567890");
        bankAccount.setUser(user);
    }

    @Test
    public void itShouldDoInternalTransfer() {
        // GIVEN
        InternalTransferDto internalTransferDto = new InternalTransferDto();
        internalTransferDto.setAmount(BigDecimal.valueOf(10));
        internalTransferDto.setEmailSender(sender.getEmail());
        internalTransferDto.setEmailReceiver(receiver.getEmail());
        internalTransferDto.setDescription("Test transfer");

        sender.setBalance(BigDecimal.valueOf(90));
        when(userDao.findById(sender.getId())).thenReturn(Optional.of(sender));

        // WHEN
        InternalTransferDto result = transferService.doInternalTransfer(internalTransferDto);

        // THEN
        assertNull(result.getAmount());
    }

    @Test
    public void itShouldNotDoInternalTransferWhenSenderHasInsufficientBalance() {
        // GIVEN
        InternalTransferDto internalTransferDto = new InternalTransferDto();
        internalTransferDto.setAmount(sender.getBalance().add(BigDecimal.ONE)); // Sender's balance + 1
        internalTransferDto.setEmailSender(sender.getEmail());
        internalTransferDto.setEmailReceiver(receiver.getEmail());
        internalTransferDto.setDescription("Test transfer");

        when(relationDao.findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail())).thenReturn(relation);

        // WHEN
        InternalTransferDto result = transferService.doInternalTransfer(internalTransferDto);

        // THEN
        verify(relationDao, times(1)).findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail());
    }

    @Test
    public void itShouldFindInternalTransfersByUser() {
        // GIVEN
        InternalTransfer internalTransfer1 = new InternalTransfer();
        internalTransfer1.setUserSender(sender);
        internalTransfer1.setUserReceiver(receiver);
        internalTransfer1.setAmount(BigDecimal.valueOf(10));
        internalTransfer1.setDescription("Test transfer 1");
        internalTransfer1.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));

        InternalTransfer internalTransfer2 = new InternalTransfer();
        internalTransfer2.setUserSender(sender);
        internalTransfer2.setUserReceiver(receiver);
        internalTransfer2.setAmount(BigDecimal.valueOf(20));
        internalTransfer2.setDescription("Test transfer 2");
        internalTransfer2.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));

        List<InternalTransfer> internalTransfers = Arrays.asList(internalTransfer1, internalTransfer2);

        when(internalTransferDao.findAllByUserSender_EmailOrderByTransactionDateDesc(
                sender.getEmail()))
                .thenReturn(internalTransfers);

        // WHEN
        List<InternalTransferDto> result = transferService
                .findInternalTransferByUser(sender.getEmail());

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmailSender()).isEqualTo(sender.getEmail());
        assertThat(result.get(0).getEmailReceiver()).isEqualTo(receiver.getEmail());
        assertThat(result.get(0).getAmount()).isEqualTo(internalTransfer1.getAmount());
        assertThat(result.get(0).getDescription()).isEqualTo(internalTransfer1.getDescription());

        assertThat(result.get(1).getEmailSender()).isEqualTo(sender.getEmail());
        assertThat(result.get(1).getEmailReceiver()).isEqualTo(receiver.getEmail());
        assertThat(result.get(1).getAmount()).isEqualTo(internalTransfer2.getAmount());
        assertThat(result.get(1).getDescription()).isEqualTo(internalTransfer2.getDescription());

        verify(internalTransferDao, times(1))
                .findAllByUserSender_EmailOrderByTransactionDateDesc(sender.getEmail());
    }

    @Test
    public void itShouldFindExternalTransfersByUser() {
        // GIVEN
        ExternalTransfer externalTransfer1 = new ExternalTransfer();
        externalTransfer1.setBankAccount(bankAccount);
        externalTransfer1.setAmount(BigDecimal.valueOf(100));
        externalTransfer1.setDescription("Test external transfer 1");
        externalTransfer1.setFees(BigDecimal.valueOf(1));

        ExternalTransfer externalTransfer2 = new ExternalTransfer();
        externalTransfer2.setBankAccount(bankAccount);
        externalTransfer2.setAmount(BigDecimal.valueOf(200));
        externalTransfer2.setDescription("Test external transfer 2");
        externalTransfer2.setFees(BigDecimal.valueOf(2));

        ExternalTransfer[] externalTransfers = {externalTransfer1, externalTransfer2};

        when(externalTransferDao.findAllByBankAccount_User_EmailOrderByTransactionDateDesc(user.getEmail()))
                .thenReturn(externalTransfers);

        // WHEN
        List<ExternalTransferDto> result = transferService.findExternalTransferByUser(user.getEmail());

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIbanUser()).isEqualTo(bankAccount.getIban());
        assertThat(result.get(0).getAmountUser()).isEqualTo(externalTransfer1.getAmount());
        assertThat(result.get(0).getDescription()).isEqualTo(externalTransfer1.getDescription());
        assertThat(result.get(0).getFees()).isEqualTo(externalTransfer1.getFees());

        assertThat(result.get(1).getIbanUser()).isEqualTo(bankAccount.getIban());
        assertThat(result.get(1).getAmountUser()).isEqualTo(externalTransfer2.getAmount());
        assertThat(result.get(1).getDescription()).isEqualTo(externalTransfer2.getDescription());
        assertThat(result.get(1).getFees()).isEqualTo(externalTransfer2.getFees());

        verify(externalTransferDao, times(1)).findAllByBankAccount_User_EmailOrderByTransactionDateDesc(user.getEmail());
    }

    @Test
    public void itShouldDoExternalTransfer() {
        // GIVEN
        ExternalTransferDto externalTransferDto = new ExternalTransferDto();
        externalTransferDto.setIbanUser(bankAccount.getIban());
        externalTransferDto.setEmailUser(user.getEmail());
        externalTransferDto.setAmountUser(BigDecimal.valueOf(100));
        externalTransferDto.setDescription("Test external transfer");
        externalTransferDto.setFees(BigDecimal.ZERO);

        when(bankAccountDao.findBankAccountByIbanAndUser_Email(bankAccount.getIban(),
                user.getEmail())).thenReturn(bankAccount);
        when(transferDao.save(any(ExternalTransfer.class))).thenAnswer(invocation -> {
            ExternalTransfer externalTransfer = invocation.getArgument(0);
            externalTransfer.setId(1); // we assign a fake ID for testing purposes
            return externalTransfer;
        });

        // WHEN
        ExternalTransferDto result = transferService.doExternalTransfer(externalTransferDto);

        // THEN
        assertThat(result.getIbanUser()).isEqualTo(externalTransferDto.getIbanUser());
        assertThat(result.getEmailUser()).isEqualTo(externalTransferDto.getEmailUser());
        assertThat(result.getAmountUser()).isEqualTo(externalTransferDto.getAmountUser());
        assertThat(result.getDescription()).isEqualTo(externalTransferDto.getDescription());
        assertThat(result.getFees()).isNotNull();
        assertThat(result.getId()).isNotNull();

        verify(bankAccountDao, times(1))
                .findBankAccountByIbanAndUser_Email(bankAccount.getIban(), user.getEmail());
        verify(transferDao, times(1)).save(any(ExternalTransfer.class));
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    public void itShouldFindBankAccountByIbanAndEmail() {
        // GIVEN
        String iban = "1234567890";
        String email = "user@example.com";

        when(bankAccountDao.findBankAccountByIbanAndUser_Email(iban, email)).thenReturn(bankAccount);

        // WHEN
        BankAccount result = ((TransferServiceImpl) transferService).findBankAccount(iban, email);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getIban()).isEqualTo(iban);
        assertThat(result.getUser().getEmail()).isEqualTo(email);

        verify(bankAccountDao, times(1))
                .findBankAccountByIbanAndUser_Email(iban, email);
    }

    @Test
    public void itShouldCalculateFee() {
        // GIVEN
        BigDecimal amount = BigDecimal.valueOf(200);
        BigDecimal expectedFee = BigDecimal.valueOf(1); // 0.5% of 200 is 1

        // WHEN
        BigDecimal fee = ((TransferServiceImpl) transferService).calculateFee(amount);

        // THEN
        assertThat(fee).isNotNull();
        assertThat(fee).isEqualByComparingTo(expectedFee.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void itShouldCreateExternalTransfer() {
        // GIVEN
        ExternalTransferDto dto = new ExternalTransferDto();
        dto.setAmountUser(BigDecimal.valueOf(100));
        dto.setDescription("Test external transfer");
        dto.setIbanUser(bankAccount.getIban());
        dto.setEmailUser(user.getEmail());
        BigDecimal fee = BigDecimal.valueOf(0.5);

        // WHEN
        ExternalTransfer externalTransfer = ((TransferServiceImpl) transferService).createExternalTransfer(dto, bankAccount, fee);

        // THEN
        assertThat(externalTransfer).isNotNull();
        assertThat(externalTransfer.getAmount()).isEqualByComparingTo(dto.getAmountUser());
        assertThat(externalTransfer.getDescription()).isEqualTo(dto.getDescription());
        assertThat(externalTransfer.getFees()).isEqualByComparingTo(fee);
        assertThat(externalTransfer.getBankAccount()).isEqualTo(bankAccount);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime transactionDateTime = timestampToLocalDateTime((Timestamp) externalTransfer.getTransactionDate());
        assertThat(transactionDateTime)
                .isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }

    // A method for converting a Timestamp to a LocalDateTime.
    LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault());
    }

    @Test
    public void itShouldUpdateUserBalance() {
        // GIVEN
        User user = new User();
        user.setBalance(BigDecimal.valueOf(100));

        ExternalTransfer externalTransfer = new ExternalTransfer();
        externalTransfer.setAmount(BigDecimal.valueOf(50));
        BigDecimal fee = BigDecimal.valueOf(0.25);

        // WHEN
        ((TransferServiceImpl) transferService).updateUserBalance(user, externalTransfer, fee);

        // THEN
        assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(149.75));
    }


}
