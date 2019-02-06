package com.dophin.alarmfragmenttest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Food_Fragement extends Fragment {
    public static String FOOD_NAME = "Food_Name";
    private String name;
    private List<String> names;
    private Context context;

    private RecyclerView mrecyclerview;
    private HomeAdapter madapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        names = getArguments().getStringArrayList(FOOD_NAME);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food,container,false);
        mrecyclerview =(RecyclerView)view.findViewById(R.id.recyclerview_food);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(context));
        mrecyclerview.setAdapter(madapter = new HomeAdapter());


        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    public static Food_Fragement newInstance(List<Food> foods) {
        ArrayList<String> foodnames = new ArrayList<>();
        Food_Fragement mfragement = new Food_Fragement();
        Bundle mbundle = new Bundle();
        for(int i = 0;i < foods.size();i++) {
            foodnames.add(foods.get(i).getName());
        }
        mbundle.putStringArrayList(FOOD_NAME,foodnames);
        mfragement.setArguments(mbundle);
        return mfragement;
    }

    public List<String> getNames() {
        return names;
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder( ViewGroup parent, int positon) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder( MyViewHolder holder, int position) {
            holder.tv.setText(names.get(position));
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView)itemView.findViewById(R.id.name);
            }
        }
    }
}
