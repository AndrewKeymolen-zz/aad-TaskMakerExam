package com.google.developer.taskmaker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.developer.taskmaker.data.Task;
import com.google.developer.taskmaker.reminders.AlarmScheduler;
import com.google.developer.taskmaker.views.DatePickerFragment;
import com.google.developer.taskmaker.views.TaskTitleView;

import java.util.Calendar;

public class TaskDetailActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    //New code from Andrew Keymolen Feature Tasks 7 and 8
    private TaskTitleView mDescriptionView;
    private ImageView mPriorityView;
    private TextView mDueDateView;
    private Uri mTaskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Task must be passed to this activity as a valid provider Uri
        final Uri taskUri = getIntent().getData();

        //New code from Andrew Keymolen Feature Tasks 7 and 8
        mTaskUri = taskUri;

        setContentView(R.layout.activity_task_detail);

        mDescriptionView = (TaskTitleView) findViewById(R.id.text_description);
        mPriorityView = (ImageView) findViewById(R.id.priority);
        mDueDateView = (TextView) findViewById(R.id.text_date);

        Cursor mCursor = getContentResolver().query(taskUri, null, null, null, null);
        mCursor.moveToFirst();

        Task mTask = new Task(mCursor);

        mDescriptionView.setText(mTask.description);
        if (mTask.hasDueDate()) {
            mDueDateView.setText(DateUtils.getRelativeTimeSpanString(mTask.dueDateMillis));
        }
        mPriorityView.setImageResource(mTask.isPriority ?
                R.drawable.ic_priority : R.drawable.ic_not_priority);

        // Set the state of the view, not asked but prettier !
        if (mTask.isComplete) {
            mDescriptionView.setState(TaskTitleView.DONE);
        } else if (mTask.hasDueDate() &&
                mTask.dueDateMillis < Calendar.getInstance().getTimeInMillis()) {
            mDescriptionView.setState(TaskTitleView.OVERDUE);
        } else {
            mDescriptionView.setState(TaskTitleView.NORMAL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //New code from Andrew Keymolen Feature Tasks 9
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(year, month, day);
        AlarmScheduler.scheduleAlarm(getApplicationContext(), mCalendar.getTimeInMillis(), mTaskUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //New code from Andrew Keymolen Feature Tasks 8 and 9

        if (id == R.id.action_delete) {
            getContentResolver().delete(mTaskUri, null, null);
            setResult(Activity.RESULT_OK, null);
            finish();
        }

        if (id == R.id.action_reminder) {
            DialogFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "dialog");
        }
        return super.onOptionsItemSelected(item);
    }
}
