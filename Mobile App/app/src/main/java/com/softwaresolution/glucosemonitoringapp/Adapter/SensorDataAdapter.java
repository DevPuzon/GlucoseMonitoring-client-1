package com.softwaresolution.glucosemonitoringapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.softwaresolution.glucosemonitoringapp.Pojo.EntrySensorData;
import com.softwaresolution.glucosemonitoringapp.Pojo.SensorData;
import com.softwaresolution.glucosemonitoringapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SensorDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG ="RecyclerAdapterDng"; 
    private Context context;
    private ArrayList<EntrySensorData> list;
    public SensorDataAdapter(Context context, ArrayList<EntrySensorData> list ) {
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
                R.layout.activity_main_content, parent, false);
        return new SensorDataAdapter.MainHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { 
        final SensorDataAdapter.MainHolder mainHolder = (SensorDataAdapter.MainHolder) holder;
        final EntrySensorData dataAdapter = list.get(position);

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

        LineData data = new LineData(lineDataSet);
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
    }


    public static class MainHolder extends RecyclerView.ViewHolder {
        LineChart chart;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            chart = (LineChart) itemView.findViewById(R.id.chart);

        }
    }
}
