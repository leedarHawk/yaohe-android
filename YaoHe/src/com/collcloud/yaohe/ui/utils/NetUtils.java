package com.collcloud.yaohe.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetUtils {
	
	/**
	 * @author 赵洋洋
     * @类说明 获取当前的网络状态  -1：没有网络  1：WIFI网络2：wap网络3：net网络
     * @param context
     * @return
     */ 
    public static int getAPNType(Context context){ 

        int netType = -1;  
        
        ConnectivityManager connMgr = (ConnectivityManager) 
        		
        context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
        
        if(networkInfo==null){ 
        	
            return netType; 
        } 
        int nType = networkInfo.getType(); 
        
        if(nType==ConnectivityManager.TYPE_MOBILE){ 
        	
            Log.e("networkInfo.getExtraInfo()","networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo());
            
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){ 
            	
                netType =3; 
            } 
            else{ 
            	
                netType =2; 
            } 
        } 
        else if(nType==ConnectivityManager.TYPE_WIFI){ 
        	
            netType =1; 
        } 
        return netType; 
    } 
    
}
