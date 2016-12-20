package ru.mipt.java2016.homework.g594.ishkhanyan.task1;

import java.util.HashMap;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class Main {
    public static void main(String[]args) throws ParsingException {
        MyCalculator calc = new MyCalculator();
        System.out.println(calc.calculate("min(max(sin(3.14/2),1),2)", new HashMap<>()));
    }
}
