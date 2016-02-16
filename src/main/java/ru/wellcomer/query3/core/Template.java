package ru.wellcomer.query3.core;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>Работа с шаблонами .odt.</h3>
 * Created on 03.11.15.
 */
public class Template {

    private String templatePath;

    /**
     * @param templatePath путь к файлам шаблонов.
     */
    public Template (String templatePath){
        this.templatePath = templatePath;
    }

    /**
     * Заполнить шаблон и сохранить в файл.
     * @param map ключ-значение.
     * @param templateName  имя файла шаблона.
     * @param saveToPath путь для сохранения заполненного шаблона.
     * @return имя результирующего файла.
     * @throws IOException
     */
    public String fillAndSave (HashMap<String,String> map, String templateName, String saveToPath) throws IOException {

        HashMap<String,String> cellAddress = new HashMap<String,String>();
        String[] cellMap, varTemplate;

        // Read file with templates
        List<String> lines = Files.readAllLines(Paths.get(templatePath, "var.template"), Charset.forName("UTF-8"));

        // Render templates and put them to the map
        for (String line : lines){

            line = line.trim();
            if (line.equals(""))
                continue;
            varTemplate = line.split(" ", 2);
            if (varTemplate.length < 2)
                continue;

            ST strTemplate = new ST(varTemplate[1]);

            for (Map.Entry<String, String> entry : map.entrySet())
                strTemplate.add(entry.getKey(), entry.getValue());

            // Add to map rendered templates
            map.put(varTemplate[0], strTemplate.render());
        }

        lines = Files.readAllLines(Paths.get(templatePath, templateName + ".ods.map"), Charset.forName("UTF-8"));

        for (String line : lines) {
            line = line.trim();
            if (line.equals(""))
                continue;
            cellMap = line.split(" ", 2); // A1 product
            cellAddress.put(cellMap[1], cellMap[0]); // product => A1
        }

        File templateFile = new File(Paths.get(templatePath, templateName).toString() + ".ods");
        final Sheet sheet = SpreadSheet.createFromFile(templateFile).getSheet(0);

        String namedField, cellName, cellValue;

        for (Map.Entry<String, String> entry : cellAddress.entrySet()){
            namedField = entry.getKey(); // product
            cellName = entry.getValue(); // A1
            cellValue = map.get(namedField); // value of product
            if (cellName == null || cellValue == null)
                continue;
            sheet.getCellAt(cellName).setValue(cellValue);
            sheet.getUsedRange();
        }

        String outputFileName = String.format("%s.ods", System.currentTimeMillis());

        File outputFile = new File(Paths.get(saveToPath, outputFileName).toString());
        sheet.getSpreadSheet().saveAs(outputFile);

        return outputFileName;
    }
}
