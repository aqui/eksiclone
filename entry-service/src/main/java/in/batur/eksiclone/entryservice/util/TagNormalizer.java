package in.batur.eksiclone.entryservice.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
public class TagNormalizer {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Trim and convert to lowercase
        String normalized = input.trim().toLowerCase();
        
        // Normalize accented characters
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = NONLATIN.matcher(normalized).replaceAll("");
        
        // Replace whitespace with hyphen
        normalized = WHITESPACE.matcher(normalized).replaceAll("-");
        
        return normalized;
    }
}