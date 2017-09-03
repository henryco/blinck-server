package net.henryco.blinckserver.mvc.model.dao.relation.core.imp;

import net.henryco.blinckserver.mvc.model.dao.relation.core.FriendshipDao;
import net.henryco.blinckserver.mvc.model.entity.relation.core.Friendship;
import net.henryco.blinckserver.mvc.model.repository.relation.core.FriendshipRepository;
import net.henryco.blinckserver.util.dao.repo.BlinckRepositoryProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Henry on 29/08/17.
 */
@Repository
public class FriendshipDaoImp extends
		BlinckRepositoryProvider<Friendship, Long>
		implements FriendshipDao {


	public FriendshipDaoImp(FriendshipRepository repository) {
		super(repository);
	}

	private FriendshipRepository getRepository() {
		return provideRepository();
	}

	@Override
	public List<Friendship> getAllByUserIdOrderByDateDesc(Long userId) {
		return getRepository().getAllByUser1OrUser2OrderByDateDesc(userId, userId);
	}

}