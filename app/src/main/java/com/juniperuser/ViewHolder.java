package com.juniperuser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;


    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView=itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        });

        //item LongClick
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view,getAdapterPosition());
                return true;
            }
        });
    }

    public void setDetails(Context ctx,String id,String name,String email,String image,String phone,String type,String distance)
    {
        TextView mName=mView.findViewById(R.id.nameEng);
        TextView mPhone=mView.findViewById(R.id.phoneEng);
        TextView mQual=mView.findViewById(R.id.qualEng);
        ImageView mImage=mView.findViewById(R.id.mainIcon);
        TextView mID=mView.findViewById(R.id.IDeng);
        TextView mType=mView.findViewById(R.id.typeEng);
        TextView mDistance=mView.findViewById(R.id.distanceEng);

        //set data to views
        mID.setText("ID : "+id);
        mName.setText("Name : "+name);
        mPhone.setText(phone);
        mQual.setText("Email : "+email);
        mType.setText("Type : "+type);
        mDistance.setText("Distance: "+distance);
        Picasso.get().load(image).into(mImage);
    }

    private ViewHolder.ClickListener mClickListener;

    //interface to end callbacks
    public interface ClickListener
    {
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);

    }
    public void setonCLickListener(ViewHolder.ClickListener clickListener)
    {
        mClickListener=clickListener;
    }
}
