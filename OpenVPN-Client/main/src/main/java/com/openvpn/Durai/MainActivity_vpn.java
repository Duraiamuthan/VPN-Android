package com.openvpn.Durai;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.openvpn.Durai.Constants;
import com.openvpn.Durai.R;
import com.openvpn.Durai.core.ProfileManager;
import com.openvpn.Durai.LaunchVPN;
import com.openvpn.Durai.VpnProfile;
import com.openvpn.Durai.core.OpenVPNService;
import com.openvpn.Durai.core.OpenVPNService.LocalBinder;
import com.openvpn.Durai.core.ProfileManager;
import com.openvpn.Durai.core.ConfigParser;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import android.widget.EditText;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import org.spongycastle.util.encoders.*;
import android.security.KeyChain;
import java.security.KeyStore;
import java.util.Enumeration;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

//More functionalities can be found here https://goo.gl/7EwDJC But to access that send me a mail.


public class MainActivity_vpn extends Activity {

	protected OpenVPNService mService;
	// variable to track whether the service is bound
	boolean mBound = false;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService =null;
			mBound = false;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_vpn);
		Intent intent = new Intent(getBaseContext(), OpenVPNService.class);
		intent.setAction(OpenVPNService.START_SERVICE);

		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


		registerReceiver(broadcastReceiver, new IntentFilter("com_openvpn_Durai_CONNECTION_CHANGE"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.settingsmenu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Constants.isVPNConnected){
			// disable connect button if VPN is connected
			menu.getItem(0).setEnabled(false);
			// enable disconnect button if VPN is connected
			menu.getItem(1).setEnabled(true);
		} else{
			// enable connect button if VPN is disconnected
			menu.getItem(0).setEnabled(true);
			// disable disconnect button if VPN is disconnected
			menu.getItem(1).setEnabled(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_startvpn:
				configureAndStartVpn() ;
				return true ;
			case R.id.action_stopvpn:
				stopVPN() ;
				return true ;
			case R.id.action_removeProfile:
				removeProfile();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void removeProfile() {

		final ProfileManager pm = ProfileManager.getInstance(MainActivity_vpn.this) ;
		final VpnProfile profile = pm.getProfileByName(Constants.VPN_PROFILE_NAME) ;

		if (profile != null) {
							stopVPN() ;
							pm.removeProfile(getApplicationContext(),profile);
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(MainActivity_vpn.this,"The VPN Configuration is deleted", duration);
							toast.show();

		} else {
					int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(MainActivity_vpn.this,"There are no VPN Configurations to delete", duration);
			toast.show();
		}
	}

	private void stopVPN() {
		try{
			ProfileManager.setConntectedVpnProfileDisconnected(MainActivity_vpn.this);
			if(mService.getManagement()!=null)
				mService.getManagement().stopVPN();

		}
		catch (Exception ex){

		}
	}

	private void configureAndStartVpn() {
		try {


					EditText Et_Ovpn = (EditText) findViewById(R.id.et_ovpn);

					String retVal = Et_Ovpn.getText().toString();

					if (retVal != null && retVal.trim().length()>0) {

						byte[] buffer = retVal.getBytes() ;

						VpnProfile vp = saveProfile(buffer) ;

						if (vp != null) {
							startVPN(vp) ;
						}
					}
					else {
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(MainActivity_vpn.this,"Connecting using the last vpn configuration", duration);
						toast.show();
						startVPN();
					}


		} catch (Exception e) {
			e.printStackTrace() ;
		}
	}

	private VpnProfile saveProfile(byte [] data) {

		ConfigParser cp = new ConfigParser();
		try {

			InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(data));

			cp.parseConfig(isr);
			VpnProfile vp = cp.convertProfile();

			ProfileManager vpl = ProfileManager.getInstance(this);

			vp.mName = Constants.VPN_PROFILE_NAME ;
			vpl.addProfile(vp);
			vpl.saveProfile(this, vp);
			vpl.saveProfileList(this);

			return vp ;
		} catch(Exception e) {
			return null ;
		}
	}

	public void startVPN(VpnProfile vp) {
		Intent intent = new Intent(getApplicationContext(),LaunchVPN.class);
		intent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUID().toString());
		intent.setAction(Intent.ACTION_MAIN);
		startActivity(intent);
	}

	private void startVPN() {

		ProfileManager pm = ProfileManager.getInstance(this) ;
		VpnProfile profile = pm.getProfileByName(Constants.VPN_PROFILE_NAME) ;

		if (profile == null) {
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(MainActivity_vpn.this,"There are no VPN Configurations.So paste the .OVPN and try", duration);
			toast.show();
			return ;
		}

		Intent intent = new Intent(this,LaunchVPN.class);
		intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
		intent.setAction(Intent.ACTION_MAIN);
		startActivity(intent);
	}


	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			stopVPN();
			startVPN();
		}
	};




	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
}
