package com.example.fall_detection_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.content.ContentValues.TAG;

public class ContactFragment extends Fragment {
    FloatingActionButton addConatct;
    private Boolean isFabOpen = false;
    Button delete;
    private FloatingActionButton fab, fab1, fab2;
    String contactNum = "";
    String email = "", Name = "";
    ListView lv;
    int pos;
    ListView mListView;
    DatabaseReference datasaveinfo, Dname, Dcontact, Demail, datauser, Deme;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    final ArrayList<EmergencyContact> peopleList = new ArrayList<>();

    public static final String SHARED_PREFS = "sharedPrefs";
    public int Tid ;
    int ConCount = 0;
    TextView numCount;
    String userid;
    String Ename, Econtact, Eemail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final EmergencyContactAdapter[] adapter = new EmergencyContactAdapter[1];//= new EmergencyContactAdapter(getContext(), R.layout.adapter_view_layout, peopleList);

        final View root = inflater.inflate(R.layout.fragment_contact, container, false);
        final View root1 = inflater.inflate(R.layout.adapter_view_layout, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Emergency Contacts");
        mListView = (ListView) root.findViewById(R.id.listview);
        ProfileFragment p = new ProfileFragment();
        final String userid = p.getUpdateId(getContext());
        datasaveinfo = FirebaseDatabase.getInstance().getReference("info");
        datauser = datasaveinfo.child(userid);
        Dcontact = datasaveinfo.child(userid).child("Contact");
        final String child3 = String.valueOf(ConCount);
       // delete=root.findViewById(R.id.delete);
       // final ConstraintLayout delete_coon=root.findViewById(R.id.delete_con);

        datauser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Contact")) {
                    Dcontact = datasaveinfo.child(userid).child("Contact");
                    Dcontact.addListenerForSingleValueEvent(new ValueEventListener() {
                        int j = Integer.parseInt(numCount.getText().toString());
                        int i;

                        public void onDataChange(DataSnapshot dataSnapshot) {
                            loadData();
                            updatView();
                            for (i = 1; i <=j; i++) {
                                if (dataSnapshot.hasChild(String.valueOf(i))) {
                                    Deme = datasaveinfo.child(userid).child("Contact").child(String.valueOf(i));
                                    Deme.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            EmergencyContact e = dataSnapshot.getValue(EmergencyContact.class);
                                            peopleList.add(e);
                                            adapter[0] = new EmergencyContactAdapter(getContext(), R.layout.adapter_view_layout, peopleList);
                                            mListView.setAdapter(adapter[0]);
                                            numCount.setText(String.valueOf(i-1));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Dcontact = datasaveinfo.child(userid).child("Contact");
                    numCount.setText("0");
                    ConCount=0;
                    saveData();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               final int which_item=i;
               final int pos=which_item+1;
               final EmergencyContact e= peopleList.get(which_item);

               new androidx.appcompat.app.AlertDialog.Builder(getContext())
                       .setIcon(android.R.drawable.ic_delete)
                       .setTitle("Are you sure")
                       .setMessage("Do you want to delete")
                       .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               peopleList.remove(which_item);
                               //adapter[0] = new EmergencyContactAdapter(getContext(), R.layout.adapter_view_layout, peopleList);

                               adapter[0].notifyDataSetChanged();

                               datasaveinfo = FirebaseDatabase.getInstance().getReference("info");
                               String child2 = "Contact";
                               String child3 = String.valueOf(pos);
                          //     datasaveinfo.child(userid).child(child2).child(child3);
                               Toast.makeText(getContext(),"deleted"+e.getEcontact(),Toast.LENGTH_SHORT).show();
                               ConCount--;
                               numCount.setText(String.valueOf(ConCount));




                               Dcontact = datasaveinfo.child(userid).child("Contact").child(child3);
                               Dcontact.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {

                                           appleSnapshot.getRef().removeValue();
                                       }
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {
                                       Log.e(TAG, "onCancelled", databaseError.toException());
                                   }
                               });
                           }
                       })
                       .setNegativeButton("no",null).show();
               return true;
           }
       });

        numCount = root.findViewById(R.id.numCount);

        //EditText editNumber=(EditText) root.findViewById(R.id.editNumber);
        //final String Number =editNumber.getText().toString().trim();
        //EditText editName=(EditText) root.findViewById(R.id.editName);
        //final String name =editName.getText().toString().trim();

        lv = root.findViewById(R.id.listview);
        registerForContextMenu(lv);

        addConatct = root.findViewById(R.id.addContact);

        fab = (FloatingActionButton) root.findViewById(R.id.addContact);
        fab1 = (FloatingActionButton) root.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) root.findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backword);

        loadData();
        updatView();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Contact");

                final TextView t1 = new TextView(getContext());
                t1.setText("Name");
                t1.setPadding(5,0,0,0);
                layout.addView(t1);
                final EditText contactname = new EditText(getContext());
               // contactname.setBackgroundColor(Color.WHITE);
                layout.addView(contactname);

                final TextView t2 = new TextView(getContext());
                t2.setText("Contact");
                t2.setPadding(5,0,0,0);
                layout.addView(t2);
                final EditText contact = new EditText(getContext());
                contact.setInputType(InputType.TYPE_CLASS_PHONE);
                layout.addView(contact);

                final TextView t3 = new TextView(getContext());
                t3.setText("E-mail");
                t3.setPadding(5,0,0,0);
                layout.addView(t3);
                t1.setTextSize(18);
                t2.setTextSize(18);
                t3.setTextSize(18);
                t1.setPadding(10, 5, 5, 5);
                t2.setPadding(10, 5, 5, 5);
                t3.setPadding(10, 5, 5, 5);
                final EditText contactemail = new EditText(getContext());
                contactemail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                layout.addView(contactemail);
                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contactNum = contact.getText().toString();
                        email = contactemail.getText().toString();
                        Name = contactname.getText().toString();
                        EmergencyContact person = new EmergencyContact(Name, email, contactNum);
                        adapter[0] = new EmergencyContactAdapter(getContext(), R.layout.adapter_view_layout, peopleList);
                        peopleList.add(person);
                        mListView.setAdapter(adapter[0]);
                        adapter[0].notifyDataSetChanged();

                        ConCount++;
                        numCount.setText(String.valueOf(ConCount));
                        saveData();

                        //===============================================================

                        datasaveinfo = FirebaseDatabase.getInstance().getReference("info");
                        String child2 = "Contact";
                        String child3 = String.valueOf(ConCount);
                        datasaveinfo.child(userid).child(child2).child(child3).setValue(person);


                        //===============================================================

                        animateFAB();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        animateFAB();
                    }
                });
                builder.show();
            }
        });


        return root;
    }


 /*   @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.exmaplemenu, menu);
    }*/

   /* @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.edit:
                Toast.makeText(getContext(), pos+"edit successfull", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove:

                Toast.makeText(getContext(), "remove successfull", Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onContextItemSelected(item);
    }*/



    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            //fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            //fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
           // fab2.startAnimation(fab_open);
            fab1.setClickable(true);
           // fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(Tid), ConCount);
        editor.apply();

    }

    public void loadData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        ConCount = sharedPreferences.getInt(String.valueOf(Tid), 0);

    }

    public void updatView() {

        numCount.setText(String.valueOf(ConCount));

    }
}
