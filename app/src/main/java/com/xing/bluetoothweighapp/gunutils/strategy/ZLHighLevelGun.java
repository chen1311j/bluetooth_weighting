package com.xing.bluetoothweighapp.gunutils.strategy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xing.bluetoothweighapp.gunutils.CompileType;

import java.util.Arrays;
import java.util.List;

public class ZLHighLevelGun extends BaseGun {
    private static final String ACTION_DISPLAY_SCAN_RESULT = "techain.intent.action.DISPLAY_SCAN_RESULT";
    private ScannerBroadcastReceiver broadcastReceiver;


    public ZLHighLevelGun(Context mContext) {
        super(mContext);
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DISPLAY_SCAN_RESULT);
            broadcastReceiver = new ScannerBroadcastReceiver(this, mContext);
            mContext.registerReceiver(broadcastReceiver, filter);

            Intent intent = new Intent("techain.intent.action.KEY_SCAN_DOWN");
            mContext.sendBroadcast(intent);
        }catch (Exception e){

        }

    }

    @Override
    public void register() {
        Intent intent = new Intent("techain.intent.action.SETUP_PARAMETERS");
        intent.putExtra("scan_mode", 0);
        intent.putExtra("decode_timeout",3);

        mContext.sendBroadcast(intent);
    }

    @Override
    public void unRegister() {
        Intent intent = new Intent("techain.intent.action.KEY_SCAN_UP");
        mContext.sendBroadcast(intent);

//        Intent intent = new Intent("techain.intent.action.SETUP_PARAMETERS");
//        intent.putExtra("scan_mode", 1);
//        intent.putExtra("decode_timeout",2);
//
//        mContext.sendBroadcast(intent);

    }

    @Override
    public CompileType getType() {
        return CompileType.PHONE_NAME;
    }

    @Override
    public List<String> getFilterList() {
        return Arrays.asList(
                "D6"
        );
    }


    private static class ScannerBroadcastReceiver extends BroadcastReceiver {
        private String lastResult;
        private static final long TIME_OUT=3000;
        private long lastTime;
        private final IGun iGun;
        private final Context mContext;


        private ScannerBroadcastReceiver(IGun iGun, Context mContext) {
            this.iGun = iGun;
            this.mContext = mContext;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_DISPLAY_SCAN_RESULT.equals(intent.getAction())) {
                String result = intent.getStringExtra("decode_data");

                if (iGun != null && iGun.getResultListener() != null) {
                    long nowTime = System.currentTimeMillis();

                    if(nowTime-lastTime>TIME_OUT){
                        iGun.getResultListener().getResult(result);
                       // mContext.unregisterReceiver(this);
                        lastTime=nowTime;
                        iGun.unRegister();
                    }else {

                    }

                }
            }
        }
    }
}
