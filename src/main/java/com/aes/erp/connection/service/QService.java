package com.aes.erp.connection.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.exception.AesException;

@Service
public class QService {
    
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Optional<?> runCommand(String msg){
        try{
        
        Query q = entityManager.createNativeQuery(msg);
        if(msg.toUpperCase().startsWith("SELECT") || msg.toUpperCase().startsWith("SHOW")){
            return Optional.of(q.getResultList());
        }else{
           
            Integer executed = q.executeUpdate();
            return Optional.of(executed+" rows changes");
        }

        }catch(Exception e){
            throw new AesException(e.getMessage());
        }finally{
           
        }
        
    }
}
