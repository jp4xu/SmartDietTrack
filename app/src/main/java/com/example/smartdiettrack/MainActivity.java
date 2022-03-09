package com.example.smartdiettrack;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.smartdiettrack.DB.FoodDB;
import com.example.smartdiettrack.DB.FoodData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.math.RoundingMode;
import java.math.BigDecimal;


public class MainActivity extends AppCompatActivity {

    String TAG = "MAIN";
    FloatingActionButton fabPlus;
    FloatingActionButton fabCamera;
    FloatingActionButton fabCalendar;
    FloatingActionButton fabText;
    FloatingActionButton fabBarcode;
    ProgressBar progress_bar;
    TextView progress_text;
    Boolean isMenuOpen = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private List<FoodData> foodList;

    private FoodDB foodDB;
    Toolbar mainToolbar;
    DatePickerDialog datePickerDialog;
    TextView dateText;
    TextView carbsText;
    TextView proteinText;
    TextView fatsText;
    RecyclerView foodRecyclerView;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
        result -> {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                BarcodeScanner barcodeScanner = new BarcodeScanner(this , result.getContents());
                barcodeScanner.nutrientQuery();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setSupportActionBar(mainToolbar);
        initDB();
        initFabMenu();
    }

    private void initDB() {
        foodDB = FoodDB.getInstance(this.getApplicationContext());
        String timestamp = new SimpleDateFormat(getString(R.string.DateFormat)).format(Calendar.getInstance().getTime());
        foodList = foodDB.foodDao().getEntitiesFromDate(timestamp);
        updateProgressBar(timestamp);
        updateMacros(timestamp);
    }

    private void updateMacros(String timestamp) {
        BigDecimal carbs = new BigDecimal(foodDB.foodDao().getCarbsFromDay(timestamp)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal protein = new BigDecimal(foodDB.foodDao().getProteinFromDay(timestamp)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fat = new BigDecimal(foodDB.foodDao().getFatsFromDay(timestamp)).setScale(2, RoundingMode.HALF_UP);
        carbsText.setText("Carbs: " + carbs + "g");
        proteinText.setText("Protein: " + protein + "g");
        fatsText.setText("Fats: " + fat + "g");
    }

    private void setupAdapter(RecyclerView recyclerView, RecycleAdapter recyclerAdapter) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void findViews() {
        carbsText = findViewById(R.id.carbText);
        proteinText = findViewById(R.id.proteinText);
        fatsText = findViewById(R.id.fatsText);
        fabPlus = findViewById(R.id.floatingPlusBtn);
        fabCamera = findViewById(R.id.floatingCameraBtn);
        fabCalendar = findViewById(R.id.floatingCalendarBtn);
        fabText = findViewById(R.id.floatingInputTxtBtn);
        fabBarcode = findViewById(R.id.floatingBarcodeBtn);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        progress_bar = findViewById(R.id.progress_bar);
        progress_text = findViewById(R.id.progress_text);
        mainToolbar = findViewById(R.id.main_toolbar);
        dateText = (TextView)findViewById(R.id.dateText);
    }
    private void initFabMenu(){
        fabCamera.setAlpha(0f);
        fabText.setAlpha(0f);
        fabCalendar.setAlpha(0f);
        fabBarcode.setAlpha(0f);

        fabCamera.setTranslationY(100f);
        fabText.setTranslationY(100f);
        fabCalendar.setTranslationY(100f);
        fabBarcode.setTranslationY(100f);

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMenuOpen) {
                    closeFabMenu();
                }
                else{
                    openFabMenu();
                }
                Log.d(TAG, "Main button opened");
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(intent);
            }
        });

        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InputTextActivity.class);
                startActivity(intent);
            }
        });

        fabBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        RecycleAdapter foodAdapter = new RecycleAdapter(foodList);
        setupAdapter(foodRecyclerView, foodAdapter);
        fabCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yr, int mth, int dy) {
                        if(year == yr && month == mth && day == dy) {
                            dateText.setText("Today");
                        }
                        else{
                            dateText.setText(new DateFormatSymbols().getMonths()[mth] + " " + dy + " " + yr);
                        }
                        String newTimestamp = changeSingleDigitFormat(dy) + "/" + changeSingleDigitFormat(mth+1) + "/" + yr;
                        foodAdapter.updateData(foodDB.foodDao().getEntitiesFromDate(newTimestamp));
                        updateProgressBar(newTimestamp);
                        updateMacros(newTimestamp);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                Log.d(TAG, "Calendar button hit");
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan a barcode");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private String changeSingleDigitFormat(int val) {
        String result = Integer.toString(val);
        //if we have only one digit need to add a zero
        if(result.length() == 1){
            result = "0" + result;
        }

        return result;
    }

    private void updateProgressBar(String timestamp) {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.Biometrics), MODE_PRIVATE);
        int calorieGoal = sharedPreferences.getInt(getString(R.string.CalorieGoal), 0);
        float currentCalories = foodDB.foodDao().getCaloriesFromDay(timestamp);
        BigDecimal bd = new BigDecimal(currentCalories).setScale(2, RoundingMode.HALF_UP);
        if (calorieGoal != 0) {
            progress_bar.setProgress((int)(currentCalories/calorieGoal * 100));
        }
        else{
            progress_bar.setProgress(0);
        }

        progress_text.setText(bd + "/\n" + calorieGoal);
    }

    private void openFabMenu() {
        isMenuOpen = !isMenuOpen;
        fabPlus.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabCamera.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabBarcode.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabText.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCalendar.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeFabMenu() {
        isMenuOpen = !isMenuOpen;
        fabPlus.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        fabCamera.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabBarcode.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabText.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabCalendar.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }

        return true;
    }
}