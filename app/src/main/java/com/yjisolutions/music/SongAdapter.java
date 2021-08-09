package com.yjisolutions.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    public void updateList(List<Song> list) {
        tempSong = list;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int index) {
        notifyItemChanged(index);
        selectedPosition = index;
        notifyItemChanged(previousPlay);
        previousPlay = index;
    }

    public void pause(int pause, boolean playpause) {
        this.playPause = playpause;
        this.playPausePosition = pause;
        notifyItemChanged(pause);
    }

    public void play(int play, boolean playpause) {
        this.playPause = playpause;
        this.playPausePosition = play;
        notifyItemChanged(play);
    }

    public SongAdapter(List<Song> audioList, Context context, OnItemClick listener) {
        this.tempSong = audioList;
        this.context = context;
        this.mCallback = listener;
    }


    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    boolean focas;

    private void setFocas(View viewToFocas) {
        viewToFocas.requestFocus();
        focas = true;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull viewHolder holder) {
        if (focas) {
            holder.itemView.requestFocus();
            focas = false;
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card, parent, false);
        Log.d("OP" , "ViewHolder is working");
        return new viewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SongAdapter.viewHolder holder, int position) {
        Log.d("OP" , "On bind VH Working");
        Song temp = tempSong.get(position);
        holder.songTitle.setText(temp.getName());
        String size = "";
        try {
            int sizel = Integer.parseInt(temp.getSize()) / 1024;
            if (sizel < 1024) {
                size = sizel + " " + "KB";
            } else {
                sizel = sizel / 1024;
                size = sizel + " " + "MB";
            }
        } catch (Exception exception) {
            Log.d("Exception","Size .....");
        }

        holder.Des.setText(temp.getDuration() + "   " + size);
        setAnimation(holder.itemView);

        holder.neumorphCardView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyItemChanged(position);
            if (previousPlay >= 0) {
                notifyItemChanged(previousPlay);
            }
            previousPlay = position;
            mCallback.onClickData(temp.getUri(), temp.getName(), position);
        });

        if (position == selectedPosition) {
//            holder.neumorphCardView.setBackgroundColor(Color.parseColor("#146200EE"));
            holder.songTitle.setTextColor(context.getResources().getColor(R.color.purple_700));
            holder.Des.setTextColor(context.getResources().getColor(R.color.purple_500));
            holder.neumorphCardView.setShapeType(1);
            holder.animationView.playAnimation();
            holder.animationView.setAlpha(1f);
            setFocas(holder.itemView);
            if (playPause) {
                holder.animationView.playAnimation();
            } else {
                holder.animationView.pauseAnimation();
            }

        } else {
            holder.neumorphCardView.setShapeType(0);
//            holder.neumorphCardView.setBackgroundColor(Color.parseColor("#F3F3F3"));
            holder.songTitle.setTextColor(context.getResources().getColor(R.color.text));
            holder.Des.setTextColor(context.getResources().getColor(R.color.ofBlack));
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
        NeumorphCardView neumorphCardView;
        LottieAnimationView animationView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songName);
            Des = itemView.findViewById(R.id.songDescription);
            neumorphCardView = itemView.findViewById(R.id.playCardView);
            animationView = itemView.findViewById(R.id.homePlayingLottie);
        }
    }


}