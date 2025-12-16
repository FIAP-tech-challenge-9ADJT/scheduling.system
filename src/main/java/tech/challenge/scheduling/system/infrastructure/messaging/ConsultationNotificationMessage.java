package tech.challenge.scheduling.system.infrastructure.messaging;

import java.time.LocalDateTime;

public class ConsultationNotificationMessage {
    private Long consultationId;
    private LocalDateTime dateTime;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private LocalDateTime processAfter;

    public ConsultationNotificationMessage() {}

    public ConsultationNotificationMessage(Long consultationId, LocalDateTime dateTime, Long patientId,
                                           String patientName, String patientEmail) {
        this.consultationId = consultationId;
        this.dateTime = dateTime;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
    }

    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    public LocalDateTime getProcessAfter() { return processAfter; }
    public void setProcessAfter(LocalDateTime processAfter) { this.processAfter = processAfter; }
}
