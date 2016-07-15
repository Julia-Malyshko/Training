package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVUtils implements IConstants {

	public static String[] readCSVHeader(final String pWholeFilePath) throws IOException {
		final FileReader fileReader = new FileReader(pWholeFilePath);
		final CSVReader reader = new CSVReader(fileReader);
		final String[] header;
		header = reader.readNext();
		reader.close();
		return header;
	}

	protected static boolean readMore(final CSVReader pReader, final int pNumLines) {
		final boolean readMoreLines;
		readMoreLines = pReader.getRecordsRead() < pNumLines;
		return readMoreLines;
	}

	public static List<String[]> readCSV(final String pWholeFilePath, final int pNumSkippedLines, final int pNumLines)
			throws IOException {
		final FileReader fileReader = new FileReader(pWholeFilePath);
		final CSVReader reader = new CSVReader(fileReader, pNumSkippedLines, new CSVParser());
		final List<String[]> lines = new ArrayList<>();
		String[] row;
		while (readMore(reader, pNumLines) && (row = reader.readNext()) != null) {
			lines.add(row);
		}
		reader.close();
		return lines;
	}

	public static void writeData(final String[] pColumns, final List<String[]> pData, final String pWholeFilePath)
			throws IOException {
		final File file = new File(pWholeFilePath);
		final FileWriter fw = new FileWriter(file);
		final CSVWriter writer = new CSVWriter(fw);
		writer.writeNext(pColumns);
		writer.writeAll(pData);
		writer.close();
	}
}
