package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author dwg
 * @version V1.0
 * @date 2018年10月12日
 */

public class svnHistory {
    private static String urlString = "https://172.100.20.154/svn/吮指原味鸡";
    private static String userName = "zwx566648";
    private static String password = "zpp123456";
    static boolean readonly = true;
    private static String tempDir = System.getProperty("java.io.tmpdir");
    private static DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(readonly);
    private static Random random = new Random();

    private static SVNRepository repos;
    private static ISVNAuthenticationManager authManager;

    public static void main(String[] args) throws Exception {
        ISVNAuthenticationManager authManager = SVNWCUtil
                .createDefaultAuthenticationManager(new File(tempDir + "/auth"), userName, password.toCharArray());
        options.setDiffCommand("-x -w");
        repos = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(urlString));
        repos.setAuthenticationManager(authManager);
        try {
        	File file = new File("D://log//svn.log");
            PrintStream out = new PrintStream(file);
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String sr = "0";
        long startRevision = Long.parseLong(sr);
        long endRevision = -1;// 最后一个版本

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = sdf.parse("2018-10-11");
        Date end = sdf.parse("2018-10-16");

        setupLibrary();


        SVNRepository repository = null;

        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(urlString));
        } catch (SVNException svne) {
            System.err.println(
                    "error while creating an SVNRepository for the location '" + urlString + "': " + svne.getMessage());
            System.exit(1);
        }

        authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repository.setAuthenticationManager(authManager);

        try {
            endRevision = repository.getLatestRevision();
        } catch (SVNException svne) {
            System.err.println("error while fetching the latest repository revision: " + svne.getMessage());
            System.exit(1);
        }

        Collection logEntries = null;
        try {
            logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);

        } catch (SVNException svne) {
            System.out.println("error while collecting log information for '" + urlString + "': " + svne.getMessage());
            System.exit(1);
        }


        for (Iterator entries = logEntries.iterator(); entries.hasNext();) {

            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

            if(logEntry.getDate().after(begin) && logEntry.getDate().before(end)) {

                System.out.println("---------------------------------------------");

                System.out.println("revision: " + logEntry.getRevision());

                System.out.println("author: " + logEntry.getAuthor());

                System.out.println("date: " + sdf.format(logEntry.getDate()));

                if (logEntry.getChangedPaths().size() > 0) {
                    System.out.println("changed paths:");

                    long lastVersion = logEntry.getRevision() - 1;
                    File logFile = getChangeLog(lastVersion, logEntry.getRevision());
                    int addSize = staticticsCodeAdd(logFile);
                }
            }
        }
    }

    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }

    /**
     * 获取版本比较日志，并存入临时文件
     *
     * @param startVersion
     * @param endVersion
     * @return
     * @throws SVNException
     * @throws IOException
     */
    public static File getChangeLog(long startVersion, long endVersion) throws SVNException, IOException {
        SVNDiffClient diffClient = new SVNDiffClient(authManager, options);
        diffClient.setGitDiffFormat(true);
        File tempLogFile = null;
        OutputStream outputStream = null;
        String svnDiffFile = null;

        do {
            svnDiffFile = tempDir + "/svn_diff_file_" + startVersion + "_" + endVersion + "_" + random.nextInt(10000)
                    + ".txt";
            tempLogFile = new File(svnDiffFile);
        } while (tempLogFile != null && tempLogFile.exists());
        try {
            tempLogFile.createNewFile();
            outputStream = new FileOutputStream(svnDiffFile);
            diffClient.doDiff(SVNURL.parseURIEncoded(urlString), SVNRevision.create(startVersion),
                    SVNURL.parseURIEncoded(urlString), SVNRevision.create(endVersion),
                    org.tmatesoft.svn.core.SVNDepth.UNKNOWN, true, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return tempLogFile;
    }

    /**
     * 分析变更的代码，统计代码增量
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static int staticticsCodeAdd(File file) throws Exception {
        FileReader fileReader = new FileReader(file);
        BufferedReader in = new BufferedReader(fileReader);
        int sum = 0;
        String line = null;
        StringBuffer buffer = new StringBuffer(1024);
        boolean start = false;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("Index:")) {
                if (start) {
                    ChangeFile changeFile = parseChangeFile(buffer);
                    int oneSize = staticOneFileChange(changeFile);
                    int delSize = staticOneFileChangeDel(changeFile);
                    System.out.println(changeFile.getChangeType() + "\t" + "addLines=" + oneSize + "   delLines="
                            + delSize + "\t" + changeFile.getFilePath());
                    sum += oneSize;
                    buffer.setLength(0);
                }
                start = true;
            }
            buffer.append(line).append('\n');
        }
        if (buffer.length() > 0) {
            ChangeFile changeFile = parseChangeFile(buffer);
            int oneSize = staticOneFileChange(changeFile);
            int delSize = staticOneFileChangeDel(changeFile);
            System.out.println(changeFile.getChangeType() + "\t" + "addLines=" + oneSize + "   delLines=" + delSize
                    + "\t" + changeFile.getFilePath());
            sum += oneSize;
        }
        in.close();
        fileReader.close();
        boolean deleteFile = file.delete();
        // System.out.println("-----delete file-----"+deleteFile);
        return sum;
    }

    /**
     * 解析单个文件变更日志
     *
     * @param str
     * @return
     */
    public static ChangeFile parseChangeFile(StringBuffer str) {
        int index = str.indexOf("\n@@");
        if (index > 0) {
            String header = str.substring(0, index);
            String[] headers = header.split("\n");
            String filePath = headers[0].substring(7);
            char changeType = 'U';
            boolean oldExist = !headers[2].endsWith("(nonexistent)");
            boolean newExist = !headers[3].endsWith("(nonexistent)");
            if (oldExist && !newExist) {
                changeType = 'D';
            } else if (!oldExist && newExist) {
                changeType = 'A';
            } else if (oldExist && newExist) {
                changeType = 'M';
            }
            int bodyIndex = str.indexOf("@@\n") + 3;
            String body = str.substring(bodyIndex);
            ChangeFile changeFile = new ChangeFile(filePath, changeType, body);
            return changeFile;
        } else {
            String[] headers = str.toString().split("\n");
            String filePath = headers[0].substring(7);
            ChangeFile changeFile = new ChangeFile(filePath, 'U', null);
            return changeFile;
        }
    }

    /**
     * 统计单个文件的增加行数，（先通过过滤器，如文件后缀、文件路径等等），也可根据修改类型来统计等，这里只统计增加或者修改的文件
     *
     * @param changeFile
     * @return
     */
    public static int staticOneFileChange(ChangeFile changeFile) {
        char changeType = changeFile.getChangeType();
        if (changeType == 'A') {
            return countAddLine(changeFile.getFileContent());
        } else if (changeType == 'M') {
            return countAddLine(changeFile.getFileContent());
        }
        return 0;
    }

    /**
     * 统计单个文件的删除行数，（先通过过滤器，如文件后缀、文件路径等等），也可根据修改类型来统计等，这里只统计增加或者修改的文件
     *
     * @param changeFile
     * @return
     */
    public static int staticOneFileChangeDel(ChangeFile changeFile) {
        char changeType = changeFile.getChangeType();
        if (changeType == 'A') {
            return countdelLine(changeFile.getFileContent());
        } else if (changeType == 'M') {
            return countdelLine(changeFile.getFileContent());
        }
        return 0;
    }

    /**
     * 通过比较日志，统计以+号开头的非空行
     *
     * @param content
     * @return
     */
    public static int countAddLine(String content) {
        int sum = 0;
        if (content != null) {
            content = '\n' + content + '\n';
            char[] chars = content.toCharArray();
            int len = chars.length;
            // 判断当前行是否以+号开头
            boolean startPlus = false;
            // 判断当前行，是否为空行（忽略第一个字符为加号）
            boolean notSpace = false;

            for (int i = 0; i < len; i++) {
                char ch = chars[i];
                if (ch == '\n') {
                    // 当当前行是+号开头，同时其它字符都不为空，则行数+1
                    if (startPlus && notSpace) {
                        sum++;
                        notSpace = false;
                    }
                    // 为下一行做准备，判断下一行是否以+头
                    if (i < len - 1 && chars[i + 1] == '+') {
                        startPlus = true;
                        // 跳过下一个字符判断，因为已经判断了
                        i++;
                    } else {
                        startPlus = false;
                    }
                } else if (startPlus && ch > ' ') {// 如果当前行以+开头才进行非空行判断
                    notSpace = true;
                }
            }
        }

        return sum;
    }

    /**
     * 通过比较日志，统计以-号开头的非空行
     *
     * @param content
     * @return
     */
    public static int countdelLine(String content) {
        int sum = 0;
        if (content != null) {
            content = '\n' + content + '\n';
            char[] chars = content.toCharArray();
            int len = chars.length;
            // 判断当前行是否以-号开头
            boolean startPlus = false;
            // 判断当前行，是否为空行（忽略第一个字符为加号）
            boolean notSpace = false;

            for (int i = 0; i < len; i++) {
                char ch = chars[i];
                if (ch == '\n') {
                    // 当当前行是-号开头，同时其它字符都不为空，则行数+1
                    if (startPlus && notSpace) {
                        sum++;
                        notSpace = false;
                    }
                    // 为下一行做准备，判断下一行是否以-头
                    if (i < len - 1 && chars[i + 1] == '-') {
                        startPlus = true;
                        // 跳过下一个字符判断，因为已经判断了
                        i++;
                    } else {
                        startPlus = false;
                    }
                } else if (startPlus && ch > ' ') {// 如果当前行以-开头才进行非空行判断
                    notSpace = true;
                }
            }
        }

        return sum;
    }
}
