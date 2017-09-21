package com.example.idont.checktime;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import static android.R.attr.filter;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeListManagerFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String json = "";
    String jsonReceive = "";
    String uid;
    String company_id;
    String employee_id;

    ListView listView;
    TextView textViewNoEmployee;
    ImageView imageView;
    SearchView searchView;

    EmployeeListManagerCustomAdapter employeeListManagerCustomAdapter;


    public static EmployeeListManagerFragment newInstance() {
        EmployeeListManagerFragment fragment = new EmployeeListManagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_list_manager, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI(view.findViewById(R.id.layout));
        
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        listView = (ListView) view.findViewById(R.id.listView);
        textViewNoEmployee = (TextView) view.findViewById(R.id.textViewNoEmployee);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        searchView =(SearchView) view.findViewById(R.id.searchView);

        getCompanyId();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getEmployeeList();

                Gson gson = new Gson();
                EmployeeListManagerRecive employeeListManagerRecive = gson.
                        fromJson(jsonReceive,EmployeeListManagerRecive.class);
                List<EmployeeListManagerDataReceive> listManagerDataReceives = employeeListManagerRecive.
                        getData().getEmployee_list();

                employee_id = listManagerDataReceives.get(position).getId();

                Intent intent = new Intent(getActivity(),EmployeeProfileActivity.class);
                intent.putExtra("employee_id",employee_id);
                startActivity(intent);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                employeeListManagerCustomAdapter.filter(newText);
                return false;
            }
        });

    }

    public void getEmployeeList() {
        Gson gson = new Gson();
        EmployeeListManagerData employeeListManagerData = new EmployeeListManagerData();
        employeeListManagerData.setCompany_id(company_id);

        EmployeeListManagerSend employeeListManagerSend = new EmployeeListManagerSend();
        employeeListManagerSend.setTarget("employee_list");
        employeeListManagerSend.setData(employeeListManagerData);

        json = gson.toJson(employeeListManagerSend);

        new HttpTask(EmployeeListManagerFragment.this).execute(json);

    }

    public void showEmployeeList() {
        Gson gson = new Gson();
        EmployeeListManagerRecive employeeListManagerRecive = gson.fromJson(jsonReceive, EmployeeListManagerRecive.class);
        List<EmployeeListManagerDataReceive> listManagerDataReceives = employeeListManagerRecive.getData().getEmployee_list();

        employeeListManagerCustomAdapter = new EmployeeListManagerCustomAdapter(getActivity(), listManagerDataReceives);
        listView.setAdapter(employeeListManagerCustomAdapter);

    }

    public void getCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdData getCompanyIdData = new GetCompanyIdData();
        getCompanyIdData.setId(uid);

        GetCompanyIdSend getCompanyIdSend = new GetCompanyIdSend();
        getCompanyIdSend.setTarget("get_company_id");
        getCompanyIdSend.setData(getCompanyIdData);

        json = gson.toJson(getCompanyIdSend);

        new HttpTask(EmployeeListManagerFragment.this).execute(json);
    }

    public void showCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdReceive getCompanyIdReceive = gson.fromJson(jsonReceive, GetCompanyIdReceive.class);
        company_id = getCompanyIdReceive.getData().getCompany_id();

        getEmployeeList();
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

            String message = checkTitle.getMessage();

            switch (message) {
                case "Get company_id success.":
                    showCompanyId();
                    break;
                case "Get employee_list sucess.":
                    showEmployeeList();
                    break;
                case "Get employee_list failed.":
                    textViewNoEmployee.setText("No Employee");
                    imageView.setImageResource(R.drawable.no_person);
                    break;
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
