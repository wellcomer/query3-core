package ru.wellcomer.query3.core;

import org.apache.commons.lang.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;

/**
 * <h3>Автодополнение.</h3>
 *
*/

public class Autocomplete {

    private String filePath;
    private Charset charset;
    private int maxItems;

/**
    * @param filePath путь к файлам автокомплита
    * @param fileCharset кодировка файлов
    * @param maxItems максимальное количество результатов в выдаче
    */
    public Autocomplete (String filePath, String fileCharset, int maxItems){
        this.filePath = filePath;
        charset = Charset.forName(fileCharset);
        this.maxItems = maxItems;
    }

/**
    Получить список строк автодополнения.
    Загрузка строк из файла, проход по строкам с использованием regex.

    @param fileName имя файла.
    @param regex регулярное выражение.
*/
    public List<String> get (String fileName, String regex) throws IOException {

        List<String> out = new ArrayList<>();

        if (regex == null || regex.equals(""))
            return out;

        List<String> lines = Files.readAllLines(Paths.get(filePath, fileName), charset);

        regex = regex.toLowerCase(); // регистронезависимый поиск

        for (String line : lines) {
            if (line.toLowerCase().matches(".*" + regex + ".*")) {
                out.add(line.trim());
                if (maxItems == 0) // Неограниченная выдача
                    continue;
                if (out.size() >= maxItems)
                    break;
            }
        }
        return out;
    }

/**
    Автообучение. Загрузка каждой заявки, формирование TreeSet для каждого поля заявки. Запись TreeSet в файлы автокомплита.

    @param queryList список заявок.
    @param scanModifiedOnly сканировать только измененные заявки.
    @param mergePrevious слияние с уже существующими файлами автокомплита.
*/
    public void autolearn (QueryList queryList, boolean scanModifiedOnly, boolean mergePrevious) throws IOException {

        FileTime timestamp;
        long modifiedSince = 0;
        Path timestampFilePath = Paths.get(filePath, ".timestamp");

        if (scanModifiedOnly) { // Сканировать только измененные
            try { // Получение референсной метки времени последнего запуска
                timestamp = Files.getLastModifiedTime(timestampFilePath);
                modifiedSince = timestamp.toMillis();
            } catch (IOException e) { // Считаем что предыдущего запуска не было и создаем файл
                Files.createFile(timestampFilePath);
            }
        }

        HashMap<String, TreeSet<String>> fields = new HashMap<>(); // ключ - имя поля, значение - набор строк автокомплита
        Iterator<Query> queryIterator = queryList.iterator(modifiedSince); // Список изменившихся с последнего времени заявок

        String k, v;

        while (queryIterator.hasNext()) {

            Query query = queryIterator.next();

            for (Map.Entry<String, String> entry : query.entrySet()) {

                k = entry.getKey().toLowerCase();
                v = entry.getValue().trim();

                if (v.length() < 2)
                    continue;

                if (!fields.containsKey(k)) {

                    TreeSet<String> treeSet = new TreeSet<>();

                    try {
                        if (mergePrevious) { // Слияние с предыдущими файлами
                            List<String> lines = Files.readAllLines(Paths.get(filePath, k), charset);
                            treeSet.addAll(lines);
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                    fields.put(k, treeSet);
                }
                TreeSet<String> treeSet = fields.get(k);
                treeSet.add(v);
            }
        }

        for (Map.Entry<String, TreeSet<String>> entry : fields.entrySet()) {

            k = entry.getKey();
            ArrayList<String> lines = new ArrayList<>(fields.get(k));

            FileWriter fileWriter = new FileWriter(Paths.get(filePath, k).toString());
            fileWriter.write(StringUtils.join(lines, System.getProperty("line.separator")));
            fileWriter.flush();
            fileWriter.close();
        }

        try {
            Files.setLastModifiedTime(timestampFilePath, FileTime.fromMillis(System.currentTimeMillis()));
        }
        catch (IOException e){
            if (e.getClass().getSimpleName().equals("NoSuchFileException"))
                Files.createFile(timestampFilePath);
            e.printStackTrace();
        }
    }
}
