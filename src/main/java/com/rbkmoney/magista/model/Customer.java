package com.rbkmoney.magista.model;

import java.time.Instant;

/**
 * Created by tolkonepiu on 08.08.16.
 */
public class Customer {

    private String id;

    private int shopId;

    private String merchantId;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", shopId='" + shopId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
