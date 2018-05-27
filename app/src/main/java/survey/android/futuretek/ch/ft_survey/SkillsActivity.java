/**
 * Copyright (C) futuretek AG 2016
 * All Rights Reserved
 *
 * @author Artan Veliju
 */
package survey.android.futuretek.ch.ft_survey;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SkillsActivity extends BaseActivity {
    private String newSkill;
    private Button btn_add;
    private ListView listview;
    public List<String> _productlist = new ArrayList<String>();
    private ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        listview = (ListView) findViewById(R.id.listView);
        View mainTextView = findViewById(R.id.textLayout);
        mainTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ViewGroup)findViewById(R.id.textLayout)).removeAllViews();
        List<String> textArray = new ArrayList<>(1);
        textArray.add("Please add a developer skill");
        animateText(textArray);
        _productlist.clear();
        _productlist = getDatabase().getAllSkills();
        adapter = new ListAdapter(this);
        listview.setAdapter(adapter);
        animateText(textArray, new AnimationListDone() {
            public void done() {
                addSkill();
            }
        });
    }

    private class ListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ViewHolder viewHolder;

        public ListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return _productlist.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_row, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView1);
                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ViewGroup row = ((ViewGroup)v.getParent());
                        final String id = ((TextView)row.findViewById(R.id.textView1)).getText().toString();
                        List<String> textArray = new ArrayList<>(1);
                        textArray.add("Update a skill " + id);
                        animateText(textArray, new AnimationListDone() {
                            public void done() {
                                updateSkill(id);
                            }
                        });
                        _productlist.remove(id);
                        adapter.notifyDataSetChanged();
                    }
                });
                viewHolder.delBtn = (Button) convertView.findViewById(R.id.deleteBtn);
                viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ViewGroup row = ((ViewGroup)v.getParent());
                        String id = ((TextView)row.findViewById(R.id.textView1)).getText().toString();
                        getDatabase().deleteSkill(id);
                        _productlist.remove(id);
                        adapter.notifyDataSetChanged();
                    }
                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(_productlist.get(position));
            return convertView;
        }
    }

    private void addSkill(){
        if(newSkill==null){
            openInputDialog(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText userInput = ((EditText) v.findViewById(R.id.userInput));
                    newSkill = null;
                    try {
                        newSkill = getDatabase().getSkill(String.valueOf(userInput.getText()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (newSkill == null || newSkill.isEmpty()) {
                        insertSkill(String.valueOf(userInput.getText()));
                        List<String> textArray = new ArrayList<String>(1);
                        textArray.add("Skill " + String.valueOf(userInput.getText()) + " added !");
                        animateText(textArray, new AnimationListDone() {
                            public void done() {
                            }
                        });

                    } else {
                        List<String> textArray = new ArrayList<String>(2);
                        textArray.add("Skill already in database. Try again.");
                        animateText(textArray, new AnimationListDone() {
                            public void done() {
                            }
                        });
                    }
                }
            });
        }
    }

    private void updateSkill(final String id){
            openInputDialog(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText userInput = ((EditText) v.findViewById(R.id.userInput));
                    if (String.valueOf(userInput.getText()).length() > 0) {
                        updateSkill(id, String.valueOf(userInput.getText()));
                        List<String> textArray = new ArrayList<String>(1);
                        textArray.add("Skill " + String.valueOf(userInput.getText()) + " updated !");
                        animateText(textArray, new AnimationListDone() {
                            public void done() {
                            }
                        });

                    } else {
                        List<String> textArray = new ArrayList<String>(2);
                        textArray.add("Skill name must not be empty");
                        animateText(textArray, new AnimationListDone() {
                            public void done() {
                                updateSkill(id);
                            }
                        });
                    }
                }
            });

    }

    private class ViewHolder {
        TextView textView;
        Button delBtn;

    }

    private void insertSkill(String skill){
        try {
            getDatabase().putSkill(skill);
            _productlist = getDatabase().getAllSkills();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSkill(String id, String skill){
        try {
            getDatabase().deleteSkill(id);
            insertSkill(skill);
            _productlist = getDatabase().getAllSkills();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}