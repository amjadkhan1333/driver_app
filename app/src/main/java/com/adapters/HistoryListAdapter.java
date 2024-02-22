package com.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.log.MyLog;
import com.infodispatch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
@SuppressLint("ViewHolder")
public class HistoryListAdapter extends BaseAdapter {

	HashMap<String, String> resultp = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> data= new ArrayList<HashMap<String, String>>();
	Context context;
	public static SimpleDateFormat sdf3;
	public String val="NULL";
	public static String DEBUG_KEY ="HistoryListAdapter";
	Typeface textFonts;
	public HistoryListAdapter(Context context, ArrayList<HashMap<String, String>> data){
		this.context=context;
		this.data=data;
		textFonts = Typeface.createFromAsset(context.getAssets(),"truenorg.otf");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size=data.size();
		System.out.println("Sissss"+size);
		return size;
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
		private static class ViewHolder {
		TextView txtJobId,txtDateTime,txtAmount,txtPickUp,txtDropAddres,
				txtDistDura,txtStartDateTime,txtEndDateTime,txtJobStatus;
		Button  btn_mail,btn_sms;
		TextView lblPickUp,lblDropAddres,lblDistDura,lblStartDateTime,lblEndDateTime;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder; 
			View view = convertView;
			try{
				if(view==null){
					LayoutInflater inflater = LayoutInflater.from(context);
					view = inflater.inflate(R.layout.history_details, parent, false);

					holder = new ViewHolder();
					holder.txtJobId = (TextView) view.findViewById(R.id.txtJobId);
					holder.txtDateTime = (TextView) view.findViewById(R.id.txtDateTime);
					holder.txtAmount = (TextView) view.findViewById(R.id.txtAmount);
					holder.txtJobStatus = (TextView) view.findViewById(R.id.txtJobStatus);

					holder.txtPickUp = (TextView) view.findViewById(R.id.txtPickUp);
					holder.txtDropAddres = (TextView) view.findViewById(R.id.txtDropAddres);
					holder.txtDistDura = (TextView) view.findViewById(R.id.txtDistDura);
					holder.txtStartDateTime = (TextView) view.findViewById(R.id.txtStartDateTime);
					holder.txtEndDateTime = (TextView) view.findViewById(R.id.txtEndDateTime);

					holder.lblPickUp = (TextView) view.findViewById(R.id.lblPickUp);
					holder.lblDropAddres = (TextView) view.findViewById(R.id.lblDropAddres);
					holder.lblDistDura = (TextView) view.findViewById(R.id.lblDistDura);
					holder.lblStartDateTime = (TextView) view.findViewById(R.id.lblStartDateTime);
					holder.lblEndDateTime = (TextView) view.findViewById(R.id.lblEndDateTime);

					holder.txtJobId.setTypeface(textFonts,Typeface.BOLD);
					holder.txtDateTime.setTypeface(textFonts,Typeface.BOLD);
					holder.txtAmount.setTypeface(textFonts,Typeface.BOLD);
					holder.txtJobStatus.setTypeface(textFonts,Typeface.BOLD);

					holder.txtPickUp.setTypeface(textFonts,Typeface.NORMAL);
					holder.txtDropAddres.setTypeface(textFonts,Typeface.NORMAL);
					holder.txtDistDura.setTypeface(textFonts,Typeface.NORMAL);
					holder.txtStartDateTime.setTypeface(textFonts,Typeface.NORMAL);
					holder.txtEndDateTime.setTypeface(textFonts,Typeface.NORMAL);

					holder.lblPickUp.setTypeface(textFonts,Typeface.BOLD);
					holder.lblDropAddres.setTypeface(textFonts,Typeface.BOLD);
					holder.lblDistDura.setTypeface(textFonts,Typeface.BOLD);
					holder.lblStartDateTime.setTypeface(textFonts,Typeface.BOLD);
					holder.lblEndDateTime.setTypeface(textFonts,Typeface.BOLD);

					view.setTag(holder);
				}
				else{
					holder = (ViewHolder) view.getTag();
				}
				resultp=data.get(position);

				holder.txtJobId.setText(resultp.get("JobId"));
				holder.txtJobStatus.setText(resultp.get("jobStatus"));
				holder.txtStartDateTime.setText(resultp.get("startDate"));

				int convert= (int) Double.parseDouble(resultp.get("totalBill"));
				holder.txtAmount.setText(String.valueOf(convert));

				holder.txtJobStatus.setText(resultp.get("jobStatus"));
				holder.txtDistDura.setText(resultp.get("totalTripDist")+"Kms | "+resultp.get("totalTripDuration")+"Min");
				holder.txtStartDateTime.setText(resultp.get("startDate")+" | "+resultp.get("startTime"));
				holder.txtEndDateTime.setText(resultp.get("endDate")+" | "+resultp.get("endTime"));

				holder.txtPickUp.setText(resultp.get("pickupLoc"));
				holder.txtDropAddres.setText(resultp.get("dropLoc"));
			}catch (Exception e){
				MyLog.appendLog(DEBUG_KEY+"getView"+e.getMessage());
			}

		return view;
	}

}
