package com.wanted.cookielms.global.logging.service;

import com.wanted.cookielms.global.logging.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.repository.BusinessServiceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceLogService {

    private final BusinessServiceLogRepository businessServiceLogRepository;

    @Async
    public void saveAsync(BusinessServiceLogEntity log) {
        businessServiceLogRepository.save(log);
    }
}