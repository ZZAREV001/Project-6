package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dao.*;
import com.projet6.paymybuddy.dto.ExternalTransferDto;
import com.projet6.paymybuddy.dto.InternalTransferDto;
import com.projet6.paymybuddy.exception.DataNotFoundException;
import com.projet6.paymybuddy.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferDAO transferDao;

    private final InternalTransferDAO internalTransferDao;

    private final RelationDAO relationDao;

    private final UserDAO userDao;

    private final ExternalTransferDAO externalTransferDao;

    private final BankAccountDAO bankAccountDao;

    @Transactional
    @Override
    public InternalTransferDto doInternalTransfer(InternalTransferDto internalTransferDto) {
        Optional<Relation> relationOptional = Optional.ofNullable(relationDao
                .findByOwner_EmailAndBuddy_Email(internalTransferDto.getEmailSender(),
                        internalTransferDto.getEmailReceiver()));
        return relationOptional.filter(relation -> internalTransferDto.getAmount()
                        .compareTo(relation.getOwner().getBalance()) <= 0)
                .map(relation -> {
                    InternalTransfer internalTransfer = new InternalTransfer();
                    internalTransfer.setUserSender(relation.getOwner());
                    internalTransfer.setUserReceiver(relation.getBuddy());
                    internalTransfer.setAmount(internalTransferDto.getAmount());
                    internalTransfer.setDescription(internalTransferDto.getDescription());
                    internalTransfer.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));

                    transferDao.save(internalTransfer);

                    internalTransferDto.setId(internalTransfer.getId());
                    relation.getOwner().setBalance(relation.getOwner()
                            .getBalance().subtract(internalTransferDto.getAmount()));
                    relation.getBuddy().setBalance(relation.getBuddy()
                            .getBalance().add(internalTransferDto.getAmount()));
                    userDao.save(relation.getOwner());
                    userDao.save(relation.getBuddy());

                    return internalTransferDto;
                })
                .orElseThrow(() -> new DataNotFoundException("the 2 users are not friends"));
    }

    @Override
    public List<InternalTransferDto> findInternalTransferByUser(String emailOwner) {
        return internalTransferDao
                .findAllByUserSender_EmailOrderByTransactionDateDesc(emailOwner)
                .stream()
                .map(internalTransfer -> {
                    InternalTransferDto dto = new InternalTransferDto();
                    dto.setEmailSender(internalTransfer.getUserSender().getEmail());
                    dto.setEmailReceiver(internalTransfer.getUserReceiver().getEmail());
                    dto.setAmount(internalTransfer.getAmount());
                    dto.setId(internalTransfer.getId());
                    dto.setDescription(internalTransfer.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExternalTransferDto> findExternalTransferByUser(String emailOwner) {
        List<ExternalTransferDto> externalTransferDtos = new ArrayList<>();
        for (ExternalTransfer externalTransfer : externalTransferDao
                .findAllByBankAccount_User_EmailOrderByTransactionDateDesc(emailOwner)) {
            ExternalTransferDto dto = new ExternalTransferDto();
            dto.setIbanUser(externalTransfer.getBankAccount().getIban());
            dto.setDescription(externalTransfer.getDescription());
            dto.setAmountUser(externalTransfer.getAmount());
            dto.setFees(externalTransfer.getFees());
            externalTransferDtos.add(dto);
        }
        return externalTransferDtos;
    }

    @Override
    public ExternalTransferDto doExternalTransfer(ExternalTransferDto externalTransferDto) {
        BankAccount bankAccount = findBankAccount(externalTransferDto.getIbanUser(), externalTransferDto.getEmailUser());
        User user = bankAccount.getUser();
        BigDecimal fee = calculateFee(externalTransferDto.getAmountUser());
        ExternalTransfer externalTransfer = createExternalTransfer(externalTransferDto, bankAccount, fee);
        transferDao.save(externalTransfer);
        externalTransferDto.setId(externalTransfer.getId());
        updateUserBalance(user, externalTransfer, fee);
        userDao.save(user);
        return externalTransferDto;
    }

    /* package-private */ BankAccount findBankAccount(String iban, String email) {
        return bankAccountDao.findBankAccountByIbanAndUser_Email(iban, email);
    }

    /* package-private */ BigDecimal calculateFee(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.005));
    }

    /* package-private */ExternalTransfer createExternalTransfer(ExternalTransferDto dto,
                                                                 BankAccount bankAccount,
                                                                 BigDecimal fee) {
        ExternalTransfer externalTransfer = new ExternalTransfer();
        externalTransfer.setAmount(dto.getAmountUser());
        externalTransfer.setDescription(dto.getDescription());
        externalTransfer.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));
        externalTransfer.setFees(fee);
        externalTransfer.setBankAccount(bankAccount);
        return externalTransfer;
    }

    /* package-private */ void updateUserBalance(User user,
                                                 ExternalTransfer externalTransfer,
                                                 BigDecimal fee) {
        user.setBalance(user.getBalance().add(externalTransfer.getAmount().subtract(fee)));
    }

}

