package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.util.List;
import java.io.StringWriter;
import java.util.ArrayList;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Parse the cvs string into rows
            // reads the csv string and splits it into rows.
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = csvReader.readAll();
            
            // Creating the JSON containers (arrays and objects)
            JsonArray prodNums = new JsonArray();
            JsonObject root = new JsonObject();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();

            //fills colHeaders 
            //add each csv column name to json colHeadings array
            String[] headers = rows.get(0);
            for (String h : headers) {
                colHeadings.add(h);
            }
            
            //data
            for (int i = 1; i < rows.size(); i++){
                String[] row = rows.get(i);
                
                //prodNum first colomn
                prodNums.add(row[0]);
                
                //build the data row array
                JsonArray dataRow = new JsonArray();
                dataRow.add(row[1]); //title
                dataRow.add(Integer.parseInt(row[2]));//season
                dataRow.add(Integer.parseInt(row[3])); //episode
                dataRow.add(row[4]); //date
                dataRow.add(row[5]); 
                dataRow.add(row[6]); 
                
                data.add(dataRow);  
                           
            }
            
            //json root object
            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);
            
            //serialize JSON to string
            result = root.toJson();
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // parse json string
            JsonObject root = Jsoner.deserialize(jsonString, new JsonObject());
            
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray data = (JsonArray) root.get("Data");
            
            List<String[]> rows = new ArrayList<>();
            
            //add header row
            String[] headerRow = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++){
                headerRow[i] = (String) colHeadings.get(i);
            }
            rows.add(headerRow);
            
            //loop through data rows
            for (int i = 0; i < prodNums.size(); i++){
                String prodNum = (String) prodNums.get(i);
                JsonArray dataRow = (JsonArray) data.get(i);
                
                String[] csvRow = new String[headerRow.length];
                csvRow[0]= prodNum;
                csvRow[1]= (String) dataRow.get(0); //title
                csvRow[2]= String.valueOf(dataRow.get(1)); //season
                
                int episode = ((Number) dataRow.get(2)).intValue();
                csvRow[3]= String.format("%02d", episode);//episonde
                
                csvRow[4]=(String) dataRow.get(3);// date
                csvRow[5]=(String) dataRow.get(4);
                csvRow[6]=(String) dataRow.get(5);
                
                rows.add(csvRow);
            }
            
            //serialize csv to string
            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw);
            
            writer.writeAll(rows);
            writer.close();
            
            result =sw.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
