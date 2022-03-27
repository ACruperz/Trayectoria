package com.svr.app.repository;


import java.util.List;

public interface Repository<T> {

    List<T> subjectsByCareerPeriod( Long id_career, Long id_period );


}
