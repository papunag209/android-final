package com.example.p2pchat.adapters;

public interface OnItemAction<T> {
    void onLongPress(T item);
    void onClick(T item);
}
