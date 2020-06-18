package com.example.buzzr;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buzzr.HelperClasses.userHelperClass;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GraphActivity extends AppCompatActivity {
    BarChart barChart;
    ArrayList<String> dates;
    Random random;
    ArrayList<BarEntry> barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        barChart = (BarChart) findViewById(R.id.bargraph);
        createRandomBarGraph("2020/06/01", "2020/06/08");
    }

    public void createRandomBarGraph(String Date1, String Date2){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date1 = simpleDateFormat.parse(Date1);
            Date date2 = simpleDateFormat.parse(Date2);

            Calendar mDate1 = Calendar.getInstance();
            Calendar mDate2 = Calendar.getInstance();
            mDate1.clear();
            mDate2.clear();

            mDate1.setTime(date1);
            mDate2.setTime(date2);

            dates = new ArrayList<>();
            dates = getList(mDate1,mDate2);

            barEntries = new ArrayList<>();
            float max = 0f;
            float value = 0f;
            random = new Random();

            userHelperClass preference = new userHelperClass(getApplicationContext());

            for(int j = 0; j< dates.size();j++){
                max = 100f;
//                value = random.nextFloat()*max;
//                barEntries.add(new BarEntry(Math.round(value),j));
                if(j == 0) {
                    barEntries.add(new BarEntry(36, j));
                } else if(j == 1) {
                    barEntries.add(new BarEntry(23, j));
                } else if (j == 2) {
                    barEntries.add(new BarEntry(preference.getCounter(), j));
                } else {
                    barEntries.add(new BarEntry(0, j));
                }
            }

        }catch(ParseException e){
            e.printStackTrace();
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Number of times you touched your face");
        BarData barData = new BarData(dates,barDataSet);
        barChart.setData(barData);
        barChart.setDescription("WEEK 3");
        barDataSet.setColor(Color.BLACK);
    }

    public ArrayList<String> getList(Calendar startDate, Calendar endDate){
        ArrayList<String> list = new ArrayList<String>();
        while(startDate.compareTo(endDate)<=0){
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_WEEK,1);
        }
        return list;
    }

    public String getDate(Calendar cld){
        String curDate = "";
        String s= cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                +cld.get(Calendar.DAY_OF_WEEK);
        try{
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(curDate);
            curDate =  new SimpleDateFormat("yyyy/MM/dd").format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return curDate;
    }

}