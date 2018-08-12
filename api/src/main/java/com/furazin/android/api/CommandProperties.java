/*
 *
 * Copyright (c) PLUX S.A., All Rights Reserved.
 * (www.plux.info)
 *
 * This software is the proprietary information of PLUX S.A.
 * Use is subject to license terms.
 *
 */
package com.furazin.android.api;

public class CommandProperties {
    private final String TAG = getClass().getSimpleName();

    public byte[] command;
    public int returnLength;

    public CommandProperties( byte[] command, int returnLength) {
        this.command = command;
        this.returnLength = returnLength;
    }
}
