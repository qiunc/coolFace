package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.Student;
import com.example.myapplication.util.ImageTools;

import java.util.List;
/**
 * Created by Qiunc on 2020/4/5.
 * 自定义adapter用于显示GroupMember
 */
public class GroupMemberInformationAdapter extends BaseAdapter {
    private static final int SCALE = 6;
    private Context context;
    private List<Student> data;
    private LayoutInflater inflater;

    public GroupMemberInformationAdapter(Context context, List<Student> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.face_item,null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Student item =data.get(position);
        Bitmap bm = BitmapFactory.decodeFile(item.getImage());
        if (bm != null) {
            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            Bitmap smallBitmap = ImageTools.zoomBitmap(bm, bm.getWidth() / SCALE, bm.getHeight() / SCALE);
            //释放原始图片占用的内存，防止out of memory异常发生
            bm.recycle();
            holder.ivIcon.setImageBitmap(smallBitmap);
        }
        holder.tvName.setText(item.getName());
        holder.tvContent.setText(item.getStudentNumber());
        return convertView;
    }

     class ViewHolder{
        TextView tvName;
        TextView tvContent;
        ImageView ivIcon;
    }
}
