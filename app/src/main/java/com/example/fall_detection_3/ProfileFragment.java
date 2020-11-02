package com.example.fall_detection_3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment{

    private static String id;
    EditText editName, editEmail, editNumber;
    DatabaseReference datasaveinfo;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String Tname = "name";
    public static final String Tnumber = "number";
    public static final String Temail = "email";
    public static final String Tuserid = "email";
    private static String name, email, Number, userid = null;
    TextView AccountName, AccountEmail;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_profile_1, container, false);
        //   Toolbar toolbar=root.findViewById(R.id.toolbar);
        // toolbar.setTitle("Profile");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");

        editName = (EditText) root.findViewById(R.id.editName);
        editEmail = (EditText) root.findViewById(R.id.editEmail);
        editNumber = (EditText) root.findViewById(R.id.editNumber);
        TextView save2 = (TextView) root.findViewById(R.id.save);
        loadData();
        updatView();
        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveinfo();
            }
        });
        return root;
    }

    private void saveinfo() {
        name = editName.getText().toString().trim();
        email = editEmail.getText().toString().trim();
        Number = editNumber.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name can't be empty..");
        } else if (email.isEmpty()) {
            editEmail.setError("Email can't be empty..");
        } else if (Number.isEmpty()) {
            editNumber.setError("Number can't be empty..");
        } else {

            saveData();
loadData();
            datasaveinfo = FirebaseDatabase.getInstance().getReference("info");
            // String id=  datasaveinfo.push().getKey();
            //id = Number.concat(name);
            id=Number;
           // if(id.isEmpty()){Toast.makeText(getContext(),"empty..",Toast.LENGTH_SHORT).show();}
            Bundle bundle = new Bundle();


            userinfo user = new userinfo(name, email, Number);
            datasaveinfo.child(id).setValue(user);
            Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveData() {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Tname, name);
        editor.putString(Tnumber, Number);
        editor.putString(Temail, email);
        editor.apply();

    }
    public void Erase_data(Context c)
    {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Tname, "");
        editor.putString(Tnumber, "");
        editor.putString(Temail, "");
        editor.apply();

    }

    public void loadData() {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences =getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        name = sharedPreferences.getString(Tname, "");
        Number = sharedPreferences.getString(Tnumber, "");
        email = sharedPreferences.getString(Temail, "");
    }

    public void updatView() {
        editName.setText(name);
        editNumber.setText(Number);
        editEmail.setText(email);
    }

    public String getUpdateId(Context c) {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences =c.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        //name = sharedPreferences.getString(Tname, "");
        Number = sharedPreferences.getString(Tnumber, "");
        return Number;
    }
    public Context getcontext()
    {
        return getContext();
    }

}
