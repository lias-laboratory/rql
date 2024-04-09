package fr.ensma.lias.rql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class FilePreview {

	private List<SheetContent> sheetList = new ArrayList<SheetContent>();
	private String fileID;

	private boolean goNext;

	public boolean isGoNext() {
		return goNext;
	}

	public void setGoNext(boolean goNext) {
		this.goNext = goNext;
	}

	public FilePreview() {

	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public List<SheetContent> getSheetList() {
		return sheetList;
	}

	public void setSheetList(List<SheetContent> sheetList) {
		this.sheetList = sheetList;
	}

}
