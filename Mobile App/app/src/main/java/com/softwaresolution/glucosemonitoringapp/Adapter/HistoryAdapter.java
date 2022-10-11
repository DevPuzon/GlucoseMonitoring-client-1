package com.softwaresolution.glucosemonitoringapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.softwaresolution.glucosemonitoringapp.Pojo.EntrySensorData;
import com.softwaresolution.glucosemonitoringapp.Pojo.ResultPojo;
import com.softwaresolution.glucosemonitoringapp.Pojo.SensorData;
import com.softwaresolution.glucosemonitoringapp.R;
import com.softwaresolution.glucosemonitoringapp.UiPatient.Result;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG ="HistoryAdapter";
    private Context context;
    private ArrayList<ResultPojo> resultPojos;
    public HistoryAdapter(Context context, ArrayList<ResultPojo> resultPojos) {
        this.context = context;
        this.resultPojos =resultPojos;
    }

    @Override
    public int getItemCount() {
        return resultPojos.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ui_history_content, parent, false);
        return new HistoryAdapter.MainHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final HistoryAdapter.MainHolder mainHolder = (HistoryAdapter.MainHolder) holder;
        final ResultPojo resultPojo = resultPojos.get(position);
        final EntrySensorData dataAdapter = resultPojo.getSensorDatas().get(0);
        Log.d(TAG,new Gson().toJson(resultPojo));
        mainHolder.chart.getXAxis().setLabelCount(dataAdapter.getEntryList().size());
        LineDataSet lineDataSet = new LineDataSet(dataAdapter.getEntryList(),"");
        lineDataSet.setDrawValues(true);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        XAxis xAxis = mainHolder.chart.getXAxis();
        xAxis.setDrawGridLines(false);
        YAxis yAxisLeft = mainHolder.chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        YAxis yAxisRight = mainHolder.chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setEnabled(false);

        final LineData data = new LineData(lineDataSet);
        mainHolder.chart.setData(data);
        mainHolder.chart.getDescription().setText(dataAdapter.getName());
        mainHolder.chart.getDescription().setTextSize(18);
        mainHolder.chart.getDescription().setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        lineDataSet.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.colorPrimaryDark));
        mainHolder.chart.getXAxis().setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        mainHolder.chart.getXAxis().setEnabled(false);
        mainHolder.chart.getAxisLeft().setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        data.setValueTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        mainHolder.chart.getLegend().setEnabled(false);

        lineDataSet.setCircleSize(3f);
        lineDataSet.setLineWidth(2f);
        data.setValueTextSize(5f);
        data.setHighlightEnabled(false);

        mainHolder.chart.invalidate();
        mainHolder.chart.setAlpha(1);
        mainHolder.chart.getXAxis().setGranularity(1f);

        SensorData sensorData = resultPojos.get(position).getMainData();
        mainHolder.txt_bgl.setText(String.valueOf(sensorData.getBgl()));
        mainHolder.txt_timestamp.setText(resultPojos.get(position).getTimeStampId());
        mainHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Result.class);

                intent.putExtra("isViewHisory",false);
                intent.putExtra("bglAve",String.valueOf(resultPojo.bglAve()));
                intent.putExtra("sensorData",new Gson().toJson(resultPojo.getMainData()));
                intent.putExtra("entries",new Gson().toJson(resultPojo.getSensorDatas()));
                context.startActivity(intent);
            }
        });

        mainHolder.v_normal.setVisibility(View.GONE);
        mainHolder.v_pre.setVisibility(View.GONE);
        mainHolder.v_diabetes.setVisibility(View.GONE);


        float bgl = sensorData.getBgl();
//        if (mmol < 0.18){
//            mainHolder.v_normal.setVisibility(View.VISIBLE);
//            mainHolder.v_pre.setVisibility(View.VISIBLE);
//            mainHolder.v_diabetes.setVisibility(View.VISIBLE);
//            mainHolder.txt_status.setText("Diabetic");
//        }else if(mmol > 0.19 && mmol < 0.20 ){
//            mainHolder.v_normal.setVisibility(View.VISIBLE);
//            mainHolder.v_pre.setVisibility(View.VISIBLE);
//            mainHolder.txt_status.setText("Pre-diabetic");
//        }else{
//            mainHolder.v_normal.setVisibility(View.VISIBLE);
//            mainHolder.txt_status.setText("Normal");
//        }
        String ret ="";
        if (bgl > 125){
            ret = "Diabetic";
            mainHolder.v_normal.setVisibility(View.GONE);
            mainHolder.v_pre.setVisibility(View.GONE);
            mainHolder.v_diabetes.setVisibility(View.VISIBLE);
        }else if(bgl > 100 && bgl < 125){
            ret = "Pre-diabetic";
            mainHolder.v_normal.setVisibility(View.GONE);
            mainHolder.v_pre.setVisibility(View.VISIBLE);
            mainHolder.v_diabetes.setVisibility(View.GONE);
        }else{
            ret = "Normal";
            mainHolder.v_normal.setVisibility(View.VISIBLE);
            mainHolder.v_pre.setVisibility(View.GONE);
            mainHolder.v_diabetes.setVisibility(View.GONE);
        }
        mainHolder.txt_status.setText(ret);
    }

    public static class MainHolder extends RecyclerView.ViewHolder {
        LineChart chart;
        CardView card;
        TextView txt_bgl,txt_timestamp, txt_status;
        View v_normal,v_pre,v_diabetes;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.card);
            chart = (LineChart) itemView.findViewById(R.id.chart);
            txt_bgl = (TextView) itemView.findViewById(R.id.txt_bgl);
            txt_timestamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);

            v_normal = (View) itemView.findViewById(R.id.v_normal);
            v_pre = (View) itemView.findViewById(R.id.v_pre);
            v_diabetes = (View) itemView.findViewById(R.id.v_diabetes);
        }
    }
}
