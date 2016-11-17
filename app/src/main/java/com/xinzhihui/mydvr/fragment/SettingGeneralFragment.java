package com.xinzhihui.mydvr.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinzhihui.mydvr.R;
/**
 * Created by Administrator on 2016/11/17.
 */

/**
 * 锁视频列表Fragment
 */
public class SettingGeneralFragment extends Fragment {

    public SettingGeneralFragment() {
        // Required empty public constructor
    }

    public static SettingGeneralFragment newInstance() {
        SettingGeneralFragment fragment = new SettingGeneralFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_general, container, false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
