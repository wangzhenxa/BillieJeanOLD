package com.ontim.billiejean.testcase;

//import android.os.storage.ExternalStorageFormatter;
//import android.os.storage.IMountService;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.ontim.billiejean.BillieJeanConfig;
import com.ontim.billiejean.Utils;

import java.io.File;

public class ResetFactoryCase implements BillieJeanCasable {
    private static final String TAG = BillieJeanConfig.PRO_TAG
            + "ResetFactoryCase";

    @Override
    public void execute(Context context) {
        Log.i(TAG, "execute!");
        StorageVolume storageVolume = null;
        File file = new File(BillieJeanConfig.RESET_FLAG);
        Log.d(TAG,"file.exists()== " + file.exists());
        try {
        
            if (file.exists()) {
                int i = Utils.readerFile(BillieJeanConfig.RESET_FLAG);
                Log.d(TAG,"readerFile= " + i );
                int i1 = i+1;
                Log.d(TAG,"i1== " + i1);
                Log.d(TAG,"String.valueOf(i1)== " + String.valueOf(i1));
                Utils.writeReaderFile(BillieJeanConfig.RESET_FLAG,String.valueOf(i1));
                if(i==0){
                    Log.d(TAG,"first" );
                }else{
                    Log.d(TAG,"not_first" );
                    Thread.sleep(60*1000);
                    
                }
                Intent intent = new Intent ("android.intent.action.MASTER_CLEAR");
                intent.setPackage("android");
                context.sendBroadcast(intent);
                
            } else {
                Utils.writeReaderFile(BillieJeanConfig.RESET_FLAG,"0");
            }

//            RecoverySystem.rebootWipeUserData(context);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }
    @Override
    public boolean isSingleCase() {
        // TODO Auto-generated method stub
        return true;
    }

}
