
package com.example.android.wifidirect.discovery;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.android.wifidirect.discovery.R;






/**
 * This fragment handles Tetris related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiTetrisFragment extends Fragment {
	
	public int count = 0;
	public JTetris tetris = new JTetris(15);
	public TextView t = null;
	public TextView t2 = null;
	static String hostip="192.168.2.107";
	final int portNumber = 1777;
	String TAG="JTetris main activity";
	
	
    NetworkManager NetworkManager=null;   //send message every down operation
    private View view;
    private List<String> items = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        

		t = (TextView) view.findViewById(R.id.textView1);
		t.setLineSpacing(0f,0.6f);
		t.setTextSize(20);
		t.setTypeface(Typeface.MONOSPACE);
		
		t2 = (TextView) view.findViewById(R.id.textView2);
		t2.setLineSpacing(0f,0.6f);
		t2.setTextSize(20);
		t2.setTypeface(Typeface.MONOSPACE);
		
		Timer timer = new Timer();
		tetris.startGame();
		timer.schedule(new TimerTask() {
			public void run()
			{
				new tickAndSet1().execute(JTetris.DOWN);
			}
		},0,700);
		t.setText(tetris.paintComponent());
        
        
        return view;
    }

    
    
    
    /**
     *tickAndSet of the time. Generate down operation once in a while
     */
	
	
    public class tickAndSet1 extends AsyncTask<Integer , Void, Void>{

		@Override
		protected Void doInBackground(Integer... arg0) {
			tetris.tick(arg0[0]);
			if(NetworkManager!=null)
				//propagate the change to the oppornent's screen
				NetworkManager.write(tetris.paintComponent().getBytes());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//display the changes made to player's own screen
			t.setText(tetris.paintComponent());
		}


	} 
    
    
    
    
    public interface MessageTarget {
        public Handler getHandler();
    }

    public void setNetworkManager(NetworkManager obj) {
        NetworkManager = obj;
    }


}
