package edu.cypress.mena;


import java.io.*;
import java.net.*; 
import java.util.*;

import edu.cypress.mena.R.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * A simple chat server implemented using TCP/IP sockets. A client can
 * connect to this server and send messages to other clients. The chat
 * server receives messages from clients and broadcast them to all the
 * connected clients. A message is an arbitrary text and is also
 * printed on stdout. The default port number is 8008.
 *
 * <pre>
 *  Usage: java ChatServer
 * </pre>
 *
 * @author Yoonsik Cheon
 */
public class Server extends Activity{

    private static final String USAGE = "Usage: java ChatServer";

    /** Default port number on which this server to be run. */
    private static final int PORT_NUMBER = 8008;

    /** List of print writers associated with current clients,
     * one for each. */
    private List<PrintWriter> clients;

    /** Creates a new server. 
    public Server() {
        clients = new LinkedList<PrintWriter>();
    }
*/
	@SuppressLint("NewApi")    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(layout.activity_generic);
	    clients = new LinkedList<PrintWriter>();		
//    }

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // Show the Up button in the action bar.
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	    }	    
	    
    /** Starts the server. */
//    public void start() {
        System.out.println("AndyChat server started on port "
                           + PORT_NUMBER + "!"); 
        try {
            ServerSocket s = new ServerSocket(PORT_NUMBER); 
            for (;;) {
                Socket incoming = s.accept(); 
                new ClientHandler(incoming).start(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("AndyChat server stopped."); 
    }

    /** Adds a new client identified by the given print writer. */
    private void addClient(PrintWriter out) {
        synchronized(clients) {
            clients.add(out);
        }
    }

    /** Adds the client with given print writer. */
    private void removeClient(PrintWriter out) {
        synchronized(clients) {
            clients.remove(out);
        }
    }

    /** Broadcasts the given text to all clients. */
    private void broadcast(String msg) {
        for (PrintWriter out: clients) {
            out.println(msg);
            out.flush();
        }
    }

/*    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.println(USAGE);
            System.exit(-1);
        }
        new Server().start();
    }*/

    /** A thread to serve a client. This class receive messages from a
     * client and broadcasts them to all clients including the message
     * sender. */
    private class ClientHandler extends Thread {

        /** Socket to read client messages. */
        private Socket incoming; 

        /** Creates a hander to serve the client on the given socket. */
        public ClientHandler(Socket incoming) {
            this.incoming = incoming;
        }

        /** Starts receiving and broadcasting messages. */
        public void run() {
            PrintWriter out = null;
            try {
                out = new PrintWriter(
                        new OutputStreamWriter(incoming.getOutputStream()));
                
                // inform the server of this new client
                Server.this.addClient(out);

                out.print("Welcome to AndyChat! ");
                out.println("Enter BYE to exit."); 
                out.flush();

                BufferedReader in 
                    = new BufferedReader(
                        new InputStreamReader(incoming.getInputStream())); 
                for (;;) {
                    String msg = in.readLine(); 
                    if (msg == null) {
                        break; 
                    } else {
                        if (msg.trim().equals("BYE")) 
                            break; 
                        System.out.println("Received: " + msg);
                        // broadcast the receive message
                        Server.this.broadcast(msg);
                    }
                }
                incoming.close(); 
                Server.this.removeClient(out);
            } catch (Exception e) {
                if (out != null) {
                    Server.this.removeClient(out);
                }
                e.printStackTrace(); 
            }
        }
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
