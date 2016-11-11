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
import android.widget.Toast;

import com.xinzhihui.mydvr.R;
import com.xinzhihui.mydvr.VideoPlayerActivity;
import com.xinzhihui.mydvr.adapter.FileListAdapter;
import com.xinzhihui.mydvr.utils.LogUtil;
import com.xinzhihui.mydvr.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采用分类形式显示文件Fragment
 */
public class VideoFileFragment extends Fragment {
    private static final String ARG_FILE_TYPE = "FileType";
    private static final String ARG_DIR_URL = "DirUrl";
    private String mFileType;
    private String mDirUrl;

    private ListView mFileListView;
    private List<Map<String, Object>> mFileList;
    private ArrayList<String> mVideopathList;  //视屏播放器需要VideoList
    private FileListAdapter mFileListAdapter;
    private String mRootDirPath = null;
    private String mCurDirPath = null;

    private OnFragmentInteractionListener mListener;

    public VideoFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fileType Parameter 1.
     * @param dirUrl   Parameter 2.
     * @return A new instance of fragment VideoFileFragment.
     */
    public static VideoFileFragment newInstance(String fileType, String dirUrl) {
        VideoFileFragment fragment = new VideoFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_TYPE, fileType);
        args.putString(ARG_DIR_URL, dirUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileType = getArguments().getString(ARG_FILE_TYPE);
            mDirUrl = getArguments().getString(ARG_DIR_URL);
            mRootDirPath = mDirUrl;
            mCurDirPath = mDirUrl;
        }
        LogUtil.d("qiansheng", mFileType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_file, container, false);
        findView(view);

        mFileList = getData();

        mFileListAdapter = new FileListAdapter(mFileList, getActivity());

        mFileListView.setAdapter(mFileListAdapter);

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((int) mFileList.get(position).get("img") == R.drawable.icon_file) {
                    //目录
                    mCurDirPath = (String) mFileList.get(position).get("path");
                    mFileList = getData();
                    mFileListAdapter = new FileListAdapter(mFileList, getActivity());
                    mFileListView.setAdapter(mFileListAdapter);
                } else if ((int) mFileList.get(position).get("img") == R.drawable.icon_file_normalvideo) {
                    //TODO 视屏文件
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putStringArrayListExtra("extra_play_list", mVideopathList);
                    mFileList.get(position).get("path");
                    if (mVideopathList.contains((String) mFileList.get(position).get("path"))) {
                        int index = mVideopathList.indexOf((String) mFileList.get(position).get("path"));
                        intent.putExtra("extra_play_index", index);
                        startActivity(intent);
                    }

                } else {
                    //其他普通文件
                    //TODO 图片文件使用图片浏览器
                    File file = new File((String) mFileList.get(position).get("path"));
                    Intent picIntent = new Intent(Intent.ACTION_VIEW);
                    picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    try {
                        startActivity(picIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "找不到应用程序打开该文件", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return view;
    }

    private void findView(View view) {
        mFileListView = (ListView) view.findViewById(R.id.listview_file);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        mVideopathList = new ArrayList<String>();
        Map<String, Object> map = null;
        File f = new File(mCurDirPath);
        File[] files = f.listFiles();

        if (!mCurDirPath.equals(mRootDirPath)) {
            //不在根目录下，加...
            map = new HashMap<String, Object>();
            map.put("title", "Back to ../");
            map.put("path", f.getParent()); //父目录
            map.put("img", R.drawable.icon_file);
            list.add(map);
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                map.put("title", files[i].getName());
                map.put("path", files[i].getPath());  //path
                if (files[i].isDirectory()) {
                    map.put("img", R.drawable.icon_file);
                } else if (files[i].isFile() & ".ts".equalsIgnoreCase(StringUtils.getPathSuffix(files[i].getName())) || ".mp4".equalsIgnoreCase(StringUtils.getPathSuffix(files[i].getName()))) {
                    //TODO 视屏文件
                    map.put("img", R.drawable.icon_file_normalvideo);

                    mVideopathList.add(files[i].getPath());
                } else {
                    //普通文件
//                    map.put("img", R.drawable.icon_doc);
                    map.put("img", R.drawable.icon_picture);
                }
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
