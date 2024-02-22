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


import com.infodispatch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressLint("ViewHolder")
public class RSAJobDetailsAdapter extends BaseAdapter {

    HashMap<String, String> resultp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> data= new ArrayList<HashMap<String, String>>();
    Context context;
    public static SimpleDateFormat sdf3;
    public String val="NULL";
    Typeface textFonts;
    public RSAJobDetailsAdapter(Context context, ArrayList<HashMap<String, String>> data){
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
        TextView txtRsaJobId,txtRsaNoOfSeats,txtRsaStartTime,txtRsaPickUp,txtRsaDropAddres,
                txtRsaCustomerName,txtRsaPhoneNo,txtRsaStartDateTime,txtRsaEndDateTime,txtRsaDistDura;
        Button  btnRsaPickUp,btnRsaDropOff;
        TextView lblRsaPickUp,lblRsaDropAddres,lblRsaCustomerName,lblRsaPhoneNo,lblRsaDistDura,lblRsaStartDateTime,lblRsaEndDateTime;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.history_details, parent, false);

            holder = new ViewHolder();
            holder.txtRsaJobId = (TextView) view.findViewById(R.id.txtRsaJobId);
            holder.txtRsaNoOfSeats = (TextView) view.findViewById(R.id.txtRsaNoOfSeats);
            holder.txtRsaStartTime = (TextView) view.findViewById(R.id.txtRsaStartTime);

            holder.txtRsaPickUp = (TextView) view.findViewById(R.id.txtRsaPickUp);
            holder.txtRsaDropAddres = (TextView) view.findViewById(R.id.txtRsaDropAddres);

            holder.txtRsaCustomerName = (TextView) view.findViewById(R.id.txtRsaCustomerName);
            holder.txtRsaPhoneNo = (TextView) view.findViewById(R.id.txtRsaPhoneNo);
            holder.txtRsaStartDateTime = (TextView) view.findViewById(R.id.txtRsaStartDateTime);
            holder.txtRsaEndDateTime = (TextView) view.findViewById(R.id.txtRsaEndDateTime);
            holder.txtRsaDistDura = (TextView) view.findViewById(R.id.txtRsaDistDura);

            holder.lblRsaPickUp = (TextView) view.findViewById(R.id.lblRsaPickUp);
            holder.lblRsaDropAddres = (TextView) view.findViewById(R.id.lblRsaDropAddres);
            holder.lblRsaCustomerName = (TextView) view.findViewById(R.id.lblRsaCustomerName);
            holder.lblRsaPhoneNo = (TextView) view.findViewById(R.id.lblRsaPhoneNo);
            holder.lblRsaDistDura = (TextView) view.findViewById(R.id.lblRsaDistDura);

            holder.lblRsaStartDateTime = (TextView) view.findViewById(R.id.lblRsaStartDateTime);
            holder.lblRsaEndDateTime = (TextView) view.findViewById(R.id.lblRsaEndDateTime);

            holder.txtRsaJobId.setTypeface(textFonts,Typeface.BOLD);
            holder.txtRsaNoOfSeats.setTypeface(textFonts,Typeface.BOLD);
            holder.txtRsaStartTime.setTypeface(textFonts,Typeface.BOLD);

            holder.txtRsaPickUp.setTypeface(textFonts,Typeface.BOLD);
            holder.txtRsaDropAddres.setTypeface(textFonts,Typeface.NORMAL);

            holder.lblRsaPickUp.setTypeface(textFonts,Typeface.NORMAL);
            holder.lblRsaDropAddres.setTypeface(textFonts,Typeface.NORMAL);
            holder.lblRsaCustomerName.setTypeface(textFonts,Typeface.NORMAL);
            holder.lblRsaPhoneNo.setTypeface(textFonts,Typeface.NORMAL);

            holder.lblRsaDistDura.setTypeface(textFonts,Typeface.BOLD);
            holder.lblRsaStartDateTime.setTypeface(textFonts,Typeface.BOLD);
            holder.lblRsaEndDateTime.setTypeface(textFonts,Typeface.BOLD);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        resultp=data.get(position);
        return view;
    }

}
