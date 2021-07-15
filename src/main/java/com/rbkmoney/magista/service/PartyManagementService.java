package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.Contract;
import com.rbkmoney.damsel.domain.PayoutTool;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.InternalUser;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.UserInfo;
import com.rbkmoney.damsel.payment_processing.UserType;
import com.rbkmoney.magista.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final PartyManagementSrv.Iface partyManagementClient;
    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    @SneakyThrows
    public PayoutToolInfo getPayoutToolInfo(String partyId, String shopId, String payoutToolId) {
        Shop shop = partyManagementClient.getShop(userInfo, partyId, shopId);
        Contract contract = partyManagementClient.getContract(userInfo, partyId, shop.getContractId());
        return contract.getPayoutTools().stream()
                .filter(pt -> payoutToolId.equals(pt.getId()))
                .findAny()
                .map(PayoutTool::getPayoutToolInfo)
                .orElseThrow(() -> new NotFoundException(
                        String.format("PayoutToolInfo not found for partyId=%s, shopId=%s, payoutToolId=%s",
                                partyId, shopId, payoutToolId)));
    }
}
