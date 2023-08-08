package com.app.ecolive.rider_module.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ecolive.R;
import com.app.ecolive.rider_module.model.VehicalCatgryListModel;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class CstmVehleCatgryAdapter extends ArrayAdapter<VehicalCatgryListModel.VehicleType> {
  
    public CstmVehleCatgryAdapter(Context context,
                                  ArrayList<VehicalCatgryListModel.VehicleType> algorithmList)
    {
        super(context, 0,  algorithmList);
    }
  
    @NonNull
    @Override
    public View getView(int position, @Nullable
                                      View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }
  
    @Override
    public View getDropDownView(int position, @Nullable
                                              View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }
  
    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spinvehicle_catgry, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.rowSpinTitle);
        TextView rowSpinSubTitle = convertView.findViewById(R.id.rowSpinSubTitle);
        VehicalCatgryListModel.VehicleType currentItem = getItem(position);
        String clr = currentItem.getVehicleTypeColor();
       // rowSpinClr.setImage
       // if(position>0) {
            // coz 0th pos was label
            rowSpinSubTitle.setTextColor(Color.parseColor(clr));
            ((GradientDrawable) rowSpinSubTitle.getBackground()).setColor(Color.parseColor(clr));
     //   }
        if (currentItem != null) {
            textViewName.setText(currentItem.getTitle());
        }
        return convertView;
    }
}