package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.StudentAttendance;

import java.util.List;

public class TableAdapter extends BaseAdapter {
    private List<StudentAttendance> list;
    private LayoutInflater inflater;

    public TableAdapter(Context context, List<StudentAttendance> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list!=null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StudentAttendance studentAttendance = (StudentAttendance) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.list_item, null);
          //  viewHolder.attendanceDate = (TextView) convertView.findViewById(R.id.text_id);
            viewHolder.attendanceClass = (TextView) convertView.findViewById(R.id.text_goods_name);
            viewHolder.attendanceStudentNumber = (TextView) convertView.findViewById(R.id.text_codeBar);
            viewHolder.attendanceStudentName = (TextView) convertView.findViewById(R.id.text_num);
            viewHolder.isAttendance = (TextView) convertView.findViewById(R.id.text_curPrice);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

       // viewHolder.attendanceDate.setText(studentAttendance.getDate());
        //viewHolder.attendanceDate.setTextSize(13);
        viewHolder.attendanceClass.setText(studentAttendance.getTheClassName());
        viewHolder.attendanceClass.setTextSize(13);
        viewHolder.attendanceStudentNumber.setText(studentAttendance.getStudentNumber());
        viewHolder.attendanceStudentNumber.setTextSize(13);
        viewHolder.attendanceStudentName.setText(studentAttendance.getStudentName());
        viewHolder.attendanceStudentName.setTextSize(13);
        if (studentAttendance.getAttendance() == null || "".equals(studentAttendance.getAttendance())) {
            viewHolder.isAttendance.setText("缺勤");
            viewHolder.isAttendance.setTextSize(13);
        }else {
            viewHolder.isAttendance.setText(studentAttendance.getAttendance());
            viewHolder.isAttendance.setTextSize(13);
        }
        return convertView;
    }

    public static class ViewHolder{
        //public TextView attendanceDate;
        public TextView attendanceClass;
        public TextView attendanceStudentNumber;
        public TextView attendanceStudentName;
        public TextView isAttendance;
    }
}
