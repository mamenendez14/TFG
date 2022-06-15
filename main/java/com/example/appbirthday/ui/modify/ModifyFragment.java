package com.example.appbirthday.ui.modify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appbirthday.Database;
import com.example.appbirthday.R;
import com.example.appbirthday.databinding.FragmentModifyBinding;

import java.text.ParseException;


public class ModifyFragment extends Fragment {

    private FragmentModifyBinding binding;

    //Declare variables
    EditText etId, etNameModified, etBirthdayModified;
    Button btnNext, btnBefore, btnModified, btnDelete;
    int id;
    String name, date;

    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModifyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Instantiate objects
        etId = root.findViewById(R.id.etId);
        etNameModified = root.findViewById(R.id.etNameModified);
        etBirthdayModified = root.findViewById(R.id.etBirthdayModified);
        btnNext = root.findViewById(R.id.btnNext);
        btnBefore = root.findViewById(R.id.btnBefore);
        btnModified = root.findViewById(R.id.btnModified);
        btnDelete = root.findViewById(R.id.btnDelete);


        //Instantiate database
        Database db = Database.getInstance(getContext(), Database.DB_NAME, null, 1);
        SQLiteDatabase select = db.getReadableDatabase();
        Cursor c = select.query("birthday", new String[]{"id", "name", "date"}, null, null, null, null, "id" );

        //Fill the fields
        if (c != null && !db.checkEmpty()) {
            c.moveToFirst();
            id = c.getInt(c.getColumnIndex("id"));
            name = c.getString(c.getColumnIndex("name"));
            date = c.getString(c.getColumnIndex("date"));
            printData();

            //Button to go to the next record
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextRecord(c);
                }
            });

            //Button to go to the previous record
            btnBefore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previousRecord(c);
                }
            });

            //Button to delete the current record
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        // Dialog construction
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.dialog_delete).setTitle(R.string.dialog_title)
                                .setPositiveButton(R.string.btnDelete, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int option) {
                                        db.deleteRecord(id);
                                        NavHostFragment.findNavController(ModifyFragment.this)
                                                .navigate(R.id.mobile_navigation);
                                        Toast.makeText(getContext(), R.string.dialog_delete_confirm, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the delete action.
                                    }
                                });
                        builder.create();
                        builder.show();
                }
            });
            //Button to modify the current record
            btnModified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = etNameModified.getText().toString();
                    date = etBirthdayModified.getText().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.dialog_modify).setTitle(R.string.dialog_title)
                            .setPositiveButton(R.string.btnModify, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int option) {
                                    if(db.checkNameLength(name)) {
                                        try {
                                            db.updateRecord(id, name, date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        NavHostFragment.findNavController(ModifyFragment.this)
                                                .navigate(R.id.mobile_navigation);
                                        Toast.makeText(getContext(), R.string.dialog_modify_confirm, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), R.string.error_name_size, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the modified.
                                }
                            });
                    builder.create();
                    builder.show();
                }
            });


        } else {
            //If there is no records, put this info into the fields and send a message if the buttons are pressed
            etId.setText("0");
            etNameModified.setFocusable(false);
            etNameModified.setText("Sin registros");
            etBirthdayModified.setFocusable(false);
            etBirthdayModified.setText("Sin registros");

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { noRecord();}
            });

            btnBefore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { noRecord();}
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noRecord();
                }
            });

            btnModified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noRecord();
                }
            });

        }
        return root;
    }
    //Method to go to the next record
    @SuppressLint("Range")
    private void nextRecord(Cursor c){
        if(!c.isLast()) {
            c.moveToNext();
            id = c.getInt(c.getColumnIndex("id"));
            name = c.getString(c.getColumnIndex("name"));
            date = c.getString(c.getColumnIndex("date"));
            printData();
        }
        else{
            c.moveToFirst();
            id = c.getInt(c.getColumnIndex("id"));
            name = c.getString(c.getColumnIndex("name"));
            date = c.getString(c.getColumnIndex("date"));
            printData();
        }
    }
    //Method to go to the previous record
    @SuppressLint("Range")
    private void previousRecord(Cursor c){
        if(!c.isFirst()) {

            c.moveToPrevious();
            id = c.getInt(c.getColumnIndex("id"));
            name = c.getString(c.getColumnIndex("name"));
            date = c.getString(c.getColumnIndex("date"));
            printData();
        }
        else {
            c.moveToLast();
            id = c.getInt(c.getColumnIndex("id"));
            name = c.getString(c.getColumnIndex("name"));
            date = c.getString(c.getColumnIndex("date"));
            printData();
        }
    }

    //Method to show a message
    private void noRecord(){
        Toast.makeText(getContext(), "No hay registros todavía", Toast.LENGTH_SHORT).show();
    }
    //Method to print the data in the fields
    private void printData(){
        etId.setText(String.valueOf(id));
        etNameModified.setText(name);
        etBirthdayModified.setText(date);
    }
}