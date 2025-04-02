package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.SubjectDTO;
import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.Type;
import com.course_graph.repository.HistoryRepository;
import com.course_graph.repository.SubjectRepository;
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

@Service
@RequiredArgsConstructor
public class FileService {
    private final SubjectRepository subjectRepository;
    private final UserService userService;
    private final HistoryRepository historyRepository;

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
                System.out.println(column);
                if (column.equals("구분") && !value.equals("전공")) break ;
                else if (column.equals("년도")) year = parseInt(value);
                else if (column.equals("교과목번호")) subjectCode = value;
                else if (column.equals("성적")) score = value;
            }

            List<SubjectEntity> subjectEntityList = subjectRepository.findByCode(subjectCode);
            if (subjectEntityList.isEmpty() || year == -1 || score.isEmpty()) continue ;

            SubjectEntity subjectEntity = findTakenSubjectEntity(year, subjectEntityList);
            if (subjectEntity == null) throw new RestApiException(CustomErrorCode.INVALID_FILE);
            HistoryEntity historyEntity = HistoryEntity.toHistoryEntity(userEntity, subjectEntity, score);
            userEntity.addHistory(historyEntity);
            historyRepository.save(historyEntity);
        }
    }

    @Transactional
    public void saveSubjectFile(int year, List<List<String>> fileData) {
        List<String> columns = fileData.get(0);

        for (int i = 1; i < fileData.size(); i++) {
            List<String> row = fileData.get(i);
            SubjectDTO subjectDTO = new SubjectDTO();

            for (int j = 0; j < columns.size(); j++) {
                String column = columns.get(j);
                String value = row.get(j);
                switch (column) {
                    case "과목코드":
                        subjectDTO.setCode(value);
                        break;
                    case "과목명":
                        subjectDTO.setName(value);
                        break;
                    case "학점":
                        subjectDTO.setCredit(parseInt(value));
                        break;
                    case "학년":
                        subjectDTO.setGrade(value);
                        break;
                    case "이수구분":
                        if (value.equals("전공선택")) subjectDTO.setType(Type.MAJOR_ELECTIVE);
                        else if (value.equals("전공필수")) subjectDTO.setType(Type.MAJOR_REQUIRED);
                        break;
                }
            }

            List<SubjectEntity> subjectEntityList = subjectRepository.findByCode(subjectDTO.getCode());
            SubjectEntity subjectEntity = findSubjectEntity(year, subjectEntityList);
            if (subjectEntity == null) {
                subjectDTO.setCreatedAt(year);
                subjectDTO.setDeletedAt(year + 1);
                subjectRepository.save(SubjectEntity.toSubjectEntity(subjectDTO));
            }
            else subjectEntity.extendDeletedAt(year + 1);
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

    public SubjectEntity findSubjectEntity(int year, List<SubjectEntity> subjectEntityList) {
        for (int j = 0; j < subjectEntityList.size(); j++) {
            SubjectEntity subjectEntity = subjectEntityList.get(j);
            if (subjectEntity.getDeletedAt() == year) {
                return subjectEntity;
            }
        }
        return null;
    }

    public SubjectEntity findTakenSubjectEntity(int year, List<SubjectEntity> subjectEntityList) {
        for (int j = 0; j < subjectEntityList.size(); j++) {
            SubjectEntity subjectEntity = subjectEntityList.get(j);
            if (subjectEntity.getDeletedAt() > year) {
                return subjectEntity;
            }
        }
        return null;
    }


}
