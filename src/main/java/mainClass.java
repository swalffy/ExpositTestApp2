
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class mainClass {
    private static String getFileExtension(String fileName) {
        int index = fileName.indexOf('.');
        return index == -1 ? null : fileName.substring(index);
    }

    private static Map<String,String> argsParser(String[] args){
        Map<String,String> result = new HashMap<String, String>();
        for (int i = 0; i < args.length; i+=2){
            result.put(args[i], args[i+1]);
        }
        return result;
    }

    private static List<Integer> getNeededColumnsFromCSV(String fileName, String regex) throws IOException {

        List<Integer> result = new ArrayList<Integer>();
        CSVParser parser = getCsvParser(fileName);
        if (parser == null) return result;

        for(CSVRecord csvRecord : parser) {
            int count = 0;
            for (String val : csvRecord){
                if (result.contains(count)){
                    count++;
                    continue;
                }
                try{
                    if (Pattern.compile(regex)
                            .matcher(val)
                            .matches())
                        result.add(count);
                    count++;
                }catch (PatternSyntaxException e) {
                    System.out.println("Wrong regular expression");
                    return result;
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    private static CSVParser getCsvParser(String fileName) throws IOException {
        CSVParser parser;
        try {
            File csvData = new File(fileName);
            parser = CSVParser.parse(csvData, Charset.defaultCharset() , CSVFormat.EXCEL);
        } catch (FileNotFoundException e){
            System.out.println("File not found");
            return null;
        }
        return parser;
    }

    private static void fileWriter(String input, String output, List<Integer> columns) throws IOException{

        CSVParser parser;
        FileWriter fileWriter;
        String outputExtension = getFileExtension(output);
        try{
            parser = getCsvParser(input);
            fileWriter = new FileWriter(output);
        } catch (FileNotFoundException e){
            System.out.println("File not found");
            return;
        }catch (NullPointerException e){
            System.out.println("File not found");
            return;
        }
        for (int col : columns){
            try {
                parser = getCsvParser(input);
            }
            catch (NullPointerException e){
                continue;
            }
            for(CSVRecord record : parser){
                if (outputExtension.equals(".txt")){
                    try {
                        fileWriter.write(record.get(col) + "\r\n");
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }
                }else if (outputExtension.equals(".csv")){
                    try {
                        fileWriter.write(record.get(col) + ",");
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }
                }else {
                    System.out.println("Wrong output file extension");
                    return;
                }
            }
            if (outputExtension.equals(".csv")){
                fileWriter.write("\r\n");
            }
        }
        fileWriter.flush();
        System.out.println("File writing complete");
    }


    public static void main(String[] args) throws IOException{
        if (args.length % 2 != 0 ) {
            System.out.println("Wrong arguments line. Execution");
            return;
        }
        Map<String,String> arguments = argsParser(args);

        String input = arguments.get("-in");
        String regex = arguments.get("-reg");
        String output = arguments.get("-out");

        List<Integer> columns = getNeededColumnsFromCSV(input,regex);
        if (!columns.isEmpty()){
            fileWriter(input,output, columns);
        }else
            System.out.println("No any matching");
    }

}
