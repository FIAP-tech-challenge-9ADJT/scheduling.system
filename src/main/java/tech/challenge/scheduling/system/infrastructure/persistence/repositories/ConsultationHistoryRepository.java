package tech.challenge.scheduling.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationHistoryRepository extends JpaRepository<ConsultationHistory, Long> {
	List<ConsultationHistory> findByPatientId(Long patientId);
	
	List<ConsultationHistory> findByDoctorId(Long doctorId);
	
	List<ConsultationHistory> findByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
	
	List<ConsultationHistory> findByPatientIdAndDateTimeAfter(Long patientId, LocalDateTime dateTime);
	
	Optional<ConsultationHistory> findByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);
	
	List<ConsultationHistory> findByDoctorIdAndDateTimeBetween(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("SELECT c FROM ConsultationHistory c WHERE " +
           "(:patientId IS NULL OR c.patientId = :patientId) AND " +
           "(:doctorId IS NULL OR c.doctorId = :doctorId) AND " +
           "(:nurseId IS NULL OR c.nurseId = :nurseId) AND " +
           "(:startDate IS NULL OR c.dateTime >= :startDate) AND " +
           "(:endDate IS NULL OR c.dateTime <= :endDate) AND " +
           "(:status IS NULL OR CAST(c.status AS string) = :status)")
    List<ConsultationHistory> findWithFilters(
        @Param("patientId") Long patientId,
        @Param("doctorId") Long doctorId,
        @Param("nurseId") Long nurseId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("status") String status
    );
}
