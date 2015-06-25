package app.indiana.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import app.indiana.R;

/**
 * Created by chris on 11.06.2015.
 */
public class ViewHelper {

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static TextWatcher createTextWatcher(final TextView textView, final Button button,
                                                final String dividerMax, final String maxChars) {
        return new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(String.valueOf(s.length() + dividerMax + maxChars));
                if (s.length() > 0) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    public static String getReplyText(int replies, View view) {
        if (replies > 0) {
            return view.getResources().getQuantityString(R.plurals.reply, replies, replies);
        }
        return view.getResources().getString(R.string.reply_default);
    }

}
