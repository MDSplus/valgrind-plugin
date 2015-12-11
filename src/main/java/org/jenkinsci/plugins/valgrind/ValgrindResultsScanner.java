package org.jenkinsci.plugins.valgrind;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Vector;

import org.apache.tools.ant.DirectoryScanner;
import org.jenkinsci.remoting.RoleChecker;

public class ValgrindResultsScanner implements FilePath.FileCallable<String[]>
{
	private static final long	serialVersionUID	= -5475538646374717099L;
	private String				pattern;
        private String base_path;
        private List<String> patterns;   


	public ValgrindResultsScanner(String pattern)
	{
		this.pattern = pattern;                
                this.patterns = Arrays.asList(pattern.split(","));        

	}

	public String[] invoke(File basedir, VirtualChannel channel) throws IOException, InterruptedException
	{
            Vector<String> file_list = new Vector<String>();        
            for (String el : patterns ) {
                DirectoryScanner ds = new DirectoryScanner();
                String[] includes = { el };
                ds.setIncludes(includes);
                ds.setBasedir(basedir);
                ds.setCaseSensitive(true);
                ds.scan();
                file_list.addAll(Arrays.asList(ds.getIncludedFiles()));
            }
            return file_list.toArray(new String[file_list.size()]);
	}

    @Override
    public void checkRoles(RoleChecker rc) throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
