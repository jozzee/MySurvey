package com.jozzee.mysurvey.register;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.RegisterBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.support.Validate;

public class RegisterNameFragment extends Fragment {
    public static  final String TAG = RegisterNameFragment.class.getSimpleName();

    private RegisterBean registerBean;
    private View rootView;
    private TextInputLayout layoutName;
    private EditText editTextName;
    private Button next;
    private Validate validate;


    public static RegisterNameFragment newInstance(String registerBeanAsJsonString){
        RegisterNameFragment fragment = new RegisterNameFragment();
        Bundle args = new Bundle();
        args.putString("registerBeanAsJsonString",registerBeanAsJsonString);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterNameFragment() {

    }

    @Override
    public void onAttach(Context context) { Log.i(TAG, "onAttach");
        super.onAttach(context);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) { Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); Log.i(TAG, "setRetainInstance");
        if (getArguments() != null) {Log.i(TAG, "getArguments");
            registerBean = new RegisterBean(getArguments().getString("registerBeanAsJsonString"));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_register_name, container, false);
        layoutName = (TextInputLayout)rootView.findViewById(R.id.TextInputLayout_name_register);
        editTextName = (EditText)rootView.findViewById(R.id.editText_name_register);
        next = (Button)rootView.findViewById(R.id.button_nextName_registerEmail);
        validate = new Validate();

        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    next.callOnClick();
                }
                return false;
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(validate.validAccountName(editTextName,layoutName)){
                    registerBean.setName(editTextName.getText().toString().trim());
                    RegisterPasswordFragment passwordFragment = new RegisterPasswordFragment().newInstance(new Gson().toJson(registerBean));
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_register, passwordFragment).commit();
                }
            }
        });
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){

        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



}
