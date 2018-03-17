package de.znews.server.lib;

public class FileSizeUtil
{
    
    public static String displaySize(long bytes)
    {
        return displaySize(bytes, true);
    }
    
    public static String displaySize(long bytes, boolean si)
    {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int    exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1));
        if (!si)
            pre += "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
}
