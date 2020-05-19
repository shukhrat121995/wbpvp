package com.shukhrat.wbpvp.ui.opendata;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shukhrat.wbpvp.R;

import java.util.ArrayList;
import java.util.List;

public class OpenDataFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private BottomSheetDialog dialog;

    public OpenDataFragment() {
        // Required empty public constructor
    }
    public static OpenDataFragment newInstance(String param1, String param2) {
        OpenDataFragment fragment = new OpenDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_open_data, container, false);

        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null){
            FragmentManager fragmentManager = getFragmentManager();
            assert fragmentManager != null;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sohil and move the camera
        LatLng sohil = new LatLng(40.87639538225784, 72.56981942524408);

        mMap.setMinZoomPreference(6.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sohil));
        mMap.addMarker(new MarkerOptions().position(sohil).title("Sohil MFY"));

        final View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                loadChartTotalPopulation(view);
                projectsTableView(view);

                dialog.show();
            }
        });
    }

    private void loadChartTotalPopulation(View view){
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_total_population);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Men", 5835));
        data.add(new ValueDataEntry("Woman", 6831));
        pie.data(data);

        pie.title("Total Population: 12666");
        pie.title().fontColor("#858585");
        pie.title().fontSize(14);
        pie.title().fontWeight("bold");
        pie.background().fill("#f2f3f5");
        pie.background().cornerType("round");
        pie.background().corners(10);

        pie.palette(new String[]{"#753099", "#3c0150"});


        anyChartView.setChart(pie);
    }

    /*private void loadChartProjects(View view){
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_projects);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Cartesian cartesian = AnyChart.column();



        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("School", 80540));
        data.add(new ValueDataEntry("Kindergarten", 94190));
        data.add(new ValueDataEntry("Road", 102610));
        data.add(new ValueDataEntry("Bridge", 110430));
        data.add(new ValueDataEntry("Hospital", 128000));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        column.fill("#753099");
        column.stroke("#753099");

        cartesian.animation(true);
        cartesian.title("Total projects investment: $515770");
        cartesian.title().fontColor("#858585");
        cartesian.title().fontSize(14);
        cartesian.title().fontWeight("bold");
        cartesian.background().fill("#f2f3f5");
        cartesian.background().cornerType("round");
        cartesian.background().corners(10);




        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        //cartesian.xAxis(0).title("Product");
        //cartesian.yAxis(0).title("Revenue");

        anyChartView.setChart(cartesian);
    }*/

    private void projectsTableView(View view){
        final TableLayout prices = (TableLayout)view.findViewById(R.id.table_projects);

        prices.setStretchAllColumns(true);
        prices.bringToFront();

        ArrayList<String> project_year = new ArrayList<>();
        project_year.add("Year");
        project_year.add("2020");
        project_year.add("2020");
        project_year.add("2021");
        project_year.add("2022");
        project_year.add("2022");

        ArrayList<String> project_name = new ArrayList<>();
        project_name.add("Name");
        project_name.add("School");
        project_name.add("Kindergarten");
        project_name.add("Road");
        project_name.add("Bridge");
        project_name.add("Hospital");

        ArrayList<String> total_investment = new ArrayList<>();
        total_investment.add("Total investment");
        total_investment.add("$80540");
        total_investment.add("$94190");
        total_investment.add("$102610");
        total_investment.add("$110430");
        total_investment.add("$128000");

        for(int i = 0; i < 6; i++){
            TableRow tr =  new TableRow(getContext());
            TextView c1 = new TextView(getContext());
            c1.setTextSize(14);
            c1.setTextColor(Color.BLACK);
            c1.setGravity(Gravity.CENTER);
            c1.setPadding(5,5,5,5);
            c1.setBackground(getResources().getDrawable(R.drawable.table_layout_border));
            c1.setText(project_name.get(i));
            TextView c2 = new TextView(getContext());
            c2.setTextSize(14);
            c2.setTextColor(Color.BLACK);
            c2.setGravity(Gravity.CENTER);
            c2.setPadding(5,5,5,5);
            c2.setBackground(getResources().getDrawable(R.drawable.table_layout_border));
            c2.setText(project_year.get(i));
            TextView c3 = new TextView(getContext());
            c3.setTextSize(14);
            c3.setTextColor(Color.BLACK);
            c3.setGravity(Gravity.CENTER);
            c3.setPadding(5,5,5,5);
            c3.setBackground(getResources().getDrawable(R.drawable.table_layout_border));
            c3.setText(total_investment.get(i));
            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);

            if(i == 0){
                c1.setTextColor(Color.WHITE);
                c1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                c2.setTextColor(Color.WHITE);
                c2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                c3.setTextColor(Color.WHITE);
                c3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            prices.addView(tr);
        }

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                prices.removeAllViews();
            }
        });
    }
}
