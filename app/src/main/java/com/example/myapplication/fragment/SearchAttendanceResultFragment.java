package com.example.myapplication.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Adapter.DropDownMenu;
import com.example.myapplication.Adapter.Madapter;
import com.example.myapplication.Adapter.SearchAdapter;
import com.example.myapplication.Adapter.TableAdapter;
import com.example.myapplication.R;
import com.example.myapplication.db.Dic;
import com.example.myapplication.db.StudentAttendance;
import com.example.myapplication.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchAttendanceResultFragment extends Fragment implements View.OnClickListener{

    private View dissmiss;
    private TextView tv_search;
    private EditText edit_query;
    private PopupWindow popupWindow;
    private View zhezhao;   //底下半透明背景，实现矩形进入效果
    private LinearLayout sex;
    private LinearLayout nation;
    private LinearLayout country;
    private LinearLayout culture;
    private TextView sex_text;
    private TextView nation_text;
    private TextView country_text;
    private TextView culture_text;
    List<Map<String, String>> sexResult;
    List<Map<String, String>> nationResult;
    List<Map<String, String>> countryResult;
    List<Map<String, String>> cultureResult;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private SearchAdapter sexAdapter;
    private SearchAdapter countryAdapter;
    private SearchAdapter nationAdapter;
    private SearchAdapter cultureAdapter;
    private DropDownMenu dropDownMenu;
    private LinearLayout layout;
    private View listItem;
    private View listView;
    private View view;

    List<StudentAttendance> alist ;
    ListView tableListView;
    ViewGroup tableTitle;
    FrameLayout frameLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendance_search, container, false);
       initViews();
        initData();
        return view;
    }

    private void initViews(){
        edit_query = view.findViewById(R.id.edit_query);
        tv_search = view.findViewById(R.id.search);
        sex = view.findViewById(R.id.sex);
        nation = view.findViewById(R.id.nation);
        country = view.findViewById(R.id.country);
        culture = view.findViewById(R.id.culture);
        sex_text = view.findViewById(R.id.sex_text);
        nation_text = view.findViewById(R.id.nation_text);
        country_text = view.findViewById(R.id.country_text);
        culture_text = view.findViewById(R.id.culture_text);
        layout = (LinearLayout) getLayoutInflater().inflate(R.layout.pup_selectlist, null, false);

        tableTitle = (ViewGroup) view.findViewById(R.id.table_title);
        tableListView = (ListView) view.findViewById(R.id.list);
        frameLayout = view.findViewById(R.id.content_frame);
        //setupToolbar();

        sex.setOnClickListener(this);
        nation.setOnClickListener(this);
        country.setOnClickListener(this);
        culture.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        dropDownMenu = DropDownMenu.getInstance(getActivity(), new DropDownMenu.OnListCkickListence() {
            @Override
            public void search(String code, String type) {
                System.out.println("======"+code+"========="+type);
            }

            @Override
            public void changeSelectPanel(Madapter madapter, View view) {

            }
        });
        dropDownMenu.setIndexColor(R.color.colorAccent);
        dropDownMenu.setShowShadow(true);
        dropDownMenu.setShowName("name");
        dropDownMenu.setSelectName("code");
    }

//    protected void setupToolbar() {
//        mToolbar =   view.findViewById(R.id.toolbar);
//        if (mToolbar != null) {
//            getActivity().setActionBar(mToolbar);
//            mActionBar = getSupportActionBar();
//            mActionBar.setDisplayHomeAsUpEnabled(true);
//            mActionBar.setDisplayShowHomeEnabled(true);
//            mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
//                public void onClick(View view){
////                    finish();
//                }
//            });
//        }
//
//    }

    private void initData(){
        sexAdapter = new SearchAdapter(getActivity());  //真实项目里，适配器初始化一定要写在这儿 不然如果new出来的设配器里面没有值，会报空指针

        List<Dic> sexResult = new ArrayList<>();
        sexResult.add(new Dic("1","全部"));
        sexResult.add(new Dic("1","男"));
        sexResult.add(new Dic("1","女"));

        sexAdapter.setItems(sexResult);



        nationAdapter = new SearchAdapter(getActivity());
        List<Dic> nationResult = new ArrayList<>();
        nationResult.add(new Dic("1","全部"));
        nationResult.add(new Dic("2","汉族"));
        nationResult.add(new Dic("3","回族"));
        nationResult.add(new Dic("4","满族"));
        nationResult.add(new Dic("5","布依族"));
        nationResult.add(new Dic("6","保安族"));
        nationResult.add(new Dic("7","保安族"));
        nationResult.add(new Dic("8","保安族"));
        nationResult.add(new Dic("9","保安族"));

        nationAdapter.setItems(nationResult);

        countryAdapter = new SearchAdapter(getActivity());
        List<Dic> countryResult = new ArrayList<>();
        countryResult.add(new Dic("000","全部"));
        countryResult.add(new Dic("001","中国"));
        countryResult.add(new Dic("002","法国"));
        countryResult.add(new Dic("003","俄罗斯"));
        countryResult.add(new Dic("004","越南"));
        countryResult.add(new Dic("005","老挝"));
        countryResult.add(new Dic("006","缅甸"));

        countryAdapter.setItems(countryResult);

        cultureAdapter = new SearchAdapter(getActivity());
        List<Dic> cultureResult = new ArrayList<>();
        cultureResult.add(new Dic("000","全部"));
        cultureResult.add(new Dic("001","小学"));
        cultureResult.add(new Dic("002","初中"));
        cultureResult.add(new Dic("003","高中"));
        cultureResult.add(new Dic("004","中专"));
        cultureResult.add(new Dic("005","大专"));
        cultureResult.add(new Dic("006","本科"));

        cultureAdapter.setItems(cultureResult);

        listItem = getLayoutInflater().inflate(R.layout.item_listview, null, false);
        listView = getLayoutInflater().inflate(R.layout.pup_selectlist, null, false);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sex:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), sexAdapter,
                        listView, listItem,sex, sex_text, "cyry.xbdm", false);

                break;
            case R.id.nation:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), nationAdapter,
                        listView, listItem,nation,nation_text,"cyry.mzdm",true);
                break;
            case R.id.country:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), countryAdapter,
                        listView, listItem,country,country_text,"cyry.gjdm",true);

                break;
            case R.id.culture:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), cultureAdapter,
                        listView, listItem,culture,culture_text,"cyry.whcd",true);
                break;
            case R.id.search:
                getSearchAttendanceResult();
                break;
            default:
                break;
        }
    }

    private void getSearchAttendanceResult() {
        frameLayout.setVisibility(View.VISIBLE);
        StudentAttendance studentAttendance = new StudentAttendance();
        studentAttendance.setDate("4月11日");
        studentAttendance.setStudentName("邱宁聪");
        studentAttendance.setStudentNumber("B20160404214");
        studentAttendance.setTheClassName("16光电2班");
        studentAttendance.setAttendance("出勤");
        studentAttendance.setSubject("高等数学");
        alist = new ArrayList<StudentAttendance>();
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        alist.add(studentAttendance);
        TableAdapter adapter = new TableAdapter(getActivity(), alist);
        tableListView.setAdapter(adapter);
    }

}
