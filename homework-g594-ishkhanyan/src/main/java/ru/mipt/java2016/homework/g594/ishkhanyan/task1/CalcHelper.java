package ru.mipt.java2016.homework.g594.ishkhanyan.task1;

import java.util.HashMap;
import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class CalcHelper {
    private HashMap<String, String> func = new HashMap<>();
    private HashMap<String, String> func2 = new HashMap<>();

    public CalcHelper() {
        func.put("cos(", "кос(");
        func.put("sin(", "син(");
        func.put("tg(", "тг(");
        func.put("sqrt(", "кор(");
        func2.put("pow(", "ст(");
        func.put("abs(", "мод(");
        func.put("sign(", "зн(");
        func2.put("log(", "лог(");
        func.put("log2(", "лг(");
        func.put("rnd(", "окр(");
        func2.put("max(", "мкс(");
        func2.put("min(", "мин(");
    }

    String parser(String exp) {
        StringBuilder sb = new StringBuilder();
        boolean changed;
        for (int i = 0; i < exp.length(); ++i) {
            changed = false;
            for (String a : func2.keySet()) {
                if (i + a.length() <= exp.length() && exp.substring(i, i + a.length()).equals(a)) {
                    sb.append(func2.get(a));
                    i += a.length() - 1;
                    changed = true;
                    break;
                }
            }
            if (changed) {
                continue;
            }
            for (String a : func.keySet()) {
                if (i + a.length() <= exp.length() && exp.substring(i, i + a.length()).equals(a)) {
                    sb.append(func.get(a));
                    i += a.length() - 1;
                    changed = true;
                    break;
                }
            }
            if (changed) {
                continue;
            } else {
                sb.append(exp.charAt(i));
            }
        }
        return sb.toString();
    }

    public Pair<Pair<String, String>, Integer> getArgs(String exp, int i) throws ParsingException {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        int brackets = 0;
        int j;
        for (j = i; j < exp.length(); ++j) {
            if (exp.charAt(j) == '(') {
                ++brackets;
            }
            if (exp.charAt(j) == ')') {
                --brackets;
            }
            if (brackets == 0 && exp.charAt(j) == ',') {
                break;
            } else {
                sb1.append(exp.charAt(j));
            }
        }
        int h;
        for (h = j + 1; h < exp.length(); ++h) {
            if (exp.charAt(h) == '(') {
                ++brackets;
            }
            if (exp.charAt(h) == ')') {
                --brackets;
            }
            if (brackets == -1) {
                break;
            } else {
                sb2.append(exp.charAt(h));
            }
            if (h == exp.length() - 1 && brackets >= 0) {
                throw new ParsingException("illegal bracket balance");
            }
        }
        return new Pair<>(new Pair<>(sb1.toString(), sb2.toString()), h);
    }

    public Pair<String, Integer> getArg(String exp, int i) throws ParsingException {
        StringBuilder sb = new StringBuilder();
        int brackets = 0;
        int j;
        for (j = i; j < exp.length(); ++j) {
            if (exp.charAt(j) == '(') {
                ++brackets;
            }
            if (exp.charAt(j) == ')') {
                --brackets;
            }
            if (brackets == -1) {
                break;
            } else {
                sb.append(exp.charAt(j));
            }
            if (j == exp.length() - 1 && brackets >= 0) {
                throw new ParsingException("illegal bracket balance");
            }
        }
        return new Pair<>(sb.toString(), j);
    }
}
