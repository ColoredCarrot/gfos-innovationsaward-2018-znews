package de.znews.server.dao.mysql;

public class TableSpec
{
    
    public final String name;
    public final String structure;
    
    public TableSpec(String name, String structure)
    {
        this.name = name;
        this.structure = structure;
    }
    
}
