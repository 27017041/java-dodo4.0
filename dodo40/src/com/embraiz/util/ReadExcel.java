package com.embraiz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.embraiz.model.Lead;

public class ReadExcel {
	// 总行数
	private int totalRows = 0;
	// 总条数
	private int totalCells = 0;
	// 错误信息接收器
	private String errorMsg;

	// 构造方法
	public ReadExcel() {
	}

	// 获取总行数
	public int getTotalRows() {
		return totalRows;
	}

	// 获取总列数
	public int getTotalCells() {
		return totalCells;
	}

	// 获取错误信息
	public String getErrorInfo() {
		return errorMsg;
	}

	/**
	 * 验证EXCEL文件
	 */
	@SuppressWarnings("static-access")
	public boolean validateExcel(String filePath) {
		if (filePath == null || !(this.isExcel2003(filePath) || this.isExcel2007(filePath))) {
			errorMsg = "文件名不是excel格式";
			return false;
		}
		return true;
	}

	/**
	 * 读EXCEL文件，获取信息集合
	 */
	@SuppressWarnings("static-access")
	public List<Lead> getExcelInfo(String fileName, MultipartFile Mfile) {
		// 把spring文件上传的MultipartFile转换成CommonsMultipartFile类型
		CommonsMultipartFile cf = (CommonsMultipartFile) Mfile; // 获取本地存储路径
		File file = new File("D:\\fileupload");
		// 创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
		if (!file.exists())
			file.mkdirs();
		// 新建一个文件
		File file1 = new File("D:\\fileupload" + new Date().getTime() + ".xlsx");
		// 将上传的文件写入新建的文件中
		try {
			cf.getFileItem().write(file1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 初始化信息的集合
		List<Lead> leadList = new ArrayList<Lead>();
		// 初始化输入流
		InputStream is = null;
		try {
			// 验证文件名是否合格
			if (!validateExcel(fileName)) {
				return null;
			}
			// 根据文件名判断文件是2003版本还是2007版本
			boolean isExcel2003 = true;
			if (this.isExcel2007(fileName)) {
				isExcel2003 = false;
			}
			// 根据新建的文件实例化输入流
			is = new FileInputStream(file1);
			// 根据excel里面的内容读取信息
			leadList = getExcelInfo(is, isExcel2003);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					is = null;
					e.printStackTrace();
				}
			}
		}
		return leadList;
	}

	/**
	 * 根据excel里面的内容读取信息
	 */
	public List<Lead> getExcelInfo(InputStream is, boolean isExcel2003) {
		List<Lead> leadList = null;
		try {
			/** 根据版本选择创建Workbook的方式 */
			Workbook wb = null;
			// 当excel是2003时
			if (isExcel2003) {
				wb = new HSSFWorkbook(is);
			} else {// 当excel是2007时
				wb = new XSSFWorkbook(is);
			}
			// 读取Excel里面的信息
			leadList = readExcelValue(wb);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return leadList;
	}

	/**
	 * 读取Excel里面的信息
	 */
	private List<Lead> readExcelValue(Workbook wb) {
		// 得到第一个shell
		Sheet sheet = wb.getSheetAt(0);

		// 得到Excel的行数
		this.totalRows = sheet.getPhysicalNumberOfRows();

		// 得到Excel的列数(前提是有行数)
		if (totalRows >= 1 && sheet.getRow(0) != null) {
			this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}

		List<Lead> leadList = new ArrayList<Lead>();
		Lead lead;
		// 循环Excel行数,从第二行开始。标题不入库
		for (int r = 1; r < totalRows; r++) {
			Row row = sheet.getRow(r);
			if (row == null)
				continue;
			lead = new Lead();

			// 循环Excel的列
			for (int c = 0; c < this.totalCells; c++) {
				Cell cell = row.getCell(c);
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
				if (null != cell) {
					// 把读取的字段存入对象(可根据需求修改这里)
					if (c == 0) {
						lead.setLeadName(cell.getStringCellValue());
					} else if (c == 1) {
						lead.setSourceId(Integer.parseInt(cell.getStringCellValue()));
					} else if (c == 2) {
						lead.setNotes(cell.getStringCellValue());
					} else if (c == 3) {
						lead.setAssignTo(Integer.parseInt(cell.getStringCellValue()));
					} else if (c == 4) {
						try {
							lead.setAssignExpiryTime(f.parse(f.format(cell.getStringCellValue())));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (c == 5) {
						lead.setClient(cell.getStringCellValue());
					} else if (c == 6) {
						lead.setDoingBusinessOtherName(cell.getStringCellValue());
					} else if (c == 7) {
						lead.setClientPhone(cell.getStringCellValue());
					} else if (c == 8) {
						lead.setClientFax(cell.getStringCellValue());
					} else if (c == 9) {
						lead.setAddress1(cell.getStringCellValue());
					} else if (c == 10) {
						lead.setAddress2(cell.getStringCellValue());
					} else if (c == 11) {
						lead.setDistrictId(Integer.parseInt(cell.getStringCellValue()));
					} else if (c == 12) {
						lead.setLocationId(Integer.parseInt(cell.getStringCellValue()));
					} else if (c == 13) {
						lead.setWebsite(cell.getStringCellValue());
					} else if (c == 14) {
						lead.setClientEmail(cell.getStringCellValue());
					} else if (c == 15) {
						lead.setNatureofbusinessId(Integer.parseInt(cell.getStringCellValue()));
					} else if (c == 16) {
						lead.setContact(cell.getStringCellValue());
					} else if (c == 17) {
						lead.setContactTitle(cell.getStringCellValue());
					} else if (c == 18) {
						lead.setContactDirectLine(cell.getStringCellValue());
					} else if (c == 19) {
						lead.setContactDirectFax(cell.getStringCellValue());
					} else if (c == 20) {
						lead.setContactMobile(cell.getStringCellValue());
					} else if (c == 21) {
						lead.setContactEmail(cell.getStringCellValue());
					} else if (c == 22) {
						lead.setConverted(Boolean.parseBoolean(cell.getStringCellValue()));
					} else if (c == 23) {
						try {
							lead.setOptOutTime(f.parse(f.format(cell.getStringCellValue())));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (c == 24) {
						lead.setMarketingCode(cell.getStringCellValue());
					}// //
				}
			}
			// 添加
			leadList.add(lead);
		}
		return leadList;
	}

	// @描述：是否是2003的excel，返回true是2003
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	// @描述：是否是2007的excel，返回true是2007
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

}
