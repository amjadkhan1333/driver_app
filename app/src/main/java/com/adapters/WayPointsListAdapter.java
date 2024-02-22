package com.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.infodispatch.R;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("ViewHolder")
public class WayPointsListAdapter extends BaseAdapter {

    HashMap<String, String> resultp = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> data= new ArrayList<HashMap<String, String>>();
    Context context;
    Typeface textFonts;
    public WayPointsListAdapter(Context context, ArrayList<HashMap<String, String>> data){
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
        TextView txtWayPoints;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if(view==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.rsa_waypoints_list_items, parent, false);
            holder = new ViewHolder();
            holder.txtWayPoints = (TextView) view.findViewById(R.id.txtWayPoints);
            holder.txtWayPoints.setTypeface(textFonts,Typeface.BOLD);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        resultp=data.get(position);
        holder.txtWayPoints.setText(resultp.get("Location"));
        return view;
    }

}
