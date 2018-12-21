package commonutils.text;

import commonutils.array.ArrayUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class Path {

    private boolean isWindows;
    private String separator;
    private String[] paths;
    private String rootPath = null;
    private int current;

    public void init(String osFamily, String separator) {
        this.isWindows = getOsFamily(osFamily).equals("windows");
        this.separator = null == separator ? getSeparator(getOsFamily(osFamily)) : separator;
    }

    public Path(String path) {
        assert StringUtil.isNotBlank(path) : "path cant not be empty!";
        init(null, null);
        this.paths = path.split(this.separator);
        if (isWindows) {
            if (path.indexOf(":") > 0 && this.paths.length >= 2) {
                this.rootPath = paths[1];
            }
        } else {
            if (path.startsWith("/")) {
                this.rootPath = "/";
            }
        }
        this.current = paths.length - 1;
    }

    public Path(String path, String osFamily, String separator) {
        assert StringUtil.isNotBlank(path) : "path cant not be empty!";
        init(osFamily, separator);
        this.paths = path.split(this.separator);
    }

    public String getRootPath() {
        return rootPath;
    }

    public Path parent() {
        if (isWindows) {
            int parent = current - 1 > 1 ? current - 1 : current;
            return new Path(toString(parent));
        } else {
            int parent = current - 1 > 0 ? current - 1 : current;
            return new Path(toString(parent));
        }
    }

    public Path join(String child) {
        String[] pathsToBeJoined = dealChild(child);
        String[] pathsJoined = Arrays.copyOf(paths, paths.length + pathsToBeJoined.length);
        System.arraycopy(pathsToBeJoined, 0, pathsJoined, paths.length, pathsToBeJoined.length);
        paths = pathsJoined;
        return this;
    }

    public Path join(String... children) {
        List<String> paths2 = Arrays.asList(paths);
        for (String child : children) {
            paths2.addAll(Arrays.asList(dealChild(child)));
        }
        paths = paths2.toArray(new String[0]);
        return this;
    }

    private String[] dealChild(String child) {
        assert StringUtil.isNotBlank(child);
        while (child.endsWith(separator) && child.length() > 0) {
            child = child.substring(0, child.length() - 1);
        }
        while (child.startsWith(separator) && child.length() > 0) {
            child = child.substring(1);
        }
        return child.split(separator);
    }

    private String toString(int index) {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i >= 0; i--) {
            sb.insert(0, paths[i]);
            if (i != index && i != 0) {
                sb.insert(0, separator);
            }
        }
        return sb.toString();
    }

    private String getOsFamily(String os) {
        os = StringUtil.isBlank(os) ? System.getProperty("os.name") : os;
        String family = "Unknown";
        if (os.contains("nix") || os.contains("mac")) {
            family = "unix";
        } else if (os.contains("indows")) {
            family = "windows";
        }
        return family;
    }

    private String getSeparator(String osFamily) {
        String separator = System.getProperty("file.separator");
        if (osFamily.equals("windows")) {
            separator = "\\";
        } else if (osFamily.equals("unix")) {
            separator = "/";
        }
        return separator;
    }

    @Override
    public String toString() {
        return ArrayUtil.mkString(separator, paths);
    }
}
