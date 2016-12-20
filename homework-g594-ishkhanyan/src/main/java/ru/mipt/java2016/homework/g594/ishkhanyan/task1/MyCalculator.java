package ru.mipt.java2016.homework.g594.ishkhanyan.task1;


import java.util.HashMap;
import java.util.Stack;
import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task1.ParsingException;


public class MyCalculator {
    public MyCalculator() {
    }

    private boolean isDelim(char c) { //check that the symbol is delimiter
        return c == ' ' || c == '\n' || c == '\t';
    }

    private boolean isOper(char c) { // check that the symbol is operator
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isDigit(char c) { //check that the symbol is digit or point
        return (c >= '0' && c <= '9' || c == '.');
    }

    private CalcHelper help = new CalcHelper();

    private int priority(char op) { // return priority of operation
        switch (op) {
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case '~':
                return 3;
            case '#':
                return 3;
            default:
                return -1;
        }
    }

    private double getVal(String str) throws ParsingException { // convert string >> double
        return Double.parseDouble(str);
    }

    private void doOper(Stack<Double> numbers, char op) { //do 1 operation from stack
        if (op == '#' || op == '~') {
            double l = numbers.pop();
            switch (op) {
                case '#':
                    numbers.push(l);
                    break;
                case '~':
                    numbers.push(-l);
                    break;
                default:
                    break;

            }
        } else {
            double r = numbers.pop();
            double l = numbers.pop();
            switch (op) {
                case '+':
                    numbers.push(r + l);
                    break;
                case '-':
                    numbers.push(l - r);
                    break;
                case '*':
                    numbers.push(r * l);
                    break;
                case '/':
                    numbers.push(l / r);
                    break;
                default:
                    break;
            }
        }
    }

    public double calculate(String exp, HashMap<String, Double> vars) throws ParsingException {
        boolean mayUnary = true; //wait unary operation
        int numberAfterOp = 0; //how many numbers were after last operation
        int brackbalnce = 0;
        if (exp == null || exp.length() == 0) {
            throw new ParsingException("empty expression");
        }
        exp = help.parser(exp);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<exp.length(); ++i){
            int max = 0;
            Double val = new Double(0);

            for(String var : vars.keySet()){
                int length = var.length();
                if(i+length<=exp.length()){
                    if(exp.substring(i,i+length).equals(var) && length>max){
                        val = vars.get(var);
                        max = length;
                    }
                }
            }

            if(max != 0){
                i+=max-1;
                builder.append(val.toString());
            }else{
                builder.append(exp.charAt(i));
            }
        }

        exp = builder.toString();

        Stack<Double> numbers = new Stack<>();
        Stack<Character> oper = new Stack<>();
        for (int i = 0; i < exp.length(); ++i) {

            if (isDelim(exp.charAt(i))) {
                continue;
            }

            if (i + 5 <= exp.length() && exp.substring(i, i + 5).equals("лог2(")) {
                Pair<String, Integer> pair = help.getArg(exp, i + 5);
                numbers.add(Math.log(calculate(pair.getKey(), new HashMap<>())) / Math.log(2));
                i = pair.getValue();
                mayUnary = false;
                numberAfterOp = 1;
                continue;
            }

            if (i + 4 <= exp.length()) {
                if (exp.substring(i, i + 4).equals("син(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 4);
                    numbers.add(Math.sin(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("кос(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 4);
                    numbers.add(Math.cos(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("окр(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 4);
                    numbers.add((double) Math.round(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }

                if (exp.substring(i, i + 4).equals("кор(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 4);
                    numbers.add(Math.sqrt(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("мод(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 4);
                    numbers.add(Math.abs(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("лог(")) {
                    Pair<Pair<String, String>, Integer> pair = help.getArgs(exp, i + 4);
                    numbers.add(Math.log(calculate(pair.getKey().getKey(), new HashMap<>())) / Math
                            .log(calculate(pair.getKey().getValue(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("мкс(")) {
                    Pair<Pair<String, String>, Integer> pair = help.getArgs(exp, i + 4);
                    numbers.add(Math.max(calculate(pair.getKey().getKey(), new HashMap<>()),
                            calculate(pair.getKey().getValue(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 4).equals("мин(")) {
                    Pair<Pair<String, String>, Integer> pair = help.getArgs(exp, i + 4);
                    numbers.add(Math.min(calculate(pair.getKey().getKey(), new HashMap<>()),
                            calculate(pair.getKey().getValue(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }

            }

            if (i + 3 <= exp.length()) {
                if (exp.substring(i, i + 3).equals("тг(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 3);
                    numbers.add(Math.tan(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 3).equals("зн(")) {
                    Pair<String, Integer> pair = help.getArg(exp, i + 3);
                    numbers.add(Math.signum(calculate(pair.getKey(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
                if (exp.substring(i, i + 3).equals("ст(")) {
                    Pair<Pair<String, String>, Integer> pair = help.getArgs(exp, i + 3);
                    numbers.add(Math.pow(calculate(pair.getKey().getKey(), new HashMap<>()),
                            calculate(pair.getKey().getValue(), new HashMap<>())));
                    i = pair.getValue();
                    mayUnary = false;
                    numberAfterOp = 1;
                    continue;
                }
            }


            if (exp.charAt(i) == '(') {
                oper.push(exp.charAt(i));
                numberAfterOp = 0;
                mayUnary = true;
                ++brackbalnce;
                continue;
            }

            if (exp.charAt(i) == ')') {
                if (numberAfterOp == 0) { // check that operation is not last meaning symbol in brackets
                    throw new ParsingException("illegal expression");
                }
                if (brackbalnce == 0) {     //checking bracket balance
                    throw new ParsingException("balance error");
                }
                while (oper.peek() != '(') {
                    doOper(numbers, oper.pop());
                }
                oper.pop();
                mayUnary = false;
                --brackbalnce;
                continue;
            }

            if (isOper(exp.charAt(i))) {
                char currentOp = exp.charAt(i);
                if (mayUnary) {
                    switch (currentOp) {
                        case '+':
                            currentOp = '#'; //unaryPlus
                            break;
                        case '-':
                            currentOp = '~'; //unaryMinus
                            break;
                        default:
                            throw new ParsingException("Illegal sequence");
                    }
                } else if (numberAfterOp == 0) { //check that we have not two unary operations
                    throw new ParsingException("double operation");
                }
                while (!oper.empty() && !numbers.empty() && (priority(oper.peek()) >= priority(currentOp)
                        || (currentOp == 'm' || currentOp == 'p') && priority(oper.peek()) > priority(currentOp))) {
                    doOper(numbers, oper.pop());
                }
                oper.push(currentOp);
                switch (currentOp) {
                    case '+':
                        mayUnary = false;
                        break;
                    case '-':
                        mayUnary = false;
                        break;
                    case '#':
                        mayUnary = false;
                        break;
                    case '~':
                        mayUnary = false;
                        break;

                    case '*':
                        mayUnary = true;
                        break;
                    case '/':
                        mayUnary = true;
                        break;
                    default:
                        break;
                }
                numberAfterOp = 0;
                continue;
            }
            if (isDigit(exp.charAt(i))) {
                String num = "";
                num += exp.charAt(i++);
                while (i < exp.length() && isDigit(exp.charAt(i))) {
                    num += exp.charAt(i++);
                }
                --i;
                numbers.push(getVal(num));
                mayUnary = false;
                numberAfterOp = 1;
                continue;
            }
            throw new ParsingException("Illegal symbol"); // if we did not meet familiar symbol
        }

        if (brackbalnce != 0) {
            throw new ParsingException("balance error");
        } //checking bracket balance
        while (!oper.empty()) {
            doOper(numbers, oper.pop());
        }
        if (numbers.size() != 1) {
            throw new ParsingException("illegal expression");
        } //check illegal expression
        return numbers.peek();
    }
}
