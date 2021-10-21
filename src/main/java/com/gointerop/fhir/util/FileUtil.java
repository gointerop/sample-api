package com.gointerop.fhir.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class FileUtil {
	public List<Path> loadFiles(String filePath) throws IOException {
		List<Path> retVal = new ArrayList<Path>();
		Path path = Paths.get(filePath); 

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
		       for (Path entry: stream) {
		           retVal.add(entry);
		       }
		   }

		return retVal;
	}

	public String readFile(File file) throws IOException {
		StringBuilder retVal = new StringBuilder();
		BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
		Stream<String> lines = br.lines();

		lines.forEach(retVal::append);

		return retVal.toString();
	}
}
