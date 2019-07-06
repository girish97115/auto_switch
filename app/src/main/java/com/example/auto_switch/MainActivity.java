package com.example.auto_switch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {


    EditText text;
    ImageView qrimage;
    Button button;
    Button addtodb;
    String inputValue;
    Bitmap bitmap;
    DatabaseReference mDatabase;
    Button update;
    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";
    private TextView textView;

    private SwitchDateTimeDialogFragment dateTimeFragment1, dateTimeFragment2;
    TextView start_time;
    TextView end_time;
    Button start_time_pick, end_time_pick;
    long epoch_start, epoch_end;


    private void writeNewPost(String start_time, String end_time, Boolean status) {
        post Box = new post();
        Box.name = text.getText().toString();
        Box.start_time = start_time;
        Box.end_time = end_time;
        Box.status = status;
        String StudentRecordIDFromServer = mDatabase.push().getKey();
        mDatabase.child(StudentRecordIDFromServer).setValue(Box.toMap());
    }



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        button = findViewById(R.id.submit);
        text = findViewById(R.id.info);
        qrimage = findViewById(R.id.qr_image);
        addtodb = findViewById(R.id.addtodb);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        start_time_pick = findViewById(R.id.start_time_picker);
        end_time_pick = findViewById(R.id.end_time_picker);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        update = findViewById(R.id.Update);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = text.getText().toString().trim();
                if (inputValue.length() > 0) {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                     QRGEncoder qrgEncoder = new QRGEncoder(
                            inputValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrimage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v("encoder", e.toString());
                    }
                } else {
                    text.setError("Required");
                }
            }
        });

         addtodb.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (qrimage != null){

                     writeNewPost("null", "null", Boolean.FALSE);
                     Toast.makeText(MainActivity.this,"Data Stored Successfully",Toast.LENGTH_LONG).show();

                 }
                 else
                     Toast.makeText(MainActivity.this,"QR not created",Toast.LENGTH_LONG).show();
             }
         });


        dateTimeFragment1 = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment1 == null) {
            dateTimeFragment1 = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)

            );
        }
        dateTimeFragment1.setTimeZone(TimeZone.getDefault());
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment1.set24HoursMode(false);
        dateTimeFragment1.setHighlightAMPMSelection(false);
        dateTimeFragment1.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment1.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment1.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment1.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                start_time.setText(myDateFormat.format(date));
                DateTime dateTime = new DateTime(myDateFormat.format(date), DateTimeZone.UTC);
                long atime = dateTime.getMillis();
                long secondsSinceUnix = (dateTime.getMillis()/1000);
                epoch_start = (long) (secondsSinceUnix - (5.5*3600));
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                textView.setText("");
            }
        });
        start_time_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Re-init each time
                dateTimeFragment1.startAtCalendarView();
                dateTimeFragment1.setDefaultDateTime(new GregorianCalendar(2019, Calendar.JULY, 4, 15, 20).getTime());
                dateTimeFragment1.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });

        dateTimeFragment2 = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment2 == null) {
            dateTimeFragment2 = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)

            );
        }
        dateTimeFragment2.setTimeZone(TimeZone.getDefault());

        dateTimeFragment2.set24HoursMode(false);
        dateTimeFragment2.setHighlightAMPMSelection(false);
        dateTimeFragment2.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment2.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment2.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment2.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                end_time.setText(myDateFormat.format(date));
                DateTime dateTime = new DateTime(myDateFormat.format(date), DateTimeZone.UTC);
                long atime = dateTime.getMillis();
                long secondsSinceUnix = (dateTime.getMillis()/1000);
                epoch_end = (long) (secondsSinceUnix - (5.5*3600));
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                textView.setText("");
            }
        });
        end_time_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Re-init each time
                dateTimeFragment2.startAtCalendarView();
                dateTimeFragment2.setDefaultDateTime(new GregorianCalendar(2019, Calendar.JULY, 4, 15, 20).getTime());
                dateTimeFragment2.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("balaji").child("start_time").setValue(epoch_start);
                mDatabase.child("balaji").child("end_time").setValue(epoch_end);
                Toast.makeText(MainActivity.this, "Database Updated", Toast.LENGTH_LONG).show();


            }
        });

            }
        }


