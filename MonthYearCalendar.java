package cordova.plugin.monthyearcalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * This class echoes a string called from JavaScript.
 */
public class MonthYearCalendar extends CordovaPlugin {

    String minDate;
    String maxDate;
    String currentDatetoSet;
    int minMonth;
    int maxMonth;
    int minYear;
    int maxYear;
    int currentMonth;
    int currentYear;
    int currentDate;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showCalendar")) {
            // now get the first element:
            JSONObject jsonObject = args.getJSONObject(0);
            this.minDate = jsonObject.getString("minDate");
            this.maxDate = jsonObject.getString("maxDate");
            this.currentDatetoSet = jsonObject.getString("date");
            parseMinimum(minDate);
            parseCurrentDate(currentDatetoSet);
            this.showCalendar(callbackContext);
        }
        return true;
    }

    private void parseCurrentDate(String date) {
        String[] mnDate = date.split("/");
        currentDate = Integer.valueOf(mnDate[0]);

        String[] mnMonth = date.split("/");
        currentMonth = Integer.valueOf(mnMonth[1]);

        String[] mnYear = date.split("/");
        currentYear = Integer.valueOf(mnYear[2]);
    }

    private void parseMinimum(String mnDate) {
        String[] mnMonth = mnDate.split("/");
        minMonth = Integer.valueOf(mnMonth[1]);

        String[] mnYear = mnDate.split("/");
        minYear = Integer.valueOf(mnYear[2]);
    }


    private void showCalendar(CallbackContext callbackContext) {
        try {
            DatePickerFragment datepicker = DatePickerFragment.newInstance(cordova.getActivity(), callbackContext, minYear, minMonth,
                    maxDate, maxYear, maxMonth, currentMonth, currentYear, currentDate);
            datepicker.show(cordova.getActivity().getFragmentManager(), "showDate");
        } catch (Exception e) {
            callbackContext.error("Error Occurred while showing calendar");
        }
    }

    @SuppressLint("ValidFragment")
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        int minYear;
        int minMonth;
        String maxDate;
        int maxYear;
        int maxMonth;
        int currentMonth;
        int currentYear;
        int currentDate;
        CallbackContext context;
        Activity cordovaActivity;

        public DatePickerFragment(Activity activity, CallbackContext callbackContext) {
            context = callbackContext;
            cordovaActivity = activity;
        }

        public static DatePickerFragment newInstance(Activity activity, CallbackContext callbackContext,
                                                     int minYear, int minMonth, String maxDate, int maxYear, int maxMonth, int currentMonth,
                                                     int currentYear, int currentDate) {
            DatePickerFragment frag = new DatePickerFragment(activity, callbackContext);
            Bundle args = new Bundle();
            args.putInt("minYear", minYear);
            args.putInt("minMonth", minMonth);
            args.putString("maxDate", maxDate);
            args.putInt("maxYear", maxYear);
            args.putInt("maxMonth", maxMonth);
            args.putInt("currentMonth", currentMonth);
            args.putInt("currentYear", currentYear);
            args.putInt("currentDate", currentDate);
            frag.setArguments(args);
            return frag;
        }

        private void getArgsFromBundle() {
            if (getArguments() != null) {
                this.minYear = getArguments().getInt("minYear");
                this.minMonth = getArguments().getInt("minMonth");
                this.maxDate = getArguments().getString("maxDate");
                this.maxYear = getArguments().getInt("maxYear");
                this.maxMonth = getArguments().getInt("maxMonth");
                this.currentMonth = getArguments().getInt("currentMonth");
                this.currentYear = getArguments().getInt("currentYear");
                this.currentDate = getArguments().getInt("currentDate");
            }
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            getArgsFromBundle();
            //Use the current date as the default date in the date picker
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog dpd = new DatePickerDialog(cordovaActivity, AlertDialog.THEME_HOLO_DARK, this, year, month, day) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    int day = cordovaActivity.getApplicationContext().getResources().getIdentifier("android:id/day", null, null);
                    if (day != 0) {
                        View dayPicker = findViewById(day);
                        if (dayPicker != null) {
                            //Set Day view visibility Off/Gone
                            dayPicker.setVisibility(View.GONE);
                        }
                    }
                }
            };

            c.set(minYear, minMonth - 1, 1);
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

            parseMaximum(maxDate);

            c.set(maxYear, maxMonth - 1, 1);
            dpd.getDatePicker().setMaxDate(c.getTimeInMillis());


            dpd.updateDate(currentYear, currentMonth - 1, currentDate);

            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Set the Month & Year to TextView which chosen by the user
            String stringOfDate = month + 1 + "/" + year;
            context.success(stringOfDate);
        }

        private void parseMaximum(String mxDate) {
            String[] mxMonth = mxDate.split("/");
            maxMonth = Integer.valueOf(mxMonth[1]);

            String[] mxYear = mxDate.split("/");
            maxYear = Integer.valueOf(mxYear[2]);
        }

    }

}
