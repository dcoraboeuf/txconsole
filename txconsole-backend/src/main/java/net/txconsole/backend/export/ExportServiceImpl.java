package net.txconsole.backend.export;

import net.sf.jstring.model.Bundle;
import net.sf.jstring.model.BundleKey;
import net.sf.jstring.model.BundleSection;
import net.sf.jstring.model.BundleValue;
import net.txconsole.core.Content;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.ExportService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public Content excel(TranslationMap map) throws IOException {
        // Creates the workbook
        HSSFWorkbook workbook = generateWorkbook(map);
        // Prepares the output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } finally {
            out.close();
        }
        // OK
        return new Content("application/vnd.ms-excel", out.toByteArray());
    }

    protected HSSFWorkbook generateWorkbook(TranslationMap map) {
        // Creates the workbook
        HSSFWorkbook workbook = new HSSFWorkbook();
        // One sheet per bundle
        for (Bundle bundle : map.getBundleCollection().getBundles()) {
            // Creates the sheet
            HSSFSheet sheet = workbook.createSheet(bundle.getName());
            // Creates the columns
            HSSFRow row = sheet.createRow(0);
            int column = 0;
            row.createCell(column++).setCellValue("Section");
            row.createCell(column++).setCellValue("Key");
            for (Locale locale : map.getSupportedLocales()) {
                row.createCell(column++).setCellValue(locale.toString());
            }
            // For all sections & keys
            int rowIndex = 1;
            for (BundleSection section : bundle.getSections()) {
                for (BundleKey bundleKey : section.getKeys()) {
                    row = sheet.createRow(rowIndex++);
                    column = 0;
                    // Section & key
                    row.createCell(column++).setCellValue(section.getName());
                    row.createCell(column++).setCellValue(bundleKey.getName());
                    // For each language
                    for (Locale locale : map.getSupportedLocales()) {
                        // Gets the value for this language
                        BundleValue bundleValue = bundleKey.getValues().get(locale);
                        String value = bundleValue != null ? bundleValue.getValue() : "";
                        row.createCell(column++).setCellValue(value);
                    }
                }
            }
            // Freezes the first row
            sheet.createFreezePane(0, 1, 0, 1);
        }
        // OK
        return workbook;
    }
}
