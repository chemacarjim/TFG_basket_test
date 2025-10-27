package com.chema.backend.service;


public interface ReportService {

    byte[] buildPdf(Long sessionId);

    String buildFileName(Long sessionId);
}
