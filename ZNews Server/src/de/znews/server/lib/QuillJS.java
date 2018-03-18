package de.znews.server.lib;

import com.coloredcarrot.jsonapi.Json;
import com.coloredcarrot.jsonapi.ast.JsonArray;
import de.znews.server.Log;
import de.znews.server.ZNews;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

@Deprecated
public class QuillJS
{
    
    /**
     * Converts the given Delta to HTML using JavaScript.
     *
     * @param delta The Quill Delta
     * @param znews The ZNews instance
     * @return The Delta, rendered as HTML
     */
    public static String renderAsHTML(JsonArray delta, ZNews znews)
    {
        
        /*
        Problem:
        jvm-npm require() cannot find modules
        How to fix:
        No idea
         */
        
        File jvmNpmFile       = znews.staticWeb.getFile("/jvm-npm/jvm-npm.js");
        File cheerioFile      = znews.staticWeb.getFile("/cheerio/lib/cheerio.js");
        File jqueryFile       = znews.staticWeb.getFile("/js/jquery.min.js");
        File hightlightjsFile = znews.staticWeb.getFile("/js/highlightjs.min.js");
        File katexFile        = znews.staticWeb.getFile("/js/katex.min.js");
        File quillFile        = znews.staticWeb.getFile("/js/quill.min.js");
        File converterFile    = znews.staticWeb.getFile("/quill_data_to_html.js");
        
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine        se  = sem.getEngineByName("JavaScript");
        
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jvmNpmFile))))
        {
            se.eval(reader);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
        catch (ScriptException e)
        {
            Log.warn("Could not load library script " + jvmNpmFile.getName() + ": " + e.getMessage() + " (" + e.getFileName() + ":" + e.getLineNumber() + ":" + e.getColumnNumber() + " in " + jvmNpmFile.getAbsolutePath() + ")", e);
            return null;
        }
        
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cheerioFile))))
        {
            se.eval(reader);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
        catch (ScriptException e)
        {
            Log.warn("Could not load library script " + cheerioFile.getName() + ": " + e.getMessage() + " (" + e.getFileName() + ":" + e.getLineNumber() + ":" + e.getColumnNumber() + " in " + cheerioFile.getAbsolutePath() + ")", e);
            return null;
        }
        
        try
        {
            se.eval("var $ = require('cheerio').load('<div><div></div></div>')");
        }
        catch (ScriptException e)
        {
            Log.warn("Could not import Cheerio", e);
            return null;
        }
        
        for (File libFile : new File[] { /*jqueryFile,*/ hightlightjsFile, katexFile, quillFile, converterFile })
            try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(libFile))))
            {
                se.eval(reader);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
            catch (ScriptException e)
            {
                Log.warn("Could not load library script " + libFile.getName() + ": " + e.getMessage() + " (" + e.getFileName() + ":" + e.getLineNumber() + ":" + e.getColumnNumber() + " in " + libFile.getAbsolutePath() + ")", e);
                return null;
            }
        
        // JavaScript engine is Invocable
        Invocable ise = (Invocable) se;
        
        Object jsResult = null;
        try
        {
            Object jsDeltaJson = ise.invokeMethod(se.get("JSON"), "parse", Json.toString(delta));
            jsResult = ise.invokeFunction("renderQuillDataAsHTML", jsDeltaJson);
        }
        catch (ScriptException e)
        {
            Log.warn("Could not invoke script: " + e.getMessage() + " (" + e.getFileName() + ":" + e.getLineNumber() + ":" + e.getColumnNumber() + " in " + converterFile.getAbsolutePath() + ")", e);
            return null;
        }
        catch (NoSuchMethodException e)
        {
            Log.warn("Could not invoke script", e);
            return null;
        }
        
        return jsResult.toString();
        
    }
    
}
