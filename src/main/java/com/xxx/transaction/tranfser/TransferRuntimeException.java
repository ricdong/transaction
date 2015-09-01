package com.xxx.transaction.tranfser;

/**
 * Created by ricdong on 15-8-30.
 */
public class TransferRuntimeException extends RuntimeException {

    private int errorCode = 0;

    public static final int ERROR_RUNTIME = 0;
    public static final int ERROR_USER_NOT_FOUND = 1;
    public static final int ERROR_COIN_INSUFFICIENT = 2;

    public static final String[] ERRORS = {"System unavailable", "User not found", "Coin insufficient"};

    public TransferRuntimeException(int errorCode) {
        super(ERRORS[errorCode]);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
