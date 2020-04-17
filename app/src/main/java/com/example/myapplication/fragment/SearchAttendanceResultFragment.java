package com.example.myapplication.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Adapter.DropDownMenu;
import com.example.myapplication.Adapter.Madapter;
import com.example.myapplication.Adapter.SearchAdapter;
import com.example.myapplication.Adapter.TableAdapter;
import com.example.myapplication.R;
import com.example.myapplication.db.Class;
import com.example.myapplication.db.DicBean;
import com.example.myapplication.db.StudentAttendance;
import com.example.myapplication.db.Subjects;
import com.example.myapplication.util.ScreenUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SearchAttendanceResultFragment extends Fragment implements View.OnClickListener{

    private View dissmiss;
    private TextView tv_search;
    private EditText edit_query;
    private PopupWindow popupWindow;
    private View zhezhao;   //底下半透明背景，实现矩形进入效果
    private LinearLayout date;
    private LinearLayout subject;
    private LinearLayout theClassName;
    private LinearLayout sex;
    private TextView date_text;
    private TextView subject_text;
    private TextView class_text;
    private TextView sex_text;
    List<Map<String, String>> sexResult;
    List<Map<String, String>> nationResult;
    List<Map<String, String>> countryResult;
    List<Map<String, String>> cultureResult;
//    private ActionBar mActionBar;
//    private Toolbar mToolbar;
    private SearchAdapter dateAdapter;
    private SearchAdapter subjectAdapter;
    private SearchAdapter classAdapter;
    private SearchAdapter sexAdapter;
    private DropDownMenu dropDownMenu;
    private LinearLayout layout;
    private View listItem;
    private View listView;
    private View view;

    private List<StudentAttendance> alist ;
    private ListView tableListView;
    private ViewGroup tableTitle;
    private FrameLayout frameLayout;



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
        date = view.findViewById(R.id.ll_date);
        subject = view.findViewById(R.id.ll_subject);
        theClassName = view.findViewById(R.id.ll_class);
        sex = view.findViewById(R.id.ll_sex);
        date_text = view.findViewById(R.id.tv_date_text);
        subject_text = view.findViewById(R.id.tv_subject_text);
        class_text = view.findViewById(R.id.tv_class_text);
        sex_text = view.findViewById(R.id.tv_sex_text);
        layout = (LinearLayout) getLayoutInflater().inflate(R.layout.pup_selectlist, null, false);

        tableTitle = (ViewGroup) view.findViewById(R.id.table_title);
        tableListView = (ListView) view.findViewById(R.id.list);
        frameLayout = view.findViewById(R.id.content_frame);
        //setupToolbar();

        date.setOnClickListener(this);
        subject.setOnClickListener(this);
        theClassName.setOnClickListener(this);
        sex.setOnClickListener(this);
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
//        dateAdapter = new SearchAdapter(getActivity());  //真实项目里，适配器初始化一定要写在这儿 不然如果new出来的设配器里面没有值，会报空指针
//
//        List<Dic> sexResult = new ArrayList<>();
//        sexResult.add(new Dic("1","全部"));
//        sexResult.add(new Dic("1","男"));
//        sexResult.add(new Dic("1","女"));
//
//        dateAdapter.setItems(sexResult);



        subjectAdapter = new SearchAdapter(getActivity());
        List<DicBean> subjectResult = new ArrayList<>();
        List<Subjects> subjectsList = LitePal.findAll(Subjects.class);
        for (Subjects subjects : subjectsList) {
            subjectResult.add(new DicBean("2",subjects.getSubjectName()));
        }
        subjectAdapter.setItems(subjectResult);



        classAdapter = new SearchAdapter(getActivity());
        List<DicBean> classResult = new ArrayList<>();
        classResult.add(new DicBean("000","全部"));
        List<Class> classList = LitePal.findAll(Class.class);
        for (Class classes : classList) {
            classResult.add(new DicBean("001",classes.getTheClassName()));
        }
        classAdapter.setItems(classResult);



        sexAdapter = new SearchAdapter(getActivity());
        List<DicBean> cultureResult = new ArrayList<>();
        cultureResult.add(new DicBean("000","全部"));
        cultureResult.add(new DicBean("001","男"));
        cultureResult.add(new DicBean("002","女"));
        sexAdapter.setItems(cultureResult);

        listItem = getLayoutInflater().inflate(R.layout.item_listview, null, false);
        listView = getLayoutInflater().inflate(R.layout.pup_selectlist, null, false);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_date:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                final String data = (month + 1) + "月-" + dayOfMonth + "日 ";
                                date_text.setText((String) (year + "年" + (month + 1) + "月" + dayOfMonth + "日"));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                break;
            case R.id.ll_subject:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), subjectAdapter,
                        listView, listItem,subject,subject_text,"cyry.mzdm",true);
                break;
            case R.id.ll_class:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), classAdapter,
                        listView, listItem,theClassName,class_text,"cyry.gjdm",true);

                break;
            case R.id.ll_sex:
                dropDownMenu.showSelectList(ScreenUtils.getScreenWidth(getActivity()),
                        ScreenUtils.getScreenHeight(getActivity()), sexAdapter,
                        listView, listItem,sex,sex_text,"cyry.whcd",true);
                break;
            case R.id.search:
                getSearchAttendanceResult();
                break;
            default:
                break;
        }
    }

    private void getSearchAttendanceResult() {
        Log.d("qnc", "getSearchAttendanceResult: ");
        String searchDate = date_text.getText().toString();
        String searchSubject = subject_text.getText().toString();
        String searchClass = class_text.getText().toString();
        String sex = sex_text.getText().toString();
        if ("班级".equals(searchClass) || "性别".equals(sex)) {
            frameLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(),"请选择搜索关键字",Toast.LENGTH_SHORT).show();
        }
        else if ("日期".equals(searchDate)||"学科".equals(searchSubject)){
            frameLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(),"请选择日期和学科",Toast.LENGTH_SHORT).show();
        }

        else if ("全部".equals(searchClass) && "全部".equals(sex)){
            alist =  LitePal.where("date = ? and subject = ? ",searchDate, searchSubject).find(StudentAttendance.class);
            if (alist.size() > 0) {
                frameLayout.setVisibility(View.VISIBLE);
                TableAdapter adapter = new TableAdapter(getActivity(), alist);
                tableListView.setAdapter(adapter);
            }
            else {
                frameLayout.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"无搜索结果",Toast.LENGTH_SHORT).show();
            }
        }
        else if ("全部".equals(sex)){
            alist =  LitePal.where("date = ? and subject = ? and theClassName = ?",searchDate, searchSubject, searchClass)
                    .find(StudentAttendance.class);
            if (alist.size() > 0) {
                frameLayout.setVisibility(View.VISIBLE);
                TableAdapter adapter = new TableAdapter(getActivity(), alist);
                tableListView.setAdapter(adapter);
            }
            else {
                frameLayout.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"无搜索结果",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
