package net.henryco.blinckserver.mvc.model.entity.relations.group;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.henryco.blinckserver.mvc.model.entity.profile.UserBaseProfile;
import net.henryco.blinckserver.mvc.model.entity.relations.ConversationEntity;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author Henry on 28/08/17.
 */
@Entity @Data
@NoArgsConstructor
public class SubGroupConversation implements ConversationEntity<Long, UserBaseProfile> {


	private @Id @Column(
			name = "id",
			unique = true
	) @GeneratedValue(
			strategy = AUTO
	) Long id;


	private @Column(
			name = "message",
			length = 512
	) String message;


	private @Column(
			name = "time_stamp",
			updatable = false
	) @Temporal(
			TIMESTAMP
	) Date date;


	private @ManyToOne(
			cascade = ALL,
			optional = false
	) @JoinColumn(
			name = "author_id"
	) UserBaseProfile author;


}