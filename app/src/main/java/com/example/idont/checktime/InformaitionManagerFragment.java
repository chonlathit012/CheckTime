package com.example.idont.checktime;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;

public class InformaitionManagerFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    CoordinatorLayout coordinatorLayout;

    TextView textViewNoData;
    TextView getTextViewNoDataList;
    TextView textViewDay;
    TextView textViewDayColor;
    TextView textViewTotal;
    TextView textViewTotalColor;

    ListView listView;
    PieView pieView;

    String json = "";
    String jsonReceive = "";
    String message;
    String uid;
    String company_id;
    String employee_id;

    int totalNum = 2;
    int totalem;
    int late;

    InformationManagerCustomAdapter informationManagerCustomAdapter;

    public static InformaitionManagerFragment newInstance() {
        InformaitionManagerFragment fragment = new InformaitionManagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informaition_manager, container, false);
        return view;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        pieView = (PieView) view.findViewById(R.id.pie_view);
        listView = (ListView) view.findViewById(R.id.listView);

        textViewDay = (TextView) view.findViewById(R.id.day);
        textViewDayColor = (TextView) view.findViewById(R.id.day_color);
        textViewTotal = (TextView) view.findViewById(R.id.total);
        textViewTotalColor = (TextView) view.findViewById(R.id.total_color);
        textViewNoData = (TextView) view.findViewById(R.id.textViewNoData);
        getTextViewNoDataList = (TextView) view.findViewById(R.id.textViewNoDataList);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);

        getCompanyId();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getEmployeeCount();
                Gson gson = new Gson();
                InformationReceive informationReceive = gson.fromJson(jsonReceive, InformationReceive.class);
                List<InformationListDataReceive> listDataReceives = informationReceive.getData().getEmployee_list_late();

                employee_id = listDataReceives.get(position).getId();

                Intent intent = new Intent(getActivity(), EmployeeProfileActivity.class);
                intent.putExtra("employee_id", employee_id);
                startActivity(intent);
            }
        });
    }

    public void showEmployeeCount() {
        Gson gson = new Gson();
        InformationReceive informationReceive = gson.fromJson(jsonReceive, InformationReceive.class);
        List<InformationListDataReceive> listDataReceives = informationReceive.getData().getEmployee_list_late();

        String day_count = informationReceive.getData().getEmployee_day_count();
        String total_count = informationReceive.getData().getEmployee_total();

        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        ArrayList<Integer> intList = new ArrayList<Integer>();

        if (!day_count.equals("0")) {
            if (listDataReceives.size() == 0) {
                getTextViewNoDataList.setText("No data");
            }
            totalem = Integer.parseInt(total_count);
            late = Integer.parseInt(day_count);
            intList.add(0, late);
            intList.add(1, totalem - late);

            for (int i = 0; i < totalNum; i++) {
                pieHelperArrayList.add(new PieHelper(100f * intList.get(i) / totalem));
            }

            textViewDay.setText("Absent : " + String.valueOf(totalem - late));
            textViewDayColor.setBackgroundResource(R.color.color_day);
            textViewTotal.setText("Present : " + String.valueOf(late));
            textViewTotalColor.setBackgroundResource(R.color.color_total);

            pieView.selectedPie(PieView.NO_SELECTED_INDEX);
            pieView.showPercentLabel(true);
            pieView.setDate(pieHelperArrayList);

            if (getActivity() != null) {
                informationManagerCustomAdapter = new InformationManagerCustomAdapter(getActivity(), listDataReceives);
                listView.setAdapter(informationManagerCustomAdapter);
            }
        } else {
            textViewNoData.setText("No data");
        }

    }

    public void getEmployeeCount() {
        Gson gson = new Gson();
        InformationData informationData = new InformationData();
        informationData.setCompany_id(company_id);

        InformationSend informationSend = new InformationSend();
        informationSend.setTarget("employee_count");
        informationSend.setData(informationData);

        json = gson.toJson(informationSend);

        new HttpTask(InformaitionManagerFragment.this).execute(json);

    }

    public void showCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdReceive getCompanyIdReceive = gson.fromJson(jsonReceive, GetCompanyIdReceive.class);
        company_id = getCompanyIdReceive.getData().getCompany_id();

        getEmployeeCount();
    }

    public void getCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdData getCompanyIdData = new GetCompanyIdData();
        getCompanyIdData.setId(uid);

        GetCompanyIdSend getCompanyIdSend = new GetCompanyIdSend();
        getCompanyIdSend.setTarget("get_company_id");
        getCompanyIdSend.setData(getCompanyIdData);

        json = gson.toJson(getCompanyIdSend);

        new HttpTask(InformaitionManagerFragment.this).execute(json);
    }

    private void randomSet(PieView pieView) {
        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
        ArrayList<Integer> intList = new ArrayList<Integer>();

        totalem = 90;
        late = (int) (Math.random() * 30) + 3;
        intList.add(0, totalem - late);
        intList.add(1, late);

        for (int i = 0; i < totalNum; i++) {
            pieHelperArrayList.add(new PieHelper(100f * intList.get(i) / totalem));
        }

        pieView.selectedPie(PieView.NO_SELECTED_INDEX);
        pieView.showPercentLabel(true);
        pieView.setDate(pieHelperArrayList);
    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")) {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage("No connection.");
            builder.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finishAffinity();
                    System.exit(0);
                }
            });
            builder.show();
        } else {

            jsonReceive = s;

            Gson gson = new Gson();
            CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);
            message = checkTitle.getMessage();

            switch (message) {
                case "Get company_id success.":
                    showCompanyId();
                    break;
                case "Get data sucess.":
                    showEmployeeCount();
                    break;
                case "Get data failed.":
                    textViewNoData.setText("No data.");
                    break;
            }
        }
    }
}
