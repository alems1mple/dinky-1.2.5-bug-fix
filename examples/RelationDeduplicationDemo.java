import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Standalone Java 8 demonstration; no Dinky dependencies. */
public class RelationDeduplicationDemo {
    static final class Relation {
        final long pre, post;
        final Integer preVersion, postVersion;
        final String condition;

        Relation(long pre, long post, Integer preVersion, Integer postVersion, String condition) {
            this.pre = pre;
            this.post = post;
            this.preVersion = preVersion;
            this.postVersion = postVersion;
            this.condition = condition;
        }
    }

    static List<Relation> deduplicate(List<Relation> input) {
        Map<String, Relation> unique = new LinkedHashMap<>();
        for (Relation relation : input) {
            String key = relation.pre + "->" + relation.post;
            Relation previous = unique.putIfAbsent(key, relation);
            if (previous != null
                    && (!Objects.equals(previous.preVersion, relation.preVersion)
                    || !Objects.equals(previous.postVersion, relation.postVersion)
                    || !Objects.equals(previous.condition, relation.condition))) {
                throw new IllegalStateException("Conflicting task relation: " + key);
            }
        }
        return new ArrayList<>(unique.values());
    }

    public static void main(String[] args) {
        Relation first = new Relation(10, 20, 1, 1, "NONE");
        Relation second = new Relation(20, 30, 1, 1, "NONE");
        List<Relation> result = deduplicate(Arrays.asList(first, first, second, first));
        if (result.size() != 2 || result.get(0) != first || result.get(1) != second) {
            throw new AssertionError("Expected ordered distinct edges");
        }
        boolean rejected = false;
        try {
            deduplicate(Arrays.asList(first, new Relation(10, 20, 2, 1, "NONE")));
        } catch (IllegalStateException expected) {
            rejected = true;
        }
        if (!rejected) {
            throw new AssertionError("Expected conflict rejection");
        }
        System.out.println("PASS: duplicates removed; order preserved; conflicts rejected");
    }
}
