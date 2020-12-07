/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.app.afyadata.widgets;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.TimeData;
import org.javarosa.form.api.FormEntryPrompt;
import org.joda.time.DateTime;
import org.sacids.app.afyadata.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

import timber.log.Timber;

/**
 * Displays a TimePicker widget.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class TimeWidget extends QuestionWidget {
    private TimePickerDialog timePickerDialog;

    private Button timeButton;
    private TextView timeTextView;

    private int hourOfDay;
    private int minuteOfHour;

    private boolean nullAnswer;

    public TimeWidget(Context context, final FormEntryPrompt prompt) {
        super(context, prompt);

        setGravity(Gravity.START);

        createTimeButton();
        createTimeTextView();
        createTimePickerDialog();
        addViews();
    }

    @Override
    public void clearAnswer() {
        nullAnswer = true;
        timeTextView.setText(R.string.no_time_selected);
    }

    @Override
    public IAnswerData getAnswer() {
        clearFocus();
        // use picker time, convert to today's date, store as utc
        DateTime dt = (new DateTime()).withTime(hourOfDay, minuteOfHour, 0, 0);
        return nullAnswer ? null : new TimeData(dt.toDate());
    }

    @Override
    public void setFocus(Context context) {
        // Hide the soft keyboard if it's showing.
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        timeButton.setOnLongClickListener(l);
        timeTextView.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        timeButton.cancelLongPress();
        timeTextView.cancelLongPress();
    }

    private void createTimeButton() {
        timeButton = getSimpleButton(getContext().getString(R.string.select_time));
        timeButton.setEnabled(!formEntryPrompt.isReadOnly());
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nullAnswer) {
                    setTimeToCurrent();
                } else {
                    timePickerDialog.updateTime(hourOfDay, minuteOfHour);
                }
                timePickerDialog.show();
            }
        });
    }

    private void createTimeTextView() {
        timeTextView = new TextView(getContext());
        timeTextView.setId(QuestionWidget.newUniqueId());
        timeTextView.setPadding(20, 20, 20, 20);
        timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, answerFontsize);
        timeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
    }

    private void addViews() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(timeButton);
        linearLayout.addView(timeTextView);
        addAnswerView(linearLayout);
    }

    public void setTimeLabel() {
        nullAnswer = false;
        timeTextView.setText(getAnswer().getDisplayText());
    }

    private void createTimePickerDialog() {
        timePickerDialog = new CustomTimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        TimeWidget.this.hourOfDay = hourOfDay;
                        TimeWidget.this.minuteOfHour = minuteOfHour;
                        setTimeLabel();
                    }
                }, 0, 0);
        timePickerDialog.setCanceledOnTouchOutside(false);

        if (formEntryPrompt.getAnswerValue() == null) {
            clearAnswer();
        } else {
            DateTime dt = new DateTime(((Date) formEntryPrompt.getAnswerValue().getValue()).getTime());
            hourOfDay = dt.getHourOfDay();
            minuteOfHour = dt.getMinuteOfHour();
            setTimeLabel();
            timePickerDialog.updateTime(hourOfDay, minuteOfHour);
        }
    }

    public int getHour() {
        return hourOfDay;
    }

    public int getMinute() {
        return minuteOfHour;
    }

    public boolean isNullAnswer() {
        return nullAnswer;
    }

    public void setTimeToCurrent() {
        DateTime dt = new DateTime();
        hourOfDay = dt.getHourOfDay();
        minuteOfHour = dt.getMinuteOfHour();
        timePickerDialog.updateTime(hourOfDay, minuteOfHour);
    }

    private class CustomTimePickerDialog extends TimePickerDialog {
        private String dialogTitle = getContext().getString(R.string.select_time);

        CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hour, int minute) {
            super(context, android.R.style.Theme_Holo_Light_Dialog, callBack, hour, minute, DateFormat.is24HourFormat(context));
            setTitle(dialogTitle);
            fixSpinner(context, hour, minute, DateFormat.is24HourFormat(context));
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        public void setTitle(CharSequence title) {
            super.setTitle(dialogTitle);
        }

        /**
         * Workaround for this bug: https://code.google.com/p/android/issues/detail?id=222208
         * In Android 7.0 Nougat, spinner mode for the TimePicker in TimePickerDialog is
         * incorrectly displayed as clock, even when the theme specifies otherwise.
         *
         * Source: https://gist.github.com/jeffdgr8/6bc5f990bf0c13a7334ce385d482af9f
         */
        private void fixSpinner(Context context, int hourOfDay, int minute, boolean is24HourView) {
            // android:timePickerMode spinner and clock began in Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    // Get the theme's android:timePickerMode
                    final int MODE_SPINNER = 2;
                    Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
                    Field timePickerStyleableField = styleableClass.getField("TimePicker");
                    int[] timePickerStyleable = (int[]) timePickerStyleableField.get(null);
                    final TypedArray a = context.obtainStyledAttributes(null, timePickerStyleable,
                            android.R.attr.timePickerStyle, 0);
                    Field timePickerModeStyleableField = styleableClass.getField("TimePicker_timePickerMode");
                    int timePickerModeStyleable = timePickerModeStyleableField.getInt(null);
                    final int mode = a.getInt(timePickerModeStyleable, MODE_SPINNER);
                    a.recycle();

                    if (mode == MODE_SPINNER) {
                        TimePicker timePicker = (TimePicker) findField(TimePickerDialog.class,
                                TimePicker.class, "mTimePicker").get(this);
                        Class<?> delegateClass = Class.forName("android.widget.TimePicker$TimePickerDelegate");
                        Field delegateField = findField(TimePicker.class, delegateClass, "mDelegate");
                        Object delegate = delegateField.get(timePicker);

                        Class<?> spinnerDelegateClass;
                        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
                            spinnerDelegateClass = Class.forName("android.widget.TimePickerSpinnerDelegate");
                        } else {
                            // TimePickerSpinnerDelegate was initially misnamed in API 21!
                            spinnerDelegateClass = Class.forName("android.widget.TimePickerClockDelegate");
                        }

                        // In 7.0 Nougat for some reason the timePickerMode is ignored and the
                        // delegate is TimePickerClockDelegate
                        if (delegate.getClass() != spinnerDelegateClass) {
                            delegateField.set(timePicker, null); // throw out the TimePickerClockDelegate!
                            timePicker.removeAllViews(); // remove the TimePickerClockDelegate views
                            Constructor spinnerDelegateConstructor = spinnerDelegateClass
                                    .getConstructor(TimePicker.class, Context.class,
                                            AttributeSet.class, int.class, int.class);
                            spinnerDelegateConstructor.setAccessible(true);

                            // Instantiate a TimePickerSpinnerDelegate
                            delegate = spinnerDelegateConstructor.newInstance(timePicker, context,
                                    null, android.R.attr.timePickerStyle, 0);

                            // set the TimePicker.mDelegate to the spinner delegate
                            delegateField.set(timePicker, delegate);

                            // Set up the TimePicker again, with the TimePickerSpinnerDelegate
                            timePicker.setIs24HourView(is24HourView);
                            timePicker.setCurrentHour(hourOfDay);
                            timePicker.setCurrentMinute(minute);
                            timePicker.setOnTimeChangedListener(this);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private Field findField(Class objectClass, Class fieldClass, String expectedName) {
            try {
                Field field = objectClass.getDeclaredField(expectedName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                Timber.i(e); // ignore
            }

            // search for it if it wasn't found under the expected ivar name
            for (Field searchField : objectClass.getDeclaredFields()) {
                if (searchField.getType() == fieldClass) {
                    searchField.setAccessible(true);
                    return searchField;
                }
            }
            return null;
        }
    }
}
