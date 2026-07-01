package fr.sdv.etloff.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fr.sdv.etloff.etl.CsvProductRecord;

public final class CsvLineParser {

    private static final int EXPECTED_FIELDS = 30;
    private static final int MAX_NOM_LENGTH = 500;

    private CsvLineParser() {
    }

    public static CsvProductRecord parseLine(String line) {
        String[] fields = splitPipe(line, EXPECTED_FIELDS);
        if (fields.length < EXPECTED_FIELDS) {
            return null;
        }
        String ingredientsRaw = trim(fields[4]);
        String allergenesRaw = trim(fields[28]);
        String additifsRaw = trim(fields[29]);
        return new CsvProductRecord(
                trim(fields[0]),
                trim(fields[1]),
                trim(fields[2]),
                normalizeGrade(fields[3]),
                ingredientsRaw,
                parseElements(ingredientsRaw),
                parseDouble(fields[5]),
                parseDouble(fields[6]),
                parseDouble(fields[7]),
                parseDouble(fields[8]),
                parseDouble(fields[9]),
                parseDouble(fields[10]),
                parseDouble(fields[11]),
                parseDouble(fields[12]),
                parseDouble(fields[13]),
                parseDouble(fields[14]),
                parseDouble(fields[15]),
                parseDouble(fields[16]),
                parseDouble(fields[17]),
                parseDouble(fields[18]),
                parseDouble(fields[19]),
                parseDouble(fields[20]),
                parseDouble(fields[21]),
                parseDouble(fields[22]),
                parseDouble(fields[23]),
                parseDouble(fields[24]),
                parseDouble(fields[25]),
                parseDouble(fields[26]),
                parseBoolean(fields[27]),
                allergenesRaw,
                parseElements(allergenesRaw),
                additifsRaw,
                parseElements(additifsRaw)
        );
    }

    public static List<String> parseElements(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String cleaned = cleanElement(raw);
        if (cleaned.isEmpty()) {
            return List.of();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String part : splitElements(cleaned)) {
            String token = cleanElement(part);
            if (!token.isEmpty()) {
                result.add(token);
            }
        }
        return new ArrayList<>(result);
    }

    static String cleanElement(String value) {
        if (value == null) return "";
        return trimToMax(cleanText(value), MAX_NOM_LENGTH);
    }

    private static List<String> splitElements(String text) {
        if (text.indexOf(',') >= 0) {
            List<String> parts = splitOnChar(text, ',');
            if (parts.size() == 1 && text.indexOf(';') >= 0) {
                return splitOnChar(text, ';');
            }
            return parts;
        }
        if (text.indexOf(';') >= 0) {
            return splitOnChar(text, ';');
        }
        if (text.contains(" - ")) {
            return splitOnDash(text);
        }
        if (text.indexOf('-') > 0) {
            return splitOnChar(text, '-');
        }
        return List.of(text);
    }

    static String[] splitPipe(String line, int minFields) {
        if (line == null) return new String[0];
        String[] fields = new String[minFields];
        int fieldIndex = 0;
        int start = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '|') {
                if (fieldIndex < minFields) fields[fieldIndex++] = line.substring(start, i);
                start = i + 1;
            }
        }
        if (fieldIndex < minFields) fields[fieldIndex++] = line.substring(start);
        if (fieldIndex < minFields) {
            String[] trimmed = new String[fieldIndex];
            System.arraycopy(fields, 0, trimmed, 0, fieldIndex);
            return trimmed;
        }
        return fields;
    }

    static List<String> splitOnChar(String text, char separator) {
        List<String> parts = new ArrayList<>();
        if (text == null || text.isEmpty()) return parts;
        StringBuilder token = new StringBuilder(text.length() / 4 + 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == separator) {
                parts.add(token.toString());
                token.setLength(0);
            } else {
                token.append(c);
            }
        }
        parts.add(token.toString());
        return parts;
    }

    static List<String> splitOnDash(String text) {
        List<String> parts = new ArrayList<>();
        if (text == null || text.isEmpty()) return parts;
        String marker = " - ";
        StringBuilder token = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (i + marker.length() <= text.length() && text.regionMatches(i, marker, 0, marker.length())) {
                parts.add(token.toString());
                token.setLength(0);
                i += marker.length();
            } else {
                token.append(text.charAt(i));
                i++;
            }
        }
        parts.add(token.toString());
        return parts;
    }

    static String cleanText(String value) {
        if (value == null || value.isEmpty()) return "";
        return trimToMax(removeSpecialChars(removePercentages(removeParentheses(value))), MAX_NOM_LENGTH);
    }

    private static String removeParentheses(String value) {
        StringBuilder out = new StringBuilder(value.length());
        int depth = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '(') depth++;
            else if (c == ')' && depth > 0) depth--;
            else if (depth == 0) out.append(c);
        }
        return out.toString();
    }

    private static String removePercentages(String value) {
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '%') {
                out.append(value.charAt(i));
                continue;
            }
            while (out.length() > 0) {
                char last = out.charAt(out.length() - 1);
                if (Character.isDigit(last) || last == '.' || last == ',') out.setLength(out.length() - 1);
                else if (last == ' ') out.setLength(out.length() - 1);
                else break;
            }
        }
        return out.toString().trim();
    }

    private static String removeSpecialChars(String value) {
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c != '*' && c != '_' && c != '"' && c != '\'') out.append(c);
        }
        return out.toString().trim();
    }

    private static String trimToMax(String value, int maxLength) {
        if (value == null) return "";
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) return trimmed;
        return trimmed.substring(0, maxLength).trim();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeGrade(String grade) {
        if (grade == null || grade.isBlank()) return null;
        return grade.trim().toUpperCase();
    }

    static Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        StringBuilder normalized = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!Character.isWhitespace(c)) normalized.append(c == ',' ? '.' : c);
        }
        try {
            return Double.parseDouble(normalized.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) return null;
        String trimmed = value.trim();
        return "1".equals(trimmed) || "true".equalsIgnoreCase(trimmed);
    }
}
