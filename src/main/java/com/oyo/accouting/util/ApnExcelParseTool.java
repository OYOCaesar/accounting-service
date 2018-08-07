package com.oyo.accouting.util;

import com.oyo.accouting.bean.PlanTemplet;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zfguo
 */
public class ApnExcelParseTool {

    private static String mFilePath;
    
    private static List<Object> objList;

    //保存源文件内容
    public static void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";

    public static Workbook initWorkBook() throws IOException {
    	objList = new LinkedList<>();
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

    public static List<Object> parseWorkbook(Workbook workbook, Class cls) {
        int numOfSheet = workbook.getNumberOfSheets();
        numOfSheet = 1;
        //依次解析每一个Sheet
        for (int i = 0; i < numOfSheet; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            parseSheet(sheet, cls);
        }
        return objList;
    }

    //保存需要调用的ApnModel中的方法
    private static List<Method> mUsedMethod;

    private static void parseSheet(Sheet sheet, Class cls) {
        Row row;

        //利用迭代器，取出每一个Row
        Iterator<Row> iterator = sheet.iterator();
        while(iterator.hasNext()) {
            row = iterator.next();
            int rowNum = row.getRowNum();
            //由于第一行是标题，因此这里单独处理
            if (rowNum == 0) {
                mUsedMethod = new LinkedList<>();
                parseRowAndFindMethod(row,cls);
            } else {
                //其它行都在这里处理
                parseRowAndFillData(row, cls);
            }

        }
    }


    private static void parseRowAndFindMethod(Row row,Class cls) {
        //利用parseRow处理每一行，得到每个cell中的String
        List<String> rst = parseRow(row);

        String methodName;
        try {
            //根据String得到需要调用的ApnModel中的方法
            for (String str : rst) {
                methodName = "set" + regexMethodName(str);
                //反射拿到method
                mUsedMethod.add(
                        cls.getDeclaredMethod(methodName, String.class));
            }
        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }

    }

    /**
     * 处理excel中不规范的方法名称
     * @param str
     * @return
     */
   private static String regexMethodName(String str){

       try {
           Pattern p = Pattern.compile("[^a-zA-Z_ ]");
           Matcher m = p.matcher(str);
           boolean hasInvalid = m.find();
           int i = hasInvalid ? m.start() : str.length(); //过滤掉无用的字符
           //第一个字母处理成大写，其他全部处理为小写
           str = str.substring(0, 1).toUpperCase() + str.substring(1, i).trim().toLowerCase();
           Pattern p1 = Pattern.compile("[_ ]{1}[a-zA-Z]{1}");
           Matcher m1 = p1.matcher(str);
           while (m1.find()) {
               int i1 = m1.start();
               //匹配空格+字母和_+字母，并替换为空格或_后面那个字母的大写
               String i1Str = str.substring(i1 + 1, i1 + 2).toUpperCase();
               str = m1.replaceFirst(i1Str);
               m1 = p1.matcher(str);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
       return str;
    }


    /**
     * 开始解析具体的数据
     */
    private static void parseRowAndFillData(Row row, Class cls) {
        //同样利用parseRow得到具体每一行的数据
        List<String> rst = parseRow(row);

        Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

        //这里主要debug一下，避免由于Excel的格式可能不太对
        //使得每一行的数据解析地不太对
        if (mUsedMethod.size() != rst.size()) {
            //
        } else {
            //利用反射，将数据填充到具体的ApnModel
            try {
                for (int i = 0; i < mUsedMethod.size(); ++i) {
                    mUsedMethod.get(i).invoke(obj, rst.get(i));
                }

                //保存到输出结果中
                objList.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这里是解析每一行的代码
     */
    private static List<String> parseRow(Row row) {
        List<String> rst = new LinkedList<>();

        Cell cell;

        if(mUsedMethod == null || mUsedMethod.size() == 0) {
            //利用迭代器得到每一个cell
            Iterator<Cell> iterator = row.iterator();
            while (iterator.hasNext()) {

                cell = iterator.next();
                //取出cell中的value
                String value = null;
                value = getCellValue(cell);
                rst.add(value);
            }
        }else {
            int c =0;
            while (c < mUsedMethod.size()) {

                cell = row.getCell(c);
                //取出cell中的value
                String value = null;
                value = cell == null? "" : getCellValue(cell);
                rst.add(value);
                c++;
            }
        }
        return rst;
    }

    private static String getCellValue(Cell cell){
        String value = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    //注：format格式 yyyy-MM-dd hh:mm:ss 中小时为12小时制，若要24小时制，则把小h变为H即可，yyyy-MM-dd HH:mm:ss
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    value = sdf.format(HSSFDateUtil.getJavaDate(cell.
                            getNumericCellValue())).toString();
                } else {
                    value = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BLANK:
                value = "";
                break;
        }
        return value;
    }

    public static void main(String[] args){
        setFilePath("/Users/oyo/Desktop/aa.xlsx");
        try {
            Workbook workbook = initWorkBook();
            List<Object> apnModelList = null;
            apnModelList = parseWorkbook(workbook,PlanTemplet.class);
            for(Object p : apnModelList){
            	PlanTemplet pt = (PlanTemplet)p;
                System.out.println(pt.getOyoId()+"=="+pt.getHotelId()+"=="+pt.getOyoShare());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
