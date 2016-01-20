package ru.wellcomer.query3.core;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <h3>Файловое хранилище.</h3>
 * Created on 18.11.15.<br>
 */
public class FileStorage implements QueryStorage {

    private String dbPath;
    private Charset charset;

    public FileStorage(String dbPath, String charsetName){
        this.dbPath = dbPath;
        this.charset = Charset.forName(charsetName);
    }

    @Override
    public List<String> read(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(dbPath, fileName), charset);
    }

    @Override
    public void write(String dataID, String data) throws IOException {

        Path filePath = Paths.get(dbPath, dataID);

        FileWriter fileNumSeq = new FileWriter(filePath.toString());
        fileNumSeq.write(data);
        fileNumSeq.flush();
        fileNumSeq.close();
    }

    @Override
    public Integer getNewNumber() throws IOException {
        List<String> lines = read("num.seq");
        return Integer.parseInt(lines.get(0));
    }

    @Override
    public void setNewNumber(Integer queryNumber) throws IOException {
        write("num.seq", queryNumber.toString());
    }

    @Override
    public Query get(Integer queryNumber) throws IOException {
        return get(String.format("%06d.req", queryNumber));
    }

    @Override
    public Query get(String queryID) throws IOException {

        List<String> lines= read(queryID);

        Query query = new Query();
        String kv[];

        for (String line : lines) {
            kv = line.split(":", 2);
            query.put(kv[0].trim(), kv[1].trim());
        }
        return query;
    }

    @Override
    public void add(Query query) throws IOException {
        add(getNewNumber(), query);
    }

    @Override
    public void add(Integer queryNumber, Query query) throws IOException {

        Integer newQueryNumber;

        try {
            newQueryNumber= getNewNumber();
        }
        catch (IOException e){
            e.printStackTrace();
            newQueryNumber = 1;
        }

        // Сохранить номер новой заявки в файл num.seq
        if (queryNumber.equals(newQueryNumber))
            setNewNumber(++newQueryNumber);

        String output, k, v;

        String lineSeparator = System.getProperty("line.separator");
        output = String.format("version:1%snum:%s%s", lineSeparator, queryNumber, lineSeparator);

        for (Map.Entry<String,String> entry : query.entrySet()){
            k = entry.getKey();
            v = entry.getValue();
            if (k.equals("n") || k.equals("f") || k.equals(""))
                continue;
            output += String.format("%s:%s%s", k, v, lineSeparator);
        }
        write(String.format("%06d.req", queryNumber), output);
    }

    @Override
    public Integer size() {
        List<String> idList = idList(0);
        if (idList == null)
            return 0;
        return idList.size();
    }

    @Override
    public List<String> idList(final long modifiedSince) {

        File file = new File(dbPath);

        FilenameFilter reqFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.length() == 10 && lowercaseName.endsWith(".req")){
                    try {
                        FileTime timestamp = Files.getLastModifiedTime(Paths.get(dbPath, name));
                        if (FileTime.fromMillis(modifiedSince).compareTo(timestamp) < 0)
                            return true;

                    } catch (IOException e) {
                    }
                }
                return false;
            }
        };

        String[] fileList = file.list(reqFilter);

        List<String> sortedFileList = Arrays.asList(fileList);
        Collections.sort(sortedFileList);

        return sortedFileList;
    }
}
