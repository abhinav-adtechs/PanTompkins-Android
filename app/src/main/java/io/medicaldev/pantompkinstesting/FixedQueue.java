package io.cardeadev.pantompkinstesting;


import java.util.LinkedList;

public class FixedQueue<E> extends LinkedList<E> {

    private int limit;

    public FixedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }
}
