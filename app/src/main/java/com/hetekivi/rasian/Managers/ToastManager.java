package com.hetekivi.rasian.Managers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.R;

/**
 * Created by Santeri Hetekivi on 31.3.2016.
 */

/**
 * Class ToastManager
 * for managing toasts.
 */
public class ToastManager
{
    /**
     * Public static final values for class.
     */
    private static final String TAG = "ToastManager";

    /**
     * Constructor
     * @param _context Applications context.
     * @param _type Type of toast.
     */
    public ToastManager(Context _context, Type _type)
    {
        this.type = _type;
        this.UpdateContext(_context);
    }

    /**
     * Toast types.
     */
    public enum Type {
        ERROR, MESSAGE
    }

    /**
     * Classes member variables.
     */
    private Context     context     = null;
    private Type        type        = Type.MESSAGE;
    private View        layout      = null;
    private TextView    textView    = null;
    private Toast       toast       = null;

    /**
     * Function UpdateContext
     * for updating context class uses.
     * @param _context Applications context.
     * @return Success of update.
     */
    public boolean UpdateContext(Context _context) {
        boolean success = false;
        if (this.Check(_context)) {
            this.context = _context;
            this.setView();
            success = true;
        }
        return success;
    }

    /**
     * Function Short
     * for making short toast from given string.
     * @param text Text part of toast.
     */
    public void Short(String text) {
        this.Show(Toast.LENGTH_SHORT, text);
    }

    /**
     * Function Short
     * for making short toast from given string id.
     * @param id Id for string that will be text part of toast.
     */
    public void Short(int id) {
        this.Show(Toast.LENGTH_SHORT, this.context.getResources().getString(id));
    }

    /**
     * Function Long
     * for making long toast from given string.
     * @param text Text part of toast.
     */
    public void Long(String text) {
        this.Show(Toast.LENGTH_LONG, text);
    }

    /**
     * Function Long
     * for making long toast from given string id.
     * @param id Id for string that will be text part of toast.
     */
    public void Long(int id) {
        this.Show(Toast.LENGTH_LONG, this.context.getResources().getString(id));
    }

    /**
     * Function Check
     * for checking if everything has been set.
     * @return Result of the check.
     */
    private boolean Check()
    {
        boolean success = (this.context != null);
        this.Error(success);
        return success;
    }

    /**
     * Function Check
     * for checking given context.
     * @param _context Application context.
     * @return Result of the check.
     */
    private boolean Check(Context _context)
    {
        boolean success = (_context != null);
        this.Error(success);
        return success;
    }

    /**
     * Function Error
     * for showing error.
     * @param success Success of given check.
     */
    private void Error(boolean success)
    {
        if(!success && Global.Check()) Log.e(TAG, "No context for ToastManager!");
    }

    /**
     * Function Show
     * for showing the toast.
     * @param duration Duration of the toast.
     * @param text Text for the toast.
     */
    private void Show(int duration, String text)
    {
        if(this.textView != null && this.layout != null && this.toast != null)
        {
            this.textView.setText(text);
            this.toast.setDuration(duration);
            this.toast.setView(layout);
            this.toast.show();
        }
    }

    /**
     * Function setView
     * for setting custom toast view.
     */
    private void setView()
    {
        if(this.Check())
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = ((Activity) this.context).getWindow().getDecorView().findViewById(android.R.id.content);

            if (this.type == Type.ERROR) {
                this.layout = this.setViewError(inflater, rootView);
            } else {
                this.layout = this.setViewMessage(inflater, rootView);
            }
            this.textView = (TextView) layout.findViewById(R.id.text);

            this.toast = new Toast(this.context);
            this.toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
       }
    }

    /**
     * Function setViewError
     * for setting custom view form layout toast_error.xml
     * @param inflater Inflater to use for inflating the view.
     * @param rootView Root view for the view.
     * @return Custom toast view.
     */
    private View setViewError(LayoutInflater inflater, View rootView)
    {
        return inflater.inflate(R.layout.toast_error, (ViewGroup) rootView.findViewById(R.id.toast_error_layout_root));
    }

    /**
     * Function setViewMessage
     * for setting custom view form layout toast_message.xml
     * @param inflater Inflater to use for inflating the view.
     * @param rootView Root view for the view.
     * @return Custom toast view.
     */
    private View setViewMessage(LayoutInflater inflater, View rootView)
    {
        return inflater.inflate(R.layout.toast_message, (ViewGroup) rootView.findViewById(R.id.toast_message_layout_root));
    }



}


