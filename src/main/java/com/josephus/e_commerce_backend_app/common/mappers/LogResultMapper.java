package com.josephus.e_commerce_backend_app.common.mappers;

import com.josephus.e_commerce_backend_app.common.models.LogResult;
import com.josephus.e_commerce_backend_app.common.dtos.LogResultDTO;

public final class LogResultMapper {
    private LogResultMapper() {}

    public static LogResult toEntity(LogResultDTO.Input dto) {
        if (dto == null) return null;
        LogResult log = new LogResult();
        log.setAction(dto.action());
        log.setUsername(dto.username());
        log.setTimestamp(dto.timestamp());
        return log;
    }

    public static LogResultDTO.Output toDTO(LogResult log) {
        if (log == null) return null;
        return new LogResultDTO.Output(
                log.getId(),
                log.getAction(),
                log.getUsername(),
                log.getTimestamp()
        );
    }
}
