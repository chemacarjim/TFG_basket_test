package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.TestSession;
import com.chema.backend.repository.TestSessionRepository;
import com.chema.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TestSessionRepository sessionRepo;

    private static final ZoneId ZONE_MADRID = ZoneId.of("Europe/Madrid");
    private static final DateTimeFormatter DATE_NAMING = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE_MADRID);

    @Override
    @Transactional(readOnly = true)
    public byte[] buildPdf(Long sessionId) {
        TestSession s = sessionRepo.findByIdWithResponses(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        // Asegurar datos mínimos: nombre/apellidos podrían venir null
        String name = nvl(s.getParticipantName(), "NA");
        String surname = nvl(s.getParticipantSurname(), "NA");
        Integer total = s.getTotal() != null ? s.getTotal() : (s.getResponses() != null ? s.getResponses().size() : null);
        Integer score = s.getScore();
        String finishedAtStr = s.getFinishedAt() != null ? s.getFinishedAt().toInstant().atZone(ZONE_MADRID).toString() : "—";

        // Generación PDF “dummy” de seguridad para descartar problemas en la librería PDF:
        // Sustituye este bloque por tu generación real (OpenPDF/iText/Flyingsaucer).
        String text = """
                Plataforma de test para toma de decisiones en baloncesto

                Sesión #%d
                Participante: %s %s
                Fecha de realización: %s
                Resultado: %s / %s

                Detalle (tiempo por pregunta):
                %s
                """.formatted(
                s.getId(),
                name, surname,
                finishedAtStr,
                score != null ? score : "—",
                total != null ? total : "—",
                buildTimes(s)
        );

        // DEV: mientras validamos la ruta end-to-end, devolvemos un PDF muy simple con PDFBox o,
        // si no tienes la dependencia, devolvemos un "PDF" plano para probar cabeceras.
        // Para no tocar tu POM ahora, devolvemos bytes de texto como fallback.
        return text.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public String buildFileName(Long sessionId) {
        TestSession s = sessionRepo.findByIdWithResponses(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        String name = sanitize(nvl(s.getParticipantName(), "NA"));
        String surname = sanitize(nvl(s.getParticipantSurname(), "NA"));
        long testId = s.getTest() != null && s.getTest().getId() != null ? s.getTest().getId() : -1L;
        String datePart = s.getFinishedAt() != null ? DATE_NAMING.format(s.getFinishedAt().toInstant().atZone(ZONE_MADRID)) : DATE_NAMING.format(java.time.Instant.now());
        return "Test%s-%s%s-%s.pdf".formatted(testId, name, surname, datePart);
    }

    private static String nvl(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
    private static String sanitize(String s) { return s.replaceAll("[^A-Za-z0-9_-]", ""); }

    private static String buildTimes(TestSession s) {
        if (s.getResponses() == null || s.getResponses().isEmpty()) return "Sin respuestas";
        var sb = new StringBuilder();
        s.getResponses().forEach(r -> {
            String q = r.getQuestion() != null ? nvl(r.getQuestion().getPrompt(), "Pregunta") : "Pregunta";
            Long ms = r.getResponseTimeMs();
            String ok = Boolean.TRUE.equals(r.getIsCorrect()) ? "✓" : "✗";
            sb.append("- ")
              .append(q)
              .append(" | t=")
              .append(ms != null ? ms : "—")
              .append(" ms")
              .append(" | ")
              .append(ok)
              .append("\n");
        });
        return sb.toString();
    }
}
