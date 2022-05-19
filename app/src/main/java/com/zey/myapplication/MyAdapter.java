package com.zey.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context context;
    ArrayList<Sarki> MusicList = new ArrayList<>();


    public MyAdapter(Context context, ArrayList<Sarki> musicList) {
        this.context = context;
        MusicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder,  int position) {
        position=holder.getAdapterPosition();

        Sarki currentSarki = MusicList.get(position);
        holder.baslik.setText(currentSarki.getBaslik());
        holder.album.setText("Albüm: "+currentSarki.getAlbum());
        holder.artist.setText(currentSarki.getSanatci());
        holder.sure.setText(milisaniye2dakika( currentSarki.getSure()));

        if(MyPlayer.currentIndex==position){
            holder.baslik.setTextColor(Color.parseColor("#ADFF80AB"));
        }else{
            holder.baslik.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int  position=holder.getAdapterPosition();

                MyPlayer.getInstance().reset();
                MyPlayer.currentIndex = position;
                Intent intent = new Intent(context,MusicPlayer_Activity.class);
                intent.putExtra("LIST",MusicList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String text = "Tıklanan şarkı boyutu: "+currentSarki.getSize();

                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                return false;
            }
        });





    }

    @Override
    public int getItemCount() {
        return MusicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView baslik;
        TextView artist;
        TextView album;
        TextView sure;
        ImageView icon;
        LinearLayout parentLayout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon  = itemView.findViewById(R.id.iconn);
            baslik  = itemView.findViewById(R.id.baslik);
            artist = itemView.findViewById(R.id.sanatci);
            album = itemView.findViewById(R.id.album);
            sure  = itemView.findViewById(R.id.sure);
            parentLayout  = itemView.findViewById(R.id.parentLayout);
            album.setSelected(true);

        }
    }

    public static String milisaniye2dakika(String sure){
        Long millisaniye = Long.parseLong(sure);

        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisaniye) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millisaniye) % TimeUnit.MINUTES.toSeconds(1));
    }

}





/// content resolver content aracılığıyla ad soyadı çekmişiz
//şarkıları tekrar tekrar alabilmemiz için bi yere kaydetmemiz gerekiyor
//burdada cursor falan kullanacakmışız

//mylistdata da main activityde content resolver ile cursor ile mp3 bilgilerini çekip onları listeleyecekmişiz