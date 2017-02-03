package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.model.SearchSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by etast on 2/2/17.
 */

public class AdvSrchOptsDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private Button mBtnSave;
    private CheckBox mCbArts;
    private CheckBox mCbFashion;
    private CheckBox mCbSports;
    private EditText mEtBeginDate;
    private Spinner mSortOrder;
    private Calendar mCalendar;
    private boolean hasCalendarChanged;
    private SearchSettings settings;
    private SimpleDateFormat mSimpleDateFormat;

    public interface AdvSrchOptsDialogListener {
        void onFinishAdvSrchOptsDialog(String inputText);
    }

    public AdvSrchOptsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AdvSrchOptsDialogFragment newInstance(Parcelable settings) {
        AdvSrchOptsDialogFragment frag = new AdvSrchOptsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("settings", settings);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adv_srch_opts, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hasCalendarChanged = false;
        settings = getArguments().getParcelable("settings");
        mBtnSave = (Button) view.findViewById(R.id.btnSave);
        mCbArts = (CheckBox) view.findViewById(R.id.cbArts);
        mCbFashion = (CheckBox) view.findViewById(R.id.cbFashion);
        mCbSports = (CheckBox) view.findViewById(R.id.cbSports);
        mEtBeginDate = (EditText)view.findViewById(R.id.etBeginDate);
        mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        mCalendar = Calendar.getInstance();
        if (settings.isArtsFilterOn()) {
            mCbArts.setChecked(true);
        }
        if (settings.isFashionFilterOn()) {
            mCbFashion.setChecked(true);
        }
        if (settings.isSportsFilterOn()) {
            mCbSports.setChecked(true);
        }
        mEtBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        if (settings.isCalendarSet()) {
            mEtBeginDate.setText(mSimpleDateFormat.format(settings.getmCalendar().getTime()));
        }
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setArtsFilterOn(mCbArts.isChecked());
                settings.setFashionFilterOn(mCbFashion.isChecked());
                settings.setSportsFilterOn(mCbSports.isChecked());
                if (hasCalendarChanged) {
                    settings.setmCalendar(mCalendar);
                    settings.setCalendarSet(true);
                }
                AdvSrchOptsDialogListener listener = (AdvSrchOptsDialogListener) getActivity();
                listener.onFinishAdvSrchOptsDialog("Setting Saved!");
                dismiss();
            }
        });
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(AdvSrchOptsDialogFragment.this, 300);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // store the values selected into a Calendar instance
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mEtBeginDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
        hasCalendarChanged=true;
    }
}