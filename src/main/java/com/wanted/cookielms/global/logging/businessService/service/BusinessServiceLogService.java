package com.wanted.cookielms.global.logging.businessService.service;

import com.wanted.cookielms.global.logging.businessService.entity.BusinessServiceLogEntity;
import com.wanted.cookielms.global.logging.businessService.repository.BusinessServiceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessServiceLogService {

    private final BusinessServiceLogRepository businessServiceLogRepository;

    @Async
    public void saveBusinessServiceLog(BusinessServiceLogEntity log) {
        businessServiceLogRepository.save(log);
    }
}