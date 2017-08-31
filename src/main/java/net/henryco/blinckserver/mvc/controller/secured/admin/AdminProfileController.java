package net.henryco.blinckserver.mvc.controller.secured.admin;

import net.henryco.blinckserver.mvc.controller.secured.BlinckProfileController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Henry on 24/08/17.
 */
@RestController
@RequestMapping("/protected/admin")
public class AdminProfileController implements BlinckProfileController {




	public @RequestMapping(
			method = POST,
			value = "/activate/admin/"
	) void activateAdminProfiles(@RequestBody String[] names) {

	}


}
