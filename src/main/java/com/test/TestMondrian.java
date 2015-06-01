package com.test;

import com.test.json.CellSetSerializerBarChart;
import com.test.json.CellSetSerializerStacked;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.olap4j.*;
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.metadata.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMondrian {
    static ObjectMapper mapperstack = new ObjectMapper();
    static ObjectMapper mapperbarchart = new ObjectMapper();

    //TODO : update values in {} to appropriate values
    public static OlapConnection getOlapConnection(String type) throws SQLException, ClassNotFoundException {
        String cnxURL = null;
        Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
        if(type.equalsIgnoreCase("mysql")) {
            cnxURL = "jdbc:mondrian:" +
                "Jdbc=jdbc:mysql://localhost:3306/foodmart?user={uname}&password={password};"+ // uname and pwd to where foodmart is on mysql db
                "JdbcDrivers=com.mysql.jdbc.Driver;"+
                "Catalog='file:/C:\\mondrian\\src\\main\\resources\\FoodMart.xml'"; // change this if project not in c:

        } else {
             cnxURL = "jdbc:mondrian:" +
                "Jdbc=jdbc:oracle:thin:@{connectionUrl}:{port}:{servicename};"+
                "JdbcUser={username}" +
                "JdbcPassword={password}" +
                "JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver,oracle.jdbc.OracleDriver;"+
                "Catalog='file:/{path to config file xml}'";


        }
        Connection connection = DriverManager.getConnection(cnxURL);
        OlapConnection connectionolap = connection.unwrap(OlapConnection.class);

        /*CacheControl cacheControl = connection.unwrap(RolapConnection.class).getCacheControl(null);
        cacheControl.flushSchemaCache();*/
        return connectionolap;
    }

    // actual method
    public static String executeAndReturnResult(String row, String col, String chartType) throws Exception {
        SimpleModule module = new SimpleModule("Test Module", new Version(1,0,0,null));
        SimpleModule module2 = new SimpleModule("Test Module2", new Version(1,0,0,null)); // note: 2 serializers cannot be added to the same module
        module.addSerializer(new CellSetSerializerBarChart());
        module2.addSerializer(new CellSetSerializerStacked());
        String query = null;
        if(chartType.equals("barchart")){
            query = "SELECT " + row + ".Children ON COLUMNS, " +    // for bar chart we have to put row on columns and col on rows
                    col + " on ROWS " +
                    "FROM [Sales]";
        }
        else if(chartType.equals("stacked")) {
            query = "SELECT " + col + ".Children ON COLUMNS, " +      // for stacked bar chart we have to put col on columns and row on rows
                    row + ".Children on ROWS " +
                    "FROM [Sales]";
        }
        mapperbarchart.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapperbarchart.registerModule(module);
        mapperstack.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapperstack.registerModule(module2);
        System.out.println(query);
        OlapConnection olapConnection = getOlapConnection("mysql");
        final CellSet cellSet = olapConnection
                .createStatement()
                .executeOlapQuery(query);

        RectangularCellSetFormatter formatter =
                new RectangularCellSetFormatter(false);

        // Print out.
        PrintWriter writer = new PrintWriter(System.out);
        formatter.format(cellSet, writer);
        writer.flush();
        String output = null;
        if(chartType.equals("barchart")){
            output = mapperbarchart.writeValueAsString(cellSet);
        } else {
            output = mapperstack.writeValueAsString(cellSet);
        }
        return output;
    }

    public static void getDimensions(String type) throws SQLException, ClassNotFoundException {
        OlapConnection olapConnection = getOlapConnection(type);
        NamedList<Cube> cubes = olapConnection.getOlapSchema().getCubes();
        Cube salesCube = cubes.get("JobSkills");
        List<Dimension> dimensions = salesCube.getDimensions();
        Map<String, String> dimensionMap = new HashMap<String, String>();
        for(Dimension dimension : dimensions) {
            System.out.println("dimension name:"+dimension.getCaption()+ ", dimension full name:" + dimension.getUniqueName());
            dimensionMap.put(dimension.getCaption(), dimension.getUniqueName());
        }
        List<Hierarchy> hierarchies = salesCube.getHierarchies();
        for(Hierarchy hierarchy : hierarchies) {
            System.out.println(hierarchy.getCaption());
            List<Level> levels = hierarchy.getLevels();
            for(Level level : levels)
                System.out.println("\t" + level.getUniqueName());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("APPLICATION STARTED....");
        //System.out.println(TestMondrian.executeAndReturnResult("[Time].[1997]", "[Store].[USA]", "barchart"));
        executeAndReturnResult_mysql();
    }


    public static void executeAndReturnResult_mysql() throws Exception {
        OlapConnection olapConnection = getOlapConnection("mysql");
        OlapStatement statement = olapConnection.createStatement();
        String query = MySQLQueries.query3; //"SELECT [Time].[1997].Children ON COLUMNS, [Store].[USA].[CA] on ROWS FROM [Sales]";
        System.out.println(query);
        executeQuery(statement, query);
       /* query = "SELECT [Time].[1997].Children ON COLUMNS, [Store].[USA] on ROWS FROM [Sales]";
        System.out.println(query);
        executeQuery(statement, query);
        query = "SELECT [Time].[1997].Children ON COLUMNS, [Store].[USA].[WA].[Seattle] on ROWS FROM [Sales]";
        System.out.println(query);
        executeQuery(statement, query);*/
    }

    public static void executeAndReturnResult_oracle() throws Exception {
        OlapConnection olapConnection = getOlapConnection("oracle");
        OlapStatement statement = olapConnection.createStatement();
        for(String query : Oraclequeries.queries)     { // TODO : create Oraclequeries class and place your trial queries there
            System.out.println("\n\n\n" + query);
            executeQuery(statement, query);
            showMemoryUsage();
        }
    }

    private static void showMemoryUsage() {
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + (memory / (1024L * 1024L)));
    }

    public static void executeQuery(OlapStatement statement, String query) throws SQLException, ClassNotFoundException {

        // Prepare a statement, and execute the query. no mondrian.properties file used currently, all default values used
        long startTime = System.currentTimeMillis();
        final CellSet cellSet = statement.executeOlapQuery(query);   // Execute some query

        System.out.println("Time to execute:" + (System.currentTimeMillis() - startTime));

        // We use the utility formatter.
        RectangularCellSetFormatter formatter =
                new RectangularCellSetFormatter(false);

        // Print out.
        PrintWriter writer = new PrintWriter(System.out);
        formatter.format(cellSet, writer);
        writer.flush();
    }

}