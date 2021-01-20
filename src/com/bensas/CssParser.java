package com.bensas;

import sun.jvm.hotspot.utilities.Assert;
//ACID: Atomicidad, consistencia, aislamiento, durabilidad

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CssParser {
    int token;
    BufferedReader sc;

    private enum State{
        STANDBY,
        PARSE_CLASS,
        PARSE_ATTRIBUTE,
        PARSE_ATTRIBUTE_VALUE,
        PARSE_STRING
    }

    private static class Attribute{
        public String name;
        public String value;

        public Attribute(){
            this.name = "";
            this.value = "";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Attribute){
                return name.equals(((Attribute) obj).name) && value.equals(((Attribute) obj).value);
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name.concat(value));
        }
    }

    private static class CssClass {
        public HashSet<Attribute> attributes;
        public CssClass(){
            this.attributes = new HashSet<>();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CssClass){
                return ((CssClass) obj).attributes.equals(this.attributes);
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            String stringRepresentation = new String();
            for (Attribute attr: attributes){
                stringRepresentation = stringRepresentation.concat(attr.name).concat(":").concat(attr.value);
            }
            return Objects.hash(stringRepresentation);
        }
    }

    public static class InvalidCssException extends Exception{}

//    public void test1(){
//        System.out.println("Running test 1...");
//        HashMap<CssClass, Integer> classCount = new HashMap<>();
//        String input = "{\n" +
//                "display: flex;\n" +
//                "\n" +
//                "flex-direction: column;\n" +
//                "    {\n" +
//                "        src: url('http://hola.com.ar/?href=303;23');\n" +
//                "\n" +
//                "    }\n" +
//                "}" +
//                "{" +
//                "src: url('http://hola.com.ar/?href=303;23');}\n\n\n";
//        BufferedReader scan = new BufferedReader(new
//                InputStreamReader(System.in));
//        standby(classCount, scan);
//
//        Assert.that(classCount.keySet().size() == 2, "Two different classes should have been found!");
//        System.out.println("Test 1 ran successfully");
//    }

    public static<T> void printSet(Set<T> input){
        for (T elem: input){
            System.out.println(((Attribute)((CssClass)elem).attributes.toArray()[0]).name + ": " + ((Attribute)((CssClass)elem).attributes.toArray()[0]).value);
        }
    }

    public static void main(String[] args) {
        HashMap<CssClass, Integer> classCount = new HashMap<>();

        BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
        CssParser parser = new CssParser();
        try{
            parser.standby(classCount, scan);
        } catch (InvalidCssException e){
            System.err.println("Invalid Css!");
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
        for (CssClass mClass: classCount.keySet()){
            System.out.println("{");
            for (Attribute attr: mClass.attributes)
                System.out.println("  " + attr.name + ": " + attr.value);
            System.out.println("}");
            System.out.println("Count: " + classCount.get(mClass));
        }
        Object[] counts = classCount.keySet().toArray();
        Arrays.sort(counts,(a, b) -> classCount.get(a).compareTo(classCount.get(b)));
        for (Object key: counts){
            for (Attribute att: ((CssClass)key).attributes){
                System.out.print(att.name + ":" + att.value + ", ");
            }
            System.out.println("|| Count: " + classCount.get(key));
        }
//        int maxRepeat = counts[0];
//        System.out.println("Max repeated:" + maxRepeat);

    }

    private static <T> void increaseMapCounter(HashMap<T, Integer> map, T newObj){
        if (map.containsKey(newObj)){
            map.put(newObj, map.get(newObj) + 1);
//            printSet(((CssClass)newObj).attributes);
        } else {
            map.put(newObj, 1);
        }
    }

    private void standby(HashMap<CssClass, Integer> classCount, BufferedReader sc) throws InvalidCssException, IOException{
        while ((token = sc.read()) != -1){
            if ((char)token == '{'){
                CssClass newClass = new CssClass();
                parseClass(classCount, sc, newClass);
                increaseMapCounter(classCount, newClass);
            }
        }
        return;
    }

    private void parseClass(HashMap<CssClass, Integer> classCount, BufferedReader sc, CssClass currentClass) throws InvalidCssException, IOException{
        if ((token = sc.read()) != -1){
            switch((char)token){
                case '{':
                    CssClass newClass = new CssClass();
                    parseClass(classCount, sc, newClass);
                    increaseMapCounter(classCount, newClass);
                    return;
                case '}':
                    return;
                default:
                    if (Character.isLetter(token)){
                        Attribute newAttribute = new Attribute();
                        newAttribute.name = newAttribute.name + (char)token;
                        parseAttribute(classCount, sc, newAttribute);
                        if (!newAttribute.value.equals(""))
                            currentClass.attributes.add(newAttribute);
                        parseClass(classCount, sc, currentClass);
                    }
                    if (token == ' ' || token == '\n'){
                        parseClass(classCount, sc, currentClass);
                    }
            }
        }
    }

    private void parseAttribute(HashMap<CssClass, Integer> classCount, BufferedReader sc, Attribute currentAttribute) throws InvalidCssException, IOException {
        if ((token = sc.read()) != -1){
            switch((char)token){
                case ':':
                    parseAttributeValue(sc, currentAttribute);
                    return;
                case '{':
                    CssClass newClass = new CssClass();
                    parseClass(classCount, sc, newClass);
                    increaseMapCounter(classCount, newClass);
                    return;
                default:
                    if (token == ' ' || token== '\n'){
                        parseAttribute(classCount, sc, currentAttribute);
                        return;
                    }
//                    if (Character.isLetter(token) || Character.isDigit(token) || token == '-') {
                        currentAttribute.name = currentAttribute.name + (char)token;
                        parseAttribute(classCount, sc, currentAttribute);
//                    } else {
//                        throw new InvalidCssException();
//                    }

            }
        }
    }

    private void parseAttributeValue(BufferedReader sc, Attribute attribute) throws IOException{
        if ((token = sc.read()) != -1){
            switch((char)token){
                case ';':
                    System.out.println(attribute.name + ": " + attribute.value);
                    return;
                default:
                    if (token == '"' || token == '\''){
                        attribute.value = attribute.value + ((char)token);
                        parseString(sc, attribute);
                        parseAttributeValue(sc, attribute);
                        return;
                    }
                    if (token == ' ' || token== '\n'){
                        parseAttributeValue(sc, attribute);
                        return;
                    }
                    attribute.value = attribute.value + ((char)token);
                    parseAttributeValue(sc, attribute);
            }
        }
    }

    private void parseString(BufferedReader sc, Attribute attribute) throws IOException {
        if ((token = sc.read()) != -1){
            switch((char)token){
                default:
                    if (token == '"' || token == '\''){
                        attribute.value = attribute.value + ((char)token);
                        return;
                    }
                    attribute.value = attribute.value + ((char)token);
                    parseString(sc, attribute);
            }
        }
    }

    // States: standby, parsingClass, parsingAttribute, parsingValue
}

//standby <-> standby(' ', '\n')   -> parseClass('{')  -> error('}')
//parseClass <-> (' ', '\n')  -> standby('}')   -> parseAttributeName([a-zA-Z])
//parseAttributeName  <-> ([a-zA-Z]) -> error([0-9]) -> parseAttributeValue(':')
//parseAttributeValue <-> (*) -> parseClass(';') ->parseStringValue('\'')
//parseStringValue <-> (*) ->parseAttributeValue('\'')

/*
{
display: flex;

flex-direction: column;
    {
        src: url('http://hola.com.ar/?href=303;23');

    }
}


 */