// typeof  State{
//     STANDBY,
//     PARSE_CLASS,
//     PARSE_ATTRIBUTE,
//     PARSE_ATTRIBUTE_VALUE,
//     PARSE_STRING
// }

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

//ATTRIBUTE CODE
int DEFAULT_ATTR_NAME_SIZE = 50;
int DEFAULT_ATTR_VALUE_SIZE = 50;
int DEFAULT_ATTR_LIST_SIZE = 50;

int DEFAULT_CLASS_LIST_LENGTH = 500;

typedef struct {
    char* name;
    char* value;
} Attribute;

Attribute* newAttribute(){
    Attribute* result = malloc(sizeof(Attribute));
    result->name = malloc(DEFAULT_ATTR_NAME_SIZE);
    result->value = malloc(DEFAULT_ATTR_VALUE_SIZE);
    return result;
}

void deleteAttribute(Attribute* attr){
    free(attr->name);
    free(attr->value);
    free(attr);
}

//CSS CLASS CODE

typedef struct {
    int numOfAttributes;
    Attribute** attributes;
} CssClass;

CssClass* newCssClass(){
    CssClass* result = malloc(sizeof(CssClass));
    result->attributes = malloc(sizeof(Attribute*)* DEFAULT_ATTR_LIST_SIZE);
    result->numOfAttributes = 0;
    return result;
}

void deleteCssClass(CssClass* class){
    for (int i = 0; i < class->numOfAttributes; i++){
        deleteAttribute(class->attributes[i]);
    }
    free(class->attributes);
    free(class);
}


void addAttributeToClass(CssClass* class, Attribute* attribute){
    class->attributes[class->numOfAttributes] = attribute;
    class->numOfAttributes++;
}

//TEST
void testAddAttributeToClass(){
    printf("Running testAddAttributeToClass...\n");
    CssClass* mClass = newCssClass();
    Attribute* mAttr = newAttribute();
    mAttr->name = "border-width";
    mAttr->value = "3px";
    addAttributeToClass(mClass, mAttr);
    if (mClass->numOfAttributes != 1 ||
        strcmp(mClass->attributes[0]->name, "border-width") != 0 ||
        strcmp(mClass->attributes[0]->value, "3px") != 0){
            printf("testAddAttributeToClass FAILED!\n");
            deleteCssClass(mClass);
            return;
    }
    printf("testAddAttributeToClass SUCCESS!\n");
}

void printCssClass(CssClass* class){
    for (int i = 0; i < class->numOfAttributes; i++){
        printf("%s:%s;\n", class->attributes[i]->name, class->attributes[i]->value);
    }
}

//TEST
void testPrintCssClass(){
    printf("Running testPrintCssClass...\n");
    CssClass* mClass = newCssClass();
    Attribute* mAttr = newAttribute();
    mAttr->name = "border-width";
    mAttr->value = "3px";
    addAttributeToClass(mClass, mAttr);
    printCssClass(mClass);
    printf("testPrintCssClass COMPLETE!\n");
}



int main(int argCount, char** args) {
    testAddAttributeToClass();
    testPrintCssClass();
    CssClass** listOfClasses = malloc(sizeof(CssClass*) * DEFAULT_CLASS_LIST_LENGTH)
    // HashMap<CssClass, Integer> classCount = new HashMap<>();
    // CssParser parser = new CssParser();
    // try{
    //     parser.standby(classCount, scan);
    // } catch (InvalidCssException e){
    //     System.err.println(e.getMessage());
    // }
    // for (CssClass mClass: classCount.keySet()){
    //     System.out.println("{");
    //     for (Attribute attr: mClass.attributes)
    //         System.out.println("  " + attr.name + ": " + attr.value);
    //     System.out.println("}");
    //     System.out.println("Count: " + classCount.get(mClass));
    // }

}

// private static <T> void increaseMapCounter(HashMap<T, Integer> map, T newObj){
//     if (map.containsKey(newObj)){
//         map.put(newObj, map.get(newObj) + 1);
//     } else {
//         map.put(newObj, 1);
//     }
// }


//PARSER FUNCTIONS

// void standby(HashMap<CssClass, Integer> classCount) throws InvalidCssException{
//     if ( (token = getchar())!= EOF){
//         switch(c){
//             case '{':
//                 CssClass* newClass = newCssClass();
//                 parseClass(classCount, sc, newClass);
//                 // increaseMapCounter(classCount, newClass);
//                 standby(classCount, sc);
//                 break;
//             default:
//                 standby(classCount, sc);
//                 return;
//         }
//     }
//     else{
//         return;
//     }
// }

// void parseClass(HashMap<CssClass, Integer> classCount, CssClass currentClass) {
//     if ( (token = getchar())!= EOF){
//         switch(token){
//             case "{":
//                 CssClass* newClass = newCssClass();
//                 parseClass(classCount, sc, newClass);
//                 increaseMapCounter(classCount, newClass);
//                 return;
//             case "}":
//                 return;
//             default:
//                 if (isalpha(token)){
//                     Attribute* newAttribute = newAttribute();
//                     newAttribute.name = newAttribute.name.concat(token);
//                     parseAttribute(classCount, sc, newAttribute);
//                     if (!newAttribute.value.equals(""))
//                         currentClass.attributes.add(newAttribute);
//                     parseClass(classCount, sc, currentClass);
//                 }
//                 if (token.matches(" ") || token.matches("\n")){
//                     parseClass(classCount, sc, currentClass);
//                 }
//         }
//     }
// }

// void parseAttribute(HashMap<CssClass, Integer> classCount, Attribute* currentAttribute) {
//     if ( (token = getchar())!= EOF){
//         switch(token){
//             case ":":
//                 parseAttributeValue(sc, currentAttribute);
//                 return;
//             case "{":
//                 CssClass* newClass = malloc(sizeof(CssClass));
//                 parseClass(classCount, sc, newClass);
//                 increaseMapCounter(classCount, newClass);
//                 return;
//             default:
//                 if (token.matches(" ") || token.matches("\n")){
//                     parseAttribute(classCount, sc, currentAttribute);
//                     return;
//                 }
//                 if (token.matches("[a-zA-Z\\-]")) {
//                     currentAttribute.name = currentAttribute.name.concat(token);
//                     parseAttribute(classCount, sc, currentAttribute);
//                 } else {
//                     throw new InvalidCssException();
//                 }

//         }
//     }
// }

// void parseAttributeValue(Attribute attribute){
//     if ( (token = getchar())!= EOF){
//         switch(token){
//             case ";":
//                 System.out.println(attribute.name + ": " + attribute.value);
//                 return;
//             default:
//                 if (token.matches("\"") || token.matches("'")){
//                     attribute.value = attribute.value.concat(token);
//                     parseString(sc, attribute);
//                     parseAttributeValue(sc, attribute);
//                     return;
//                 }
//                 if (token.matches(" ") || token.matches("\n")){
//                     parseAttributeValue(sc, attribute);
//                     return;
//                 }
//                 attribute.value = attribute.value.concat(token);
//                 parseAttributeValue(sc, attribute);
//         }
//     }
// }

// void parseString(Attribute attribute){
//     if ( (token = getchar())!= EOF){
//         switch(token){
//             default:
//                 if (token.matches("\"") || token.matches("'")){
//                     attribute.value = attribute.value.concat(token);
//                     return;
//                 }
//                 attribute.value = attribute.value.concat(token);
//                 parseString(sc, attribute);
//         }
//     }
// }