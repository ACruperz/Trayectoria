package com.svr.app.repository;

import com.svr.app.model.*;
import com.svr.app.util.DataBase;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import java.util.stream.*;

@Component()
public class MoodelGroupRepository implements GroupRepository<Group>{

    private Connection getConnection() throws SQLException {
        return DataBase.getInstance();
    }

    @Override
    public List<Group> toListMoodleGroups(Long id_career, Long id_period) {
        List<Group> groups = getDataSourceGroups();
        List<Grade> grades = getDataSourceGrades( id_career, id_period);
        return svrAlgorithmMdl( groups, grades);
    }

    private List<Group> svrAlgorithmMdl( List<Group> groups, List<Grade> grades ) {
        List<Group> idGroupsDelete = new ArrayList<>();
        List<Subject> arraySubjects = new ArrayList<>();
        List<Integer> historySubjects = new ArrayList<>();
        List<String> historyGroups = new ArrayList<>();

        for (Group group : groups) {
            Stream<Grade> gradeMdl = grades.stream()
                    .filter(grade -> grade.getGroup().equals(group.getGroup()));
            List<Grade> arrayGrades = gradeMdl.collect(Collectors.toList());

            if ( historyGroups.contains( group.getGroup() ) || arrayGrades.isEmpty() ) {
                idGroupsDelete.add(group);
                continue;
            }
            historyGroups.add( group.getGroup() );

            // Filter and generate array of subjects belonging to the group
            arrayGrades.forEach( grade -> {
                if ( !historySubjects.contains( grade.getCourse() )) {
                    Subject subject = new Subject();
                    subject.setIdCourse( grade.getCourse() );
                    subject.setClave( grade.getClave() );

                    arraySubjects.add( subject );
                    historySubjects.add( grade.getCourse() );
                }
            });
            group.addArraySubjects( arraySubjects );

            // Filter and generate array of students belonging to the group
            List<Long> studentRegistry = new ArrayList<>();
            for (Grade grade : arrayGrades) {
                if ( studentRegistry.contains( grade.getUserid()) ) continue;
                studentRegistry.add( grade.getUserid() );

                Student student = new Student();
                student.setUserid( grade.getUserid() );
                student.setStudentFirstname( grade.getStudentFirstname() );
                student.setStudentLastname( grade.getStudentLastname() );

                List<GradeSt> arrayGradeSt = new ArrayList<>();
                List<UnitSt> arrayUnitSt = new ArrayList<>();
                arraySubjects.forEach( s -> {
                    GradeSt gradeSt = new GradeSt();
                    arrayGrades.forEach(  gx -> {
                        if ( gx.getCourse().equals( s.getIdCourse() ) && gx.getUserid().equals( grade.getUserid())) {
                            UnitSt unitSt = new UnitSt();
                            if ( gradeSt.getClave().isEmpty() ) {
                                gradeSt.setClave( gx.getClave() );
                                gradeSt.setIdCourse( gx.getCourse() );
                            }
                            unitSt.setUnit( gx.getUnit() );
                            unitSt.setGrade( gx.getGrade() );
                            unitSt.setFailedUnit( gx.getGrade() < 70 ? 1 : 0 );
                            gradeSt.setFailedUnits( gradeSt.getFailedUnits() + unitSt.getFailedUnit() );
                            student.setTotalFailedUnits( student.getTotalFailedUnits() + gradeSt.getFailedUnits());
                            arrayUnitSt.add( unitSt );
                        }
                    });
                    gradeSt.addUnits( arrayUnitSt );
                    arrayGradeSt.add( gradeSt );
                    arrayUnitSt.clear();
                });
                student.addArraySubjectSt( arrayGradeSt );
                group.addArrayStudents( student );
            }
        }
        groups.removeAll( idGroupsDelete );
        return  groups;
    }

    // Groups
    private List<Group> getDataSourceGroups() {
        List<Group> datasource = new ArrayList<>();

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery( "SELECT id, name FROM mdl_groups ")) {

            while ( rs.next() ){
                Group group = new Group();
                group.setId( rs.getLong( "id"));
                group.setGroup( rs.getString("name"));
                datasource.add( group );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  datasource;
    }

    // Grade
    private List<Grade> getDataSourceGrades(Long id_career, Long id_period ) {
        List<Grade> datasource = new ArrayList<>();

        try ( PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT mdl_course_calificaciones.groupid,\n" +
                        "       mdl_groups.name,\n" +
                        "       mdl_course_calificaciones.course,\n" +
                        "       mdl_course.clave,\n" +
                        "       mdl_course_calificaciones.docente,\n" +
                        "       mdl_user.firstname, mdl_user.lastname,\n" +
                        "       mdl_course_calificaciones.unidad,\n" +
                        "       mdl_course_calificaciones.userid,\n" +
                        "       mdl_course_calificaciones.calificacion\n" +
                        "FROM mdl_course_calificaciones\n" +
                        "    INNER JOIN mdl_user ON ( mdl_course_calificaciones.userid = mdl_user.id )\n" +
                        "    INNER JOIN mdl_course ON ( mdl_course_calificaciones.course = mdl_course.id )\n" +
                        "    INNER JOIN mdl_groups ON ( mdl_course_calificaciones.groupid = mdl_groups.id )\n" +
                        "    WHERE mdl_course_calificaciones.carrera = ? " +
                        "and mdl_course_calificaciones.cicloescolar = ?;" )) {

            stmt.setLong( 1, id_career);
            stmt.setLong( 2, id_period);

            try ( ResultSet rs = stmt.executeQuery() ) {
                while ( rs.next() ) {
                    Grade grade = new Grade();
                    grade.setIdGroup( rs.getLong("groupid"));
                    grade.setGroup( rs.getString("name"));
                    grade.setCourse( rs.getInt("course"));
                    grade.setClave( rs.getString("clave"));
                    grade.setStudentFirstname( rs.getString("firstname"));
                    grade.setStudentLastname( rs.getString("lastname"));
                    grade.setUnit( rs.getLong("unidad"));
                    grade.setUserid( rs.getLong("userid"));
                    grade.setGrade( rs.getLong("calificacion"));
                    datasource.add( grade );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return datasource;
    }

}
