package belaquaa.practic.database.formatter;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Service
public class SimilarityService {

    private final LevenshteinDistance levenshtein;
    private final int threshold;

    public SimilarityService() {
        this.levenshtein = new LevenshteinDistance();
        this.threshold = 2; // Максимальное допустимое расстояние
    }

    public boolean isSimilar(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        int distance = levenshtein.apply(a.toLowerCase(), b.toLowerCase());
        return distance <= threshold;
    }
}