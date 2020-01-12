package com.juniperuser;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private int[] images;
    private List<String > names;
    List<SearchEngineer.engineer> engineers;

    RecyclerAdapter(List<SearchEngineer.engineer> engineers){this.engineers=engineers;}

    /*public RecyclerAdapter(List<SearchEngineer.engineer> names){this.names=names;}

    public RecyclerAdapter(int[] images)
    {
        this.images=images;
    }*/


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout,parent,false);
        ImageViewHolder imageViewHolder=new ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int position) {

        //int image_id=images[position];
        viewHolder.engName.setText(engineers.get(position).name);
        viewHolder.engType.setText(engineers.get(position).Type);
        viewHolder.education.setText(engineers.get(position).qualification);
        viewHolder.engImg.setImageResource(engineers.get(position).imgId);

    }

    @Override
    public int getItemCount() {
        return engineers.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder
    {
        //ImageView Album;
        //TextView AlbumTitle;
        CardView cardView;
        TextView engName;
        TextView engType;
        TextView education;
        ImageView engImg;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            //Album=itemView.findViewById(R.id.album);
            //AlbumTitle=itemView.findViewById(R.id.album_title);
            cardView=(CardView)itemView.findViewById(R.id.cardView);
            engName=itemView.findViewById(R.id.engName);
            engType=itemView.findViewById(R.id.engType);
            education=itemView.findViewById(R.id.education);
            engImg=itemView.findViewById(R.id.engImage);

        }
    }

}
