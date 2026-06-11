package com.pers.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ACCOUNT_NOT_FOUND("account.not.found"),
    ACCOUNT_NOT_OWNED("account.not.owned"),
    ACCOUNT_CLOSED("account.closed"),
    ACCOUNT_ALREADY_CLOSED("account.already.closed"),
    ACCOUNT_CLOSE_BALANCE_NOT_ZERO("account.close.balance.not.zero"),
    ACCOUNT_CLOSE_CHECK_FAILED("account.close.check.failed"),
    ACCOUNT_SAME("account.same"),
    ACCOUNT_INSUFFICIENT_FUNDS("account.insufficient.funds"),
    ACCOUNT_RECIPIENT_UNAVAILABLE("account.recipient.unavailable"),


    CARD_NOT_FOUND("card.not.found"),
    CARD_NUMBER_NOT_FOUND("card.number.not.found"),
    CARD_SENDER_NOT_FOUND("card.sender.not.found"),
    CARD_RECIPIENT_NOT_FOUND("card.recipient.not.found"),
    CARD_CREATE_CLOSED_ACCOUNT("card.create.closed.account"),
    CARD_CURRENCY_MISMATCH("card.currency.mismatch"),
    CARD_ACCOUNT_MISMATCH("card.account.mismatch"),
    CARD_SENDER_NOT_OWNED("card.sender.not.owned"),
    CARD_SENDER_UNAVAILABLE("card.sender.unavailable"),
    CARD_RECIPIENT_UNAVAILABLE("card.recipient.unavailable"),

    CLIENT_NOT_FOUND("client.not.found"),

    TRANSFER_NOT_FOUND("transfer.not.found"),
    TRANSFER_SAME_CARD("transfer.same.card"),
    TRANSFER_SAME_ACCOUNT("transfer.same.account"),
    TRANSFER_PHONE_RUB_ONLY("transfer.phone.rub.only"),
    TRANSFER_RECIPIENT_PHONE_NOT_FOUND("transfer.recipient.phone.not.found"),
    TRANSFER_RECIPIENT_NOT_FOUND("transfer.recipient.not.found"),
    TRANSFER_RECIPIENT_BLOCKED("transfer.recipient.blocked"),
    TRANSFER_RECIPIENT_UNAVAILABLE("transfer.recipient.unavailable"),
    TRANSFER_RECIPIENT_RUB_CARD_UNAVAILABLE("transfer.recipient.rub.card.unavailable"),
    TRANSFER_RECIPIENT_NAME_MISSING("transfer.recipient.name.missing"),
    TRANSFER_REFUND_SENDER_CARD_NOT_FOUND("transfer.refund.sender.card.not.found"),
    TRANSFER_REFUND_SENDER_ACCOUNT_NOT_FOUND("transfer.refund.sender.account.not.found"),

    CURRENCY_RATE_NOT_FOUND("currency.rate.not.found"),
    OUTBOX_SERIALIZE_FAILED("outbox.serialize.failed"),
    AUTH_PRINCIPAL_UNSUPPORTED("auth.principal.unsupported");

    private final String key;

    ErrorCode(String key) {
        this.key = key;
    }

}

