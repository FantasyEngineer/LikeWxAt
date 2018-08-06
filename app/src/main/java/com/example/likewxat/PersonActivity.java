package com.example.likewxat;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.likewxat.adapter.PersonAdapter;
import com.example.likewxat.person.Person;

public class PersonActivity extends Activity {

    /**
     * ����ѡ�е��˶�Ӧ��id���ַ��� id�Կո�ָ�
     */
    private String mIniSelectedCids;

    public static final String KEY_CID = "cid";
    public static final String KEY_NAME = "name";
    public static final String KEY_SELECTED = "selected";
    private ListView mListView;
    private PersonAdapter mAdapter;
    private List<Person> mPersons = new ArrayList<Person>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at);
        Intent intent = getIntent();
        mIniSelectedCids = intent.getStringExtra(KEY_SELECTED);
        findViewById();
        mAdapter = new PersonAdapter(mPersons, this);
        mListView.setAdapter(mAdapter);
        inflateListView();

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Person person = (Person) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(KEY_CID, person.getId() + " ");
                intent.putExtra(KEY_NAME, "@" + person.getName() + " ");

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void findViewById() {
        mListView = (ListView) findViewById(R.id.lv);
    }

    private void inflateListView() {
        mPersons.clear();
        for (int i = 0; i < 30; i++) {
            Person person = new Person();
            person.setId(String.valueOf(i));
            person.setName("啊啊啊啊啊啊啊" + i);
            mPersons.add(person);
        }
        removeSelectedCids();
        mAdapter.notifyDataSetChanged();
    }

    private void removeSelectedCids() {
        Person entity = null;
        List<Person> tmp = new ArrayList<Person>();
        for (int i = 0; i < mPersons.size(); i++) {
            entity = mPersons.get(i);
            if (mIniSelectedCids != null) {
                if (mIniSelectedCids.contains(entity.getId())) {
                    tmp.add(entity);
                }
            }
        }

        mPersons.removeAll(tmp);
    }

}
