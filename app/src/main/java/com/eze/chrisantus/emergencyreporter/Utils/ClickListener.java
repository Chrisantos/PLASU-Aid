package com.eze.chrisantus.emergencyreporter.Utils;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int position);
    ClickListener getRef();
}
