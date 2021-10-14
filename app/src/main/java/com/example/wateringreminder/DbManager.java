package com.example.wateringreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DbManager {
    private Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbManager(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
    }
    public void openDb(){
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDbItem(String name, String description, Long date, String uri){

        ContentValues cv = new ContentValues();
        cv.put(Constants.NAME_PLANT, name);
        cv.put(Constants.DESCRIPTION_PLANT, description);
        cv.put(Constants.DATE_PLANT, date);
        cv.put(Constants.URI_PLANT, uri);
        db.insert(Constants.TABLE_PLANTS_NAME, null, cv);

    }

    public void insertToDbReminder(String name, int plant, long time, int period, long last){

        ContentValues cv = new ContentValues();
        cv.put(Constants.NAME_REMINDER, name);
        cv.put(Constants.PLANT_REMINDER, plant);
        cv.put(Constants.TIME_REMINDER, time);
        cv.put(Constants.PERIOD_REMINDER, period);
        cv.put(Constants.LAST_REMINDER, last);
        db.insert(Constants.TABLE_REMINDERS_NAME, null, cv);

    }

    public void updateItem(String name, String description, Long date, String uri, int id){
        String selection = Constants.ID_PLANT + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(Constants.NAME_PLANT, name);
        cv.put(Constants.DESCRIPTION_PLANT, description);
        cv.put(Constants.DATE_PLANT, date);
        cv.put(Constants.URI_PLANT, uri);
        db.update(Constants.TABLE_PLANTS_NAME,cv,selection, null);
    }

    public void updateReminder(String name, int plant, long time, int period, long last, int id){
        // TODO: Возможная Sql-инъекция, почему бы не сделать как в getFromDB?
        String selection = Constants.ID_REMINDER + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(Constants.NAME_REMINDER, name);
        cv.put(Constants.PLANT_REMINDER, plant);
        cv.put(Constants.TIME_REMINDER, time);
        cv.put(Constants.PERIOD_REMINDER, period);
        cv.put(Constants.LAST_REMINDER, last);
        db.update(Constants.TABLE_REMINDERS_NAME,cv,selection, null);
    }

    public void updateReminderDate(long last, int id){
        String selection = Constants.ID_REMINDER + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(Constants.LAST_REMINDER, last);
        db.update(Constants.TABLE_REMINDERS_NAME,cv,selection, null);
    }

    public void deleteItem(int id){
        String selection = Constants.ID_PLANT + "=" + id;
        db.delete(Constants.TABLE_PLANTS_NAME,selection, null);
        //добавить удаление напоминаний данного растения
        String selection2 = Constants.PLANT_REMINDER + "=" + id;
        db.delete(Constants.TABLE_REMINDERS_NAME,selection2, null);
    }

    public void deleteReminder(int id){
        String selection = Constants.ID_REMINDER + "=" + id;
        db.delete(Constants.TABLE_REMINDERS_NAME,selection, null);
    }

    public void getFromDb(String searchText, OnDataReceived onDataReceived) {
        final List<Item> items = new ArrayList<>();
        //final List<Reminder> reminders = new ArrayList<>();

        String selection = Constants.NAME_PLANT + " like ?";
        final Cursor cursorItem = db.query(Constants.TABLE_PLANTS_NAME, null, selection,
                new String[]{"%" + searchText + "%"}, null, null, null);

        while (cursorItem.moveToNext()) {
            Item item = new Item();
            // TODO: Можно сделать конструктор у класса Item для упрощения создания
            String name = cursorItem.getString(cursorItem.getColumnIndex(Constants.NAME_PLANT));
            String description = cursorItem.getString(cursorItem.getColumnIndex(Constants.DESCRIPTION_PLANT));
            Long date = cursorItem.getLong(cursorItem.getColumnIndex(Constants.DATE_PLANT));
            String uri = cursorItem.getString(cursorItem.getColumnIndex(Constants.URI_PLANT));
            int id = cursorItem.getInt(cursorItem.getColumnIndex(Constants.ID_PLANT));
            item.setName(name);
            item.setDescription(description);
            item.setDate(date);
            item.setUri(uri);
            item.setId(id);
            items.add(item);
        }
        cursorItem.close();

        /*final Cursor cursorReminder = db.query(Constants.TABLE_REMINDERS_NAME, null, null,
                null, null, null, null);

        while (cursorReminder.moveToNext()) {
            Reminder reminder = new Reminder();
            int id = cursorReminder.getInt(cursorReminder.getColumnIndex(Constants.ID_REMINDER));
            String name = cursorReminder.getString(cursorReminder.getColumnIndex(Constants.NAME_REMINDER));
            int plant = cursorReminder.getInt(cursorReminder.getColumnIndex(Constants.PLANT_REMINDER));
            long time = cursorReminder.getLong(cursorReminder.getColumnIndex(Constants.TIME_REMINDER));
            int period = cursorReminder.getInt(cursorReminder.getColumnIndex(Constants.PERIOD_REMINDER));
            long last = cursorReminder.getLong(cursorReminder.getColumnIndex(Constants.LAST_REMINDER));
            reminder.setId(id);
            reminder.setName(name);
            reminder.setPlant(plant);
            reminder.setTime(time);
            reminder.setPeriod(period);
            reminder.setLast(last);
            reminders.add(reminder);
        }
        cursorReminder.close();*/

        onDataReceived.onReceived(items, getAllReminder());
    }

    public List<Reminder> getAllReminder() {
        final List<Reminder> reminders = new ArrayList<>();
        final Cursor cursorReminder = db.query(Constants.TABLE_REMINDERS_NAME, null, null,
                null, null, null, null);
        // TODO: Может быть быстрее сначала найти все номера столбцов и сложить в ассоциативный массив?
        Map<String, Integer> headers = new TreeMap<>();
        for (int i = 0;  i < cursorReminder.getColumnCount(); i++) {
            headers.put(cursorReminder.getColumnName(i), i);
        }
        while (cursorReminder.moveToNext()) {
            Reminder reminder = new Reminder();
            int id = cursorReminder.getInt(headers.get(Constants.ID_REMINDER));
            String name = cursorReminder.getString(headers.get(Constants.NAME_REMINDER));
            int plant = cursorReminder.getInt(cursorReminder.getColumnIndex(Constants.PLANT_REMINDER));
            long time = cursorReminder.getLong(cursorReminder.getColumnIndex(Constants.TIME_REMINDER));
            int period = cursorReminder.getInt(cursorReminder.getColumnIndex(Constants.PERIOD_REMINDER));
            long last = cursorReminder.getLong(cursorReminder.getColumnIndex(Constants.LAST_REMINDER));
            reminder.setId(id);
            reminder.setName(name);
            reminder.setPlant(plant);
            reminder.setTime(time);
            reminder.setPeriod(period);
            reminder.setLast(last);
            reminders.add(reminder);
        }
        cursorReminder.close();
        return reminders;
    }

    public int getLastId(String tableName) {
        int lastId = -1;
        String query = "SELECT ROWID from " + tableName + " order by ROWID DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0);
            c.close();
        }
        return lastId;
        //return (int) DatabaseUtils.queryNumEntries(db, tableName);
    }

    public void closeDb(){
        dbHelper.close();
    }

}
