package com.oyo.accouting.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zfguo
 */
public class ApnExcelParseTool {

    private static String mFilePath;

    //保存源文件内容
    public static void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";

    public static Workbook initWorkBook() throws IOException {
        File file = new File(mFilePath);
        InputStream is = new FileInputStream(file);

        Workbook workbook = null;
        //根据后缀，得到不同的Workbook子类，即HSSFWorkbook或XSSFWorkbook
        if (mFilePath.endsWith(SUFFIX_2003)) {
            workbook = new HSSFWorkbook(is);
        } else if (mFilePath.endsWith(SUFFIX_2007)) {
            workbook = new XSSFWorkbook(is);
        }

        return workbook;
    }

    public static void parseWorkbook(Workbook workbook, List<Object> apnModelList) {
        int numOfSheet = workbook.getNumberOfSheets();

        //依次解析每一个Sheet
        for (int i = 0; i < numOfSheet; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            parseSheet(sheet, apnModelList);
        }
    }

    //保存需要调用的ApnModel中的方法
    private static List<Method> mUsedMethod;

    private static void parseSheet(Sheet sheet, List<Object> apnModelList) {
        Row row;

        int count = 0;

        //利用迭代器，取出每一个Row
        Iterator<Row> iterator = sheet.iterator();
        while(iterator.hasNext()) {
            row = iterator.next();

            //由于第一行是标题，因此这里单独处理
            if (count == 0) {
                mUsedMethod = new ArrayList<>();
                parseRowAndFindMethod(row);
            } else {
                //其它行都在这里处理
                parseRowAndFillData(row, apnModelList);
            }

            ++count;
        }
    }


    private static void parseRowAndFindMethod(Row row) {
        //利用parseRow处理每一行，得到每个cell中的String
        List<String> rst = parseRow(row);

        String methodName;
        try {
            //根据String得到需要调用的ApnModel中的方法
            for (String str : rst) {
                methodName = "set" + str;
                //反射拿到method
                mUsedMethod.add(
                        Object.class.getDeclaredMethod(methodName, String.class));
            }
        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }

    }

    /**
     * 开始解析具体的数据
     */
    private static void parseRowAndFillData(Row row, List<Object> apnModelList) {
        //同样利用parseRow得到具体每一行的数据
        List<String> rst = parseRow(row);

        Object apnModel = new Object();

        //这里主要debug一下，避免由于Excel的格式可能不太对
        //使得每一行的数据解析地不太对
        if (mUsedMethod.size() != rst.size()) {
            //
        } else {
            //利用反射，将数据填充到具体的ApnModel
            try {
                for (int i = 0; i < mUsedMethod.size(); ++i) {
                    mUsedMethod.get(i).invoke(apnModel, rst.get(i));
                }

                //保存到输出结果中
                apnModelList.add(apnModel);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * 这里是解析每一行的代码
     */
    private static List<String> parseRow(Row row) {
        List<String> rst = new ArrayList<>();

        Cell cell;

        //利用迭代器得到每一个cell
        Iterator<Cell> iterator = row.iterator();
        while (iterator.hasNext()) {
            cell = iterator.next();

            //定义每一个cell的数据类型
            cell.setCellType(CellType.STRING);

            //取出cell中的value
            rst.add(cell.getStringCellValue());
        }

        return rst;
    }

}
