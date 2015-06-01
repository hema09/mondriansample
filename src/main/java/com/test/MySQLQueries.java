package com.test;

public class MySQLQueries {
    // SAMPLE QUERIES

    public static final String query1 = "SELECT " +
            "{ Except( {[Product].[All Products].[Drink].[Beverages].Children}, {[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]} ) } ON COLUMNS, " +
            "{ [Store].[All Stores].[USA].Children } ON ROWS FROM [Sales] WHERE ([Time].[1997])";

    public static final String query2 = "SELECT " +
            "FROM [Sales] WHERE ([Time].[1997])";

    public static final String query3 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS FROM [Sales] ";
    public static final String query4 = "SELECT [Store].[All Stores].[USA].[WA].Children ON COLUMNS FROM [Sales] ";
    public static final String query5 = "SELECT [Store].[All Stores].[USA].[WA].[Tacoma].Children ON COLUMNS " +
            "FROM [Sales] ";

    public static final String query = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[1997].Children on ROWS " +
            "FROM [Sales] ";

    public static final String query7 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[1997].[Q1].Children on ROWS " +
            "FROM [Sales] ";

    public static final String query10 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[1997].[Q1].Children on ROWS " +
            "FROM [Sales] ";

    public static final String query8 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[Month].members on ROWS " +
            "FROM [Sales] ";

    public static final String query_1 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[1997].[Q1] on ROWS " +
            "FROM [Sales] ";

    public static final String query_3 = "SELECT [Store].[All Stores].[USA].Children ON COLUMNS, " +
            " [Time].[1997].Children on ROWS " +
            "FROM [Sales]";

    public static final String query9 = "SELECT [Store].[All Stores].[USA].[WA].Children ON COLUMNS, " +
            " [Time].[1997].[] on ROWS " +
            "FROM [Sales]";


            
}
