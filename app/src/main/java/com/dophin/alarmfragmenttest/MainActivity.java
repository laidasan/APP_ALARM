package com.dophin.alarmfragmenttest;

import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> names;
    private List<Food> foods;
    private List<Food> eats;
    private List<Food> drinks;

    DataBaseHelper dbHelper;
    Cursor cursor;

    //private List<ArrayList> menu = new ArrayList<>();

    private TabLayout mtablayout;
    private FmPagerAdapter mpageradapter;
    private ViewPager mviewpager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = {"吃的","鬧鐘"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //20181226測試Fragment裡面使用RecyclerView
        //setfoods();
        //getnames(foods);
        //Food_Fragement frag = Food_Fragement.newInstance(names);
        //getSupportFragmentManager().beginTransaction().add(R.id.main_layout,frag,"f1").commit();

        mtablayout = (TabLayout)findViewById(R.id.tablayout);
        mviewpager = (ViewPager)findViewById(R.id.viewpager);

        setfragements(fragments);
        mpageradapter = new FmPagerAdapter(fragments,getSupportFragmentManager());
        mviewpager.setAdapter(mpageradapter);
        mtablayout.setupWithViewPager(mviewpager,false); //20181228建立Tab標題要在這個方法之後，不燃此方法會清除tab
                                                   //20181228是不是會自動建立tab，在實做出來後結果會，但要看一下doc
        for( int i = 0; i <fragments.size();i++) {
            //mtablayout.addTab(mtablayout.newTab().setText(titles[i]) );
            mtablayout.getTabAt(i).setText(titles[i]); //20181228看一下這個方法戶會不會New出新的tab
            Log.v("tab","for loop");
        }
    }

    public void setfoods() {
        foods = new ArrayList<>();
        for(int i = 0;i < 3;i++) {
           foods.add(new Eat(Integer.toString(i)));
        }
    }
    public void setMenu() {
        eats = new ArrayList<>();
        drinks = new ArrayList<>();
        for (int i = 0; i < 26;i++) {
            eats.add(new Eat(Integer.toString(i)));
            drinks.add(new Drink(Integer.toString(i)));
        }
    }

    public void setfragements(ArrayList<Fragment> list) {
        //dbHelper = new DataBaseHelper(this);
        //cursor = dbHelper.selectAlarmColock();
        setMenu();
        list.add(Food_Fragement.newInstance(eats));
            list.add(Alarm_Fragment.newInstence(this));


        //dbHelper.close();
        //cursor.close();
    }


    public List<String> getnames(List<Food> foods) {
        names = new ArrayList<>();
        for(int i = 0;i < foods.size();i++)  {
            names.add(foods.get(i).getName());
        }
        return names;
    }
}
