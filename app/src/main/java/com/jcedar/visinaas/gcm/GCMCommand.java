package com.jcedar.visinaas.gcm;

import android.content.Context;

public abstract class GCMCommand
{
    public abstract void execute(Context context, String type, String extraData);
}
