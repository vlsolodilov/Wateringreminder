package com.example.wateringreminder;

import android.app.Notification;

public class Constants {
    public static final String EDIT_STATE_PLANT = "edit_state_plant";
    public static final String EDIT_STATE_REMINDER = "edit_state_reminder";
    public static final String ITEM_INTENT = "list_item_intent";
    public static final String ID_ITEM_INTENT = "id_item_intent";
    public static final String REMINDER_INTENT = "reminder_intent";
    public static final String NOTIFICATION_INTENT = "notification_intent";
    public static final String TABLE_PLANTS_NAME = "table_plants";
    public static final String TABLE_REMINDERS_NAME = "table_reminders";
    public static final String ID_PLANT = "id";
    public static final String NAME_PLANT = "name";
    public static final String DESCRIPTION_PLANT = "description";
    public static final String DATE_PLANT = "date";
    public static final String URI_PLANT = "uri";
    public static final String ID_REMINDER = "id";
    public static final String NAME_REMINDER = "name";
    public static final String PLANT_REMINDER = "plant";
    public static final String TIME_REMINDER = "time";
    public static final String PERIOD_REMINDER = "period";
    public static final String LAST_REMINDER = "last";
    public static final String DB_NAME = "my_db.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_PLANTS_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PLANTS_NAME + " (" + ID_PLANT + " INTEGER PRIMARY KEY," + NAME_PLANT + " TEXT,"
            + DESCRIPTION_PLANT + " TEXT," + DATE_PLANT + " INTEGER," + URI_PLANT + " TEXT)";
    public static final String TABLE_REMINDERS_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_REMINDERS_NAME + " (" + ID_REMINDER + " INTEGER PRIMARY KEY," + NAME_REMINDER
            + " TEXT," + PLANT_REMINDER + " INTEGER," + TIME_REMINDER + " INTEGER,"
            + PERIOD_REMINDER + " INTEGER,"+ LAST_REMINDER + " INTEGER)";

    public static final String DROP_TABLE_PLANTS = "DROP TABLE IF EXISTS " + TABLE_PLANTS_NAME;
    public static final String DROP_TABLE_REMINDERS = "DROP TABLE IF EXISTS " + TABLE_REMINDERS_NAME;

    public static final String CHANNEL_ID = "water_reminder";
}
