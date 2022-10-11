package com.example.glucosetrainmodel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.example.glucosetrainmodel.ViewResulTrainModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.example.glucosetrainmodel.Pojo.EntrySensorData;
import com.example.glucosetrainmodel.Pojo.TrainPojo;
import com.example.glucosetrainmodel.Pojo.SensorData;
import com.example.glucosetrainmodel.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ResultTrainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG ="ResultTrainAdapter";
    private Context context;
    private ArrayList<TrainPojo> trainPojos;
    private String title;
    public ResultTrainAdapter(Context context, ArrayList<TrainPojo> trainPojos,String title) {
        this.context = context;
        this.trainPojos =trainPojos;
        this.title =title;
    }

    @Override
    public int getItemCount() {
        return trainPojos.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.ui_history_content, parent, false);
        return new ResultTrainAdapter.MainHolder(menuItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ResultTrainAdapter.MainHolder mainHolder = (ResultTrainAdapter.MainHolder) holder;
        final EntrySensorData dataAdapter = trainPojos.get(position).getEntrilList().get(0);

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

        SensorData sensorData = trainPojos.get(position).getSensorData();
        mainHolder.txt_mq3ppm.setText(String.valueOf(sensorData.getMq3_ppm()));
        mainHolder.txt_timestamp.setText(trainPojos.get(position).getCreatedAt());
        mainHolder.txt_humidity.setText(Html.fromHtml("<b>Humidity </b>"+String.valueOf((sensorData.getDht_humidity()))));
        mainHolder.txt_celcius.setText(Html.fromHtml("<b>Celcius </b>"+String.valueOf(sensorData.getDht_celcius())+" °C"));
        mainHolder.txt_fahrenheit.setText(Html.fromHtml("<b>Fahrenheit </b>"+String.valueOf(sensorData.getDht_fahrenheit())+" °F"));
        mainHolder.txt_name.setText(trainPojos.get(position).getName());
        mainHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ViewResulTrainModel.class)
                        .putExtra("title", title)
                        .putExtra("trainPojo",new Gson().toJson(trainPojos.get(position)));
                context.startActivity(intent);
            }
        });
    }


    public static class MainHolder extends RecyclerView.ViewHolder {
        LineChart chart;
        CardView card;
        TextView txt_mq3ppm,txt_timestamp,txt_humidity,txt_celcius,txt_fahrenheit
                ,txt_name;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            chart = (LineChart) itemView.findViewById(R.id.chart);
            txt_mq3ppm = (TextView) itemView.findViewById(R.id.txt_mq3ppm);
            txt_timestamp = (TextView) itemView.findViewById(R.id.txt_timestamp);
            txt_humidity = (TextView) itemView.findViewById(R.id.txt_humidity);
            txt_celcius = (TextView) itemView.findViewById(R.id.txt_celcius);
            txt_fahrenheit = (TextView) itemView.findViewById(R.id.txt_fahrenheit);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            card = (CardView) itemView.findViewById(R.id.card);
        }
    }
}
