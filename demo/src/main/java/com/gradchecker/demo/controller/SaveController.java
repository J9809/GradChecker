package com.gradchecker.demo.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class SaveController {

    // MongoDB 연결 설정
    private static final String DATABASE_NAME = "GradChecker";
    private static final String COLLECTION_NAME = "courses";

    public static void saveData() {
        try {
            // MongoDB 연결 설정
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // 컬렉션의 모든 데이터 삭제
            collection.deleteMany(new Document());

            // 엑셀 파일 경로
            String excelFilePath = "/Users/yujisong/Downloads/기이수성적조회_20240229.xlsx";
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(inputStream);

            // 엑셀 시트에서 각 행을 읽어와 MongoDB에 삽입
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 사용
            Iterator<Row> rowIterator = sheet.iterator();
            // 헤더 행 건너뛰기
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // credit 열이 숫자인 경우에만 데이터를 저장
                if (row.getCell(8).getCellType() == CellType.STRING && !"학점".equals(getCellValue(row.getCell(8)))) {
                    Document document = new Document();
                    // 열 이름에 맞게 데이터를 저장
                    document.append("year", getCellValue(row.getCell(1)));
                    document.append("semester", getCellValue(row.getCell(2)));
                    document.append("course_number", getCellValue(row.getCell(3)));
                    document.append("course_name", getCellValue(row.getCell(4)));
                    document.append("course_classification", getCellValue(row.getCell(5)));
                    document.append("selection_area", getCellValue(row.getCell(7))); // 수정 필요
                    document.append("credit", Double.parseDouble(getCellValue(row.getCell(8))));
                    collection.insertOne(document);
                }
            }

            // 연결 종료
            workbook.close();
            inputStream.close();
            mongoClient.close();

            System.out.println("데이터베이스에 데이터를 성공적으로 저장했습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Date 형식의 경우 문자열로 반환
                    return cell.getDateCellValue().toString();
                } else {
                    // 숫자 값을 문자열로 변환하여 반환
                    return String.valueOf(cell.getNumericCellValue());
                }
            case STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }
}
