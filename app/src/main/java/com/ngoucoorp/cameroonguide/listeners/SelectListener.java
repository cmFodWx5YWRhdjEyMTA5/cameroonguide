package com.ngoucoorp.cameroonguide.listeners;

import android.view.View;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */


public interface SelectListener {
    public void Select(View view, int position, CharSequence text);
    public void Select(View view, int position, CharSequence text, int id);
    public void Select(View view, int position, CharSequence text, int id, float additionalPrice);
}
