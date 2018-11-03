package de.thm.ap.leistungen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import de.thm.ap.leistungen.data.AppDatabase;
import de.thm.ap.leistungen.data.RecordFileDAO;
import de.thm.ap.leistungen.model.Record;

public class RecordFormActivity extends AppCompatActivity{

    private EditText moduleNum, creditPoints, markProzent;
    private AutoCompleteTextView moduleName;
    private CheckBox weight, summerTerm;
    private Spinner year;

    private Integer record_ex = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_form);
        // Show up button in action bar
        Optional.ofNullable(getSupportActionBar())
                .ifPresent(
                        actionBar -> actionBar.setDisplayHomeAsUpEnabled(true)
                );

        //getViews
        moduleNum = findViewById(R.id.module_num);
        creditPoints = findViewById(R.id.credit_points);
        markProzent = findViewById(R.id.mark);
        weight = findViewById(R.id.gewicht);
        summerTerm = findViewById(R.id.summer);
        weight = findViewById(R.id.gewicht);
        year = findViewById(R.id.years);
        moduleName = findViewById(R.id.module_name);

        // configure suggestions in auto complete text view
        String[] names =
                getResources().getStringArray(R.array.module_names);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, names);
        moduleName.setAdapter(adapter);
        // configure year spinner
        ArrayAdapter<Integer> adapter2 =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, getYears());
        year.setAdapter(adapter2);
        Intent intent = getIntent();
        record_ex = intent.getIntExtra("selected_record",-1);
        if(record_ex > 0) {
            Context ctx = this;
            Executors.newSingleThreadExecutor()
                    .submit(() -> {
                        List<Record> recordsFilled = AppDatabase.getDb(ctx).recordDAO().findById(record_ex);
                        for (Record r : recordsFilled) setFields(r);
                    });
        }
    }

    public void onSave(View view) {
        Record record = new Record();
        Boolean noNumber = false;
        // validate user input
        boolean isValid = true;
        // ModulName validieren---------------------------------------------------------------------
        record.setModuleName(moduleName.getText().toString().trim());
        if ("".equals(record.getModuleName())) {
            moduleName.setError(getString(R.string.module_name_not_empty));
            isValid = false;
        }
        //ModulNummer validieren--------------------------------------------------------------------
        record.setModuleNum(moduleNum.getText().toString());
        if("".equals(record.getModuleNum())){
            moduleNum.setError(getString(R.string.module_num_not_empty));
            isValid = false;
        }
        // Credit points validieren-----------------------------------------------------------------
        Integer crp = null;
        if(!"".equals(creditPoints.getText().toString())){
            try {
                crp = Integer.parseInt(creditPoints.getText().toString());
            } catch (NumberFormatException e) {
                noNumber = true;
            }
        }
        record.setCrp(crp);
        if(record.getCrp() == null){
            if(noNumber) creditPoints.setError(getString(R.string.credit_points_not_number));
            else creditPoints.setError(getString(R.string.credit_points_not_empty));
            isValid = false;
            noNumber = false;
        } else if(record.getCrp() < 0 ){
            creditPoints.setError(getString(R.string.credit_points_not_valid));
            isValid = false;
        } else if(record.getCrp() > 15) {
            creditPoints.setError(getString(R.string.credit_points_to_big));
            isValid = false;
        }
        // Note validieren--------------------------------------------------------------------------
        Integer mark = null;
        try {
            mark = Integer.parseInt(markProzent.getText().toString());
        } catch (NumberFormatException e) {
            noNumber = true;
        }
        record.setMark(mark);
        if(record.getMark() == null){
            record.setHasMark(false);
        } else if(record.getMark() < 50){
            //TODO set wenn bestanden bei änderung wichtig
            record.setHasPassed(false);
        } else if(record.getMark() > 100) {
            markProzent.setError(getString(R.string.mark_not_valid));
            isValid = false;
        }
        // Checken, ob es ein Fehler bei der Eingabe gab--------------------------------------------
        if (isValid) {
            record.setHalfWeighted(weight.isChecked());
            record.setSummerTerm(summerTerm.isChecked());
            int yearInt = 0;
            try {
                yearInt = Integer.parseInt(year.getSelectedItem().toString().replace("Sommersemester ", ""));
            } catch (NumberFormatException e){
                // Exception
            }
            record.setYear(yearInt);
            // persist record and finish activity
            if(record_ex > 0){
                record.setId(record_ex);
                Executors.newSingleThreadExecutor()
                        .submit(() -> AppDatabase.getDb(this).recordDAO().update(record));
            } else {
                Executors.newSingleThreadExecutor()
                        .submit(() -> AppDatabase.getDb(this).recordDAO().persist(record));
            }
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Integer[] getYears() {
        int[] little = getResources().getIntArray(R.array.year);
        Integer[] big = new Integer[little.length];
        for(int i = 0; i < little.length; i++) {
            big[i] = little[i];
        }
        return big;
    }

    @SuppressLint("SetTextI18n")
    private void setFields(Record record){
        moduleNum.setText(record.getModuleNum());
        creditPoints.setText(record.getCrp().toString());
        if(record.hasMark()) markProzent.setText(record.getMark().toString());
        moduleName.setText(record.getModuleName());
        if(record.isHalfWeighted()) weight.performClick();
        if(record.isSummerTerm()) summerTerm.performClick();
        int pos = 0;
        int[] years = getResources().getIntArray(R.array.year);
        for(int i = 0; i < years.length;i++) {
            if(record.getYear().equals(years[i])) pos = i;
        }
        year.setSelection(pos);
    }
}
