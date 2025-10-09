package com.chema.backend.service;

import com.chema.backend.dto.CreateSessionResponseDto;
import com.chema.backend.dto.FinishSessionResponseDto;
import com.chema.backend.dto.SubmitResponsesRequestDto;

public interface SessionService {
    CreateSessionResponseDto createSession(Long testId, String anonUserCode);
    void submitResponses(Long sessionId, SubmitResponsesRequestDto request);
    FinishSessionResponseDto finishSession(Long sessionId);
}
