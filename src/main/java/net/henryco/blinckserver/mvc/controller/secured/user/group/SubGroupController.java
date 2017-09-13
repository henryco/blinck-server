package net.henryco.blinckserver.mvc.controller.secured.user.group;

import net.henryco.blinckserver.mvc.controller.BlinckController;
import net.henryco.blinckserver.mvc.service.relation.core.SubPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Henry on 12/09/17.
 */
@RestController
@RequestMapping("/protected/user/subgroup")
public class SubGroupController implements BlinckController {


	private final SubPartyService subPartyService;

	@Autowired
	public SubGroupController(SubPartyService subPartyService) {
		this.subPartyService = subPartyService;
	}

}