package dev.afonso.galleryac.util;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;

public class EvictingList<T> extends AbstractCollection<T> {
    private final LinkedList<T> list = new LinkedList<>();
    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T element) {
        if (list.size() >= maxSize) {
            list.removeFirst();
        }
        list.add(element);
        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    public boolean isFull() {
        return list.size() >= maxSize;
    }

    @Override
    public void clear() {
        list.clear();
    }

    public T get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}