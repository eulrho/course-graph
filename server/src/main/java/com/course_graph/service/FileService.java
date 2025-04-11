package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.dto.ClassroomDTO;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.Track;
import com.course_graph.enums.Type;
import com.course_graph.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {
    private final SubjectRepository subjectRepository;
    private final UserService userService;
    private final HistoryRepository historyRepository;
    private final CurriculumRepository curriculumRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final GraduationRepository graduationRepository;
    private final SubjectEquivalenceRepository subjectEquivalenceRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void historyFileUpload(MultipartFile file, String email) {
        try {
            if (!isValidFile(file)) throw new Exception("");

            List<List<String>> resultData = readFile(file, "구분", "자격종별");
            if (resultData.isEmpty()) throw new Exception("");
            saveHistoryFile(resultData, email);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    @Transactional
    public void subjectFileUpload(MultipartFile file, int year) {
        try {
            List<List<String>> resultData = readFile(file, "순번", "");
            if (resultData.isEmpty()) throw new Exception("");
            saveSubjectFile(year, resultData);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    @Transactional
    public void curriculumFileUpload(MultipartFile file) {
        try {
            List<List<String>> resultData = readFile(file, "트랙명", "");
            if (resultData.isEmpty()) throw new Exception("");
            saveCurriculumFile(resultData);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    @Transactional
    public void graduationFileUpload(MultipartFile file) {
        try {
            List<List<String>> resultData = readFile(file, "연도", "");
            if (resultData.isEmpty()) throw new Exception("");
            saveGraduationFile(resultData);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    @Transactional
    public void equivalenceFileUpload(MultipartFile file) {
        try {
            List<List<String>> resultData = readFile(file, "폐강 교과목번호", "");
            if (resultData.isEmpty()) throw new Exception("");
            saveEquivalenceFile(resultData);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    @Transactional
    public void scheduleFileUpload(MultipartFile file) {
        try {
            List<List<String>> resultData = readFile(file, "순번", "");
            if (resultData.isEmpty()) throw new Exception("");
            for (List<String> row : resultData) {
                System.out.println(row);
            }
            saveScheduleFile(resultData);
        } catch (IOException e) {
            throw new RestApiException(CustomErrorCode.FAIL_TO_UPLOAD_FILE);
        } catch (Exception e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }

    private static String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1).toLowerCase();
        }
        return "";
    }

    public boolean isValidFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty())  return false;

        String fileExtension = getFileExtension(fileName);
        return fileExtension.equals("xlsx");
    }

    public void findAttributeIndexes(List<Integer> attributeIndexes, Row row) {
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                if (!cell.getStringCellValue().isEmpty()) attributeIndexes.add(i);
            }
        }
    }

    public List<List<String>> readFile(MultipartFile file, String startPoint, String endPoint) throws IOException {
        List<List<String>> resultData = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        boolean startFound = false;
        List<Integer> attributeIndexes = new ArrayList<>();

        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            if (firstCell != null && firstCell.getCellType() != CellType.BLANK) {
                if (firstCell.getCellType() == CellType.STRING) {
                    String cellValue = firstCell.getStringCellValue();
                    if (cellValue.equals(startPoint)) // start of data
                        startFound = true;
                    if (cellValue.equals(endPoint) && startFound) break ; // end of data
                }
                if (startFound) {
                    if (attributeIndexes.isEmpty()) findAttributeIndexes(attributeIndexes, row);
                    List<String> rowData = new ArrayList<>();
                    for (Integer index : attributeIndexes) {
                        Cell cell = row.getCell(index);
                        if (cell != null) rowData.add(cell.toString());
                    }
                    resultData.add(rowData);
                }
            }
        }

        workbook.close();
        return resultData;
    }

    @Transactional
    public void saveHistoryFile(List<List<String>> fileData, String email) {
        List<String> columns = fileData.get(0);
        UserEntity userEntity = userService.getLoginUserByEmail(email);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);

            int year = -1;
            String subjectCode = "";
            String score = "";
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                if (column.equals("구분") && !value.equals("전공")) break ;
                else if (column.equals("년도")) year = parseInt(value);
                else if (column.equals("교과목번호")) subjectCode = value;
                else if (column.equals("성적")) score = value;
            }

            if (subjectCode.isEmpty() || year == -1 || score.isEmpty()) continue ;

            Optional<SubjectEntity> optionalSubject = subjectRepository.findByCodeAndDeletedAtGreaterThan(subjectCode, year);
            if (optionalSubject.isEmpty()) continue ;

            SubjectEntity subjectEntity = optionalSubject.get();
            Optional<HistoryEntity> optionalHistory = historyRepository.findByUserEntityAndSubjectEntity(userEntity, subjectEntity);
            if (optionalHistory.isPresent()) { // 수강 이력이 이미 존재하는 경우 성적 갱신만 진행
                HistoryEntity historyEntity = optionalHistory.get();
                historyEntity.edit(score);
                continue ;
            }
            HistoryEntity historyEntity = HistoryEntity.toHistoryEntity(userEntity, optionalSubject.get(), score);
            userEntity.addHistory(historyEntity);
            historyRepository.save(historyEntity);
        }
    }

    @Transactional
    public void saveSubjectFile(int year, List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        String code = "", name = "", grade = "";
        int credit = -1;
        Type type = null;
        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);

            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "과목코드":
                        code = value;
                        break;
                    case "과목명":
                        name = value;
                        break;
                    case "학점":
                        credit = parseInt(value);
                        break;
                    case "학년":
                        grade = value;
                        break;
                    case "이수구분":
                        if (value.equals("전공선택")) type = Type.MAJOR_ELECTIVE;
                        else if (value.equals("전공필수")) type = Type.MAJOR_REQUIRED;
                        break;
                }
            }

            Optional<SubjectEntity> optionalSubject = subjectRepository.findByCodeAndName(code, name);
            if (optionalSubject.isPresent()) {
                SubjectEntity subjectEntity = optionalSubject.get();
                subjectEntity.extendDeletedAt(year + 1);

                List<SubjectTypeEntity> subjectTypeEntityList = subjectTypeRepository.findAllBySubjectEntityOrderByEndedAtDesc(subjectEntity);
                SubjectTypeEntity subjectTypeEntity = subjectTypeEntityList.get(0);

                if (subjectTypeEntity.getType().equals(type.name())) // 이수구분 유지
                    subjectTypeEntity.extendEndedAt(year + 1);
                else { // 이수구분 변경
                    SubjectTypeEntity newSubjectTypeEntity = SubjectTypeEntity.toSubjectTypeEntity(subjectEntity, type, year, year + 1);
                    subjectTypeRepository.save(newSubjectTypeEntity);
                }
            }
            else {
                if (code.isEmpty() || name.isEmpty() || credit == -1 || grade.isEmpty() || type == null) continue ;
                SubjectEntity subjectEntity = SubjectEntity.toSubjectEntity(code, name, credit, grade, year, year + 1);
                subjectRepository.save(subjectEntity);
                SubjectTypeEntity subjectTypeEntity = SubjectTypeEntity.toSubjectTypeEntity(subjectEntity, type, year, year + 1);
                subjectTypeRepository.save(subjectTypeEntity);
            }
        }
    }

    @Transactional
    public void saveCurriculumFile(List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);
            String subjectCode = "", name = "";
            Track track = null;
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "트랙명":
                        if (value.equals("지능형시스템")) track = Track.INTELLIGENT_SYS;
                        else if (value.equals("자율주행차 V2X 통신시스템")) track = Track.AUTO_V2X_COMM;
                        else throw new RestApiException(CustomErrorCode.INVALID_FILE);
                        break;
                    case "교과목번호":
                        subjectCode = value;
                        break;
                    case "교과목명":
                        name = value;
                        break;
                }
            }

            Optional<SubjectEntity> optionalSubject = subjectRepository.findByCodeAndName(subjectCode, name);
            if (optionalSubject.isEmpty() || track == null) throw new RestApiException(CustomErrorCode.INVALID_FILE);

            SubjectEntity subjectEntity = optionalSubject.get();
            CurriculumEntity curriculumEntity = CurriculumEntity.toCurriculumEntity(subjectEntity, track);
            curriculumRepository.save(curriculumEntity);
            subjectEntity.addCurriculum(curriculumEntity);
        }
    }

    @Transactional
    public void saveGraduationFile(List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);
            int year = -1, requiredMinCredit = -1, electiveMinCredit = -1;
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "연도":
                        year = parseInt(value);
                        break;
                    case "전공 필수 최소 학점":
                        requiredMinCredit = parseInt(value);
                        break;
                    case "전공 선택 최소 학점":
                        electiveMinCredit = parseInt(value);
                        break;
                }
            }

            if (year == -1 || requiredMinCredit == -1 || electiveMinCredit == -1)
                throw new RestApiException(CustomErrorCode.INVALID_FILE);
            GraduationEntity graduationEntity = GraduationEntity.toGraduationEntity(year, requiredMinCredit, electiveMinCredit);
            graduationRepository.save(graduationEntity);
        }
    }

    @Transactional
    public void saveEquivalenceFile(List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);
            String originalCode = "", originalName = "", equivalenceCode = "", equivalenceName = "";
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "폐강 교과목번호":
                        originalCode = value;
                        break;
                    case "폐강 교과목명":
                        originalName = value;
                        break;
                    case "대체된 교과목번호":
                        equivalenceCode = value;
                        break;
                    case "대체된 교과목명":
                        equivalenceName = value;
                        break;
                }
            }

            if (originalCode.isEmpty() || originalName.isEmpty() || equivalenceCode.isEmpty() || equivalenceName.isEmpty())
                throw new RestApiException(CustomErrorCode.INVALID_FILE);

            Optional<SubjectEntity> optionalOriginalSubject = subjectRepository.findByCodeAndName(originalCode, originalName);
            Optional<SubjectEntity> optionalEquivalenceSubject = subjectRepository.findByCodeAndName(equivalenceCode, equivalenceName);
            if (optionalOriginalSubject.isEmpty() || optionalEquivalenceSubject.isEmpty())
                throw new RestApiException(CustomErrorCode.INVALID_FILE);
            SubjectEquivalenceEntity subjectEquivalenceEntity = SubjectEquivalenceEntity.toSubjectEquivalenceEntity(optionalOriginalSubject.get(), optionalEquivalenceSubject.get());
            subjectEquivalenceRepository.save(subjectEquivalenceEntity);
        }
    }

    @Transactional
    public void saveScheduleFile(List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "과목코드":
                        scheduleDTO.setCode(value);
                        break;
                    case "과목명":
                        scheduleDTO.setName(value);
                        break;
                    case "분반":
                        scheduleDTO.setClassNumber(parseInt(value));
                        break;
                    case "담당교수":
                        scheduleDTO.setProfessor(value);
                        break;
                    case "수업시간":
                        extractClassroom(scheduleDTO, value);
                        break;
                }
            }

            Optional<SubjectEntity> optionalSubject = subjectRepository.findByCodeAndName(
                    scheduleDTO.getCode(), scheduleDTO.getName());
            if (optionalSubject.isEmpty())
                throw new RestApiException(CustomErrorCode.INVALID_FILE);
            SubjectEntity subjectEntity = optionalSubject.get();
            for (ClassroomDTO classroomDTO : scheduleDTO.getClassroomList()) {
                ScheduleEntity scheduleEntity = ScheduleEntity.toScheduleEntity(subjectEntity,
                        scheduleDTO.getClassNumber(), classroomDTO.getRoom(), classroomDTO.getTime(), scheduleDTO.getProfessor());
                subjectEntity.addSchedule(scheduleEntity);
                scheduleRepository.save(scheduleEntity);
            }
        }
    }

    public void extractClassroom(ScheduleDTO scheduleDTO, String data) {
        List<ClassroomDTO> classroomList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            int idx = data.indexOf("[", i);
            if (idx == -1) throw new RestApiException(CustomErrorCode.INVALID_FILE);
            int endIdx = data.indexOf("]", i);
            if (endIdx == -1) throw new RestApiException(CustomErrorCode.INVALID_FILE);

            ClassroomDTO classroomDTO = new ClassroomDTO();
            classroomDTO.setTime(data.substring(i, idx - 1));
            classroomDTO.setRoom(data.substring(idx + 1, endIdx));
            classroomList.add(classroomDTO);

            i = endIdx + 2;
        }
        scheduleDTO.setClassroomList(classroomList);
    }

    private int parseInt(String value) {
        try {
            double tmp = Double.parseDouble(value);
            return (int)tmp;
        } catch (NumberFormatException e) {
            throw new RestApiException(CustomErrorCode.INVALID_FILE);
        }
    }
}
