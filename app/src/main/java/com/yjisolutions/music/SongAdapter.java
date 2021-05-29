package com.yjisolutions.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import soup.neumorphism.NeumorphCardView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.viewHolder> {


    private int selectedPosition = -1;
    private final OnItemClick mCallback;
    List<Song> tempSong;
    Context context;
    int playPausePosition;
    int previousPlay = -1;
    boolean playPause;
//    boolean cardAnimationflag;

    public void updateList(List<Song> list) {
        tempSong = list;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int index) {
//        cardAnimationflag = false;
        notifyItemChanged(index);
        selectedPosition = index;
        notifyItemChanged(previousPlay);
        previousPlay = index;
//        cardAnimationflag = true;
    }

    public void pause(int pause, boolean playpause) {
//        cardAnimationflag = false;
        this.playPause = playpause;
        this.playPausePosition = pause;
        notifyItemChanged(pause);
//        cardAnimationflag = true;
    }

    public void play(int play, boolean playpause) {
//        cardAnimationflag = false;
        this.playPause = playpause;
        this.playPausePosition = play;
        notifyItemChanged(play);
//        cardAnimationflag = true;
    }

    public SongAdapter(List<Song> audioList, Context context, OnItemClick listener) {
        this.tempSong = audioList;
        this.context = context;
        this.mCallback = listener;
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
//        if (cardAnimationflag) {
            viewToAnimate.startAnimation(animation);
//        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card, parent, false);
        return new viewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SongAdapter.viewHolder holder, int position) {
        Song temp = tempSong.get(position);
        holder.songTitle.setText(temp.getName());
        String size = null;
        try {
            int sizel = Integer.parseInt(temp.getSize()) / 1024;
            if (sizel < 1024) {
                size = sizel + " " + "KB";
            } else {
                sizel = sizel / 1024;
                size = sizel + " " + "MB";
            }
        } catch (Exception ignored) {
        }
        holder.Des.setText(temp.getDuration() + "   " + size);
        setAnimation(holder.itemView);

        holder.neumorphCardView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyItemChanged(position);
            if (previousPlay>=0){
            notifyItemChanged(previousPlay);}
            previousPlay = position;
            mCallback.onClickData(temp.getUri(), temp.getName(), position);
        });

        if (position == selectedPosition) {
            holder.neumorphCardView.setBackgroundColor(Color.parseColor("#146200EE"));
            holder.neumorphCardView.setShapeType(0);
            holder.animationView.playAnimation();
            holder.animationView.setAlpha(1f);
            if (playPause) {
                holder.animationView.playAnimation();
            } else {
                holder.animationView.pauseAnimation();
            }

        } else {
            holder.neumorphCardView.setShapeType(1);
            holder.neumorphCardView.setBackgroundColor(Color.parseColor("#F3F3F3"));
            holder.animationView.cancelAnimation();
            holder.animationView.setAlpha(0f);
        }

    }

    @Override
    public int getItemCount() {
        return tempSong.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, Des;
        ImageView imageView;
        NeumorphCardView neumorphCardView;
        LottieAnimationView animationView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.songThumb);
            songTitle = itemView.findViewById(R.id.songName);
            Des = itemView.findViewById(R.id.songDescription);
            neumorphCardView = itemView.findViewById(R.id.playCardView);
            animationView = itemView.findViewById(R.id.homePlayingLottie);
        }
    }


}