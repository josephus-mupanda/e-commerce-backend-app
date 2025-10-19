package com.josephus.e_commerce_backend_app.common.services.impl;

import com.josephus.e_commerce_backend_app.common.repositories.LogRepository;
import com.josephus.e_commerce_backend_app.common.services.LogService;
import com.josephus.e_commerce_backend_app.common.models.LogResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;
    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logAction (String action,String email){
        try {
            LogResult logResult = new LogResult();
            logResult.setUsername(email);
            logResult.setAction(action);
            logResult.setTimestamp(LocalDateTime.now());
            logRepository.save(logResult);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
