package com.survivorserver.Dasfaust.Bridge;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLResult{
    
    private ResultSet RESULT_SET = null;
    private int POSITION = 0;
    
    /**
     * MySQLResult Object for simplified database access and manipulation.
     * 
     * @param result: A standard Java SQL ResultSet
     */
    public MySQLResult(ResultSet result){
        RESULT_SET = result;
    }
    
    /**
     * Get the current row as a standard String array
     * 
     * @return String Array with all columns
     */
    public String[] FetchArray(){
        String[] columns = null;
        try{
            RESULT_SET.next();
            columns = new String[RESULT_SET.getMetaData().getColumnCount()];
            for(int column = 1; column <= RESULT_SET.getMetaData().getColumnCount(); column++){
                columns[column - 1] = RESULT_SET.getString(column);
            }
        }
        catch(Exception e){            
        }
        return columns;
    }
    /**
     * Get the current row as a hash map.  Similar to an associative array.
     * Each column can be referenced by name using the get() method.
     * 
     * @return Map object
     */
    public Map<String, String> FetchAssoc(){
        Map<String, String> map = null;        
        try{
            RESULT_SET.next();
            map = new HashMap<String, String>();
            for(int column = 1; column <= RESULT_SET.getMetaData().getColumnCount(); column++){
                map.put(RESULT_SET.getMetaData().getColumnLabel(column), RESULT_SET.getString(column));
            }
        }
        catch(Exception e){            
        }        
        return map;
    }
    
    private void save_position(){
        try{
            if(RESULT_SET.isBeforeFirst()){
                POSITION = 0;
            }
            else if(RESULT_SET.isAfterLast()){
                POSITION = -1;
            }
            else{
                POSITION = getRow();
            }
        }
        catch(Exception e){            
        }                
    }
    private void restore_position(){
        try{        
            if(POSITION == 0){
                RESULT_SET.beforeFirst();
            }
            else if(POSITION == -1){
                RESULT_SET.afterLast();
            }
            else{
                absolute(POSITION);
            }
        }
        catch(Exception e){            
        }                 
    }
    
    /**
     * Total number of rows in this result
     * 
     * @return number of rows in result set, or 0 if empty
     */
    public int getNumRows(){        
        int returnValue = 0;
        try{
            save_position();
            RESULT_SET.last();
            returnValue = RESULT_SET.getRow();   
            restore_position();
        }
        catch(Exception e){}
        return returnValue;
    }    
    /**
     * Gets the current row number
     * 
     * @return current row number
     */
    public int getRow(){
        try {return RESULT_SET.getRow();}
        catch (Exception e){return 0;}
    }
    /**
     * Lets you reference the wrapped ResultSet object directly for advanced functions
     * 
     * @return {@link ResultSet} object
     */
    public ResultSet getResultSet(){
        return RESULT_SET;
    }
    
    /**
     * Sets to cursor to a specific row
     * 
     * @param row: the row number to go to
     * @return True on success
     */
    public boolean absolute(int row){
        try {return RESULT_SET.absolute(row);}
        catch(Exception e){return false;}
    }    
    /**
     * Moves to the next row in the result set.
     * 
     * @return True on success.
     * False if there are no more rows.
     */
    public boolean next(){
        try{
            return RESULT_SET.next();
        }
        catch(Exception e){
            return false;
        }        
    }
    /**
     * 
     * @return
     */
    public boolean previous(){
        try{
            return RESULT_SET.previous();
        }
        catch(Exception e){
            return false;
        }        
    }    
    /**
     * 
     * @return
     */
    public boolean first(){
        try{
            return RESULT_SET.first();
        }
        catch(Exception e){
            return false;
        }        
    }     
    /**
     * 
     * @return
     */
    public boolean last(){
        try{
            return RESULT_SET.last();
        }
        catch(Exception e){
            return false;
        }        
    }  
    /**
     * 
     */
    public void beforeFirst(){
        try{
            RESULT_SET.beforeFirst();
        }
        catch(Exception e){            
        }        
    }     
    /**
     * 
     */
    public void afterLast(){
        try{
            RESULT_SET.afterLast();
        }
        catch(Exception e){            
        }        
    }
    /**
     * 
     */
    public void close(){
        try{
            RESULT_SET.close();
        }
        catch(Exception e){            
        }        
    }      
    
    /**
     * 
     * @param columnLabel
     * @return
     */
    public String getString(String columnLabel){          
        String returnValue = null;
        save_position();
        try{
            if(RESULT_SET.isBeforeFirst()){
                RESULT_SET.next();
            }
            else if(RESULT_SET.isAfterLast()){
                RESULT_SET.previous();
            }
            returnValue = RESULT_SET.getString(columnLabel);
        }
        catch(Exception e){
        }                        
        finally{
            restore_position();
        }
        return returnValue;
    }    
    /**
     * 
     * @param columnIndex
     * @return
     */
    public String getString(int columnIndex){
        String returnValue = null;
        save_position();
        try{
            if(RESULT_SET.isBeforeFirst()){
                RESULT_SET.next();
            }
            else if(RESULT_SET.isAfterLast()){
                RESULT_SET.previous();
            }            
            returnValue = RESULT_SET.getString(columnIndex);
        }
        catch(Exception e){            
        }                
        finally{
            restore_position();
        }
        return returnValue;
    }
    /**
     * 
     * @param result
     * @param column
     * @return
     */
    public List<Object> mysqlArray(MySQLResult result, String column){
		List<Object> list = new ArrayList<Object>();  
		while(result.next())  
		{  
			list.add(result.getString(column));  
		}
		return list;
	}

}