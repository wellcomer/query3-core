package com.github.wellcomer.query3.core;

import java.io.*;
import java.util.*;

/**
 * <h3>Список заявок.</h3>
 * Created on 18.11.15.
 */
public class QueryList extends FileStorage {

    public QueryList(String dbPath, String charsetName) {
        super(dbPath, charsetName);
    }

    // Номер заявки из пути к заявке

    /*public Integer getNumber(String path) {

        String[] tokens = path.split("\\.(?=[^\\.]+$)");
        return Integer.parseInt(tokens[0].substring(0, 6));
    }*/

    public Iterator<Query> iterator(){
        return new QueryIterator(this, 0);
    }

    public Iterator<Query> iterator(long modifiedSince){
        return new QueryIterator(this, modifiedSince);
    }

    /*public boolean grep(String fileName, ArrayList<String> patterns) throws IOException {

        FileReader file = new FileReader(fileName);
        BufferedReader reader = new BufferedReader(file);

        String line;
        boolean match = false;
        int matchCount = 0;

        while ((line = reader.readLine()) != null){
            for (String pattern : patterns){
                if (line.toLowerCase().matches(pattern))
                    matchCount+=1;
            }
            if (matchCount >= patterns.size()){
                match = true;
                break;
            }
        }
        reader.close();
        return match;
    }*/

    /**
     * Поиск заявок.
     * @param query Объект заявки с заполненными полями для поиска.
     * @return Список с номерами найденных заявок.
     */

    public List<Integer> find(Query query){

        List<Integer> queryNumbers = new LinkedList<>();
        List<String> idList = idList(0);

        for (String queryID : idList){
            try {

                Query nextQuery = get(queryID);
                boolean match = true;

                for (Map.Entry<String,String> entry: query.entrySet()){
                    String k = entry.getKey();
                    String v = entry.getValue().toLowerCase();
                    String vn = nextQuery.get(k);
                    if (vn == null || !vn.toLowerCase().matches(String.format(".*%s.*", v))) {
                        match = false;
                        break;
                    }
                }
                try {
                    if (match)
                        queryNumbers.add(Integer.decode(nextQuery.get("num")));
                }
                catch (NullPointerException e){
                    continue;
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryNumbers;
    }
}
