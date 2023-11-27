package com.aes.erp.helper;

import com.aes.erp.exception.AesException;
import com.aes.erp.organogram_system.dto.FSReturnObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Primary
public class BaseHelper {

    public boolean isNameInvalid(String arg) {
        return arg == null || arg.isEmpty() || arg.equals(".") || arg.equals("..");
    }

    public FSReturnObject isNameValid(String name){
        if(name.isBlank()){
//            return new FSReturnObject().setReturnObject(
//                    false,
//                    "Invalid Name",
//                    null
//            );
            throw new AesException("Invalid Name: " + name);

        }else{
            return null;
        }
    }

    public FSReturnObject getFailedReturnObject(String message){
        return new FSReturnObject().setReturnObject(
                false,
                message,
                null
        );
    }

    public String getSubstringFromString(String sourceString, String offsetString){
        return sourceString.substring(
                sourceString.indexOf(offsetString)
                +
                offsetString.length()
        );
    }

    public int convertStringToInt(String arg) {//get this into a helper function for remove methods
        int val = -1;
        try{
            val = Integer.parseInt(arg);
        }
        catch (Exception ex){
            System.out.println("[ERROR]: Not a number");
        }
        return val;
    }
    public ArrayList<String> getUpdateArgs(String arg) {
        List<String> lis = (List<String>) Arrays.asList(arg.split(" ", 2));
        lis.set(1, lis.get(1).trim());
        //List<String> lis = Arrays.asList(arg.split("\\W+", 2));
        ArrayList<String> tmp = new ArrayList<String>(lis);
        return tmp;
    }

    //convert argument to Arraylist of strings
    public ArrayList<String> convertAllArgsToListOfArgs(String arg){
        List<String> lis = (List<String>) Arrays.asList(arg.split("/"));
        ArrayList<String> tmp = new ArrayList<String>(lis);
        return tmp;
    }

    public ArrayList<String> getPermissionTree(String arg, String type) {
        List<String> lis = (List<String>) Arrays.asList(arg.split("//"));
        ArrayList<String> tmp = new ArrayList<String>(lis);
        if(tmp.size()==2){
            if(type.equals("department")){
                return convertAllArgsToListOfArgs(tmp.get(0));//get department tree
            }else if(type.equals("role")){
                return convertAllArgsToListOfArgs(tmp.get(1));//get role tree
            }

        }
        return null;
    }

}
