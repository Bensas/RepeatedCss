package com.bensas;

import sun.jvm.hotspot.utilities.Assert;
//ACID: Atomicidad, consistencia, aislamiento, durabilidad

import java.util.*;

public class CssParser {
    String token;
    Scanner sc;

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
                stringRepresentation.concat(attr.name).concat(attr.value);
            }
            return Objects.hash(stringRepresentation);
        }
    }

    public static class InvalidCssException extends Exception{}

//    public static void test1(){
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
//        Scanner scan = new Scanner(input);
//        scan.useDelimiter("");
//        try{
//            standby(classCount, scan);
//        } catch (InvalidCssException e){
//            System.err.println(e.getMessage());
//        }
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
//        test1();
        HashMap<CssClass, Integer> classCount = new HashMap<>();
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("");
        CssParser parser = new CssParser();
        try{
            parser.standby(classCount, scan);
        } catch (InvalidCssException e){
            System.err.println(e.getMessage());
        }
        for (CssClass mClass: classCount.keySet()){
            System.out.println("{");
            for (Attribute attr: mClass.attributes)
                System.out.println("  " + attr.name + ": " + attr.value);
            System.out.println("}");
            System.out.println("Count: " + classCount.get(mClass));
        }

    }

//    private static void parseFileIterative(HashMap<CssClass, Integer> classCount, Scanner sc) throws InvalidCssException{
//        State currentState = State.STANDBY;
//        Stack<CssClass> classStack = new Stack<>();
//        Stack<Attribute> attributeStack = new Stack<>();
//
//        sc.useDelimiter("");
//        while (sc.hasNext()){
//            String token = sc.next();
//            switch (currentState){
//                case STANDBY:
//                    switch(token){
//                        case '{':
//                            CssClass newClass = new CssClass();
//                            classStack.push(newClass);
//                            currentState = State.PARSE_CLASS;
//                            break;
//                        default:
//                    }
//                    break;
//                case PARSE_CLASS:
//                    switch(token){
//                        case "{":
//                            CssClass newClass = new CssClass();
//                            classStack.push(newClass);
//                            break;
//                        case "}":
//                            increaseMapCounter(classCount, classStack.pop());
//                            currentState = State.STANDBY;
//                            break;
//                        default:
//                            if (token.matches("[a-zA-Z]")){
//                                Attribute newAttribute = new Attribute();
//                                newAttribute.name = newAttribute.name.concat(token);
//                                currentState = State.PARSE_ATTRIBUTE;
//                                break;
////                                parseAttribute(classCount, sc, newAttribute);
//                                if (!newAttribute.value.equals(""))
//                                    currentClass.attributes.add(newAttribute);
//                                parseClass(classCount, sc, currentClass);
//                            }
//                            if (token.matches(" ") || token.matches("\n")){
//                                parseClass(classCount, sc, currentClass);
//                            }
//                    }
//                    break;
//                case PARSE_ATTRIBUTE:
//                    switch(token){
//                        case ":":
//                            currentState = State.PARSE_ATTRIBUTE_VALUE;
//                            break;
////                            parseAttributeValue(sc, currentAttribute);
//                            return;
//                        case "{":
//                            CssClass newClass = new CssClass();
//                            classStack.push(newClass);
//                            currentState = State.PARSE_CLASS;
//                            increaseMapCounter(classCount, newClass);
//                            return;
//                        default:
//                            if (token.matches(" ") || token.matches("\n")){
//                                parseAttribute(classCount, sc, currentAttribute);
//                                return;
//                            }
//                            if (token.matches("[a-zA-Z\\-]")) {
//                                currentAttribute.name = currentAttribute.name.concat(token);
//                                parseAttribute(classCount, sc, currentAttribute);
//                            } else {
//                                throw new InvalidCssException();
//                            }
//
//                    }
//                    break;
//                case PARSE_ATTRIBUTE_VALUE:
//                    break;
//                case PARSE_STRING:
//                    break;
//
//            }
//        }
//    }

    private static <T> void increaseMapCounter(HashMap<T, Integer> map, T newObj){
        if (map.containsKey(newObj)){
            map.put(newObj, map.get(newObj) + 1);
        } else {
            map.put(newObj, 1);
        }
    }

    private void standby(HashMap<CssClass, Integer> classCount, Scanner sc) throws InvalidCssException{
        if (sc.hasNext()){
            token = sc.next();
            switch(token){
                case "{":
                    CssClass newClass = new CssClass();
                    parseClass(classCount, sc, newClass);
                    increaseMapCounter(classCount, newClass);
                    standby(classCount, sc);
                    break;
                default:
                    standby(classCount, sc);
                    return;
            }
        }
        else{
            return;
        }
    }

    private void parseClass(HashMap<CssClass, Integer> classCount, Scanner sc, CssClass currentClass) throws InvalidCssException{
        if (sc.hasNext()){
            token = sc.next();
            switch(token){
                case "{":
                    CssClass newClass = new CssClass();
                    parseClass(classCount, sc, newClass);
                    increaseMapCounter(classCount, newClass);
                    return;
                case "}":
                    return;
                default:
                    if (token.matches("[a-zA-Z]")){
                        Attribute newAttribute = new Attribute();
                        newAttribute.name = newAttribute.name.concat(token);
                        parseAttribute(classCount, sc, newAttribute);
                        if (!newAttribute.value.equals(""))
                            currentClass.attributes.add(newAttribute);
                        parseClass(classCount, sc, currentClass);
                    }
                    if (token.matches(" ") || token.matches("\n")){
                        parseClass(classCount, sc, currentClass);
                    }
            }
        }
    }

    private void parseAttribute(HashMap<CssClass, Integer> classCount, Scanner sc, Attribute currentAttribute) throws InvalidCssException {
        if (sc.hasNext()){
            token = sc.next();
            switch(token){
                case ":":
                    parseAttributeValue(sc, currentAttribute);
                    return;
                case "{":
                    CssClass newClass = new CssClass();
                    parseClass(classCount, sc, newClass);
                    increaseMapCounter(classCount, newClass);
                    return;
                default:
                    if (token.matches(" ") || token.matches("\n")){
                        parseAttribute(classCount, sc, currentAttribute);
                        return;
                    }
                    if (token.matches("[a-zA-Z\\-]")) {
                        currentAttribute.name = currentAttribute.name.concat(token);
                        parseAttribute(classCount, sc, currentAttribute);
                    } else {
                        throw new InvalidCssException();
                    }

            }
        }
    }

    private void parseAttributeValue(Scanner sc, Attribute attribute){
        if (sc.hasNext()){
            token = sc.next();
            switch(token){
                case ";":
                    System.out.println(attribute.name + ": " + attribute.value);
                    return;
                default:
                    if (token.matches("\"") || token.matches("'")){
                        attribute.value = attribute.value.concat(token);
                        parseString(sc, attribute);
                        parseAttributeValue(sc, attribute);
                        return;
                    }
                    if (token.matches(" ") || token.matches("\n")){
                        parseAttributeValue(sc, attribute);
                        return;
                    }
                    attribute.value = attribute.value.concat(token);
                    parseAttributeValue(sc, attribute);
            }
        }
    }

    private void parseString(Scanner sc, Attribute attribute){
        if (sc.hasNext()){
            token = sc.next();
            switch(token){
                default:
                    if (token.matches("\"") || token.matches("'")){
                        attribute.value = attribute.value.concat(token);
                        return;
                    }
                    attribute.value = attribute.value.concat(token);
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