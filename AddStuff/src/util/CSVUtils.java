package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVUtils implements IConstants {
	private static String mPropDelimiter = ";\t";

	public static FileWriter getFileWriter(final File pFile) throws IOException {
		final FileWriter fw;
		fw = new FileWriter(pFile);
		return fw;
	}

	//TODO develop only
	public static void readCSVToTXT(final String pFile) throws IOException {
		final CSVReader reader = new CSVReader(new FileReader(pFile));
		final String path = pFile.replace(CSV_FILE, TXT_FILE);
		final File dataFile = new File(path);
		final FileWriter fw = getFileWriter(dataFile);
		String[] row;
		StringBuffer sb;
		while ((row = reader.readNext()) != null) {
			sb = new StringBuffer();
			for (String element : row) {
				sb.append(element + getPropDelimiter());
			}
			sb.append(NEW_LINE);
			fw.write(sb.toString());
		}
		reader.close();
		fw.flush();
		fw.close();
	}

	public static void writeData(final String[] pColumns, final List<String[]> pData, final String pWholeFilePath)
			throws IOException {
		final File file = new File(pWholeFilePath);
		final FileWriter fw = getFileWriter(file);
		final CSVWriter writer = new CSVWriter(fw);
		writer.writeNext(pColumns);
		writer.writeAll(pData);
		writer.close();
	}

	public static String getPropDelimiter() {
		return mPropDelimiter;
	}

	public static void setPropDelimiter(String pPropDelimiter) {
		mPropDelimiter = pPropDelimiter;
	}

}
