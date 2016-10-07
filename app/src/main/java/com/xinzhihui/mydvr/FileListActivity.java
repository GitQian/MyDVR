package com.xinzhihui.mydvr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xinzhihui.mydvr.adapter.FileListAdapter;
import com.xinzhihui.mydvr.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileListActivity extends AppCompatActivity {

    private ListView fileListView;
    private List<Map<String, Object>> fileList;
    private ArrayList<String> videopathList;
    private FileListAdapter fileListAdapter;
    private String curDir = AppConfig.DVR_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        findView();

        fileList = getData();

        fileListAdapter = new FileListAdapter(fileList, this);

        fileListView.setAdapter(fileListAdapter);

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((int) fileList.get(position).get("img") == R.drawable.icon_file) {
                    //目录
                    curDir = (String) fileList.get(position).get("path");
                    fileList = getData();
                    fileListAdapter = new FileListAdapter(fileList, FileListActivity.this);
                    fileListView.setAdapter(fileListAdapter);
                } else if ((int) fileList.get(position).get("img") == R.drawable.icon_doc) {
                    //TODO 视屏文件
                    Intent intent = new Intent(FileListActivity.this, VideoPlayerActivity.class);
                    intent.putStringArrayListExtra("extra_play_list", videopathList);
                    fileList.get(position).get("path");
                    if (videopathList.contains((String) fileList.get(position).get("path"))) {
                        int index = videopathList.indexOf((String) fileList.get(position).get("path"));
                        intent.putExtra("extra_play_index", index);
                        startActivity(intent);
                    }

                } else {
                    //其他普通文件
                }
            }
        });
    }

    private void findView() {
        fileListView = (ListView) findViewById(R.id.listview_file);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        videopathList = new ArrayList<String>();
        Map<String, Object> map = null;
        File f = new File(curDir);
        File[] files = f.listFiles();

        if (!curDir.equals(AppConfig.DVR_PATH)) {
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
                } else if (files[i].isFile() & ".mp4".equalsIgnoreCase(StringUtils.getPathSuffix(files[i].getName()))) {
                    //TODO 视屏文件
                    map.put("img", R.drawable.icon_doc);

                    videopathList.add(i, files[i].getPath());
                } else {
                    //普通文件
//                    map.put("img", R.drawable.icon_doc);
                }
                list.add(map);
            }
        }
        return list;
    }
}
