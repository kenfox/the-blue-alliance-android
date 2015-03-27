package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.WebcastHelper;

/**
 * Created by phil on 3/27/15.
 */
public class WebcastListElement extends ListElement {

    private String eventName;
    private JsonObject webcast;
    private int number;

    public WebcastListElement(String eventName, JsonObject webcast, int number){
        super();
        this.eventName = eventName;
        this.webcast = webcast;
        this.number = number;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;

        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_carded_summary, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            holder.container = (LinearLayout) convertView.findViewById(R.id.summary_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(eventName + " - " + number);
        final String service = webcast.get("type").getAsString();
        if (holder.container.getChildCount() > 2) {
            holder.container.removeViewAt(2);
        }
        if (service != null) {
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(service);
            holder.value.setTypeface(null, Typeface.NORMAL);
            holder.value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = WebcastHelper.getUrlForWebcast(c, WebcastHelper.getType(service), webcast);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                    c.startActivity(intent);
                }
            });
        } else {
            holder.value.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView label;
        TextView value;
        LinearLayout container;
    }
}
