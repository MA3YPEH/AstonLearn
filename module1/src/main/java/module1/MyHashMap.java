package module1;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

class MyHashMap<K, V> implements Iterable<Map.Entry<K, V>> {
    private static class Node<K, V> implements Map.Entry<K, V> {
        final int keyHash;
        final K key;
        V value;
        Node<K, V> nextNode;

        Node(int keyHash, K key, V value) {
            this.keyHash = keyHash;
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }
    }

    private Node<K, V>[] buckets;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = new Node[DEFAULT_CAPACITY];
    }

    public int size() {
        return size;
    }

    static final int hash(Object key) {
        if (key == null) {
            return 0;
        } else {
            int keyHash = key.hashCode();
            int keyShiftHash = keyHash >>> 16;
            return keyHash ^ keyShiftHash;
        }
    }

    public int getIndex(int keyHash) {
        return keyHash & (buckets.length - 1);
    }

    public V put(K key, V value) {
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }

        int keyHash = hash(key);
        int index = getIndex(keyHash);
        Node<K, V> currentNode = buckets[index];

        if (currentNode == null) {
            buckets[index] = new Node<>(keyHash, key, value);
            size++;
            return null;
        }

        while (currentNode != null) {
            if (currentNode.keyHash == keyHash
                    && ((key == currentNode.key) || (key != null && key.equals(currentNode.key)))) {
                V oldValue = currentNode.value;
                currentNode.value = value;
                return oldValue;
            }

            if (currentNode.nextNode == null) {
                break;
            }

            currentNode = currentNode.nextNode;
        }

        currentNode.nextNode = new Node<>(keyHash, key, value);
        size++;
        return null;
    }

    public V get(K key) {
        int keyHash = hash(key);
        int index = getIndex(keyHash);
        Node<K, V> currentNode = this.buckets[index];

        while (currentNode != null) {
            if (currentNode.keyHash == keyHash
                    && ((key == currentNode.key) || (key != null && key.equals(currentNode.key)))) {
                return currentNode.value;
            }

            currentNode = currentNode.nextNode;
        }

        return null;
    }

    public V remove(K key) {
        int keyHash = hash(key);
        int index = getIndex(keyHash);
        Node<K, V> currentNode = this.buckets[index];
        Node<K, V> previousNode = null;

        while (currentNode != null) {
            if (currentNode.keyHash == keyHash
                    && ((key == currentNode.key) || (key != null && key.equals(currentNode.key)))) {
                if (previousNode == null) {
                    this.buckets[index] = currentNode.nextNode;
                } else {
                    previousNode.nextNode = currentNode.nextNode;
                }

                this.size--;
                return currentNode.value;
            }

            previousNode = currentNode;
            currentNode = currentNode.nextNode;
        }

        return null;
    }

    public void resize() {
        Node<K, V>[] oldBuckets = this.buckets;
        this.buckets = new Node[oldBuckets.length * 2];
        this.size = 0;

        for (Node<K, V> bucket : oldBuckets) {
            Node<K, V> currentNode = bucket;

            while (currentNode != null) {
                int index = getIndex(currentNode.keyHash);
                Node<K, V> nextNode = currentNode.nextNode;

                currentNode.nextNode = this.buckets[index];
                this.buckets[index] = currentNode;
                this.size++;

                currentNode = nextNode;
            }
        }
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>() {
            private int currentBucket = 0;
            private Node<K, V> nextToReturnNode = null;

            {
                toNextBucket();
            }

            private void toNextBucket() {
                while (currentBucket < buckets.length && buckets[currentBucket] == null) {
                    currentBucket++;
                }
                if (currentBucket < buckets.length) {
                    nextToReturnNode = buckets[currentBucket];
                    currentBucket++;
                } else {
                    nextToReturnNode = null;
                }
            }

            @Override
            public boolean hasNext() {
                return nextToReturnNode != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (nextToReturnNode == null) {
                    throw new NoSuchElementException();
                }

                Node<K, V> currentToReturnNode = nextToReturnNode;

                if (nextToReturnNode.nextNode != null) {
                    nextToReturnNode = nextToReturnNode.nextNode;
                } else {
                    toNextBucket();
                }

                return currentToReturnNode;
            }
        };
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');

        Iterator<Map.Entry<K, V>> it = this.iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            K key = entry.getKey();
            V value = entry.getValue();

            sb.append(key == this ? "this" : key);
            sb.append(": ");
            sb.append(value == this ? "this" : value);

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append('}');
        return sb.toString();
    }
}