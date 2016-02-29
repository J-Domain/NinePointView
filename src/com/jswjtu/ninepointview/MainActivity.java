package com.jswjtu.ninepointview;

import com.jswjtu.ninepointview.NinePointView.OnPassListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText edtKey;
	private Button btnSet;
	private NinePointView ninePointView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtKey = (EditText) findViewById(R.id.edt_key);
        btnSet = (Button) findViewById(R.id.btn_set);
        ninePointView = (NinePointView) findViewById(R.id.ninepointView);
        
        btnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ninePointView.setPassKey(edtKey.getText().toString());
			}
		});
        ninePointView.setOnPassListener(new OnPassListener() {
			@Override
			public void onPass() {
				edtKey.setText("½âËø³É¹¦£¡");
			}
		});
    }
}
