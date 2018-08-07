package boa.test.datagen;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import boa.datagen.util.FileIO;
import boa.evaluator.BoaEvaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class TestQueries {

	@Test
	public void testBugFix() {
		try {
			Process p = Runtime.getRuntime().exec("./boa.sh -e "
					+ "-i test/known-good/bug-fix.boa "
					+ "-d test/datagen/test_datagen "
					+ "-o test/datagen/Bug-fix_output");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			 while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String expected = "AddedNullCheck[] = 1\n";
		File outputDir = new File("test/datagen/Bug-fix_output");
		String actual = getResults(outputDir);// evaluator.getResults();
		try {
			FileUtils.deleteDirectory(outputDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testq20() {
		try {
			Process p = Runtime.getRuntime().exec("./boa.sh -e "
					+ "-i test/known-good/q20.boa "
					+ "-d test/datagen/test_datagen "
					+ "-o test/datagen/q20_output");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			 while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String expected = "TransientMax[] = 140492550, 3.0\n"
				+ "TransientMean[] = 0.0410958904109589\n"
				+ "TransientMin[] = 140492550, 0.0\n"
				+ "TransientTotal[] = 3\n"
				+ "VolatileMax[] = 140492550, 1.0\n"
				+ "VolatileMean[] = 0.0136986301369863\n"
				+ "VolatileMin[] = 140492550, 0.0\n"
				+ "VolatileTotal[] = 1\n";
		File outputDir = new File("test/datagen/q20_output");
		String actual = getResults(outputDir);// evaluator.getResults();
		try {
			FileUtils.deleteDirectory(outputDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}

	@Test
	public void testAnnot_names() {
		try {
			Process p = Runtime.getRuntime().exec("./boa.sh -e "
					+ "-i test/known-good/annot-names.boa "
					+ "-d test/datagen/test_datagen "
					+ "-o test/datagen/annot_names_output");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			 while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String expected = "AnnotUse[FunctionalInterface] = 3\n"
				+ "AnnotUse[Override] = 11\n"
				+ "AnnotUse[Retention] = 1\n"
				+ "AnnotUse[SafeVarargs] = 1\n"
				+ "AnnotUse[SuppressWarnings] = 1\n"
				+ "AnnotUse[Target] = 2\n"
				+ "AnnotUse[ThreadSafe] = 1\n"
				+ "AnnotUse[ToDo] = 17\n";
		File outputDir = new File("test/datagen/annot_names_output");
		String actual = getResults(outputDir);// evaluator.getResults();
		try {
			FileUtils.deleteDirectory(outputDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAnnotations_define() {
		try {
			Process p = Runtime.getRuntime().exec("./boa.sh -e "
					+ "-i test/known-good/annotations-define.boa "
					+ "-d test/datagen/test_datagen "
					+ "-o test/datagen/annot_define_output");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			 while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String expected = "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnDemo/v1/ThreadSafe.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnDemo/v2/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnDemo/v3/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnDemo/v4/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnDemo/v5/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnProcDemo/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS3/AnnProcDemo/ToDo.java][1531880750000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS8/RADemo/ToDo.java][1531520725000000] = 1\n"
				+ "USES[https://github.com/boalang/test-datagen][src/JLS8/RADemo/ToDos.java][1531520725000000] = 1\n";
		File outputDir = new File("test/datagen/annot_define_output");
		String actual = getResults(outputDir);// evaluator.getResults();
		try {
			FileUtils.deleteDirectory(outputDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	
	public String getResults(File outputDir) {
		for (final File f : outputDir.listFiles()) {
			if (f.getName().startsWith("part")) {
				return FileIO.readFileContents(f);
			}
		}
		return "";
	}
}
