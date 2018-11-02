package code;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author dwg
 * @version V1.0
 * @date 2018年10月11日
 */
public class gitHistory extends TestCase {

	String gitFilePath = "E:\\resume-system";
	File root = new File(gitFilePath);
	Git git;
	Repository repository;

	@Before
	public void init() {
		try {
			git = Git.open(root);
			repository = git.getRepository();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Exception {

		try {
			File file = new File("D://log//git.log");
			PrintStream out = new PrintStream(file);
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		init();
		RevWalk walk = new RevWalk(repository);
		List<RevCommit> commitList = new ArrayList<RevCommit>();
		// 获取提交的记录
		Iterable<RevCommit> commits = git.log().call();

		for (RevCommit commit : commits) {
			commitList.add(commit);
		}

		String dateStart = "2018-10-10 00:00:00";

		for (int i = commitList.size() - 1; i > 0; i--) {
			AbstractTreeIterator newTree = prepareTreeParser(commitList.get(i));
			AbstractTreeIterator oldTree = prepareTreeParser(commitList.get(i - 1));
			List<DiffEntry> diff = git.diff().setOldTree(oldTree).setNewTree(newTree).setShowNameAndStatusOnly(true)
					.call();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DiffFormatter df = new DiffFormatter(out);
			// 设置比较器为忽略空白字符对比（Ignores all whitespace）
			df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
			df.setRepository(git.getRepository());

			Date dateA = commitList.get(i - 1).getAuthorIdent().getWhen();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateB = sdf.parse(dateStart);

			if (dateA.before(dateB)) {
				continue;
			}
			String sdfDate = sdf.format(dateA);
			System.out.println(commitList.get(i - 1).getAuthorIdent().getName() + "|" + sdfDate);
			// 每一个diffEntry都是第个文件版本之间的变动差异
			for (DiffEntry diffEntry : diff) {
				df.format(diffEntry);
				String diffText = out.toString("UTF-8");
				String[] line = diffText.split("\n");
				String str = line[0].split("b/")[line[0].split("b/").length - 1];

				// System.out.println(diffText);

				// 获取文件差异位置，从而统计差异的行数（增加行数，减少行数）
				FileHeader fileHeader = df.toFileHeader(diffEntry);
				List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();
				int addLines = 0;
				int subLines = 0;
				for (HunkHeader hunkHeader : hunks) {
					EditList editList = hunkHeader.toEditList();
					for (Edit edit : editList) {
						addLines += edit.getEndA() - edit.getBeginA();
						subLines += edit.getEndB() - edit.getBeginB();

					}

				}
				System.out.println("addLines=" + addLines + "\t" + "subLines=" + subLines + "\t" + str);

				out.reset();
			}
			System.out.println("---------------------------------------------");
		}

	}

	public AbstractTreeIterator prepareTreeParser(RevCommit commit) {
		// System.out.println(commit.getId());
		try (RevWalk walk = new RevWalk(repository)) {
			// System.out.println(commit.getTree().getId());
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
			try (ObjectReader oldReader = repository.newObjectReader()) {
				oldTreeParser.reset(oldReader, tree.getId());
			}

			walk.dispose();

			return oldTreeParser;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
