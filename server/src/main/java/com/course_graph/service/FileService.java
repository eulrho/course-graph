package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.Track;
import com.course_graph.enums.Type;
import com.course_graph.repository.CurriculumRepository;
import com.course_graph.repository.HistoryRepository;
import com.course_graph.repository.SubjectRepository;
import com.course_graph.repository.SubjectTypeRepository;
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
            for (List<String> data : resultData) {
                System.out.println(data);
            }
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
            List<List<String>> resultData = readFile(file, "트랙명", "");
            if (resultData.isEmpty()) throw new Exception("");
            for (List<String> data : resultData) {
                System.out.println(data);
            }
            saveCurriculumFile(resultData);
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
            if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                String cellValue = firstCell.getStringCellValue();

                if (cellValue.equals(startPoint)) // start of data
                    startFound = true;
                if (cellValue.equals(endPoint) && startFound) break ; // end of data
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

                if (subjectTypeEntity.getType().equals(type.toString())) // 이수구분 유지
                    subjectTypeEntity.extendEndedAt(year + 1);
                else { // 이수구분 변경
                    SubjectTypeEntity newSubjectTypeEntity = SubjectTypeEntity.toSubjectTypeEntity(subjectEntity, type.toString(), year, year + 1);
                    subjectTypeRepository.save(newSubjectTypeEntity);
                }
            }
            else {
                if (code.isEmpty() || name.isEmpty() || credit == -1 || grade.isEmpty() || type == null) continue ;
                SubjectEntity subjectEntity = SubjectEntity.toSubjectEntity(code, name, credit, grade, year, year + 1);
                subjectRepository.save(subjectEntity);
                SubjectTypeEntity subjectTypeEntity = SubjectTypeEntity.toSubjectTypeEntity(subjectEntity, type.toString(), year, year + 1);
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
            CurriculumEntity curriculumEntity = CurriculumEntity.toCurriculumEntity(optionalSubject.get(), track);
            curriculumRepository.save(curriculumEntity);
        }
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
