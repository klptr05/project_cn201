import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Counts how many of each thing you hold, e.g. a Bag<String> basket: "Turnip" -> 3. */
public class Bag<T> {
    private final Map<T, Integer> counts = new LinkedHashMap<>();   // keeps the order things were added

    public void add(T item, int amount) {
        counts.merge(item, amount, Integer::sum);
    }

    /** Takes out `amount` of an item. @return false, and takes nothing, if there aren't enough */
    public boolean remove(T item, int amount) {
        int have = count(item);
        if (have < amount) return false;
        if (have == amount) counts.remove(item);
        else counts.put(item, have - amount);
        return true;
    }

    public int count(T item) { return counts.getOrDefault(item, 0); }
    public boolean isEmpty() { return counts.isEmpty(); }
    public void clear() { counts.clear(); }

    /** A read-only view, so nobody can change the counts behind the bag's back. */
    public Map<T, Integer> asMap() {
        return Collections.unmodifiableMap(counts);
    }

    @Override
    public String toString() {
        if (counts.isEmpty()) return "empty";
        StringBuilder text = new StringBuilder();
        for (Map.Entry<T, Integer> entry : counts.entrySet()) {
            if (text.length() > 0) text.append(", ");
            text.append(entry.getKey()).append(" x").append(entry.getValue());
        }
        return text.toString();
    }
}
