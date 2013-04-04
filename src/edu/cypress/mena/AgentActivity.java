package edu.cypress.mena;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class AgentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agent, menu);
		return true;
	}
	
	public void startClient(View view){
		Intent intent = new Intent(this, Client.class);
		startActivity(intent);
	}

	public void startServer(View view){
		Intent intent = new Intent(this, Server.class);
		startActivity(intent);
	}	
	
}
