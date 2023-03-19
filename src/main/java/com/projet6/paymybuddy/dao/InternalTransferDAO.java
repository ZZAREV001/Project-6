package com.projet6.paymybuddy.dao;

import com.projet6.paymybuddy.model.InternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InternalTransferDAO extends JpaRepository<InternalTransfer, Integer> {

    List<InternalTransfer> findAllByUserSender_EmailOrderByTransactionDateDesc(
            String emailOwner);

}
