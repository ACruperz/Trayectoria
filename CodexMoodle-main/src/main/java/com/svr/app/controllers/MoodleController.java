package com.svr.app.controllers;

import com.svr.app.models.*;
import com.svr.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class MoodleController {

    @Autowired
    private MoodleRepository repositoryMdl;

    @RequestMapping(value = "api/moodle/subjects/career/{id_career}/period/{id_period}",method = RequestMethod.GET)
    public List<SubjectMdl> subByCareerPeriod(@PathVariable Long id_career, @PathVariable Long id_period) {
        return repositoryMdl.subjectsByCareerPeriod( id_career, id_period);
    }

    @RequestMapping(value = "api/moodle/lab/career/{id_career}/period/{id_period}",method = RequestMethod.GET)
    public List<Subject> subByCareerPeriodLab(@PathVariable Long id_career, @PathVariable Long id_period) {
        return  repositoryMdl.subjectsByCareerPeriodLab( id_career, id_period);
    }

    @RequestMapping( value = "api/moodle/subjects/career/{id_career}/period/{id_period}/xlsx")
    public void exelExporterMdl(HttpServletResponse response,
                                @PathVariable Long id_career, @PathVariable Long id_period) throws IOException {

        String filenameFormat = repositoryMdl.filenameFormat( id_career, id_period);
        List<SubjectMdl> datasource = repositoryMdl.subjectsByCareerPeriod( id_career, id_period);
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+ filenameFormat +".xlsx";

        response.setHeader( headerKey, headerValue);
        repositoryMdl.exelExporterMdl( response, datasource);
    }




}
