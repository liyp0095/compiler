package boa.test.datagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.eclipse.jgit.lib.Constants;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import com.google.protobuf.CodedInputStream;

import boa.datagen.BoaGenerator;
import boa.datagen.DefaultProperties;
import boa.datagen.forges.github.RepositoryCloner;
import boa.datagen.scm.AbstractCommit;
import boa.datagen.scm.GitConnector;
import boa.datagen.util.FileIO;
import boa.functions.BoaAstIntrinsics;
import boa.functions.BoaIntrinsics;
import boa.test.datagen.SequenceFileReader.SequenceFileReaderReducer;
import boa.test.datagen.SequenceFileReader.SequenceFileReaderMapper;
import boa.types.Code.CodeRepository;
import boa.types.Code.CodeRepository.RepositoryKind;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Toplevel.Project;

public class TestBuildSnapshot {
	
	@Test
	public void testBuildSnapshot() throws Exception {
		DefaultProperties.DEBUG = true;
		
		String[] repoNames = new String[]{"candoia/candoia", "boalang/compiler", "junit-team/junit4"};
		for (String repoName : repoNames)
			buildCodeRepository(repoName);
	}
	
	@Ignore
	@Test
	public void testBuildSnapshotWithTypes() throws Exception {
		DefaultProperties.DEBUG = true;
		
		File gitDir = new File("D:/Projects/Boa-compiler/dataset/repos/candoia/candoia");
//		File gitDir = new File("D:/Projects/Boa-compiler/dataset/repos/boalang/compiler");
//		File gitDir = new File("F:\\testrepos\\repos-test\\hoan\\test1");
		if (!gitDir.exists())
			return;
		GitConnector gc = new GitConnector(gitDir.getAbsolutePath(), "condoia");
		gc.setRevisions();
		System.out.println("Finish processing commits");
		List<ChangedFile> snapshot1 = gc.buildHeadSnapshot(new String[]{"java"}, "");
		System.out.println("Finish building head snapshot");
		List<String> snapshot2 = gc.getSnapshot(Constants.HEAD);
		gc.close();
		Set<String> s1 = new HashSet<String>(), s2 = new HashSet<String>(snapshot2), s = new HashSet<String>(s2), in2 = new HashSet<String>(s2);
		for (ChangedFile cf : snapshot1)
			s1.add(cf.getName());
//		print(s1);
//		print(s2);
//		s.retainAll(s1);
//		print(s);
//		in2.removeAll(s1);
//		print(in2);
		System.out.println(s1.size() + " " + s2.size() + " " + s.size() + " " + in2.size());
		assertEquals(s2,  s1);
	}

	private static Configuration conf = new Configuration();
	private static FileSystem fileSystem = null;
	
	private SequenceFile.Writer projectWriter, astWriter, commitWriter, contentWriter;
	private long astWriterLen = 0, contentWriterLen = 0;
	
	@Test
	public void testGetSnapshotFromProtobuf1() throws Exception {
		DefaultProperties.DEBUG = true;
		
		CodeRepository cr = buildCodeRepository("boalang/test-datagen");

		ChangedFile[] snapshot = BoaIntrinsics.getSnapshotById(cr, "8041f1281cf6b615861768631097e22127a1e32e", new String[]{"SOURCE_JAVA_JLS"});
		String[] fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
		assertArrayEquals(new String[]{}, fileNames);
		
		snapshot = BoaIntrinsics.getSnapshotById(cr, "269424473466542fad9c426f7edf7d10a742e2be", new String[]{"SOURCE_JAVA_JLS"});
		fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
		assertArrayEquals(new String[]{"src/Foo.java"}, fileNames);
		
		snapshot = BoaIntrinsics.getSnapshotById(cr, "5e9291c8e830754479bf836686734045faa5c021", new String[]{"SOURCE_JAVA_JLS"});
		fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
		assertArrayEquals(new String[]{}, fileNames);
		
		snapshot = BoaIntrinsics.getSnapshotById(cr, "06288fd7cf36415629e3eafdce2448a5406a8c1e", new String[]{"SOURCE_JAVA_JLS"});
		fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
		assertArrayEquals(new String[]{}, fileNames);
	}
	
	@Test
	public void testGetSnapshotFromProtobuf2() throws Exception {
		DefaultProperties.DEBUG = true;
		
		CodeRepository cr = buildCodeRepository("hyjorc1/my-example");

		ChangedFile[] snapshot = BoaIntrinsics.getSnapshot(cr);
		String[] fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
//			assertArrayEquals(new String[]{}, fileNames);
		
		snapshot = BoaIntrinsics.getSnapshotById(cr, "d7a4aced37af672f9a55238a47bb0e4974193ebe");
		fileNames = new String[snapshot.length];
		for (int i = 0; i < snapshot.length; i++)
			fileNames[i] = snapshot[i].getName();
		assertThat(fileNames, Matchers.hasItemInArray("src/org/birds/Bird.java"));
		assertThat(fileNames, Matchers.not(Matchers.hasItemInArray("src/org/animals/Bird.java")));
	}

	private CodeRepository buildCodeRepository(String repoName) throws Exception {
		fileSystem = FileSystem.get(conf);
		
		System.out.println("Repo: " + repoName);
		File gitDir = new File("dataset/repos/" + repoName);
		openWriters(gitDir.getAbsolutePath());
		FileIO.DirectoryRemover filecheck = new FileIO.DirectoryRemover(gitDir.getAbsolutePath());
		filecheck.run();
		String url = "https://github.com/" + repoName + ".git";
		RepositoryCloner.clone(new String[]{url, gitDir.getAbsolutePath()});
		GitConnector conn = new GitConnector(gitDir.getAbsolutePath(), repoName, astWriter, astWriterLen, contentWriter, contentWriterLen);
		final CodeRepository.Builder repoBuilder = CodeRepository.newBuilder();
		repoBuilder.setKind(RepositoryKind.GIT);
		repoBuilder.setUrl(url);
		for (final Revision rev : conn.getCommits(true, repoName)) {
			final Revision.Builder revBuilder = Revision.newBuilder(rev);
			repoBuilder.addRevisions(revBuilder);
		}
		if (repoBuilder.getRevisionsCount() > 0) {
//			System.out.println("Build head snapshot");
			repoBuilder.setHead(conn.getHeadCommitOffset());
			repoBuilder.addAllHeadSnapshot(conn.buildHeadSnapshot(new String[] { "java" }, repoName));
		}
		repoBuilder.addAllBranches(conn.getBranchIndices());
		repoBuilder.addAllBranchNames(conn.getBranchNames());
		repoBuilder.addAllTags(conn.getTagIndices());
		repoBuilder.addAllTagNames(conn.getTagNames());
		
		closeWriters();

		List<ChangedFile> snapshot1 = new ArrayList<ChangedFile>();
		Map<String, AbstractCommit> commits = new HashMap<String, AbstractCommit>();
		conn.getSnapshot(conn.getHeadCommitOffset(), snapshot1, commits);
//		System.out.println("Finish building head snapshot");
		List<String> snapshot2 = conn.getSnapshot(Constants.HEAD);
		Set<String> s1 = new HashSet<String>(), s2 = new HashSet<String>(snapshot2);
		for (ChangedFile cf : snapshot1)
			s1.add(cf.getName());
//		System.out.println("Test head snapshot");
		assertEquals(s2, s1);

		for (int i = conn.getRevisions().size()-1; i >= 0; i--) {
			AbstractCommit commit = conn.getRevisions().get(i);
			snapshot1 = new ArrayList<ChangedFile>();
			conn.getSnapshot(i, snapshot1, new HashMap<String, AbstractCommit>());
			snapshot2 = conn.getSnapshot(commit.getId());
			s1 = new HashSet<String>();
			s2 = new HashSet<String>(snapshot2);
			for (ChangedFile cf : snapshot1)
				s1.add(cf.getName());
//			System.out.println("Test snapshot at " + commit.getId());
			assertEquals(s2, s1);
		}
		
		CodeRepository cr = repoBuilder.build();
		
		{
			ChangedFile[] snapshot = BoaIntrinsics.getSnapshot(cr);
			String[] fileNames = new String[snapshot.length];
			for (int i = 0; i < snapshot.length; i++)
				fileNames[i] = snapshot[i].getName();
			Arrays.sort(fileNames);
			String[] expectedFileNames = conn.getSnapshot(Constants.HEAD).toArray(new String[0]);
			Arrays.sort(expectedFileNames);
//			System.out.println("Test head snapshot");
			assertArrayEquals(expectedFileNames, fileNames);
		}
		
		for (Revision rev : cr.getRevisionsList()) {
			ChangedFile[] snapshot = BoaIntrinsics.getSnapshotById(cr, rev.getId());
			String[] fileNames = new String[snapshot.length];
			for (int i = 0; i < snapshot.length; i++)
				fileNames[i] = snapshot[i].getName();
			Arrays.sort(fileNames);
			String[] expectedFileNames = conn.getSnapshot(rev.getId()).toArray(new String[0]);
			Arrays.sort(expectedFileNames);
//			System.out.println("Test snapshot at " + rev.getId());
			assertArrayEquals(expectedFileNames, fileNames);
		}
		
		new Thread(new FileIO.DirectoryRemover(gitDir.getAbsolutePath())).start();
		conn.close();
		
		return cr;
	}

	public void openWriters(String base) {
		long time = System.currentTimeMillis();
		String suffix = time + ".seq";
		while (true) {
			try {
				projectWriter = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/project/" + suffix),
						Text.class, BytesWritable.class, CompressionType.BLOCK);
				astWriter = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/ast/" + suffix),
						LongWritable.class, BytesWritable.class, CompressionType.BLOCK);
				commitWriter = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/commit/" + suffix),
						LongWritable.class, BytesWritable.class, CompressionType.BLOCK);
				contentWriter = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/source/" + suffix),
						LongWritable.class, BytesWritable.class, CompressionType.BLOCK);
				break;
			} catch (Throwable t) {
				t.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void closeWriters() {
		while (true) {
			try {
				projectWriter.close();
				astWriter.close();
				commitWriter.close();
				contentWriter.close();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				break;
			} catch (Throwable t) {
				t.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void print(Set<String> s, List<ChangedFile> snapshot, Map<String, AbstractCommit> commits) {
		List<String> l = new ArrayList<String>(s);
		Collections.sort(l);
		for (String f : l)
			System.out.println(f + " " + commits.get(f).getId());
		System.out.println("==========================================");
	}
	
	@Test
	public void testBuildSnapshotFromSeq() throws Exception {
		String dataPath = "dataset/temp_data";
		File dataFile = new File(dataPath);
		dataPath = dataFile.getAbsolutePath();
		
		DefaultProperties.DEBUG = true;
		DefaultProperties.localDataPath = dataPath;
		
		FileIO.DirectoryRemover filecheck = new FileIO.DirectoryRemover(dataFile.getAbsolutePath());
		filecheck.run();
		
		String[] args = {	"-inputJson", "test/datagen/jsons", 
							"-inputRepo", "dataset/repos",
							"-output", dataPath,
							"-size", "1"};
		BoaGenerator.main(args);
		
		fileSystem = FileSystem.get(conf);
		Path projectPath = new Path(dataPath, "projects.seq");
		SequenceFile.Reader pr = new SequenceFile.Reader(fileSystem, projectPath, conf);
		Writable key = new Text();
		BytesWritable val = new BytesWritable();
		while (pr.next(key, val)) {
			byte[] bytes = val.getBytes();
			Project project = Project.parseFrom(CodedInputStream.newInstance(bytes, 0, val.getLength()));
			String repoName = project.getName();
			File gitDir = new File("dataset/repos/" + repoName);
			filecheck = new FileIO.DirectoryRemover(gitDir.getAbsolutePath());
			filecheck.run();
			String url = "https://github.com/" + repoName + ".git";
			RepositoryCloner.clone(new String[]{url, gitDir.getAbsolutePath()});
			GitConnector conn = new GitConnector(gitDir.getAbsolutePath(), repoName);
			
			ChangedFile[] snapshot = getSnapshot(dataPath, repoName, -1);
			String[] fileNames = new String[snapshot.length];
			for (int i = 0; i < snapshot.length; i++)
				fileNames[i] = snapshot[i].getName();
			Arrays.sort(fileNames);
			String[] expectedFileNames = conn.getSnapshot(Constants.HEAD).toArray(new String[0]);
			Arrays.sort(expectedFileNames);
			System.out.println("Test head snapshot");
			assertArrayEquals(expectedFileNames, fileNames);
			
			List<String> commitIds = conn.logCommitIds();
			for (int i = 0; i < commitIds.size(); i++) {
				String cid = commitIds.get(i);
				snapshot = getSnapshot(dataPath, repoName, i);
				fileNames = new String[snapshot.length];
				for (int j = 0; j < snapshot.length; j++) {
					fileNames[j] = snapshot[j].getName();
//					System.out.println(fileNames[j]);
				}
				Arrays.sort(fileNames);
				expectedFileNames = conn.getSnapshot(cid).toArray(new String[0]);
				Arrays.sort(expectedFileNames);
				System.out.println("Test snapshot at " + cid);
				assertArrayEquals(expectedFileNames, fileNames);
			}
			
			filecheck = new FileIO.DirectoryRemover(gitDir.getAbsolutePath());
			filecheck.run();
			conn.close();
		}
		pr.close();
		
		filecheck = new FileIO.DirectoryRemover(dataPath);
		filecheck.run();
	}

	public ChangedFile[] getSnapshot(String dataPath, String repoName, int index) throws Exception {
		SequenceFileReader.repoName = repoName;
		SequenceFileReader.index = index;
		SequenceFileReader.snapshot = null;
		
		File outDir = new File("dataset/temp_output");
		if (outDir.exists())
			new FileIO.DirectoryRemover(outDir.getAbsolutePath()).run();
		
		Configuration conf = new Configuration();
		Job job = new Job(conf, "read sequence file");
		job.setJarByClass(SequenceFileReader.class);
		job.setMapperClass(SequenceFileReaderMapper.class);
		job.setCombinerClass(SequenceFileReaderReducer.class);
		job.setReducerClass(SequenceFileReaderReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat.class);
		FileInputFormat.addInputPath(job, new Path(dataPath, "projects.seq"));
		FileOutputFormat.setOutputPath(job, new Path(outDir.getAbsolutePath()));
		boolean completed = job.waitForCompletion(true);
		assertEquals(completed, true);

		if (outDir.exists())
			new FileIO.DirectoryRemover(outDir.getAbsolutePath()).run();
		
		return SequenceFileReader.snapshot;
	}
}

class SequenceFileReader {
	static String repoName;
	static int index = -1;
	static ChangedFile[] snapshot;

	public static class SequenceFileReaderMapper extends Mapper<Text, BytesWritable, Text, IntWritable> {
		
		@Override
		protected void setup(Mapper<Text, BytesWritable, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			BoaAstIntrinsics.setup(context);
			super.setup(context);
		}

		@Override
		public void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException {
			Project project = Project.parseFrom(CodedInputStream.newInstance(value.getBytes(), 0, value.getLength()));
			if (project.getName().equals(repoName)) {
				for (CodeRepository cr : project.getCodeRepositoriesList()) {
					try {
						if (index == -1)
							snapshot = BoaIntrinsics.getSnapshot(cr);
						else
							snapshot = BoaIntrinsics.getSnapshot(cr, index);
						context.write(key, new IntWritable(1));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		@Override
		protected void cleanup(Mapper<Text, BytesWritable, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			BoaAstIntrinsics.cleanup(context);
			super.cleanup(context);
		}
	}

	public static class SequenceFileReaderReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			assertThat(sum, Matchers.is(1));
			context.write(key, new IntWritable(sum));
		}
	}
}

