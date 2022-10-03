package com.softwaresolution.glucosemonitoringapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
 
import com.softwaresolution.glucosemonitoringapp.Pojo.PrescriptionData;
import com.softwaresolution.glucosemonitoringapp.R; 

import java.util.ArrayList;

public class PrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG ="RecyclerAdapterDng";
    private Context context;
    private ArrayList<PrescriptionData> list;
    public PrescriptionAdapter(Context context, ArrayList<PrescriptionData> list) {
        this.context = context;
        this.list =list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.prescription_content, parent, false);
        return new PrescriptionAdapter.MainHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { 
        MainHolder mainHolder = (MainHolder) holder;
        PrescriptionData data = list.get(position);
        mainHolder.txt_createdAt.setText(data.getCreateAt());
        mainHolder.txt_note.setText(data.getNote());
    }


    public static class MainHolder extends RecyclerView.ViewHolder {
        TextView txt_createdAt,txt_note;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            txt_createdAt = (TextView) itemView.findViewById(R.id.txt_createdAt);
            txt_note = (TextView) itemView.findViewById(R.id.txt_note);
        }
    }
}
