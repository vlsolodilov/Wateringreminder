package com.example.wateringreminder;

import com.example.wateringreminder.entity.Item;
import com.example.wateringreminder.entity.Reminder;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<Item> items, List<Reminder> reminders);
}
