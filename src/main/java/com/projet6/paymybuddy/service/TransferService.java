package com.projet6.paymybuddy.service;

import com.projet6.paymybuddy.dto.ExternalTransferDto;
import com.projet6.paymybuddy.dto.InternalTransferDto;

import java.util.List;

public interface TransferService {

    InternalTransferDto doInternalTransfer(InternalTransferDto internalTransferDto);
    List<InternalTransferDto> findInternalTransferByUser(String emailOwner);
    ExternalTransferDto doExternalTransfer(ExternalTransferDto externalTransferDto);
    List<ExternalTransferDto> findExternalTransferByUser(String username);
}
