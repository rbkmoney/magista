package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.PayoutTool;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final PartyManagementSrv.Iface partyManagementClient;

    @SneakyThrows
    public PayoutToolInfo getPayoutToolInfo(String partyId, String shopId, String payoutToolId) {
        Shop shop = partyManagementClient.getShop(new UserInfo(), partyId, shopId);
        Contract contract = partyManagementClient.getContract(new UserInfo(), partyId, shop.getContractId());
        return contract.getPayoutTools().stream()
                .filter(pt -> payoutToolId.equals(pt.getId()))
                .findAny()
                .map(PayoutTool::getPayoutToolInfo)
                .orElse(null);
    }
}
