package com.example.appbirthday.ui.add;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.appbirthday.Database;
import com.example.appbirthday.R;
import com.example.appbirthday.databinding.FragmentAddBinding;;import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddFragment extends Fragment {

    //Declare variables
    private FragmentAddBinding binding;
    TextView tvDate, tvName;
    Button btnDate, btnAdd;
    EditText etName;
    Calendar calendario = Calendar.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Instantiate objects
        tvDate = root.findViewById(R.id.tvDate);
        btnDate = root.findViewById(R.id.btnDate);
        btnAdd = root.findViewById(R.id.btnAdd);
        tvName = root.findViewById(R.id.tvName);
        etName = root.findViewById(R.id.etName);

        //Show the date picker dialog
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Button to add a new record
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = Database.getInstance(getContext(), Database.DB_NAME, null, 1);
                long resultado = 0;

                String name = etName.getText().toString();
                String date = tvDate.getText().toString();


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateFormat = formatter.format(calendario.getTime());
                int daysRemaining = (int) daysRemaining(dateFormat);


                if (name.length() > 0 && date.length() > 0) {
                    if(db.checkNameLength(name)) {
                        resultado = db.addRecord(name, date, daysRemaining);
                    } else {
                        Toast.makeText(getContext(), R.string.error_name_size, Toast.LENGTH_SHORT).show();
                    }
                }

                if (resultado == -1) {
                    Toast.makeText(getContext(), "Registro no añadido", Toast.LENGTH_SHORT).show();
                } else if (resultado == 0){
                    Toast.makeText(getContext(), "Ha ocurrido un error. Faltan por rellenar uno o más campos.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Registro añadido", Toast.LENGTH_SHORT).show();
                }
                clearFields();

            }
            });
        return root;
    }
    //Listener for date picker dialog
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, monthOfYear);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarInput();
        }

    };
    //Method to format date and fill the date field.
    private void actualizarInput() {
        String formatoDeFecha = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.getDefault());

        tvDate.setText(sdf.format(calendario.getTime()));
    }
    //Method to clear the fields after add a record.
    private void clearFields(){
        etName.setText("");
        tvDate.setText("");
    }
    //Method to calculate the remaining days before the birthday
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long daysRemaining(String date){
        /*Fecha actual*/
        LocalDate today = LocalDate.now();

        /*Fecha de nacimiento. Ambas formas son válidas*/
        LocalDate birthday = LocalDate.parse(date);
        LocalDate nextBDay = birthday.withYear(today.getYear());

        /*Si el cumpleaños ya ocurrió este año, agrega 1 año*/
        if (nextBDay.isBefore(today)) {
            nextBDay = nextBDay.plusYears(1);
        }

        long days = ChronoUnit.DAYS.between(today, nextBDay);

        return days;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}