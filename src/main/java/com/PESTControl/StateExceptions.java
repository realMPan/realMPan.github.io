package com.PESTControl;

/**
 * A static class containing all possible exceptions that can be thrown by PESTControl. Also contains constructable, multipurpose exceptions
 */
public class StateExceptions {
    public static Exception NonExistentClassVarException(String libraryClass, String VarName, String className){
        return new Exception("The variable "+VarName+" in the" + libraryClass+" named '"+className+"' recieved an attempted change despite being unused. If you plan on using this variable, make sure that it is properly initialized in the constructor for "+className);
    }
    
}
