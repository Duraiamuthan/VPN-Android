package com.openvpn.Durai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ConnectionChangeReceiver extends BroadcastReceiver {

//	public static void dumpIntent(Intent i){
//
//		Bundle bundle = i.getExtras();
//		if (bundle != null) {
//			Set<String> keys = bundle.keySet();
//			Iterator<String> it = keys.iterator();
//			Log.e("Dump","Dumping Intent start");
//			while (it.hasNext()) {
//				String key = it.next();
//				Log.e("Intent keyvalues","[" + key + "=" + bundle.get(key)+"]");
//			}
//			Log.e("Dump","Dumping Intent end");
//		}
//	}

	@Override
	public void onReceive(Context context, Intent intent) {

		//Toast.makeText(context,"ConnectionChangeReceiver",Toast.LENGTH_LONG).show();

		Bundle bundle = intent.getExtras();

		String KeyNI="networkInfo";

		android.net.NetworkInfo bundleNetworkInfo=(android.net.NetworkInfo)bundle.get(KeyNI);

		//Log.e("NetworkType", bundleNetworkInfo.getTypeName());

		if(!bundleNetworkInfo.getTypeName().toString().equalsIgnoreCase("VPN"))
		{
			context.sendBroadcast(new Intent("com_openvpn_Durai_CONNECTION_CHANGE"));
		}


//		System.out.println("INSIDE CONNECTION CHANGE RECEIVER");
//		ConnectivityManager connectivityMgr = (ConnectivityManager) 
//				context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		NetworkInfo activeNetInfo = connectivityMgr.getActiveNetworkInfo();
//		NetworkInfo mobNetInfo = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

//		if ( activeNetInfo != null )
//		{
//			Toast.makeText( context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
//		}
//		if( mobNetInfo != null )
//		{
//			Toast.makeText( context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT ).show();
//		}

	}

}
