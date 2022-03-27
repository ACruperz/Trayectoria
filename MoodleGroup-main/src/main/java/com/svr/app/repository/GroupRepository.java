package com.svr.app.repository;

import java.util.List;

public interface GroupRepository<T> {

    List<T> toListMoodleGroups(Long id_career, Long id_period );
}