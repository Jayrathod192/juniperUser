package com.juniperuser;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    List<ModelData> modelDataList;
    ArrayList<ModelData> arrayList;

    public ListViewAdapter(Context context,List<ModelData> modelDataList)
    {
        this.mContext=context;
        this.modelDataList=modelDataList;
        inflater=LayoutInflater.from(mContext);
        this.arrayList=new ArrayList<ModelData>();
        this.arrayList.addAll(modelDataList);
    }

    public class ViewHolder{
        TextView mNameeng,mTypeeng,mQualification;
        ImageView mIcon;
    }


    @Override
    public int getCount() {
        return modelDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.raw, null);

            //locate views in raw.xml
            holder.mNameeng = view.findViewById(R.id.nameEng);
            holder.mTypeeng = view.findViewById(R.id.typeEng);
            holder.mQualification = view.findViewById(R.id.qualEng);
            holder.mIcon = view.findViewById(R.id.mainIcon);

            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }

        //set resu;ts into textview and imageview
        holder.mNameeng.setText(modelDataList.get(position).getNameeng());
        holder.mTypeeng.setText(modelDataList.get(position).getTypeeng());
        holder.mQualification.setText(modelDataList.get(position).getQualification());
        holder.mIcon.setImageResource(modelDataList.get(position).getIcon());

        //listview item clicks
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelDataList.get(position).getTypeeng().equals("Hardware Engineer")){
                    Intent intent=new Intent(mContext,EngineerDeatilsActivity.class);
                    intent.putExtra("actionBarTitle","Hardware Engineer");
                    intent.putExtra("Engineer","This is Hardware Engineer...");
                    mContext.startActivity(intent);
                }
                if(modelDataList.get(position).getTypeeng().equals("Software Engineer")){
                    Intent intent=new Intent(mContext,EngineerDeatilsActivity.class);
                    intent.putExtra("actionBarTitle","Software Engineer");
                    intent.putExtra("Engineer","This is Software Engineer...");
                    mContext.startActivity(intent);
                }
                if(modelDataList.get(position).getTypeeng().equals("Netware Engineer")){
                    Intent intent=new Intent(mContext,EngineerDeatilsActivity.class);
                    intent.putExtra("actionBarTitle","Netware Engineer");
                    intent.putExtra("Engineer","This is Netware Engineer...");
                    mContext.startActivity(intent);
                }

            }
        });
        return view;
    }

    //filter
    public void filter(String charText)
    {
        charText=charText.toLowerCase(Locale.getDefault());
        modelDataList.clear();
        if(charText.length()==0){
            modelDataList.addAll(arrayList);
        }
        else
        {
            for(ModelData model : arrayList){
                if(model.getTypeeng().toLowerCase(Locale.getDefault())
                        .contains(charText)){
                    modelDataList.add(model);
                }

            }
        }
        notifyDataSetChanged();
    }
}
