package codechicken.lib.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A simple directory walker similar to Apache's except not dumb.
 * Created by covers1624 on 17/07/2017.
 */
public class DirectoryWalker {

    private final Predicate<File> folder_filter;
    private final Predicate<File> file_filter;

    public static final Predicate<File> TRUE = file -> true;
    public static final Predicate<File> FALSE = file -> false;

    public DirectoryWalker(Predicate<File> folder_filter, Predicate<File> file_filter) {

        this.folder_filter = folder_filter;
        this.file_filter = file_filter;
    }

    public List<File> walk(File folder) {
        List<File> files = new LinkedList<>();
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    if (file.isDirectory() && folder_filter.test(file)) {
                        files.addAll(walk(file));
                    } else if (file.isFile() && file_filter.test(file)) {
                        files.add(file);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Input was not a directory.");
        }
        return files;
    }

}
