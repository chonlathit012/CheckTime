package com.example.idont.checktime;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyListFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ListView listView;
    TextView textViewNoCompany;
    CompanyListCustomAdapter companyListCustomAdapter;

    String json = "";
    String jsonReceive = "";
    String uid;
    int company_id;

    public static CompanyListFragment newInstance() {
        CompanyListFragment fragment = new CompanyListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_list, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        listView = (ListView) view.findViewById(R.id.listView);
        textViewNoCompany = (TextView) view.findViewById(R.id.textViewNoCompany);

        getCompanyData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                CompanyListReceive companyListReceive = gson.fromJson(jsonReceive, CompanyListReceive.class);
                List<CompanyListDataReceive> companyListDataReceives = companyListReceive.getData().getCompany_list();

                company_id = companyListDataReceives.get(position).getCompany_id();

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle("Welcome to the " + companyListDataReceives.get(position)
                        .getCompany_name() + " company.");
                builder.setMessage("Would you like to join this company?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        joinCompany();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

    }

    public void joinCompany(){
        Gson gson = new Gson();
        SelectCompanyData selectCompanyData = new SelectCompanyData();
        selectCompanyData.setId(uid);
        selectCompanyData.setCompany_id(String.valueOf(company_id));

        SelectCompanySned selectCompanySned = new SelectCompanySned();
        selectCompanySned.setTarget("select_company");
        selectCompanySned.setData(selectCompanyData);

        json = gson.toJson(selectCompanySned);

        new HttpTask(CompanyListFragment.this).execute(json);

    }

    public void getCompanyData() {
        Gson gson = new Gson();

        CompanyListSend companyListSend = new CompanyListSend();
        companyListSend.setTarget("company_list");

        json = gson.toJson(companyListSend);

        new HttpTask(CompanyListFragment.this).execute(json);
    }

    public void showData() {

        Gson gson = new Gson();
        CompanyListReceive companyListReceive = gson.fromJson(jsonReceive, CompanyListReceive.class);
        List<CompanyListDataReceive> companyListDataReceives = companyListReceive.getData().getCompany_list();

        int sizeCompanyList = companyListDataReceives.size();

        if (sizeCompanyList == 0) {
            textViewNoCompany.setText("No Company");
        } else {
            companyListCustomAdapter = new CompanyListCustomAdapter(getActivity(), companyListDataReceives);
            listView.setAdapter(companyListCustomAdapter);
        }
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);

        String message = checkTitle.getMessage();

        switch (message) {
            case "Join success.":
                startActivity(new Intent(getActivity(), LoadActivity.class));
                getActivity().finish();
                break;
            default :
                showData();
        }
    }
}
