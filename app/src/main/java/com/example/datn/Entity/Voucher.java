package com.example.datn.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Voucher {
    private String voucherId;
    private String code;
    private int discount;
    private String validFrom;
    private String validUntil;


    // Constructor không tham số
    public Voucher() {
    }

    public Voucher(String voucherId, String code, int discount, String validFrom, String validUntil) {
        this.voucherId = voucherId;
        this.code = code;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }



    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

}
