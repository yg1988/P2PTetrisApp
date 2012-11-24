
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
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiChatFragment extends Fragment {
	
	public int count = 0;
	public JTetris tetris = new JTetris(15);
	public TextView t = null;
//	public JTetris tetris2 = new JTetris(15);
	public TextView t2 = null;
	static String hostip="192.168.2.107";
	final int portNumber = 1777;
	String TAG="JTetris main activity";
	
	
    private ChatManager chatManager=null;   //send message every down operation
    private View view;
    ChatMessageAdapter adapter = null;	
	
	
	


//    private TextView chatLine;
//    private ListView listView;
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
        
//        view.findViewById(R.id.button1).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        if (chatManager != null) {
//                            chatManager.write(chatLine.getText().toString()
//                                    .getBytes());
//                            pushMessage("Me: " + chatLine.getText().toString());
//                            chatLine.setText("");
//                            chatLine.clearFocus();
//                        }
//                    }
//                });
        
        return view;
    }

    
    
    

	
	
    public class tickAndSet1 extends AsyncTask<Integer , Void, Void>{

		@Override
		protected Void doInBackground(Integer... arg0) {
			tetris.tick(arg0[0]);
			if(chatManager!=null)
			chatManager.write(tetris.paintComponent().getBytes());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			t.setText(tetris.paintComponent());
		}


	} 
    
    
    
    
    
    
    


 
    
    
    public interface MessageTarget {
        public Handler getHandler();
    }

    public void setChatManager(ChatManager obj) {
        chatManager = obj;
    }

    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    /**
     * ArrayAdapter to manage chat messages.
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {

        List<String> messages = null;

        public ChatMessageAdapter(Context context, int textViewResourceId,
                List<String> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(message);
                    if (message.startsWith("Me: ")) {
                        nameText.setTextAppearance(getActivity(),
                                R.style.normalText);
                    } else {
                        nameText.setTextAppearance(getActivity(),
                                R.style.boldText);
                    }
                }
            }
            return v;
        }
    }
}
