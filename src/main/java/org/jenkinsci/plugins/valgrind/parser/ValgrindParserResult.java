package org.jenkinsci.plugins.valgrind.parser;

import hudson.FilePath;
import hudson.Util;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.*;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.jenkinsci.plugins.valgrind.model.ValgrindReport;
import org.jenkinsci.plugins.valgrind.util.ValgrindLogger;
import org.jenkinsci.remoting.RoleChecker;


public class ValgrindParserResult implements FilePath.FileCallable<ValgrindReport>
{
    private static final long serialVersionUID = -5475538646374717099L;
    private String base_path;
    private List<String> patterns;   
    
    public ValgrindParserResult(String base_path, String pattern )
    {
        this.base_path = base_path;
        this.patterns = Arrays.asList(pattern.split(","));        
    }
    
    public ValgrindReport invoke(File basedir, VirtualChannel channel) throws IOException, InterruptedException
    {        
        for(String item : this.patterns) {
            System.err.println("looking for valgrind files in '" + basedir.getAbsolutePath() + "' with pattern '" + item + "'");
            ValgrindLogger.logFine("looking for valgrind files in '" + basedir.getAbsolutePath() + "' with pattern '" + item + "'");
        }
        
        ValgrindReport valgrindReport = new ValgrindReport();
        
        for ( String fileName : findValgrindsReports( basedir ) )
        {
            ValgrindLogger.logFine("parsing " + fileName + "...");
            try
            {
                ValgrindReport report = new ValgrindSaxParser().parse( new File(basedir, fileName) );
                if(report != null && report.isValid())
                {
                    valgrindReport.integrate( report );
                }
                else
                {
                    valgrindReport.addParserError(fileName, "no valid data");
                }
            }
            catch (Exception e)
            {
                valgrindReport.addParserError(fileName, e.getMessage());
            }
        }
        
        
        return valgrindReport;
    }
    
    private String[] findValgrindsReports(File parentPath)
    {
        Vector<String> file_list = new Vector<String>();        
        for( String el : patterns ) {
            String name = base_path+el;
            FileSet fs = Util.createFileSet(parentPath, this.base_path+el);        
            DirectoryScanner ds = fs.getDirectoryScanner();
            String[] files  = ds.getIncludedFiles();
            file_list.addAll(Arrays.asList(ds.getIncludedFiles()));
        }
        return file_list.toArray(new String[file_list.size()]);
    }
    
    @Override
    public void checkRoles(RoleChecker rc) throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
