package net.henryco.blinckserver.mvc.model.entity.infrastructure;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.henryco.blinckserver.mvc.model.entity.profile.core.UserCoreProfile;

import javax.persistence.*;

/**
 * @author Henry on 28/08/17.
 */
@Entity @Data
@NoArgsConstructor
public class ReportList {

	private @Id @Column(
			unique = true
	) @GeneratedValue(
			strategy = GenerationType.AUTO
	) long id;


	private @ManyToOne(
			cascade = CascadeType.ALL,
			optional = false,
			targetEntity = UserCoreProfile.class
	) @JoinColumn(
			name = "reporter_id"
	) long reporterId;


	private @ManyToOne(
			cascade = CascadeType.ALL,
			optional = false,
			targetEntity = UserCoreProfile.class
	) @JoinColumn(
			name = "reported_id"
	) long reportedId;


	private @Column(
			name = "reason"
	) String reason;
}