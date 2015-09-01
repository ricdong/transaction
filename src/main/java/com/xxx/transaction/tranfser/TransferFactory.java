package com.xxx.transaction.tranfser;

/**
 * Created by ricdong on 15-8-30.
 */
public class TransferFactory {

    private static ITransfer transfer = null;

//    public static ITransfer createDefaultTransfer() throws Exception {
//        if(transfer != null)
//            return transfer;
//
//        synchronized(TransferFactory.class) {
//            if(transfer == null) {
//                transfer = new TradingSystem();
//            }
//        }
//        return transfer;
//    }

    public static void setTransfer(ITransfer transfer) {
        TransferFactory.transfer = transfer;
    }


    public static ITransfer getTransfer() {
        return transfer;
    }
}
