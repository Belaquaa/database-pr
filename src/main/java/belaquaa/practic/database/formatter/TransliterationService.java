package belaquaa.practic.database.formatter;

import com.ibm.icu.text.Transliterator;
import org.springframework.stereotype.Service;

@Service
public class TransliterationService {

    private final Transliterator toLatinTrans;
    private final Transliterator toCyrillicTrans;

    public TransliterationService() {
        this.toLatinTrans = Transliterator.getInstance("Cyrillic-Latin");
        this.toCyrillicTrans = Transliterator.getInstance("Latin-Cyrillic");
    }

    public String transliterateToLatin(String input) {
        return toLatinTrans.transliterate(input);
    }

    public String transliterateToCyrillic(String input) {
        return toCyrillicTrans.transliterate(input);
    }
}