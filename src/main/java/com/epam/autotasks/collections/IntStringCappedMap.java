package com.epam.autotasks.collections;

import java.util.*;

class IntStringCappedMap extends AbstractMap<Integer, String> {

    private final long capacity;
    private Item[] items;

    public IntStringCappedMap(final long capacity) {
        items = new Item[0];
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<Integer, String>> iterator() {
                return new Iterator<>() {
                    private int i = 0;

                    @Override
                    public boolean hasNext() {
                        return items.length > i;
                    }

                    @Override
                    public Entry<Integer, String> next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Entry<Integer, String> result = Map.entry(items[i].getKey(), items[i].getValue());
                        i++;
                        return result;
                    }
                };
            }

            @Override
            public int size() {
                return IntStringCappedMap.this.size();
            }
        };
    }

    @Override
    public String get(final Object key) {
        String result = null;

        if (!checkIfKeyExists((Integer) key)) {
            return null;
        }
        for (Item it : items) {
            if (it.getKey().equals(key)) {
                result = it.getValue();
                break;
            }
        }
        return result;
    }

    @Override
    public String put(Integer key, final String value) {
        if (capacity < value.length()) throw new IllegalArgumentException();


        String result = null;
        boolean isExist = checkIfKeyExists(key);

        if (isExist) {
            int position = getPosition(key);
            result = items[position].setValue(value);
            Item tempItem = items[position];
            for (int i = position; i < items.length - 1; i++) {
                items[i] = items[i + 1];
            }

            items[items.length - 1] = tempItem;
        }

        while (getCurrentCapacity() + (isExist ? 0 : value.length()) > capacity) {
            removeFirst();
        }
        if (result == null) append(key, value);

        return result;
    }

    private boolean canBeInserted(String value) {
        return getCurrentCapacity() + value.length() <= capacity;
    }

    private void removeFirst() {
        if (items.length - 1 >= 0) System.arraycopy(items, 1, items, 0, items.length - 1);
        items = Arrays.copyOf(items, items.length - 1);
    }

    private boolean checkIfKeyExists(final Integer key) {
        for (Item item : items) {
            if (key.equals(item.getKey())) {
                return true;
            }
        }
        return false;
    }

    private void append(final Integer key, final String value) {
        Item[] newArray = Arrays.copyOf(items, items.length + 1);
        newArray[items.length] = new Item(key, value);
        items = Arrays.copyOf(newArray, newArray.length);
    }

    private int getCurrentCapacity() {
        int count = 0;
        for (Item item : items) {
            count += item.getValue().length();
        }
        return count;
    }

    @Override
    public String remove(final Object key) {
        Integer item = (Integer) key;
        if (!checkIfKeyExists(item)) return null;

        int position = getPosition(item);
        String value = items[position].getValue();

        for (int i = position; i < items.length - 1; i++) {
            items[i] = items[i + 1];
        }
        items = Arrays.copyOf(items, items.length - 1);
        return value;
    }


    private int getPosition(Object key) {

        for (int i = 0; i < items.length; i++) {
            if (items[i].getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return items.length;
    }

    static class Item {
        private Integer key;
        private String value;

        public Item(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public Integer getKey() {
            return key;
        }

        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public void setKey(Integer key) {
            this.key = key;
        }
    }
}
