package helpers;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static List<String []> readLines(String fileName) {

        HSSFWorkbook wb;
        try (InputStream in = new FileInputStream(fileName)) {
            wb = new HSSFWorkbook(in);
            Sheet sheet = wb.getSheetAt(0);
            List<String[]> list = new ArrayList<>(sheet.getLastRowNum() + 1);

            for (Row row : sheet) {
                String[] line = new String[row.getLastCellNum()];
                Iterator<Cell> cells = row.iterator();
                int i = 0;
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    int cellType = cell.getCellType();
                    switch (cellType) {
                        case Cell.CELL_TYPE_STRING:
                            line[i] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            line[i] = cell.getNumericCellValue() + "";
                            break;

                        case Cell.CELL_TYPE_FORMULA:
                            line[i] = cell.getNumericCellValue() + "";
                            break;
                        default:
                            line[i] = "";
                            break;
                    }
                    if (line[i] == null) line[i] = "";
                    i++;
                }
                list.add(line);
            }

            return list;

        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
