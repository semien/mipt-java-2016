package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;


/**
 * * Перзистентное хранилище ключ-значение.
 *
 * @author Vardan Manucharyan
 * @since 30.10.16
 **/
public class KVS<K, V> implements KeyValueStorage<K, V> {

    private SerializationStrategy<K> keySerializationStrategy;
    private SerializationStrategy<V> valueSerializaionStrategy;

    private HashMap<K, V> base = new HashMap<>();
    private File storage;
    private File mutexFile; // для многопоточности
    private boolean isClosed;

    public KVS(SerializationStrategy<K> keySerializationStrategy_,
               SerializationStrategy<V> valueSerializaionStrategy_,
               String path) throws IOException {

        keySerializationStrategy = keySerializationStrategy_;
        valueSerializaionStrategy = valueSerializaionStrategy_;

        mutexFile = new File(path, "Mutex");
        if (mutexFile.exists()) {
            throw new RuntimeException("Storage is already used!");
        }
        File dr = new File(path);
        if (!dr.isDirectory() || !dr.exists()) throw new RuntimeException("wrong path");

        storage = new File(path, "storage.db");

        if (storage.exists()) {
            checkStorage();
        }

        isClosed = false;
    }

    private void checkStorage() throws IOException {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(storage))) {
            int count;
            count = stream.readInt();

            for (int i = 0; i < count; i++) {
                K key = keySerializationStrategy.deserializeFromStream(stream);
                V val = valueSerializaionStrategy.deserializeFromStream(stream);
                base.put(key, val);
            }
            stream.close();
        } catch (IOException exception) {
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    private void refreshStorage() {
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(storage))) {

            stream.writeInt(base.size());

            for (HashMap.Entry<K, V> pair : base.entrySet()) {
                keySerializationStrategy.serializeToStream(pair.getKey(), stream);
                valueSerializaionStrategy.serializeToStream(pair.getValue(), stream);
            }
            stream.close();
        } catch (IOException exception) {
            throw new RuntimeException("Trouble with storage.db");
        }
    }

    /**
     * Возвращает значение для данного ключа, если оно есть в хранилище.
     * Иначе возвращает null.
     */
    @Override
    public V read(K key) {
        if (isClosed) {
            return null;
        } else {
            return base.get(key);
        }
    }

    /**
     * Возвращает true, если данный ключ есть в хранилище
     */
    @Override
    public boolean exists(K key) {
        if (isClosed || !base.containsKey(key)) return false;
        else return true;
    }

    /**
     * Записывает в хранилище пару ключ-значение.
     */
    @Override
    public void write(K key, V value) {
        if (isClosed) throw new RuntimeException("Can't write: storage is closed");
        else base.put(key, value);
    }

    /**
     * Удаляет пару ключ-значение из хранилища.
     */
    @Override
    public void delete(K key) {
        if (isClosed) throw new RuntimeException("Can't delete: storage is closed");
        else base.remove(key);
    }

    /**
     * Читает все ключи в хранилище.
     * <p>
     * Итератор должен бросать {@link java.util.ConcurrentModificationException},
     * если данные в хранилище были изменены в процессе итерирования.
     */
    @Override
    public Iterator<K> readKeys() {
        if (isClosed) throw new RuntimeException("Can't iterate: storage is closed");
        return base.keySet().iterator();
    }

    /**
     * Возвращает число ключей, которые сейчас в хранилище.
     */
    @Override
    public int size() {
        return base.size();
    }

    @Override
    public void close() {
        refreshStorage();
        isClosed = true;
        base.clear();
        mutexFile.delete();
    }
}
