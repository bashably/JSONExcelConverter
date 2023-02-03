import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonToExcelConverter {

	private static ObjectMapper mapper = new ObjectMapper();
	static String downloadPath = System.getProperty("user.home") + "/Downloads/conflict18Keys.txt";

	/**
	 * Method to convert json file to excel file
	 *
	 * @param srcFile
	 * @param targetFileExtension
	 * @return file
	 */
	public File jsonFileToExcelFile(File srcFile, String targetFileExtension) {
		try {
			if (!srcFile.getName().endsWith(".json")) {
				throw new IllegalArgumentException("The source file should be .json file only");
			} else {
				Workbook workbook = null;
				//Creating workbook object based on target file format
				if (targetFileExtension.equals(".xls")) {
					workbook = new HSSFWorkbook();
				} else if (targetFileExtension.equals(".xlsx")) {
					workbook = new XSSFWorkbook();
				} else {
					throw new IllegalArgumentException("The target file extension should be .xls or .xlsx only");
				}

				//Reading the json file
				ArrayNode jsonData = (ArrayNode) mapper.readTree(srcFile);
				String sheetName = "GeoInfo";
				Sheet sheet = workbook.createSheet(sheetName);

				ArrayList<String> headers = new ArrayList<>();

				//Creating cell style for header to make it bold
				CellStyle headerStyle = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBold(true);
				headerStyle.setFont(font);

				//creating the header into the sheet
				Row header = sheet.createRow(0);
				Iterator<String> it = jsonData.get(0).fieldNames();
				int headerIdx = 0;
				while (it.hasNext()) {
					String headerName = it.next();
					headers.add(headerName);
					Cell cell = header.createCell(headerIdx++);
					cell.setCellValue(headerName);
					//apply the bold style to headers
					cell.setCellStyle(headerStyle);
				}

				if (!jsonData.isEmpty()) {
					//Iterating over the each row data and writing into the sheet
					for (int i = 0; i < jsonData.size(); i++) {
						ObjectNode rowData = (ObjectNode) jsonData.get(i);
						Row row = sheet.createRow(i + 1);
						for (int j = 0; j < headers.size(); j++) {
							String value = rowData.get(headers.get(j)).asText();
							row.createCell(j).setCellValue(value);
						}
					}
				}
				/*
				 * automatic adjust data in column using autoSizeColumn, autoSizeColumn should
				 * be made after populating the data into the excel. Calling before populating
				 * data will not have any effect.
				 */
				for (int i = 0; i < headers.size(); i++) {
					sheet.autoSizeColumn(i);
				}

//				}

				//creating a target file
				String filename = srcFile.getName();
				filename = filename.substring(0, filename.lastIndexOf(".json")) + targetFileExtension;
				File targetFile = new File(pathWithSeparator("src/main/resources/basedata/xslx/" + filename));

				// write the workbook into target file
				FileOutputStream fos = new FileOutputStream(targetFile);
				workbook.write(fos);

				//close the workbook and fos
				workbook.close();
				fos.close();
				return targetFile;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String pathWithSeparator(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) return path;
		return path + System.getProperty("file.separator");
	}

	public static <T> byte[] jsonString(T object) {
		return jsonString(object, false).getBytes();
	}

	public static <T> String jsonString(T object, boolean prettyPrint) {
		if (object == null) return null;
		String out = "";
		try {
			if (prettyPrint) {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
				objectWriter.writeValue(outStream, object);
				out = outStream.toString();
			} else {
				StringWriter sw = new StringWriter();
				mapper.writeValue(sw, object);
				out = sw.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public static void main(String[] args) throws IOException {

		JsonToExcelConverter converter = new JsonToExcelConverter();
//		File srcFile = new File("src/main/resources/basedata/knl_geo_info_PR.json");
//		File xlsxFile = converter.jsonFileToExcelFile(srcFile, ".xlsx");
//		System.out.println("Sucessfully converted JSON to Excel file at =" + xlsxFile.getAbsolutePath());
		File baseDirectory = new File(pathWithSeparator("src/main/resources/basedata/json/"));
		String[] fileNames = baseDirectory.list(RfxFileUtil.fileExtentionFilter(false, "json"));
		for (String fileName : fileNames) {
			File _baseDataFile = new File(pathWithSeparator(baseDirectory.getAbsolutePath()) + fileName);
			File file = converter.jsonFileToExcelFile(_baseDataFile, ".xlsx");
		}
	}


}
