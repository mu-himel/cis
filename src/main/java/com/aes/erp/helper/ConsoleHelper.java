package com.aes.erp.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsoleHelper extends BaseHelper {

    public String getService(String commandString) {
        return commandString.split(" ")[0];
    }

    public String getArgument(String commandString){
        String[] splitStrings = commandString.split(" ");
        if (splitStrings.length == 1) {
            return null;
        } else {
            return commandString.substring(commandString.indexOf(" ") + 1);
        }
    }

}