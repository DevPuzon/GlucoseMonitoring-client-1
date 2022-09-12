package com.softwaresolution.glucosemonitoringapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Auth.PatientLoginForm;
import com.softwaresolution.glucosemonitoringapp.Pojo.ConvoData;
import com.softwaresolution.glucosemonitoringapp.Pojo.EntrySensorData;
import com.softwaresolution.glucosemonitoringapp.Pojo.UserAccount;
import com.softwaresolution.glucosemonitoringapp.R;

import java.util.ArrayList;

public class ConvoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG ="ConvoAdapter";
    private ArrayList<Object> recyclerViewItems;
    private Context context;
    private UserAccount myaccount;
    public  ConvoAdapter(Context context, ArrayList<Object> recyclerViewItems,
                         UserAccount myaccount) {
        this.context = context;
        this.recyclerViewItems =recyclerViewItems;
        this.myaccount = myaccount;
    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.chat_convo_content,parent, false);
        return new MainHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConvoData data =(ConvoData) recyclerViewItems.get(position);
        MainHolder mainHolder = (MainHolder) holder;
        mainHolder.card.setRadius(10f);

        Log.d(TAG,myaccount.getId()+" | "+ data.getFromUid());
        if (data.getFromUid().equals(myaccount.getId())){
            Log.d(TAG,"Chat from me"+ new Gson().toJson(data));
            //Chat from me
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.LEFT;
            mainHolder.card.setLayoutParams(layoutParams);
            mainHolder.card.setCardBackgroundColor(Color.parseColor("#F1F1F1"));
            mainHolder.txt_message.setTextColor(Color.parseColor("#00838F"));
            mainHolder.txt_createdAT.setText(data.getCreatedAt()+" "+ data.getTime());
            mainHolder.txt_createdAT.setLayoutParams(layoutParams);
            mainHolder.card.requestLayout();
        }
        else{
            Log.d(TAG,"Chat from my chatmate"+ new Gson().toJson(data));
            //Chat from my chatmate
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.RIGHT;
            mainHolder.card.setLayoutParams(layoutParams);
            mainHolder.card.setCardBackgroundColor(Color.parseColor("#00838F"));
            mainHolder.txt_createdAT.setText(data.getCreatedAt()+" "+ data.getTime());
            mainHolder.txt_createdAT.setLayoutParams(layoutParams);
            mainHolder.card.requestLayout();
            mainHolder.txt_message.setTextColor(Color.parseColor("#FFFFFF"));
        }
        mainHolder.txt_message.setText(data.getMessage());
        Linkify.addLinks(mainHolder.txt_message, Linkify.WEB_URLS);
    }

    public static class MainHolder extends RecyclerView.ViewHolder {
        TextView txt_message,txt_createdAT;
        CardView card;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            txt_message = (TextView) itemView.findViewById(R.id.txt_message);
            txt_createdAT = (TextView) itemView.findViewById(R.id.txt_createdAt);
            card = (CardView) itemView.findViewById(R.id.card);
        }
    }
}