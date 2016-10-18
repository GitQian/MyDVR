package com.xinzhihui.mydvr.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xinzhihui.mydvr.MyApplication;
import com.xinzhihui.mydvr.R;
import com.xinzhihui.mydvr.VideoPlayerActivity;
import com.xinzhihui.mydvr.adapter.LockVideoAdapter;
import com.xinzhihui.mydvr.db.LockVideoDAL;

import java.util.ArrayList;
import java.util.List;

/**
 * 锁视频列表Fragment
 */
public class LockVideoFragment extends Fragment {

    private ListView mListView;
    private LockVideoAdapter mAdapter;
    private List<String> mPathList;

    public LockVideoFragment() {
        // Required empty public constructor
    }

    public static LockVideoFragment newInstance() {
        LockVideoFragment fragment = new LockVideoFragment();
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
        View view = inflater.inflate(R.layout.fragment_lock_video, container, false);
        mListView = (ListView) view.findViewById(R.id.listview_file);

        mPathList = getData();
        mAdapter = new LockVideoAdapter(mPathList, getActivity());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putStringArrayListExtra("extra_play_list", (ArrayList<String>) mPathList);
                intent.putExtra("extra_play_index", position);
                startActivity(intent);
            }
        });
        return view;
    }

    private List<String> getData() {
        List<String> list;
        LockVideoDAL lockVideoDAL = new LockVideoDAL(MyApplication.getContext());
        list = lockVideoDAL.getAllLockVideo();
        return list;
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
