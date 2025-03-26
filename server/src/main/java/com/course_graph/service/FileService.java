package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.enums.CustomErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    public void upload(MultipartFile file) {
        try {
            if (!isValidFile(file)) throw new Exception("");

            List<List<String>> resultData = readFile(file);
            if (resultData.isEmpty()) throw new Exception("");
            // test: parsing data
            for (List<String> dataList : resultData) {
                for (String data : dataList) {
                    if (data.isEmpty()) data = "***";
                    System.out.print(data + " ");
                }
                System.out.print("\n");
            }
            // test end
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

    public List<List<String>> readFile(MultipartFile file) throws IOException {
        List<List<String>> resultData = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        boolean startFound = false;
        List<Integer> attributeIndexes = new ArrayList<>();
        String startString = "학점이수현황", endString = "자격종별";

        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);

            if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                String cellValue = firstCell.getStringCellValue();

                if (cellValue.contains(startString)) { // start of data
                    startFound = true;
                    continue ;
                }
                if (cellValue.contains(endString) && startFound) break ; // end of data
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
}
