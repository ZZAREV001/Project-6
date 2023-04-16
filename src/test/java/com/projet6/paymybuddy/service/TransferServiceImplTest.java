package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.*;
import com.projet6.paymybuddy.dto.InternalTransferDto;
import com.projet6.paymybuddy.exception.DataNotFoundException;
import com.projet6.paymybuddy.model.InternalTransfer;
import com.projet6.paymybuddy.model.Relation;
import com.projet6.paymybuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private RelationDAO relationDao;

    @MockBean
    private UserDAO userDao;

    @MockBean
    private ExternalTransferDAO externalTransferDao;

    @MockBean
    private BankAccountDAO bankAccountDao;

    private User sender;
    private User receiver;
    private Relation relation;

    @BeforeEach
    public void setUp() {
        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setBalance(BigDecimal.valueOf(100));

        receiver = new User();
        receiver.setEmail("receiver@example.com");
        receiver.setBalance(BigDecimal.valueOf(50));

        relation = new Relation(sender, receiver);
    }

    @Test
    public void itShouldDoInternalTransfer() {
        // GIVEN
        InternalTransferDto internalTransferDto = new InternalTransferDto();
        internalTransferDto.setAmount(BigDecimal.valueOf(10));
        internalTransferDto.setEmailSender(sender.getEmail());
        internalTransferDto.setEmailReceiver(receiver.getEmail());
        internalTransferDto.setDescription("Test transfer");

        when(relationDao.findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail())).thenReturn(relation);

        // WHEN
        InternalTransferDto result = transferService.doInternalTransfer(internalTransferDto);

        // THEN
        assertThat(result.getAmount()).isEqualTo(internalTransferDto.getAmount());
        assertThat(result.getEmailSender()).isEqualTo(internalTransferDto.getEmailSender());
        assertThat(result.getEmailReceiver()).isEqualTo(internalTransferDto.getEmailReceiver());
        assertThat(result.getDescription()).isEqualTo(internalTransferDto.getDescription());

        verify(relationDao, times(1)).findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail());
        verify(transferDao, times(1)).save(any(InternalTransfer.class));
        verify(userDao, times(2)).save(any(User.class));
    }

    @Test
    public void itShouldNotDoInternalTransferIfNotFriends() {
        // GIVEN
        InternalTransferDto internalTransferDto = new InternalTransferDto();
        internalTransferDto.setAmount(BigDecimal.valueOf(10));
        internalTransferDto.setEmailSender(sender.getEmail());
        internalTransferDto.setEmailReceiver(receiver.getEmail());
        internalTransferDto.setDescription("Test transfer");

        when(relationDao.findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail())).thenReturn(null);

        // WHEN & THEN
        assertThatThrownBy(() -> transferService.doInternalTransfer(internalTransferDto))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("the 2 users are not friends");

        verify(relationDao, times(1)).findByOwner_EmailAndBuddy_Email(sender.getEmail(), receiver.getEmail());
        verify(transferDao, never()).save(any(InternalTransfer.class));
        verify(userDao, never()).save(any(User.class));
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
}
