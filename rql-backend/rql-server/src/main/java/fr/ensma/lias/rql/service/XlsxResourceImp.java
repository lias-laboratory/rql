package fr.ensma.lias.rql.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.ensma.lias.rql.api.XlsxResource;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.core.IRql;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dao.inmemory.InMemory;
import fr.ensma.lias.rql.dto.CheckRuleResult;
import fr.ensma.lias.rql.dto.RQLResult;
import fr.ensma.lias.rql.dto.RQLRow;
import fr.ensma.lias.rql.rqlgrammar.ParseException;
import fr.ensma.lias.rql.sql.SQLResultsRow;
import fr.ensma.lias.rql.sql.SQLRow;

/**
 * @author Bilal REZKELLAH
 */
public class XlsxResourceImp implements XlsxResource {
	@Inject
	IRql irql;

	@Inject
	InMemory myInstance;

	@Inject
	IConfiguration cfg;

	@Inject
	IConfigurationDAO iconfig;

	RQLResult rqlResult;

	private Workbook workbook;

	@Override
	public Response getRule(String id, String support, String confidence) {
		ResponseBuilder rb = null;
		try {
			rqlResult = irql.executeRqlQuery(id, Double.parseDouble(support), Double.parseDouble(confidence));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
		workbook = new XSSFWorkbook();
		String query = myInstance.getRqlQueryDB().get(id);
		// Create a Sheets
		Sheet sheet = workbook.createSheet("Rules");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a header row for exactRules
		Row headerRow1 = sheet.createRow(0);
		Row headerRow = sheet.createRow(1);
		// Creating cells
		Cell cell = headerRow.createCell(0);
		cell.setCellValue("LeftAttributes");
		cell.setCellStyle(headerCellStyle);

		Cell cell2 = headerRow.createCell(1);
		cell2.setCellValue("RightAttribut");
		cell2.setCellStyle(headerCellStyle);

		Cell cellquery = headerRow1.createCell(0);
		cellquery.setCellValue("RQL Query");
		cellquery.setCellStyle(headerCellStyle);

		Cell cell2query = headerRow1.createCell(2);
		cell2query.setCellValue(query);
		// Create Other rows and cells with exactRules Data
		int rowNum = 2;
		for (RQLRow rqlrow : rqlResult.getexactRule()) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(String.join("   ", rqlrow.getLeftAttributes()));

			row.createCell(1).setCellValue(rqlrow.getRightAttribute());
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < 2; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(cfg.getConfiguration().tmpResultsDir() + "/" + id + "poi.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(cfg.getConfiguration().tmpResultsDir() + "/" + id + "poi.xlsx");
		rb = Response.ok((Object) file);
		rb.header("Content-Disposition", "attachment; filename=" + "/" + id + "_Rules.xlsx");
		return rb.build();
	}

	@Override
	public Response getCounterExample(String id, String leftAttributes, String rightAttribute, String support,
			String confidence, String projectID) {
		ResponseBuilder rb = null;
		File resultDir = new File(cfg.getConfiguration().tmpResultsDir());
		resultDir.mkdir();
		long debut = System.currentTimeMillis();
		String query = myInstance.getRqlQueryDB().get(id);
		CheckRuleResult crr = null;
		try {
			crr = irql.checkRule(debut, iconfig.getConfigByID(projectID), id, leftAttributes, rightAttribute,
					Double.parseDouble(support), Double.parseDouble(confidence));
		} catch (ParseException | NumberFormatException | SQLException | IOException e) {
			e.printStackTrace();
			throw new WebApplicationException("Query is bad.", Status.BAD_REQUEST);
		}

		workbook = new XSSFWorkbook();
		// Create a Sheets
		Sheet sheet = workbook.createSheet("CounterExamples1");
		Sheet sheet2 = workbook.createSheet("CounterExamples2");
		Sheet sheet3 = workbook.createSheet("CounterExamples3");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a header row for counter examples
		Row headerRow = sheet.createRow(1);
		Row headerRow1 = sheet.createRow(0);

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Creating cells
		Cell cell = headerRow.createCell(0);
		cell.setCellValue("LeftAttributes");
		cell.setCellStyle(headerCellStyle);

		Cell cell2 = headerRow.createCell(1);
		cell2.setCellValue("RightAttribut");
		cell2.setCellStyle(headerCellStyle);

		Cell cellquery = headerRow1.createCell(0);
		cellquery.setCellValue("RQL Query");
		cellquery.setCellStyle(headerCellStyle);

		Cell cellquery2 = headerRow1.createCell(2);
		cellquery2.setCellValue(query);
		// save the rule
		Row row = sheet.createRow(2);
		row.createCell(0).setCellValue(leftAttributes);
		row.createCell(1).setCellValue(rightAttribute);
		// Create Other rows and cells with counterExamples Data
		Row headerRow2 = sheet.createRow(3);
		headerCellStyle.setFont(headerFont);
		// Creating cells
		Cell cell1 = headerRow2.createCell(0);
		cell1.setCellValue("Counter Example N°");
		cell1.setCellStyle(headerCellStyle);
		for (int i = 0; i < crr.getCheckResult().getCounterExamples3().getHeader().size(); i++) {
			cell1 = headerRow2.createCell(i + 1);
			cell1.setCellValue(crr.getCheckResult().getCounterExamples3().getHeader().get(i));
			cell1.setCellStyle(headerCellStyle);
		}

		int rowNum = 4;
		for (SQLRow sqlrow : crr.getCheckResult().getCounterExamples3().getRows()) {
			Row row1 = sheet.createRow(rowNum++);

			row1.createCell(0).setCellValue(Math.floor((rowNum - 5) / crr.getSummary().getTupleNB() + 1));

			int cellNum = 1;
			for (String cel : sqlrow.getCels()) {
				row1.createCell(cellNum++).setCellValue(cel);
			}
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < crr.getCheckResult().getCounterExamples3().getHeader().size(); i++) {
			sheet.autoSizeColumn(i);
		}

		// Create a header row for counter examples
		Row headerRow21 = sheet2.createRow(0);
		// Creating cells
		Cell cell21 = headerRow21.createCell(0);
		cell21.setCellValue("LeftAttributes");
		cell21.setCellStyle(headerCellStyle);

		Cell cell22 = headerRow21.createCell(1);
		cell22.setCellValue("RightAttribut");
		cell22.setCellStyle(headerCellStyle);
		// save the rule
		Row row21 = sheet2.createRow(1);
		row21.createCell(0).setCellValue(leftAttributes);
		row21.createCell(1).setCellValue(rightAttribute);
		// Create Other rows and cells with counterExamples Data
		Row headerRow22 = sheet2.createRow(2);
		headerCellStyle.setFont(headerFont);
		// Creating cells
		Cell cell23 = headerRow22.createCell(0);
		cell23.setCellValue("Counter Example N°");
		cell23.setCellStyle(headerCellStyle);
		for (int i = 0; i < crr.getCheckResult().getCounterExamples().getHeader().size(); i++) {
			cell23 = headerRow22.createCell(i + 1);
			cell23.setCellValue(crr.getCheckResult().getCounterExamples().getHeader().get(i));
			cell23.setCellStyle(headerCellStyle);
		}

		int rowNum2 = 3;
		for (SQLRow sqlrow : crr.getCheckResult().getCounterExamples().getRows()) {
			Row row1 = sheet2.createRow(rowNum2++);

			row1.createCell(0).setCellValue(Math.floor((rowNum2 - 4) / crr.getSummary().getTupleNB() + 1));

			int cellNum = 1;
			for (String cel : sqlrow.getCels()) {
				row1.createCell(cellNum++).setCellValue(cel);
			}
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < crr.getCheckResult().getCounterExamples().getHeader().size(); i++) {
			sheet2.autoSizeColumn(i);
		}

		// Create a header row for counter examples
		Row headerRow31 = sheet3.createRow(0);
		// Creating cells
		Cell cell31 = headerRow31.createCell(0);
		cell31.setCellValue("LeftAttributes");
		cell31.setCellStyle(headerCellStyle);

		Cell cell32 = headerRow31.createCell(1);
		cell32.setCellValue("RightAttribut");
		cell32.setCellStyle(headerCellStyle);
		// save the rule
		Row row31 = sheet3.createRow(1);
		row31.createCell(0).setCellValue(leftAttributes);
		row31.createCell(1).setCellValue(rightAttribute);
		// Create Other rows and cells with counterExamples Data
		Row headerRow32 = sheet3.createRow(2);
		headerCellStyle.setFont(headerFont);
		// Creating cells
		Cell cell33 = headerRow32.createCell(0);
		cell33.setCellValue("Counter Example N°");
		cell33.setCellStyle(headerCellStyle);
		for (int i = 0; i < crr.getCheckResult().getCounterExamples2().getHeader().size(); i++) {
			cell33 = headerRow32.createCell(i + 1);
			cell33.setCellValue(crr.getCheckResult().getCounterExamples2().getHeader().get(i));
			cell33.setCellStyle(headerCellStyle);
		}

		int rowNum3 = 3;
		for (SQLRow sqlrow : crr.getCheckResult().getCounterExamples2().getRows()) {
			Row row1 = sheet3.createRow(rowNum3++);

			row1.createCell(0).setCellValue(Math.floor((rowNum3 - 4) / crr.getSummary().getTupleNB() + 1));

			int cellNum = 1;
			for (String cel : sqlrow.getCels()) {
				row1.createCell(cellNum++).setCellValue(cel);
			}
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < crr.getCheckResult().getCounterExamples2().getHeader().size(); i++) {
			sheet3.autoSizeColumn(i);
		}

		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(cfg.getConfiguration().tmpResultsDir() + "/" + id + "CounterExample.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(cfg.getConfiguration().tmpResultsDir() + "/" + id + "CounterExample.xlsx");
		rb = Response.ok((Object) file);
		rb.header("Content-Disposition", "attachment; filename=" + "/" + id + "_CounterExample.xlsx");
		return rb.build();
	}

	@Override
	public Response getResultSet(String ID, String projectID) {
		SQLResultsRow executeSqlQuery = null;
		ResponseBuilder rb = null;
		File resultDir = new File(cfg.getConfiguration().tmpResultsDir());
		resultDir.mkdir();
		workbook = new XSSFWorkbook();
		String query;
		if (ID == null || ID.isEmpty()) {
			throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
		}
		query = myInstance.getSqlQueryDB().get(ID);
		if (ID == null || ID.isEmpty()) {
			throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
		}
		try {
			executeSqlQuery = irql.executeSqlQuery2(query, iconfig.getConfigByID(projectID));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}

		// Create a Sheets
		Sheet sheet = workbook.createSheet("ResultSet");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a header row for query
		Row headerRow = sheet.createRow(0);

		// Creating cells
		Cell cell = headerRow.createCell(0);
		cell.setCellValue("SQL query");
		cell.setCellStyle(headerCellStyle);

		Cell cell2 = headerRow.createCell(1);
		cell2.setCellValue(query);

		// Create a header row for query
		Row headerRow2 = sheet.createRow(1);
		int cellNum = 0;
		for (String cel : executeSqlQuery.getHeader()) {
			Cell cell1 = headerRow2.createCell(cellNum++);
			cell1.setCellValue(cel);
			cell1.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with exactRules Data
		int rowNum = 2;
		for (SQLRow sqlrow : executeSqlQuery.getRows()) {
			Row row = sheet.createRow(rowNum++);
			int cellNum1 = 0;
			for (String cel : sqlrow.getCels()) {
				row.createCell(cellNum1++).setCellValue(cel);
			}
		}
		// Resize all columns to fit the content size
		for (int i = 0; i < executeSqlQuery.getHeader().size(); i++) {
			if (i != 1)
				sheet.autoSizeColumn(i);
		}
		// Write the output to a file
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(cfg.getConfiguration().tmpResultsDir() + "/" + ID + "SQL.xlsx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(cfg.getConfiguration().tmpResultsDir() + "/" + ID + "SQL.xlsx");
		rb = Response.ok((Object) file);
		rb.header("Content-Disposition", "attachment; filename=" + "/" + ID + "_ResultSet.xlsx");
		return rb.build();
	}
}
