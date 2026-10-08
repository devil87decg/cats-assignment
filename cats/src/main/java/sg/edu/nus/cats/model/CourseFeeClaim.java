package sg.edu.nus.cats.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class CourseFeeClaim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Each course application can have at most one reimbursement claim.
	// optional = false means every claim must belong to a course application.
	@OneToOne(optional = false)
	@JoinColumn(name = "application_id", nullable = false, unique = true)
	private CourseApplication application;

	// Amount claimed for reimbursement.
	// This will be copied from the fee stored in the course application.
	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal amount;

	// Date and time when the employee submitted the claim.
	@Column(nullable = false)
	private LocalDateTime claimDate;

	// Paths to the supporting documents uploaded by the employee.
	@Column(nullable = false)
	private String receiptPath;

	@Column(nullable = false)
	private String certificatePath;

	// New claims begin as PENDING.
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ClaimStatus status = ClaimStatus.PENDING;

	// Manager's reason remains empty until the claim is approved or rejected.
	private String managerReason;

	// One manager may decide many reimbursement claims.
	// This remains empty while the claim is pending.
	@ManyToOne
	@JoinColumn(name = "decided_by")
	private Employee decidedBy;

	// Remains empty until the manager makes a decision.
	private LocalDateTime decisionDate;
}