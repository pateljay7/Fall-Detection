package com.example.fall_detection_3;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


public class EmergencyContactAdapter extends ArrayAdapter<EmergencyContact> {

    private static final String TAG = "PersonListAdapter";
    List<EmergencyContact> employeeList;
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;



    private static class ViewHolder {
        TextView Ename, Eemail, Econtact;

    }


    public EmergencyContactAdapter(Context context, int resource, ArrayList<EmergencyContact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String Ename = getItem(position).getEname();
        String Eemail = getItem(position).getEemail();
        String Econtact = getItem(position).getEcontact();
     //   Button deleteContact=convertView.findViewById(R.id.delete_contact);
       // employeeList=convertView.findViewById(R.id.listview);
        //final EmergencyContact employee = employeeList.get(position);
        //Create the person object with the information
        EmergencyContact person = new EmergencyContact(Ename, Eemail, Econtact);


        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

       /* deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM employees WHERE id = ?";


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });*/

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Ename = (TextView) convertView.findViewById(R.id.Emergencyname);
            holder.Eemail = (TextView) convertView.findViewById(R.id.Emergencyemail);
            holder.Econtact = (TextView) convertView.findViewById(R.id.Emergencycontact);


            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        holder.Ename.setText(person.getEname());
        holder.Eemail.setText(person.getEemail());
        holder.Econtact.setText(person.getEcontact());


        return convertView;
    }


}

























