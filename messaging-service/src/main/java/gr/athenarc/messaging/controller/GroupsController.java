package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping
public class GroupsController {

    private static final Logger logger = LoggerFactory.getLogger(GroupsController.class);
    private final GroupService groupService;

    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("groups")
    public Mono<?> getGroups(@RequestParam(defaultValue = "") String type) {
        return groupService.getGroups(type);
    }

}
