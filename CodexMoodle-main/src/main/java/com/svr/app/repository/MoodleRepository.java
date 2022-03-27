package com.svr.app.repository;

import com.svr.app.models.*;
import com.svr.app.util.DataBase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

@Component
public class MoodleRepository implements Repository<SubjectMdl> {

    private Connection getConnection() throws SQLException {
        return DataBase.getInstance();
    }

    @Override
    public List<SubjectMdl> subjectsByCareerPeriod(Long id_career, Long id_period ) {
        return getSubjectMoodles( renderSubjects( id_career, id_period ) );
    }

    public List<Subject> subjectsByCareerPeriodLab( Long id_career, Long id_period) {
        return renderSubjects( id_career, id_period );
    }

    private List<Subject> renderSubjects(Long id_career, Long id_period ) {

        List<Subject> subjects = toListSubjectCourse( id_career, id_period);
        List<Subject> subjectsMdl = toListSubjectsMdl( id_career );
        List<Grade> grades = toListGradesByCareerPeriod( id_career, id_period);
        List<AttendanceMdl> attendance = toListAttendanceByCareerPeriod( id_career, id_period);


        List<Subject> datasource = hdrMoodleAlgorithm( subjects, grades);
        List<Subject> datasourceAtt = renderAttendanceLambda( datasource, attendance);
        List<Subject> datasourceMdl = repeatedSubjectsLambda( datasourceAtt );

        datasourceMdl.forEach( subject -> subjectsMdl.forEach(sub -> {
            if (Objects.equals(subject.getClave(), sub.getClave())) {
                subject.setSemester( sub.getSemester() );
            }
        }));
        return datasourceMdl;
    }


    private List<Subject> hdrMoodleAlgorithm( List<Subject> subjects, List<Grade> grades ) {

        for (Subject subject : subjects) {
            List<Grade> listGrades = new ArrayList<>();

            for (Grade grade : grades) {
                if (grade.getCourse().equals(subject.getId())) {
                    if (subject.getTeacher().isEmpty()) {
                        subject.setTeacher(grade.getTeacherFirstname() + " " + grade.getTeacherLastname());
                    }
                    listGrades.add(grade);
                }
            }
            if (listGrades.isEmpty()) continue;

            List<Grade> listGradesUnits = new ArrayList<>();
            List<Unit> units = new ArrayList<>();
            List<Long> failureHistory = new ArrayList<>();

            // Filter grades by unity
            int failedStudentBySub = 0;
            for (int i = 1; i <= 10; i++) {

                for (Grade gr : listGrades) {
                    if (gr.getUnit() == i) {
                        listGradesUnits.add(gr);
                    }
                }
                if (listGradesUnits.isEmpty()) continue;

                // Filter failed students
                int failedStudent = 0;

                for (Grade grade : listGradesUnits) {
                    if (grade.getGrade() < 70) {
                        Stream<Long> fh = failureHistory.stream().filter(f -> f.equals(grade.getUserid()));
                        if (fh.findAny().isEmpty()) {
                            failedStudent++;
                            failureHistory.add(grade.getUserid());
                        }
                    }
                }
                failedStudentBySub += failedStudent;
                units.add(new Unit(i, failedStudent));
                if (subject.getTotalStudents() == 0) {
                    subject.setTotalStudents(listGradesUnits.size());
                }
                listGradesUnits.clear();
            }
            subject.setFailureHistory(failureHistory);
            subject.setFailingStudents(failedStudentBySub);
            subject.setFailingRank((float) failedStudentBySub / subject.getTotalStudents());
            subject.setActiveStudent( subject.getTotalStudents() - subject.getDropoutStudents() );
            subject.setArrayUnits(units);
            subject.setUnits((long) units.size());
        }
        return subjects;
    }

    private List<Subject> repeatedSubjectsLambda( List<Subject> datasource ) {

        List<Subject> subjectsDelete = new ArrayList<>();
        List<Subject> arraySubject = new ArrayList<>();

        loop: for (Subject subject: datasource) {
            // Generated list by subject repeating
            Stream<Subject> dataFilter = datasource.stream()
                    .filter(s -> s.getClave().equals( subject.getClave() ));
            List<Subject> arrayRepeating = dataFilter.collect(Collectors.toList());

            if (arrayRepeating.size() == 1 ) { continue; }

            for ( Subject sub: subjectsDelete ) {
                if (Objects.equals(sub.getId(), subject.getId())) { continue loop; }
            }

            Subject subRp = new Subject();
            AtomicInteger counter = new AtomicInteger();
            arrayRepeating.forEach( sub -> {
                if ( subRp.getClave() == null) {
                    subRp.setClave(sub.getClave());
                    subRp.setSubject(sub.getSubject());
                    subRp.setTeacher(sub.getTeacher());
                    subRp.setUnits(sub.getUnits());
                    subRp.setSemester(sub.getSemester());
                    subRp.setUnits( sub.getUnits() );
                    subRp.setArrayUnits( sub.getArrayUnits() );
                    subRp.setTotalAttendance( sub.getTotalAttendance());
                    subRp.setActiveStudent( sub.getActiveStudent() );
                }
                if ( counter.get() > 0 ) {
                    subRp.getArrayUnits().forEach( unit -> sub.getArrayUnits().forEach(u -> {
                        if ( unit.getUnit() == u.getUnit() ) {
                            unit.setFailedStudent( unit.getFailedStudent() + u.getFailedStudent() );
                        }
                    }));
                    if ( subRp.getActiveStudent() >= sub.getActiveStudent() ) {
                        subRp.setActiveStudent( sub.getActiveStudent() );
                    }
                }
                subRp.setDropoutStudents(subRp.getDropoutStudents() + sub.getDropoutStudents() );
                subRp.addAllDropoutHistory( sub.getDropoutHistory() );
                subRp.setTotalStudents(subRp.getTotalStudents() + sub.getTotalStudents());
                subRp.setActiveStudent( subRp.getTotalStudents() -  subRp.getDropoutStudents() );
                subjectsDelete.add(sub);
                subRp.addFailedHistory( sub.getFailureHistory() );
                counter.getAndIncrement();
            });
            subRp.getArrayUnits().forEach( unit ->
                    subRp.setFailingStudents( subRp.getFailingStudents() + unit.getFailedStudent()));
            subRp.setFailingRank( (float) subRp.getFailingStudents() / subRp.getTotalStudents() );
            arraySubject.add(subRp);
        }
        datasource.removeAll(subjectsDelete);
        datasource.addAll(arraySubject);
        return datasource;
    }

    private List<Subject> renderAttendanceLambda( List<Subject> datasource, List<AttendanceMdl> attendance) {

        for ( Subject subject: datasource ) {
            List<Long> checkHistory = new ArrayList<>();
            Stream<AttendanceMdl> atBySub  = attendance.stream()
                    .filter( at -> at.getCourse().equals( subject.getId()));
            List<AttendanceMdl> attMdls = atBySub.collect(Collectors.toList());

            if ( attMdls.isEmpty() ) { continue; }

            attMdls.forEach( at -> {
                Stream<AttendanceMdl> stAttByUser = attMdls.stream()
                        .filter( atf -> atf.getUserid().equals( at.getUserid() ));
                List<AttendanceMdl> attArrayByUser = stAttByUser.collect(Collectors.toList());

                attArrayByUser.forEach( atx -> {
                    Stream<Long> chst = checkHistory.stream().filter(c -> c.equals( at.getUserid() ));

                    if (chst.findFirst().isEmpty() && atx.getFinalgrade().equals( atx.getGrademin() ) ) {
                        subject.setDropoutStudents( subject.getDropoutStudents() + 1);
                        subject.setActiveStudent( subject.getTotalStudents() - subject.getDropoutStudents() );
                        subject.getArrayUnits().forEach( unit -> {
                            if ( unit.getUnit() == atx.getSection() ) {
                                unit.setDropoutStudents( unit.getDropoutStudents() + 1);
                            }
                        });
                        checkHistory.add( atx.getUserid() );
                        subject.addDropoutHistory( atx.getUserid() );
                    }
                });
            });
        }
        return  datasource;
    }


    // #............     Subject     .................
    // Filter subject by mdl_course
    private List<Subject> toListSubjectCourse( Long id_career, Long id_period ) {
        List<Subject> subjects = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT id,fullname,shortname FROM mdl_course WHERE category = ? AND cicloescolar = ?;") ){
            stmt.setLong(1, id_career);
            stmt.setLong(2, id_period);

            try ( ResultSet rs = stmt.executeQuery() ) {
                subjects = dataSourceSubject( rs );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjects;
    }

    // Filter subject by mdl_catalogo_materias_plan_estudios
    private List<Subject> toListSubjectsMdl( Long id_career ) {
        List<Subject> subjects = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT mdl_catalogo_materias_plan_estudios.id," +
                        " mdl_catalogo_materias_plan_estudios.materia,\n" +
                        "    mdl_catalogo_materias_plan_estudios.semestre, " +
                        "mdl_catalogo_materias_plan_estudios.clave \n" +
                        "FROM mdl_catalogo_materias_plan_estudios\n" +
                        "    INNER JOIN mdl_catalogo_plan_estudios\n" +
                        "        ON  (mdl_catalogo_materias_plan_estudios.id_catalogo_plan_estudios =" +
                        " mdl_catalogo_plan_estudios.id)\n" +
                        "    WHERE mdl_catalogo_plan_estudios.id_carrera = ?;")) {
            stmt.setLong( 1, id_career );

            try ( ResultSet rs = stmt.executeQuery()) {
                while ( rs.next() ) {
                    Subject subject = new Subject();
                    subject.setId( rs.getLong("id"));
                    subject.setSubject( rs.getString("materia"));
                    subject.setSemester( rs.getLong("semestre"));
                    subject.setClave( rs.getString("clave"));
                    subjects.add( subject );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  subjects;
    }

    private List<Subject> dataSourceSubject( ResultSet rs ) throws SQLException {
        List<Subject> subjects = new ArrayList<>();

        while ( rs.next() ) {
            Subject subject = new Subject();
            subject.setId( rs.getLong( "id"));
            subject.setSubject( rs.getString("fullname"));
            subject.setClave( rs.getString("shortname"));
            subjects.add( subject );
        }
        return subjects;
    }

    private List<SubjectMdl> getSubjectMoodles(List<Subject> subjects) {
        List<SubjectMdl> dataResults = new ArrayList<>();

        subjects.forEach( subject -> {
            SubjectMdl sub = new SubjectMdl();
            sub.setSubject( subject.getSubject() );
            sub.setTeacher( subject.getTeacher() );
            sub.setSemester( subject.getSemester() );
            sub.setUnits( subject.getUnits() );
            sub.setTotalStudents( subject.getTotalStudents() );
            sub.setFailingStudents( subject.getFailingStudents() );
            sub.setFailingRank( subject.getFailingRank() );
            sub.setArrayUnits( subject.getArrayUnits() );
            sub.setDropoutStudents( subject.getDropoutStudents() );
            sub.setActiveStudents( subject.getActiveStudent() );
            dataResults.add( sub );
        });
        return dataResults;
    }


    // #............        Grade       ...............
    // filter by view tables mdl_course_calificaciones
    private List<Grade> toListGradesByCareerPeriod(Long id_career, Long id_period ) {
        List<Grade> grades = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT mdl_course_calificaciones.course, mdl_course_calificaciones.docente,\n" +
                        "    mdl_user.firstname, mdl_user.lastname, mdl_course_calificaciones.unidad,\n" +
                        "       mdl_course_calificaciones.userid, mdl_course_calificaciones.calificacion\n" +
                        "FROM mdl_course_calificaciones\n" +
                        "    INNER JOIN mdl_user\n" +
                        "    ON ( mdl_course_calificaciones.docente = mdl_user.id )\n" +
                        "    WHERE mdl_course_calificaciones.carrera = ? " +
                        "AND mdl_course_calificaciones.cicloescolar = ?;" )) {
            stmt.setLong(1, id_career );
            stmt.setLong(2, id_period);

            try ( ResultSet rs = stmt.executeQuery() ) {
                grades = dataSourceGrades( rs );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return grades;
    }

    private List<Grade> dataSourceGrades( ResultSet rs ) throws SQLException {
        List<Grade> grades = new ArrayList<>();

        while ( rs.next() ) {
            Grade grade = new Grade();
            grade.setCourse( rs.getLong("course"));
            grade.setTeacher( rs.getLong("docente"));
            grade.setTeacherFirstname( rs.getString("firstname"));
            grade.setTeacherLastname( rs.getString("lastname"));
            grade.setUnit( rs.getLong("unidad"));
            grade.setUserid( rs.getLong("userid"));
            grade.setGrade( rs.getLong("calificacion"));
            grades.add( grade );
        }
        return grades;
    }

    // #...........      Attendance     ...................
    private List<AttendanceMdl> toListAttendanceByCareerPeriod( Long id_career, Long id_period) {
        List<AttendanceMdl> attendanceMoodles = new ArrayList<>();

        try ( PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT gradeid, courseid, userid, section, grademax, grademin, finalgrade " +
                        "FROM mdl_view_course_section_grades\n" +
                        "WHERE modulename = 'asistencias' AND categoryid = ? AND cicloescolarid = ? ;" )) {
            stmt.setLong(1, id_career);
            stmt.setLong(2, id_period);

            try( ResultSet rs = stmt.executeQuery() ) {
                while ( rs.next() ) {
                    AttendanceMdl attendance = new AttendanceMdl();
                    attendance.setId( rs.getLong("gradeid"));
                    attendance.setUserid( rs.getLong("userid"));
                    attendance.setCourse( rs.getLong("courseid"));
                    attendance.setSection( rs.getLong("section"));
                    attendance.setGrademax( rs.getFloat("grademax"));
                    attendance.setGrademin( rs.getFloat("grademin"));
                    attendance.setFinalgrade( rs.getFloat("finalgrade"));
                    attendanceMoodles.add( attendance );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  attendanceMoodles;
    }

    // Exel exporter DataSource
    public void exelExporterMdl(HttpServletResponse response, List<SubjectMdl> datasource) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Moodle Lab");

        writeHeaderLine( workbook, sheet );
        writeDataLine( workbook, sheet, datasource);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write( outputStream );
        workbook.close();
    }

    private void writeHeaderLine(XSSFWorkbook workbook, XSSFSheet sheet) {
        Row row = sheet.createRow(0);
        Row rowXl = sheet.createRow(1);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(11);
        style.setFont( font );

        CellStyle styleB = workbook.createCellStyle();
        XSSFFont fontB = workbook.createFont();
        fontB.setFontHeight(11);

        createCell( sheet, row, 0, "ID\n", style);
        createCell( sheet, row, 1, "MATERIA\n", style);
        createCell( sheet, row, 2, "SEMESTRE\n", style);
        createCell( sheet, row, 3, "Total \nEstudiantes\n", style);
        createCell( sheet, row, 4, "DESERTORES \nPOR UNIDAD \n", styleB);
        createCell( sheet, row, 10, "Total \nAlumnos\nActivos", styleB);
        createCell( sheet, row, 11, "REPROBADOS \nPOR UNIDAD\n", styleB);
        createCell( sheet, row, 17, "Total \nReprobados \n", styleB);
        createCell( sheet, row, 18, "INDICE \nREPROBACION \n", styleB);

        sheet.addMergedRegion(new CellRangeAddress(0,0,4,9));
        sheet.addMergedRegion(new CellRangeAddress(0,0,11,16));

        createCell( sheet, rowXl, 0, " ", style);
        createCell( sheet, rowXl, 1, " ", style);
        createCell( sheet, rowXl, 2, " ", style);

        int counter = 1;
        for (int i = 4; i <= 9; i++) {
            createCell( sheet, rowXl, i , counter++ , styleB);
        }
        counter = 1;
        for (int i = 11; i <= 16; i++) {
            createCell( sheet, rowXl, i , counter++ , styleB);
        }

    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell( columnCount );

        if ( value instanceof  Integer ) {
            cell.setCellValue((Integer) value);
        } else if ( value instanceof String ) {
            cell.setCellValue( (String) value);
        } else if ( value instanceof Long ) {
            cell.setCellValue( (Long) value);
        }else if ( value instanceof Float ) {
            cell.setCellValue( (Float) value);
        }
        cell.setCellStyle( style );
    }

    private void writeDataLine( XSSFWorkbook workbook,XSSFSheet sheet, List<SubjectMdl> datasource) {
        AtomicInteger counter = new AtomicInteger(2);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);

        datasource.forEach( subject -> {
            Row row = sheet.createRow( counter.getAndIncrement() );
            AtomicInteger counterCell =  new AtomicInteger(0);
            AtomicInteger counterUnit =  new AtomicInteger(1);
            createCell( sheet, row, counterCell.getAndIncrement(), counter.get() - 2, style );
            createCell( sheet, row, counterCell.getAndIncrement(), subject.getSubject(), style );
            createCell( sheet, row, counterCell.getAndIncrement(), subject.getSemester(), style);
            createCell( sheet, row, counterCell.getAndIncrement(), subject.getTotalStudents(), style);

            List<Unit> units = subject.getArrayUnits();

            while ( counterUnit.get() <= 15) {
                if ( counterUnit.get() <= 6 ) {
                    units.forEach( unit ->  {
                        if ( unit.getUnit() == counterUnit.get() && unit.getDropoutStudents() > 0  ) {
                            createCell( sheet, row, counterCell.getAndIncrement(),
                                    unit.getDropoutStudents(), style);
                        } else if ( unit.getUnit() == counterUnit.get() && unit.getDropoutStudents() == 0) {
                            createCell( sheet, row, counterCell.getAndIncrement(), " ", style);
                        }
                    });
                } else if ( counterUnit.get() == 7) {
                    createCell( sheet, row, 10, subject.getActiveStudents(), style);
                    counterCell.set(11);
                } else if ( counterUnit.get() > 7 && counterUnit.get() <= 13 ){
                    units.forEach( unit ->  {
                        if ( unit.getUnit() == counterUnit.get() - 7 && unit.getFailedStudent() > 0  ) {
                            createCell( sheet, row, counterCell.getAndIncrement(),
                                    unit.getFailedStudent(), style);
                        } else if ( unit.getUnit() == counterUnit.get() - 7 && unit.getFailedStudent() == 0) {
                            createCell( sheet, row, counterCell.getAndIncrement(), " ", style);
                        }
                    });
                } else if ( counterUnit.get() == 14) {
                    createCell( sheet, row, 17, subject.getFailingStudents(), style);
                    counterCell.set(18);
                } else if ( counterUnit.get() == 15) {
                    createCell( sheet, row, 18, subject.getFailingRank(), style);
                    counterCell.set(19);
                }
                counterUnit.getAndIncrement();
            }
        });
    }

    public String filenameFormat( Long id_career, Long id_period) {
        String filename = "";
        try ( PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT mdl_course_categories.name, ciclo, ano   FROM mdl_course_cicloescolar\n" +
                        "    INNER JOIN mdl_course_categories\n" +
                        "        ON  mdl_course_cicloescolar.carrera = mdl_course_categories.id\n" +
                        "    WHERE mdl_course_cicloescolar.carrera = ? AND mdl_course_cicloescolar.id = ?;")) {
            stmt.setLong(1, id_career);
            stmt.setLong(2, id_period);
            try( ResultSet rs = stmt.executeQuery() ) {
                if ( rs.next() ) {
                    filename = rs.getString( "name" ).concat(" ")
                            .concat(rs.getString( "ciclo" )).concat(" ")
                            .concat(String.valueOf(rs.getLong("ano")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  filename;
    }






}
