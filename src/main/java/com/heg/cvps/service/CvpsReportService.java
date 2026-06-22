package com.heg.cvps.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.heg.cvps.entity.CvpsRequest;
import com.heg.cvps.repository.CvpsRequestRepository;

@Service
public class CvpsReportService {

    private final CvpsRequestRepository repository;

    public CvpsReportService(CvpsRequestRepository repository) {
        this.repository = repository;
    }

    /**
     * Extracts all vehicle request entries from Oracle and packages them 
     * into a fully formatted Excel spreadsheet binary array.
     */
    public byte[] generateMasterExcelReport() throws IOException {
        List<CvpsRequest> requests = repository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("CVPS Master Sheet");

            // 1. Title Styles
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 2. Define Table Columns
            String[] headers = {"Request No", "Contractor ID", "Vehicle No", "Vehicle Type", "Nature of Job", "Status", "Valid From", "Valid To"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // 3. Render Database Rows
            int rowIdx = 1;
            for (CvpsRequest req : requests) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(req.getRequestNo());
                row.createCell(1).setCellValue(req.getContractorId());
                row.createCell(2).setCellValue(req.getVehicleNo());
                row.createCell(3).setCellValue(req.getVehicleType());
                row.createCell(4).setCellValue(req.getNatureOfJob());
                row.createCell(5).setCellValue(req.getReqStatus());
                row.createCell(6).setCellValue(req.getPermissionFrom().toString());
                row.createCell(7).setCellValue(req.getPermissionTo().toString());
            }

            // 4. Auto-size columns for clear readability
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}