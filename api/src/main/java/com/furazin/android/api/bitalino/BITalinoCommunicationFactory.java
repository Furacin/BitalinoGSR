/*
*
* Copyright (c) PLUX S.A., All Rights Reserved.
* (www.plux.info)
*
* This software is the proprietary information of PLUX S.A.
* Use is subject to license terms.
*
*/
package com.furazin.android.api.bitalino;

import android.content.Context;

import com.furazin.android.api.Communication;
import com.furazin.android.api.bitalino.ble.BLECommunication;
import com.furazin.android.api.bitalino.bth.BTHCommunication;
import com.furazin.android.api.bitalino.bth.OnBITalinoDataAvailable;

public class BITalinoCommunicationFactory {
    private final String TAG = this.getClass().getSimpleName();

    public BITalinoCommunication getCommunication(Communication type, Context activityContext){
        BITalinoCommunication communication = null;
        switch (type){
            case BLE:
                //BLE
                communication = new BLECommunication(activityContext);
                break;
            case BTH:
                //BTH
                communication =  new BTHCommunication(activityContext, null);
                break;
        }
        return communication;
    }

    public BITalinoCommunication getCommunication(Communication type, Context activityContext, OnBITalinoDataAvailable callback){
        BITalinoCommunication communication = null;
        switch (type){
            case BLE:
                //BLE
                communication = new BLECommunication(activityContext);
                break;
            case BTH:
                //BTH
                communication =  new BTHCommunication(activityContext, callback);
                break;
        }

        return communication;
    }
}