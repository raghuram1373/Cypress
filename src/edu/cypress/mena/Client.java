package edu.cypress.mena;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import edu.cypress.mena.R;
//import edu.cypress.mena.R.id;
import edu.cypress.mena.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Client extends Activity {

	private Handler handler = new Handler();
	public ListView msgView;
	public ArrayAdapter<String> msgList;
	private Socket socket = null;
	private String message = null;
	private InetAddress serverAddress = null;
    private static final int PORT_NUMBER = 60101;
    private static final int MCAST_PORT = 60001;
    private final Semaphore sPhore1 = new Semaphore(1);

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.activity_generic);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

		msgView = (ListView) findViewById(R.id.listView);
		msgList = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		msgView.setAdapter(msgList);

		
		try {
			wait(5000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		

		getServerAddress();
		try {
			sPhore1.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			socket = new Socket(serverAddress.getHostAddress(), 60101);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		receiveMsg();
		
		ConnectivityManager cnvyMngr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cnvyMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		displayMsg("Server address is: "+serverAddress.getHostAddress());
		displayMsg("Client address is: "+socket.getLocalAddress().getHostAddress());
		displayMsg("Sending message to Server\n");		
		
/*		switch (Integer.parseInt(message)) {
		case 1:*/
			String netType = null;
			if(netInfo.isConnected()){
				netType = "Wi-Fi";
			}
			else {
				netType = "Mobile";
			}
			sendMessageToServer(netType);
			displayMsg(netType);
/*			break;
		case 2:
			break;
		default:
			break;
		}*/
	}
	
	private void getServerAddress(){
		try {
			sPhore1.acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				MulticastSocket mSocket = null;		
				InetAddress group = null;
				DatagramPacket serverDetails=null;
				byte[] buf  = new byte[256];
		
				//Generate multicast group address
				try {
					group = InetAddress.getByName("224.0.0.123");
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				
				/**
				 * Create Multicast socket and join MCast group
				 * Receive hello message from server and extract
				 * 		server IP address details
				 */
				try {
					mSocket = new MulticastSocket(MCAST_PORT);
					mSocket.joinGroup(group);
					serverDetails = new DatagramPacket(buf, buf.length);
					while(true){
						mSocket.receive(serverDetails);
						String recvMsg = serverDetails.getData().toString(); 
						if(recvMsg != null){
							displayMsg("Received this message from Server: ");
							displayMsg(recvMsg);
							break;
						}
						else {
							displayMsg("Received this message from Server: NULL");
						}
						try {
							wait(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					serverAddress = serverDetails.getAddress();
					sPhore1.release();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void sendMessageToServer(String str) {
		final String str1=str;
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 * Create a socket output stream writer.
				 * Write contents on output stream writer to
				 * 		send message to server.	 
				 */
				PrintWriter out;
				try {
					out = new PrintWriter(socket.getOutputStream());
					out.println(str1);
					Log.d("", "hello");
					out.flush();
					out.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.d("", "hello222");
				} catch (IOException e) {
					e.printStackTrace();
					Log.d("", "hello4333");
				}
	
			}
		}).start();
	}


	/**
	 * This function is not being used currently.
	 */
	public void receiveMsg()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() {
		    	BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}

		    	while(true)
		    	{
					try {
						message = in.readLine();
						Log.d("","MSGGG:  "+ message);
					} catch (IOException e) {
						e.printStackTrace();
					}
		    		if(message != null)
		    		{
		    			displayMsg(message);
		    			break;
		    		}
		    	}
			
			}
		}).start();
	}

	public void displayMsg(String msg)
	{ 
		final String mssg=msg;
	    handler.post(new Runnable() {

			@Override
			public void run() {
				msgList.add(mssg);
				msgView.setAdapter(msgList);
				msgView.smoothScrollToPosition(msgList.getCount() - 1);
				Log.d("","hi");
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}