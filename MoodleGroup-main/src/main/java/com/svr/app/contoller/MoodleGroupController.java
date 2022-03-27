package com.svr.app.contoller;


import com.svr.app.model.Group;
import com.svr.app.repository.MoodelGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MoodleGroupController {

    @Autowired
    private MoodelGroupRepository mdlGroupRepository;

    @RequestMapping( value = "api/moodle/groups/career/{id_career}/period/{id_period}")
    public List<Group> toListMoodleGroups(@PathVariable Long id_career, @PathVariable Long id_period ) {
        return mdlGroupRepository.toListMoodleGroups( id_career, id_period );
    }
}
