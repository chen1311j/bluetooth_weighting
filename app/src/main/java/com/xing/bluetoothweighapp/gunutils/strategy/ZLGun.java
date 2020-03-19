package com.xing.bluetoothweighapp.gunutils.strategy;

import android.content.Context;

import com.xing.bluetoothweighapp.gunutils.CompileType;
import com.zltd.industry.ScannerManager;

import java.util.Arrays;
import java.util.List;

public class ZLGun extends BaseGun {

    private ScannerManager mScannerManager;
    private ScanStatusListener mIScannerStatusListener;

    public ZLGun(Context mContext) {
        super(mContext);
        try {
            mScannerManager = ScannerManager.getInstance();
            mScannerManager.scannerEnable(true);//
            mScannerManager.setScanMode(ScannerManager.SCAN_SINGLE_MODE);
            mScannerManager.setDataTransferType(ScannerManager.TRANSFER_BY_API);
        }catch (Exception e){

        }

    }

    @Override
    public void register() {
        try {
            mIScannerStatusListener = new ScanStatusListener(this);
            mScannerManager.addScannerStatusListener(mIScannerStatusListener);
        } catch (Exception e) {

        }
    }

    @Override
    public void unRegister() {
        try {
            if(mIScannerStatusListener!=null){
                mScannerManager.removeScannerStatusListener(mIScannerStatusListener);
                mIScannerStatusListener=null;
            }

        }catch (Exception e){

        }

    }

    @Override
    public CompileType getType() {
        return CompileType.PHONE_NAME;
    }

    @Override
    public List<String> getFilterList() {
        return Arrays.asList(
                "simphone",
                "N2S000",
                "N5"
        );
    }

    private  static class  ScanStatusListener implements ScannerManager.IScannerStatusListener{
        private final IGun gun;

        private ScanStatusListener(IGun gun) {
            this.gun = gun;
        }

        @Override
        public void onScannerStatusChanage(int i) {

        }

        @Override
        public void onScannerResultChanage(byte[] bytes) {
            try {
                String result=new String(bytes,"UTF-8");
                if(gun!=null && gun.getResultListener()!=null){
                    gun.getResultListener().getResult(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
