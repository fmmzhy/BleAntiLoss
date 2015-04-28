package com.android.jackapp.btantiloss;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DFBase extends DialogFragment {
	private Button buttonBack;
	private Button buttonOK;
	
	private OnClickListener backOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			DFBase.this.getDialog().dismiss();
		}
	};
/*	private OnClickListener okClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			
		}
	};*/
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(1, R.style.anti_loss_df);
		
	    Dialog localDialog = super.onCreateDialog(savedInstanceState);
	    localDialog.setCanceledOnTouchOutside(false);
	    return localDialog;
	}
	
	void customTitle(View view){
		buttonBack = (Button)view.findViewById(R.id.btnBack);
		if(buttonBack != null){
			buttonBack.setOnClickListener(backOnClickListener );
		}
		
/*		buttonOK = (Button)view.findViewById(R.id.btnOK);
		if(buttonOK != null){
			buttonOK.setOnClickListener(okClickListener );
		}*/
		
	}
}
