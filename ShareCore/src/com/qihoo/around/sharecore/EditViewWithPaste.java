package com.qihoo.around.sharecore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by zhangqiang-s on 2014/7/28.
 */
public class EditViewWithPaste extends EditText {
    private PasteAction pasteAction;

    public EditViewWithPaste(Context context) {
        super(context);
    }

    public EditViewWithPaste(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditViewWithPaste(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            if (pasteAction != null) {
                pasteAction.paste();
            }
        }
        return super.onTextContextMenuItem(id);
    }

    public void setPasteAction(PasteAction pasteAction) {
        this.pasteAction = pasteAction;
    }

    public interface PasteAction{
        public void paste();
    }
}
