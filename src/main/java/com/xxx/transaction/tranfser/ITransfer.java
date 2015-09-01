package com.xxx.transaction.tranfser;

/**
 * Created by ricdong on 15-8-30.
 */
public interface ITransfer {


    /**
     * Add specified number of coin to the user.
     * @param userName Not null, the name of user.
     * @param coin Number of coin, should be > 0
     * @throws TransferRuntimeException Exception throwed if add user failed
     */
    void addUserCoin(String userName, int coin)throws TransferRuntimeException;

    /**
     * Transfer the number of coin from the account fromUser to toUser.
     * @param fromUser The user will be transfer from
     * @param toUser The user will be transfer to
     * @param numberOfCoin Amount of coin, should be > 0
     * @throws TransferRuntimeException Exception throwed if transaction failed
     */
    void transferTo(String fromUser, String toUser, int numberOfCoin)throws TransferRuntimeException;

    /**
     * Get the coin by the specific user.
     * @param userName The name of user.
     * @return Return the number of coin of the user.
     * @throws TransferRuntimeException Exception throwed due to {System unavailable | Account not found}
     */
    int getAmountOfCoinByUser(String userName)throws TransferRuntimeException;

}