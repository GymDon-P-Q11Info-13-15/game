package de.gymdon.inf1315.game;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {

    public static List<String> getResourceListing(ClassLoader cl) throws URISyntaxException, IOException {
        URL dirURL = cl.getResource("");
        if (dirURL != null) {
            if (dirURL.getProtocol().equals("file")) {
                File f = new File(dirURL.toURI());
                return recurse(f, f.getAbsolutePath().replace('\\', '/'));
            }

            if (dirURL.getProtocol().equals("jar")) {
                String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                Enumeration<JarEntry> entries = jar.entries();
                Set<String> result = new HashSet<>();
                while (entries.hasMoreElements())
                    result.add("/" + entries.nextElement().getName().replace('\\', '/'));
                jar.close();
                return new ArrayList<>(result);
            }
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    public static List<String> getResourceListing(ClassLoader cl, String prefix) throws URISyntaxException, IOException {
        List<String> l = getResourceListing(cl);
        l.removeIf(s -> !s.startsWith(prefix));
        return l;
    }

    private static List<String> recurse(File dir, @Nullable List<String> append, String relative) {
        if (append == null)
            append = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return append;
        for (File f : files) {
            if (f.isDirectory())
                recurse(f, append, relative);
            else
                append.add(f.getAbsolutePath().replace('\\', '/').substring(relative.length()));
        }
        return append;
    }

    private static List<String> recurse(File directory, String relative) {
        return recurse(directory, null, relative);
    }

}
