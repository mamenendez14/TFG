package com.example.appbirthday.ui.home;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.appbirthday.CheckBirthday;
import com.example.appbirthday.Database;
import com.example.appbirthday.R;
import com.example.appbirthday.databinding.FragmentHomeBinding;

import java.text.ParseException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TextView tvNext, tvDays, tvFirst;
    String nextName;
    int nextDays;
    NotificationCompat.Builder notification;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvNext = root.findViewById(R.id.tvNext);
        tvDays = root.findViewById(R.id.tvDays);

        try {
            nextBirthday();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return root;
    }
    //Method to show the next birthday and how many days left.
    @SuppressLint("Range")
    private void nextBirthday() throws ParseException {
        Database db = Database.getInstance(getContext(), Database.DB_NAME, null, 1);
        SQLiteDatabase select = db.getReadableDatabase();

        db.updateRemainingDays();


        Cursor c = select.query("birthday", new String[]{"name", "daysRemaining"}, null, null, null, null, "daysRemaining" );

        if (c != null && !db.checkEmpty()) {
            c.moveToFirst();
            nextName = c.getString(c.getColumnIndex("name"));
            nextDays = c.getInt(c.getColumnIndex("daysRemaining"));

            if(nextName.length() > 11){
                tvNext.setTextSize(30);
            } else {
                tvNext.setTextSize(40);
            }

            if (nextDays == 0){
                tvNext.setText("Hoy es el cumpleaños de "+nextName);
                tvDays.setText("Felicítalo ya!");


            } else {
                tvNext.setText("El siguiente cumpleaños es de "+nextName);
                tvDays.setText("Faltán "+nextDays+" día(s)");
            }
        } else {
            tvNext.setText("Bievenido a AppBirthday =)");
            tvDays.setText("Inserta tu primer registro para comenzar!");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}