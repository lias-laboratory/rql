package fr.ensma.lias.rql.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ensma.lias.rql.api.DataBaseImport;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.core.IRql;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dto.FilePreview;
import fr.ensma.lias.rql.dto.SheetContent;
import fr.ensma.lias.rql.dto.TableImportResult;
import fr.ensma.lias.rql.model.DataBaseConfig;
import fr.ensma.lias.rql.sql.SQLManager;

/**
 * @author Bilal REZKELLAH
 */
public class DataBaseImportImp implements DataBaseImport {

	@Inject
	IConfiguration cfg;

	@Inject
	IRql irql;

	@Inject
	IConfigurationDAO iconfig;

	private XSSFWorkbook workbook;

	@Override
	public FilePreview uploadFile(InputStream file, FormDataContentDisposition fileDetail) {
		FilePreview filePreview = new FilePreview();
		long key = 0;
		File resultDir = new File(cfg.getConfiguration().tmpResultsDir());
		resultDir.mkdir();
		if (fileDetail == null || file == null) {
			throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
		} else {
			Workbook workbook = null;
			try {
				key = System.currentTimeMillis();
				workbook = WorkbookFactory.create(file);
				SheetContent sheeti = null;
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					sheeti = new SheetContent();
					sheeti.setRows(getXlsxToJsonarray(workbook, i));
					Sheet sheet = workbook.getSheetAt(i);
					sheeti.setNbRow(sheet.getLastRowNum());
					Row titleRow = sheet.getRow(0);
					List<String> header = new ArrayList<String>();
					DataFormatter formatter = new DataFormatter();
					for (int j = 0; j < titleRow.getLastCellNum(); j++) {
						String val = formatter.formatCellValue(titleRow.getCell(j));
						header.add(val);
					}
					Set<String> set = new LinkedHashSet<>();
					for (String str : header) {
						if (!str.equals("")) {
							String value = str;
							// Iterate as long as you can't add the value indicating that we have
							// already the value in the set
							for (int i1 = 1; !set.add(value); i1++) {
								value = str + i1;
							}
						}
					}
					header = new ArrayList<String>();
					header.addAll(set);
					System.out.println(set);
					sheeti.setHeader(header);
					sheeti.setTableName(workbook.getSheetName(i));
					filePreview.getSheetList().add(sheeti);
				}
				filePreview.setGoNext(true);
				filePreview.setFileID(Long.toString(key));

			} catch (EncryptedDocumentException | InvalidFormatException | IOException | NullPointerException e1) {
				e1.printStackTrace();
				Response response = Response.status(Status.BAD_REQUEST).entity(e1.getMessage()).build();
				throw new WebApplicationException(response);
			}
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(cfg.getConfiguration().tmpResultsDir() + "/" + key + "tmpUpload.xlsx");
				workbook.write(fileOut);
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
				Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
				throw new WebApplicationException(response);
			}
		}
		return filePreview;
	}

	public List<Object> getXlsxToJsonarray(Workbook wb, int sheetIndex) throws IOException, InvalidFormatException {

		Sheet sheet = wb.getSheetAt(sheetIndex);
		Row titleRow = sheet.getRow(0);
		Row dataRow = null;
		List<Object> listObject = new ArrayList<Object>();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jtmp = null;
		List<String> header = new ArrayList<String>();
		DataFormatter formatter = new DataFormatter();
		for (int j = 0; j < titleRow.getLastCellNum(); j++) {
			String val = formatter.formatCellValue(titleRow.getCell(j));
			header.add(val);
		}
		Set<String> set = new LinkedHashSet<>();
		for (String str : header) {
			if (!str.equals("")) {
				String value = str;
				// Iterate as long as you can't add the value indicating that we have
				// already the value in the set
				for (int i1 = 1; !set.add(value); i1++) {
					value = str + i1;
				}
			}
		}
		header = new ArrayList<String>();
		header.addAll(set);
		for (int i = 1; i <= Math.min(sheet.getLastRowNum(), 100); i++) {
			jtmp = new JSONObject();
			dataRow = sheet.getRow(i);
			for (int j = 0; j < header.size(); j++) {

				if (formatter.formatCellValue(dataRow.getCell(j)) == null) {
				} else {
					String val = header.get(j);
					String val2 = formatter.formatCellValue(dataRow.getCell(j));
					jtmp.put(val, val2);
				}
			}
			Object obj = mapper.readValue(jtmp.toString(), Object.class);
			listObject.add(obj);
		}
		return listObject;
	}

	public CellType getColType(XSSFSheet sheet, Integer index) {
		Row rowi = sheet.getRow(1);
		CellType cellType = null;
		int rowIndex = 1;
		cellType = rowi.getCell(index).getCellTypeEnum();
		if (cellType == CellType.BLANK) {
			while (cellType == CellType.BLANK && rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
				rowi = sheet.getRow(++rowIndex);
				cellType = rowi.getCell(index).getCellTypeEnum();
			}
			if (rowIndex == sheet.getPhysicalNumberOfRows() - 1) {
				cellType = CellType.STRING;
			}
		}
		if (cellType == CellType.NUMERIC) {
			rowIndex = 1;
			while ((cellType == CellType.NUMERIC || cellType == CellType.BLANK)
					&& rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
				rowi = sheet.getRow(++rowIndex);
				cellType = rowi.getCell(index).getCellTypeEnum();
			}
			if (cellType != CellType.NUMERIC && cellType != CellType.BLANK) {
				cellType = CellType.STRING;
			} else {

				cellType = CellType.NUMERIC;
			}
		}
		return cellType;
	}

	public String cellTypeMap(CellType cellType, String dbType) {
		if (cellType == CellType.STRING && dbType.equals("oracle")) {
			return "VARCHAR2(200)";
		} else if (cellType == CellType.STRING && dbType.equals("postgresql")) {
			return "TEXT";
		} else if (cellType == CellType.NUMERIC && dbType.equals("oracle")) {
			return "NUMBER(20)";
		} else if (cellType == CellType.NUMERIC && dbType.equals("postgresql")) {
			return "BIGINT";
		} else if (cellType == CellType.BOOLEAN) {
			return "BOOLEAN";
		} else {
			return null;
		}
	}

	@Override
	public TableImportResult tableImport(String id, Integer sheetIndex, String tableName, String projectID,
			String columnList) {
		if (columnList.equals("[]") || columnList == null) {
			Response response = Response.status(Status.BAD_REQUEST).entity("Please select one attribut at less")
					.build();
			throw new WebApplicationException(response);
		}
		TableImportResult tir = null;
		DataBaseConfig dbc = null;
		try {
			System.out.println(projectID);
			dbc = iconfig.getConfigByID(projectID);
		} catch (SQLException | IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(cfg.getConfiguration().tmpResultsDir() + "/" + id + "_Script.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			sql.openDB();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String createTable = null;
		columnList = columnList.replace("[", "").replace("]", "");
		List<String> columnRang = Arrays.asList(columnList.split(","));
		List<Integer> intList = new ArrayList<Integer>();
		List<CellType> colTypeList = new ArrayList<CellType>();
		if (columnRang.size() != 0) {
			for (String s : columnRang)
				if (!s.equals(""))
					intList.add(Integer.valueOf(s));
		}
		FileInputStream file;
		try {
			file = new FileInputStream(new File(cfg.getConfiguration().tmpResultsDir() + "/" + id + "tmpUpload.xlsx"));
			workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
			tir = new TableImportResult(irql.normalizeName(tableName, dbc.getDataBaseType()), sheet.getLastRowNum(),
					intList.size(), true);
			for (int i = 0; i < intList.size(); i++) {
				colTypeList.add(getColType(sheet, intList.get(i)));
			}
			Row titleRow = sheet.getRow(0);
			createTable = "CREATE TABLE " + irql.normalizeName(tableName, dbc.getDataBaseType()) + " (";
			List<String> header = new ArrayList<String>();
			DataFormatter formatter = new DataFormatter();
			for (int j = 0; j < titleRow.getLastCellNum(); j++) {
				String val = formatter.formatCellValue(titleRow.getCell(j));
				header.add(val);
			}
			Set<String> set = new LinkedHashSet<>();
			for (String str : header) {
				if (!str.equals("")) {
					String value = str;
					for (int i1 = 1; !set.add(value); i1++) {
						value = str + i1;
					}
				}
			}
			header = new ArrayList<String>();
			header.addAll(set);
			for (int i = 0; i < intList.size(); i++) {
				if (i == 0) {
					createTable += irql.normalizeName(header.get(intList.get(i)), dbc.getDataBaseType()) + " "
							+ cellTypeMap(getColType(sheet, intList.get(i)), dbc.getDataBaseType());
				} else {
					createTable += ", " + irql.normalizeName(header.get(intList.get(i)), dbc.getDataBaseType()) + " "
							+ cellTypeMap(colTypeList.get(i), dbc.getDataBaseType());

				}
			}
			createTable += ")";
			writer.println(createTable);
			irql.executeSqlQuery(createTable, dbc);
			String insertRow = "";
			insertRow = "INSERT INTO " + irql.normalizeName(tableName, dbc.getDataBaseType()) + " (";
			for (int i = 0; i < intList.size(); i++) {
				if (i == 0) {
					insertRow += irql.normalizeName(header.get(intList.get(i)), dbc.getDataBaseType());
				} else {
					insertRow += ", " + irql.normalizeName(header.get(intList.get(i)), dbc.getDataBaseType());
				}
			}
			insertRow += ") ";
			String values = "";
			Iterator<Row> iteratorRow = sheet.iterator();
			if (iteratorRow.hasNext())
				iteratorRow.next();
			while (iteratorRow.hasNext()) {
				values = insertRow + "VALUES(";
				Row row = iteratorRow.next();
				for (int i = 0; i < intList.size(); i++) {
					if (colTypeList.get(i) == CellType.STRING) {
						String val = formatter.formatCellValue(row.getCell(intList.get(i)));
						if (i == 0) {
							values += "'" + val.replace("'", "") + "'";
						} else {
							values += ", " + "'" + val.replace("'", "") + "'";
						}
					} else if (colTypeList.get(i) == CellType.NUMERIC) {
						Integer val = -1;
						val = (int) row.getCell(intList.get(i)).getNumericCellValue();
						if (i == 0) {
							values += val;
						} else {
							values += ", " + val;
						}
					} else if (colTypeList.get(i) == CellType.BOOLEAN) {

					} else {
						boolean val = (boolean) row.getCell(intList.get(i)).getBooleanCellValue();
						if (i == 0) {
							values += val;
						} else {
							values += ", " + val;
						}
					}
				}
				values += ")";
				writer.println(values);
				sql.insertRow(values);
			}
			sql.closeDB();
			writer.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}

		return tir;
	}

	@Override
	public Response scriptDownload(String id) {
		ResponseBuilder rb = null;
		File file = new File(cfg.getConfiguration().tmpResultsDir() + "/" + id + "_Script.txt");
		rb = Response.ok((Object) file);
		rb.header("Content-Disposition", "attachment; filename=" + "/" + id + "_Script.txt");
		return rb.build();
	}

}
