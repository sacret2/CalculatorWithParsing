package com.mcompany;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class KalkulatorJPanel extends JPanel {
    private char mode='r';  // one of values:
                    // 'r' wpisywanie pierwszej liczby;
                    // '+' wpis drugiej liczby dodawanie;
                    // '-' wpis drugiej liczby odejmowanie;
                    // '*' wpis drugiej liczby mnozenie;
                    // '/' wpis drugiej liczby dzielenie;
    private boolean eraseScreen = true;
    private double firstInput = 0;
    private JTextField screenTextField;
    private JButton[] digitsButtons = new JButton[10];
    private JButton plus, minus, times, dot, divide, equals;
    private HashSet<Character> additiveOps = new HashSet<Character>(Arrays.asList(new Character[] {'+','-'}));
    private HashSet<Character> multiplicativeOps = new HashSet<Character>(Arrays.asList(new Character[] {'*','/'}));


    public KalkulatorJPanel() {
        setLayout(null);
        createAndAddJTextFields();
        createAndAddJButtons();
    }

    private class Res{
        double val;
        Res(double val){
            this.val = val;
        }
        Res subtract(double sub){
            this.val -= sub;
            return this;
        }
        Res add(double addition){
            this.val += addition;
            return this;
        }
        Res times(double val){
            this.val *= val;
            return this;
        }
        Res divide(double val){        // check if divisor != 0 outside
            this.val /= val;
            return this;
        }
    }

    private String evaluateExpression(String expression){
        char[] expr = expression.toCharArray();
        Res res = new Res(0);
        int ind = 0;

        try {
            addSimpleExpressions(expr, ind,  res);
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return "" + res.val;
    }

    /* TODO:version of addSimpleExpressions with handling excessive ')'  (not complete, not obvious, not now)
            ')' is an escape character for addSimpleExpressions method
            to create its version with handling excessive ')':
            store current nb_of_opened_brackets  pass it as argument of addSimpleExpressions,
                exit only if that nb is >0
            evalSimpleExpression should store nb_of_opened_brackets as well to pass it to its addSimpleExpressions calls
            the following unsuccessful attempt has been made:
                       if (nb_of_opened_brackets > 0)
                            return ind;
                        else {
                            while (ind < expr.length && expr[ind] == ')')
                                ind = addOneAndSkipSpaces(expr, ind);
                        }
                        //Problem of this solution: assessment of when to return from evalSimpleExpression i.e.
                        //  to neglect the ')' a method requires knowledge of nb_of_opened_brackets of all methods lower in the call stack
                        //  not only the calling method.
                        //  I.e. a List<Integer> list_of_nb_of_opened_brackets is required as an argument instead of int
    */
    private int addSimpleExpressions (char[] expr, int ind, Res res) throws Exception{
        char op = '+';
        while (ind < expr.length) {
            Res b = new Res(1);
            ind = evalSimpleExpression(expr, ind, b);
            switch (op) {
                case '+':
                    res.add(b.val);
                    break;
                case '-':
                    res.subtract(b.val);
            }
            ind = skipSpaces(expr,ind);                         // get the next operator
            if(ind >= expr.length)
                return ind;
            op = expr[ind];
            ind = addOneAndSkipSpaces(expr,ind);
            if( !additiveOps.contains(op))                      // return if op == ')'
                return ind;
        }
        return ind;
    }

    /*
    Args:
        ind - index of the start of expression
        a - object which after the execution contains the value of a simple expression + shiftValue,
            passed to the method as a = new Res(shiftValue);

    Returns:
        the index of the first significant character after the expression i.e. '+','-'
        or
        expr.length

    Def: Significant characters are any characters other than " "
    Def: Simple expression is a contiguous series of signs starting with:
        ''(no sign) and ending with '+','-' or the end of expr String
        or
        starting with '(' and ending with its respective closing ')'
        composed of multiplications and divisions
        of floating point numbers and simple expressions enclosed by round brackets

    Examples of simple expressions (correct input):
        expr1   3/-3
        expr2   (6 + 2) 2.5 ( 2+ 2) 0.4
        expr3:  -- -- -2 ( - -- - 30 + -- -- 2 * -- - 3)
    Examples of wrong input (not simple expressions):
        3//3                    // double operators
        ()                      // lack of expression inside brackets
        (2))*5                  // lack of opening bracket returns expression before excessive ')'
    Special Examples of correct input:
        ((((4+5) +2             // lack of closing brackets at the end of expr String

     */
    private int evalSimpleExpression(char[] expr, int ind, Res a) throws Exception{
        do {
            char op = '*';
            ind = skipSpaces(expr,ind);
            while (ind < expr.length) {
                double b;
                int sign = 1;
                while (expr[ind] == '-') {
                    sign = -sign;
                    ind = addOneAndSkipSpaces(expr,ind);
                    if (ind >= expr.length)
                        throw new Exception("_digit required, found: null");
                }
                char c = expr[ind];
                if (c == '(') {
                    Res res = new Res(0);           // b = evaluate expr in brackets
                    ind = addSimpleExpressions(expr, ind + 1, res);
                    b = res.val * sign;
                } else {
                    int start = ind;                    // b = read next nb
                    if (ind >= expr.length || !isDigit(expr[ind]))
                        throw new Exception("_digit required, found: " + expr[ind]);
                    while (ind < expr.length && isDigit(expr[ind])) {
                        ind++;
                    }
                    b = parseDouble(expr, start, ind) * sign;
                }
                switch (op) {
                    case '*':
                        a.times(b);
                        break;
                    case '/':
                        if (b == 0)
                            throw new Exception("_division by zero not possible");
                        a.divide(b);
                }
                ind = skipSpaces(expr,ind);
                if (ind >= expr.length)
                    return ind;
                op = expr[ind];
                if( op == '(' || isDigit(expr[ind])) {
                    break;
                }
                if (!multiplicativeOps.contains(op))
                    return ind;
                ind = addOneAndSkipSpaces(expr,ind);
            }
        } while (ind< expr.length && (expr[ind] == '(' || isDigit(expr[ind])) );
        return ind;
    }

    private double parseDouble(char[] expr, int start, int end){
        StringBuilder sb = new StringBuilder();
        while (start<end)
            sb.append(expr[start++]);
        return Double.parseDouble(sb.toString());
    }

    private int skipSpaces(char[] expr, int ind){
        while (ind < expr.length && expr[ind] == ' ') {
            ind++;
        }
        return ind;
    }
    private int addOneAndSkipSpaces(char[] expr, int ind){
        do
            ind++;
        while (ind < expr.length && expr[ind] == ' ');
        return ind;
    }

////  correct but unused method
//    private void performOperation(Res a, char op, Res b) throws Exception{
//        switch (op) {
//            case '*':
//                a.times(b);
//                break;
//            case '/':
//                if (b.val == 0)
//                    throw new Exception("_division by zero not possible");
//                a.divide(b);
//                break;
//            case '+':
//                a.add(b.val);
//                break;
//            case '-':
//                a.subtract(b.val);
//                break;
//                default:
//                    throw new Exception("_expected operator, found: "+ op);
//        }
//    }

    private boolean isDigit(char c){       // includes dot and space characters:'.', ' '
        int cInt = (int) c;
        return (cInt >= 48 && cInt <= 57) || cInt ==32 || cInt == 46; 
    }

    class DigitInput implements ActionListener {
        int dig;
        public DigitInput(int dig)
        {
            this.dig = dig;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(eraseScreen){
                screenTextField.setText("");
                eraseScreen = false;
            }
            screenTextField.setText(screenTextField.getText()+dig);
        }
    }

    class Operation implements ActionListener {
        char operation;
        public Operation(char operation)
        {
            this.operation = operation;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(mode != 'r')
            {
                String inp = screenTextField.getText();
                if (!(inp.length() == 1 && inp.charAt(0) =='-'))
                    new EqualsActionListener().actionPerformed(e);
            }
            String firstInputString = screenTextField.getText();
            if (firstInputString.charAt(0) != '_'){
                firstInput = Double.parseDouble(
                        evaluateExpression(screenTextField.getText()));
                System.out.println("first input == " + firstInput);
                screenTextField.setText("");
                mode = this.operation;
            }
        }
    }

    class EqualsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            double result = 0;
            String secondInputString = evaluateExpression(screenTextField.getText());
            System.out.println("second input == " + secondInputString);
            if(secondInputString.charAt(0) == '_'){
                screenTextField.setText(secondInputString);
                mode = 'r';
                return;
            }
            double secondInput = Double.parseDouble(secondInputString);
            if(mode == 'r'){
                firstInput = secondInput;
                screenTextField.setText(""+firstInput);
                return;
            }
            if(mode == '+')
                result = firstInput + secondInput;
            if(mode == '-')
                result = firstInput - secondInput;
            if(mode == '*')
                result = firstInput * secondInput;
            if(mode == '/') {
                if (secondInput !=0)
                    result = firstInput / secondInput;
                else{
                    screenTextField.setText("_division by 0 impossible");
                    mode = 'r';
                    return;
                }
            }
            System.out.println("mode:"+mode);
            eraseScreen = true;
            screenTextField.setText(""+result);
            firstInput = result;
            mode = 'r';
        }
    }

    private void createAndAddJButtons() {
        for(int digit = 1; digit<= 9; digit++){
            JButton digitJButton = new JButton();
            this.digitsButtons[digit]=digitJButton;

            digitJButton.setText("" + digit);
            digitJButton.addActionListener( new DigitInput(digit) );
            int i = (digit -1) / 3;
            int j = (digit -1) % 3;
            int xCoordinate = 10 + 50 * j;
            int yCoordinate = 155 - 50 * i;
            digitJButton.setBounds(xCoordinate,yCoordinate,50,50);
        }

        this.plus = new JButton();
        this.plus.setText("+");
        this.plus.setBounds(160,55,50,50);
        this.plus.addActionListener(new Operation('+'));

        this.minus = new JButton();
        this.minus.setText("-");
        this.minus.setBounds(160,105,50,50);
        this.minus.addActionListener(new Operation('-'));

        this.times = new JButton();
        this.times.setText("*");
        this.times.setBounds(160,155,50,50);
        this.times.addActionListener(new Operation('*'));

        JButton zero = new JButton();
        digitsButtons[0] = zero;
        zero.addActionListener( new DigitInput(0));
        zero.setText("0");
        zero.setBounds(10,205, 50, 50);

        this.dot = new JButton();
        this.dot.setText(".");
        this.dot.setBounds(60,205,50,50);
        this.dot.addActionListener(new DigitInput('.'));

        this.divide = new JButton();
        this.divide.setText("/");
        this.divide.setBounds(110,205,50,50);
        this.divide.addActionListener(new Operation('/'));

        this.equals = new JButton();
        this.equals.setText("=");
        this.equals.setBounds(160,205,50,50);
        this.equals.addActionListener(new EqualsActionListener());

        for(JButton jb : this.digitsButtons)
            add(jb);
        add(this.plus);
        add(this.minus);
        add(this.times);
        add(zero);
        add(this.dot);
        add(this.divide);
        add(this.equals);
    }

    private void createAndAddJTextFields() {
        this.screenTextField = new JTextField();
        this.screenTextField.setText("0");
        this.screenTextField.setBounds(10,10,201,40);

        add(screenTextField);
    }
}