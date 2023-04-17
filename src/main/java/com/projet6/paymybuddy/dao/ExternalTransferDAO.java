package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.ExternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalTransferDAO extends JpaRepository<ExternalTransfer, Integer> {

    ExternalTransfer[] findAllByBankAccount_User_EmailOrderByTransactionDateDesc(
            String emailOwner);

}
