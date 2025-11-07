package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.ResponseEntity;
import com.chema.backend.domain.entity.TestSession;
import com.chema.backend.repository.TestSessionRepository;
import com.chema.backend.service.ReportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TestSessionRepository sessionRepo;

    private static final ZoneId ZONE_MADRID = ZoneId.of("Europe/Madrid");
    private static final DateTimeFormatter DATE_NAMING =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE_MADRID);

    @Override
    @Transactional(readOnly = true)
    public byte[] buildPdf(Long sessionId) {
        TestSession s = sessionRepo.findByIdWithResponses(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        // Preparar información básica
        String name = nvl(s.getParticipantName(), "NA");
        String surname = nvl(s.getParticipantSurname(), "NA");
        Integer total = s.getTotal() != null
                ? s.getTotal()
                : (s.getResponses() != null ? s.getResponses().size() : null);
        Integer score = s.getScore();
        String finishedAtStr = s.getFinishedAt() != null
                ? s.getFinishedAt().toInstant().atZone(ZONE_MADRID).toString()
                : "—";

        // Crear un documento PDF en memoria
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, bos);
            document.open();

            // Título
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Plataforma de test: toma de decisiones en baloncesto", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);

            // Datos generales
            Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 12);
            document.add(new Paragraph(String.format("Test: %s", s.getTest().getTitle()), bold));
            document.add(new Paragraph(String.format("Participante: %s %s", name, surname), normal));
            document.add(new Paragraph(String.format("Fecha: %s", finishedAtStr), normal));
            document.add(new Paragraph(String.format("Resultado: %s / %s",
                    score != null ? score : "—",
                    total != null ? total : "—"), normal));
            document.add(Chunk.NEWLINE);

            // Tabla detallada por pregunta
            PdfPTable table = new PdfPTable(3); // columnas: Pregunta, Tiempo (ms), Correcta
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.addCell(new Phrase("Pregunta", bold));
            table.addCell(new Phrase("Tiempo (ms)", bold));
            table.addCell(new Phrase("Correcta", bold));

            if (s.getResponses() != null && !s.getResponses().isEmpty()) {
                for (ResponseEntity r : s.getResponses()) {
                    String q = r.getQuestion() != null
                            ? nvl(r.getQuestion().getPrompt(), "Pregunta")
                            : "Pregunta";
                    Long ms = r.getResponseTimeMs();
                    String ok = Boolean.TRUE.equals(r.getIsCorrect()) ? "Acertada" : "Fallada";

                    table.addCell(new Phrase(q, normal));
                    table.addCell(new Phrase(ms != null ? ms.toString() : "—", normal));
                    table.addCell(new Phrase(ok, normal));
                }
            } else {
                table.addCell(new Phrase("Sin respuestas registradas", normal));
                table.addCell("");
                table.addCell("");
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            // Si fallase la generación, devuelve un mensaje de error como PDF simple
            String fallback = "Error generando el informe PDF: " + e.getMessage();
            return fallback.getBytes();
        }

        return bos.toByteArray();
    }

    @Override
    @Transactional(readOnly = true)
    public String buildFileName(Long sessionId) {
        TestSession s = sessionRepo.findByIdWithResponses(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        String name = sanitize(nvl(s.getParticipantName(), "NA"));
        String surname = sanitize(nvl(s.getParticipantSurname(), "NA"));
        long testId = s.getTest() != null && s.getTest().getId() != null ? s.getTest().getId() : -1L;
        String datePart = s.getFinishedAt() != null
                ? DATE_NAMING.format(s.getFinishedAt().toInstant().atZone(ZONE_MADRID))
                : DATE_NAMING.format(java.time.Instant.now());
        return "Test%s-%s%s-%s.pdf".formatted(testId, name, surname, datePart);
    }

    private static String nvl(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
    private static String sanitize(String s) { return s.replaceAll("[^A-Za-z0-9_-]", ""); }
}
