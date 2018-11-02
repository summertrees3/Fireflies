package code;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class svnCheckout {
	// 声明SVN客户端管理类
	private static SVNClientManager ourClientManager;

	public static void main(String... args) throws Exception {
		/*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
		// 相关变量赋值
		SVNURL repositoryURL = null;
		try {
			repositoryURL = SVNURL.parseURIEncoded("https://172.100.20.154/svn/吮指原味鸡");
		} catch (SVNException e) {
			System.out.println("无法连接");
		}
		String name = "zwx566648";
		String password = "zpp123456";
		String workPath = "E:/svntest";
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		// 实例化客户端管理类
		ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, name, password);
		// 要把版本库的内容check out到的目录
		// FIle wcDir = new File("E:/svntest")
		File wcDir = new File(workPath);
		// 通过客户端管理类获得updateClient类的实例。
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		// sets externals not to be ignored during the checkout
		updateClient.setIgnoreExternals(false);
		// 执行check out 操作，返回工作副本的版本号。

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse("2018-10-08 12:00:00");
		long rn = 100;
		long workingVersion = updateClient.doCheckout(repositoryURL, wcDir, SVNRevision.create(rn),
				SVNRevision.create(rn), SVNDepth.INFINITY, false);
		System.out.println("把版本：" + workingVersion + " checkout到目录：" + wcDir + "中。");
	}
}
