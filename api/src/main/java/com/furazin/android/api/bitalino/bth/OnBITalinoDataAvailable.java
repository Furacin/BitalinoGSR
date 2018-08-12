/*
 *
 * Copyright (c) PLUX S.A., All Rights Reserved.
 * (www.plux.info)
 *
 * This software is the proprietary information of PLUX S.A.
 * Use is subject to license terms.
 *
 */
package com.furazin.android.api.bitalino.bth;

import com.furazin.android.api.bitalino.BITalinoFrame;

/**
 * We can use this interface as a callback function for Bioplux Data frames
 */
public interface OnBITalinoDataAvailable {
    void onBITalinoDataAvailable(final BITalinoFrame bitalinoFrame);

}
