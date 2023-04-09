package com.example.luxapprev1;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class LuxWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lux_widget);

        // The time logic
        Date dateInstance = new Date();
        int hours = dateInstance.getHours();
        if (hours >= 5 && hours <= 12) {
            views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_greet_morning));
            if (hours > 11) {
                views.setTextViewText(R.id.widget_fact, context.getString(R.string.widget_facts_noon));
            } else {
                views.setTextViewText(R.id.widget_fact, context.getString(R.string.widget_facts_morning));
            }
        } else if (hours > 12 && hours <= 18) {
            views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_greet_afternoon));
            if (hours < 13) {
                views.setTextViewText(R.id.widget_fact, context.getString(R.string.widget_facts_noon));
            } else {
                views.setTextViewText(R.id.widget_fact, context.getString(R.string.widget_facts_afternoon));
            }
        } else {
            views.setTextViewText(R.id.widget_title, context.getString(R.string.widget_greet_evening));
            views.setTextViewText(R.id.widget_fact, context.getString(R.string.widget_facts_night));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}