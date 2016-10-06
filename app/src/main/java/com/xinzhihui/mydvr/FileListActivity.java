package com.xinzhihui.mydvr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xinzhihui.mydvr.adapter.FileListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileListActivity extends AppCompatActivity {

    private ListView fileListView;
    private List<Map<String, Object>> fileList;
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
                    curDir = (String) fileList.get(position).get("info");
                    fileList = getData();
                    fileListAdapter = new FileListAdapter(fileList, FileListActivity.this);
                    fileListView.setAdapter(fileListAdapter);
                } else {
                    //视屏文件

                }
            }
        });
    }

    private void findView() {
        fileListView = (ListView) findViewById(R.id.listview_file);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        File f = new File(curDir);
        File[] files = f.listFiles();

        if (!curDir.equals(AppConfig.DVR_PATH)) {
            //不在根目录下，加...
            map = new HashMap<String, Object>();
            map.put("title", "Back to ../");
            map.put("info", f.getParent()); //父目录
            map.put("img", R.drawable.icon_file);
            list.add(map);
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                map.put("title", files[i].getName());
                map.put("info", files[i].getPath());
                if (files[i].isDirectory()) {
                    map.put("img", R.drawable.icon_file);
                } else if (files[i].isFile()) {
                    map.put("img", R.drawable.icon_doc);
                }
                list.add(map);
            }
        }
        return list;
    }
}
