package net.cacpixel.rtmmetro.traincontrollerdemo;

import jp.ngt.ngtlib.io.NGTLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

//code from usb4java

public final class LibRxtxLoader
{
    private static final int BUFFER_SIZE = 8192;
    private static File tmp;
    private static boolean loaded = false;
    private static final String tmpDirName = "librxtx";

    private static final String libFileName = "rxtxSerial";

    private LibRxtxLoader()
    {
        // Nothing to do here
    }

    static String getOS()
    {
        final String os = System.getProperty("os.name").toLowerCase()
                .replace(" ", "");
        if (os.contains("windows"))
        {
            return "win32";
        }
        if (os.equals("macosx") || os.equals("macos"))
        {
            return "darwin";
        }
        return os;
    }

    private static String getArch()
    {
        final String arch = System.getProperty("os.arch").toLowerCase()
                .replace(" ", "");
        if (arch.equals("i386"))
        {
            return "x86";
        }
        if (arch.equals("amd64") || arch.equals("x86_64"))
        {
            return "x86-64";
        }
        if (arch.equals("arm64"))
        {
            return "aarch64";
        }
        if (arch.equals("armhf") || arch.equals("aarch32") || arch.equals("armv7l"))
        {
            return "arm";
        }
        return arch;
    }

    public static String getExt()
    {
        final String os = getOS();
        final String key = "usb4java.libext." + getOS();
        final String ext = System.getProperty(key);
        if (ext != null)
        {
            return ext;
        }
        if (os.equals("linux") || os.equals("freebsd") || os.equals("sunos"))
        {
            return "so";
        }
        if (os.equals("win32"))
        {
            return "dll";
        }
        if (os.equals("darwin"))
        {
            return "dylib";
        }
        throw new RuntimeException("Unable to determine the shared library "
                + "file extension for operating system '" + os
                + "'. Please specify Java parameter -D" + key
                + "=<FILE-EXTENSION>");
    }

    static String getPlatform()
    {
        return getOS() + "-" + getArch();
    }

    public static void addPathToJavaLibraryPath(String libDir)
    {
        String lib = System.getProperty("java.library.path");
        if (!lib.contains(libDir))
        {
            System.setProperty("java.library.path", libDir + System.getProperty("path.separator") + lib);
            Field fieldSysPath;
            try
            {
                fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            }
            catch (NoSuchFieldException e)
            {
                throw new RuntimeException(e);
            }
            fieldSysPath.setAccessible(true);
            try
            {
                fieldSysPath.set(null, null);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private static File createTempDirectory(String dir)
    {
        if (tmp != null)
        {
            return tmp;
        }

        try
        {
//            tmp = new File(dir);
//            File libFile = new File(tmp, getLibName() + "." + getExt());
            tmp = File.createTempFile(tmpDirName, null);
            if (!tmp.isDirectory())
            {
                tmp.delete();
                if (!tmp.mkdirs())
                {
                    throw new IOException("Unable to create temporary directory "
                            + tmp);
                }
            }
//            libFile.delete();
            return tmp;
        }
        catch (final IOException | NullPointerException e)
        {
            throw new RuntimeException("Unable to create temporary directory "
                    + "for " + libFileName + " natives: " + e, e);
        }
    }

    static void extractLibrary(final String platform,
                               final String lib)
    {
        final String source = '/' + platform + "/" + lib;
        final URL url = LibRxtxLoader.class.getResource(source);
        if (url == null)
        {
            throw new RuntimeException("Native library not found in classpath: "
                    + source);
        }
        NGTLog.debug("url of lib file: " + url);

        final File dest = new File(createTempDirectory("./" + tmpDirName), lib);
        addPathToJavaLibraryPath(dest.getParent());
        try
        {
            final InputStream stream = LibRxtxLoader.class.getResourceAsStream(source);
            if (stream == null)
            {
                throw new RuntimeException("Unable to find " + source
                        + " in the classpath");
            }
            try
            {
                copy(stream, dest);
            }
            finally
            {
                stream.close();
            }
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Unable to extract native library "
                    + source + " to " + dest + ": " + e, e);
        }
    }

    private static void copy(final InputStream input, final File output)
            throws IOException
    {
        final byte[] buffer = new byte[BUFFER_SIZE];
        try (FileOutputStream stream = new FileOutputStream(output))
        {
            int read;
            while ((read = input.read(buffer)) != -1)
            {
                stream.write(buffer, 0, read);
            }
        }
    }

    public static String getLibName()
    {
        if (getOS().equals("win32"))
        {
            return libFileName;
        }
        else
        {
            return "lib" + libFileName;
        }
    }

    public static synchronized void load()
    {
        if (loaded)
        {
            return;
        }

        final String platform = getPlatform();
        final String lib = getLibName() + "." + getExt();
        extractLibrary(platform, lib);
        System.loadLibrary(getLibName());
        loaded = true;
    }

    public static boolean isLoaded()
    {
        return loaded;
    }
}


