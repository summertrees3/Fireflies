package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *  将Excel转换为HTML解决方案
 *  
 *  @author dwg
 *  @version V1.0
 *  @date 2018年10月30日
 */
public class ExcelToHtmlUtils {

	private static List<Sheet> list1 =new ArrayList<Sheet>();
	private static List<Sheet> list2 =new ArrayList<Sheet>();
	private static List<Sheet> list3 =new ArrayList<Sheet>();
	private static List<Sheet> list4 =new ArrayList<Sheet>();
	
    public static void main(String[] args) {

        String htmlExcel = readExcelToHtml(ReadProperties.readPath("path1"),true,ReadProperties.readPath("name"));
//        System.out.println(htmlExcel);
        try {
            FileUtils.writeStringToFile(new File(ReadProperties.readPath("path3")), htmlExcel, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
//    public static void excelConvertHTMLHandle(String excePath, String htmlPath) {
//
//        InputStream is = null;
//        String htmlExcel = null;
//        try {
//            File sourcefile = new File(excePath);
//            is = new FileInputStream(sourcefile);
//            Workbook wb = WorkbookFactory.create(is);
//            if (wb instanceof XSSFWorkbook) {
//                XSSFWorkbook xWb = (XSSFWorkbook) wb;
//                htmlExcel = ExcelToHtmlUtils.getExcelInfo(xWb,true);
//            }else if(wb instanceof HSSFWorkbook){
//                HSSFWorkbook hWb = (HSSFWorkbook) wb;
//                htmlExcel = ExcelToHtmlUtils.getExcelInfo(hWb,true);
//            }
//            SaveHtml(htmlExcel, htmlPath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            if(is!=null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    /**
     * 将提取出来的html内容写入保存的路径中。
     *
//     * @param strText
//     * @param strHtml
     */
//    public static boolean SaveHtml(String htmlTxt, String htmlPath) {
//        FileOutputStream out=null;
//        BufferedWriter write=null;
//        try {
//            File file = new File(htmlPath);
//            if(!file.exists()){
//                file = new File(file.getParent());
//                if(!file.exists()){
//                    file.mkdirs();
//                }
//            }
//            out=new FileOutputStream(htmlPath, false);
//            write=new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//            write.write(htmlTxt);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            try{
//                if(write!=null){
//                    write.close();
//                }
//            }catch(IOException ex){
//                ex.printStackTrace();
//            }
//            try{
//                if(out!=null){
//                    out.close();
//                }
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }

    /**
     * 程序入口方法
     * @param filePath 文件的路径
     * @param isWithStyle 是否需要表格样式 包含 字体 颜色 边框 对齐方式
     * @return <table>...</table> 字符串
     */
    public static String readExcelToHtml(String filePath , boolean isWithStyle,String pn){
        InputStream is = null;
        String htmlExcel = null;
        try {
            File sourcefile = new File(filePath);
            is = new FileInputStream(sourcefile);
            Workbook wb = WorkbookFactory.create(is);
            if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook xWb = (XSSFWorkbook) wb;
                htmlExcel = ExcelToHtmlUtils.getExcelInfo(xWb,isWithStyle,pn);
            }else if(wb instanceof HSSFWorkbook){
                HSSFWorkbook hWb = (HSSFWorkbook) wb;
                htmlExcel = ExcelToHtmlUtils.getExcelInfo(hWb,isWithStyle,pn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return htmlExcel;
    }

    public static String getExcelInfo(Workbook wb,boolean isWithStyle,String pn){
        StringBuffer sb = new StringBuffer("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset='utf-8'> ");
        sb.append("<style>"); 
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h2>"+pn+"</h2>");
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
        	classify(sb,i,wb);
//        	Sheet sheet = wb.getSheetAt(i);//获取Sheet的内容
//        	getHtml(wb,sheet,sb,i,isWithStyle);
        }
        sb.append("<h3>一、问题单</h3>");
        for(int j=0;j<list1.size();j++){
        	getHtml(wb,list1.get(j),sb,j,true,"list1");
        	getScript(sb,"list1_"+j);
        }
        sb.append("<h3>二、个人代码</h3>");
        for(int j=0;j<list2.size();j++){
        	getHtml(wb,list2.get(j),sb,j,true,"list2");
        	getScript(sb,"list2_"+j);
        }
        sb.append("<h3>三、项目代码</h3>");
        for(int j=0;j<list3.size();j++){
        	getHtml(wb,list3.get(j),sb,j,true,"list3");
        	getScript(sb,"list3_"+j);
        }
        sb.append("<h3>四、代码详情</h3>");
        for(int j=0;j<list4.size();j++){
        	getHtml(wb,list4.get(j),sb,j,true,"list4");
        	getScript(sb,"list4_"+j);
        }
        return sb.toString();
    }

    private static StringBuffer getScript(StringBuffer sb,String idFlag){
        sb.append("<script>");
        sb.append("function con(idFlag){");
        sb.append(" if( document.getElementById(idFlag).style.display == \"none\" && document.getElementById(idFlag).rows.length > 1)");
        sb.append(" {");
        sb.append("	document.getElementById(idFlag).style.display = \"table\";");
        sb.append(" } else {");
        sb.append(" document.getElementById(idFlag).style.display = \"none\";");
        sb.append(" } ");
        sb.append("}");
        sb.append("</script>");
    	return sb;
    }
    private static StringBuffer getHtml(Workbook wb,Sheet sheet,StringBuffer sb,int i,boolean isWithStyle,String idFlag){
        int lastRowNum = sheet.getLastRowNum();
//        System.out.println(lastRowNum);
        Map<String, String> map[] = getRowSpanColSpanMap(sheet);
        if(lastRowNum >0){
        	sb.append("<a style='cursor:pointer;color:blue"+"' onclick=\"con('"+idFlag+"_"+i+"')\">"+(i+1)+"."+sheet.getSheetName()+"</a><br>");
        }else{
        	sb.append("<a style='cursor:pointer;color:grey"+"' onclick=\"con('"+idFlag+"_"+i+"')\">"+(i+1)+"."+sheet.getSheetName()+"（无数据）"+"</a><br>");
        }
        sb.append("<table style='border-collapse:collapse;display:none;' id="+idFlag+"_"+i+">");
        Row row = null;        //兼容
        Cell cell = null;    //兼容
        for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                sb.append("<tr><td >  </td></tr>");
                continue;
            }
            sb.append("<tr>");
            int lastColNum = row.getLastCellNum();
            for (int colNum = 0; colNum < lastColNum; colNum++) {
                cell = row.getCell(colNum);
                if (cell == null) {    //特殊情况 空白的单元格会返回null
                    sb.append("<td> </td>");
                    continue;
                }
                String stringValue = getCellValue(cell);
                if (map[0].containsKey(rowNum + "," + colNum)) {
                    String pointString = map[0].get(rowNum + "," + colNum);
                    map[0].remove(rowNum + "," + colNum);
                    int bottomeRow = Integer.valueOf(pointString.split(",")[0]);
                    int bottomeCol = Integer.valueOf(pointString.split(",")[1]);
                    int rowSpan = bottomeRow - rowNum + 1;
                    int colSpan = bottomeCol - colNum + 1;
                    sb.append("<td rowspan= '" + rowSpan + "' colspan= '"+ colSpan + "' ");
                } else if (map[1].containsKey(rowNum + "," + colNum)) {
                    map[1].remove(rowNum + "," + colNum);
                    continue;
                } else {
                    sb.append("<td ");
                }
                //判断是否需要样式
                if(isWithStyle){
                    dealExcelStyle(wb, sheet, cell, sb);//处理单元格样式
                }
                sb.append(">");
                if (stringValue == null || "".equals(stringValue.trim())) {
                    sb.append("   ");
                } else {
                    // 将ascii码为160的空格转换为html下的空格（ ）
                    sb.append(stringValue.replace(String.valueOf((char) 160)," "));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table><br>");
    	return sb;
    }
    
    private static void classify(StringBuffer sb,int i,Workbook wb){
    	if(wb.getSheetName(i).contains("DTS")){
    		list1.add(wb.getSheetAt(i));
    	}else if(wb.getSheetName(i).contains("个人")){
    		list2.add(wb.getSheetAt(i));
    	}else if(wb.getSheetName(i).contains("项目代码")){
    		list3.add(wb.getSheetAt(i));
    	}else{
    		list4.add(wb.getSheetAt(i));
    	}
    }
    private static Map<String, String>[] getRowSpanColSpanMap(Sheet sheet) {
        Map<String, String> map0 = new HashMap<String, String>();
        Map<String, String> map1 = new HashMap<String, String>();
        int mergedNum = sheet.getNumMergedRegions();
        CellRangeAddress range = null;
        for (int i = 0; i < mergedNum; i++) {
            range = sheet.getMergedRegion(i);
            int topRow = range.getFirstRow();
            int topCol = range.getFirstColumn();
            int bottomRow = range.getLastRow();
            int bottomCol = range.getLastColumn();
            map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
            // System.out.println(topRow + "," + topCol + "," + bottomRow + "," + bottomCol);
            int tempRow = topRow;
            while (tempRow <= bottomRow) {
                int tempCol = topCol;
                while (tempCol <= bottomCol) {
                    map1.put(tempRow + "," + tempCol, "");
                    tempCol++;
                }
                tempRow++;
            }
            map1.remove(topRow + "," + topCol);
        }
        Map[] map = { map0, map1 };
        return map;
    }

    /**
     * 获取表格单元格Cell内容
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        String result = new String();
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:// 数字类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                    result = sdf.format(date);
                } else {
                    double value = cell.getNumericCellValue();
                    CellStyle style = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
                    if (temp.equals("General")) {
                        format.applyPattern("#");
                    }
                    result = format.format(value);
                }
                break;
            case Cell.CELL_TYPE_STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            case Cell.CELL_TYPE_BLANK:
                result = "";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * 处理表格样式
     * @param wb
     * @param sheet
     * @param cell
     * @param sb
     */
    private static void dealExcelStyle(Workbook wb,Sheet sheet,Cell cell,StringBuffer sb){
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            short alignment = cellStyle.getAlignment();
            sb.append("align='" + convertAlignToHtml(alignment) + "' ");//单元格内容的水平对齐方式
            short verticalAlignment = cellStyle.getVerticalAlignment();
            sb.append("valign='"+ convertVerticalAlignToHtml(verticalAlignment)+ "' ");//单元格中内容的垂直排列方式
            if (wb instanceof XSSFWorkbook) {
                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                short boldWeight = xf.getBoldweight();
                sb.append("style='");
                sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
                sb.append("font-size: " + xf.getFontHeight() / 2 + "%;"); // 字体大小
                //表的宽度
//                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
//                sb.append("width:" + columnWidth + "px;");
//                XSSFColor xc = xf.getXSSFColor();
//                if (xc != null && !"".equals(xc)) {
//                    sb.append("color:#" + xc.getARGBHex().substring(2) + ";"); // 字体颜色
//                }
//                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
//                if (bgColor != null && !"".equals(bgColor)) {
//                    sb.append("background-color:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
//                }
                sb.append(getBorderStyle(0,cellStyle.getBorderTop(), ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
                sb.append(getBorderStyle(1,cellStyle.getBorderRight(), ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
                sb.append(getBorderStyle(2,cellStyle.getBorderBottom(), ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
                sb.append(getBorderStyle(3,cellStyle.getBorderLeft(), ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));
            }else if(wb instanceof HSSFWorkbook){
                HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
                short boldWeight = hf.getBoldweight();
                short fontColor = hf.getColor();
                sb.append("style='");
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
                HSSFColor hc = palette.getColor(fontColor);
                sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
                sb.append("font-size: " + hf.getFontHeight() / 2 + "%;"); // 字体大小
//                String fontColorStr = convertToStardColor(hc);
//                if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
//                    sb.append("color:" + fontColorStr + ";"); // 字体颜色
//                }
              //表的宽度
//                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) ;
//                sb.append("width:" + columnWidth + "px;");
//                short bgColor = cellStyle.getFillForegroundColor();
//                hc = palette.getColor(bgColor);
//                String bgColorStr = convertToStardColor(hc);
//                if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
//                    sb.append("background-color:" + bgColorStr + ";"); // 背景颜色
//                }
                sb.append( getBorderStyle(palette,0,cellStyle.getBorderTop(),cellStyle.getTopBorderColor()));
                sb.append( getBorderStyle(palette,1,cellStyle.getBorderRight(),cellStyle.getRightBorderColor()));
                sb.append( getBorderStyle(palette,3,cellStyle.getBorderLeft(),cellStyle.getLeftBorderColor()));
                sb.append( getBorderStyle(palette,2,cellStyle.getBorderBottom(),cellStyle.getBottomBorderColor()));
            }
            sb.append("' ");
        }
    }

    /**
     * 单元格内容的水平对齐方式
     * @param alignment
     * @return
     */
    private static String convertAlignToHtml(short alignment) {
        String align = "center";
        switch (alignment) {
            case CellStyle.ALIGN_LEFT:
                align = "left";
                break;
            case CellStyle.ALIGN_CENTER:
                align = "center";
                break;
            case CellStyle.ALIGN_RIGHT:
                align = "right";
                break;
            default:
                break;
        }
        return align;
    }

    /**
     * 单元格中内容的垂直排列方式
     * @param verticalAlignment
     * @return
     */
    private static String convertVerticalAlignToHtml(short verticalAlignment) {
        String valign = "middle";
        switch (verticalAlignment) {
            case CellStyle.VERTICAL_BOTTOM:
                valign = "bottom";
                break;
            case CellStyle.VERTICAL_CENTER:
                valign = "center";
                break;
            case CellStyle.VERTICAL_TOP:
                valign = "top";
                break;
            default:
                break;
        }
        return valign;
    }

    private static String convertToStardColor(HSSFColor hc) {
        StringBuffer sb = new StringBuffer("");
        if (hc != null) {
            if (HSSFColor.AUTOMATIC.index == hc.getIndex()) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
            }
        }
        return sb.toString();
    }

    private static String fillWithZero(String str) {
        if (str != null && str.length() < 2) {
            return "0" + str;
        }
        return str;
        
    }
    
    static String[] bordesr={"border-top:","border-right:","border-bottom:","border-left:"};
    static String[] borderStyles={"solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid ","solid","solid","solid","solid","solid"};

    private static  String getBorderStyle(  HSSFPalette palette ,int b,short s, short t){
        if(s==0)return  bordesr[b]+borderStyles[s]+"#d0d7e5 1px;";;
        String borderColorStr = convertToStardColor( palette.getColor(t));
        borderColorStr=borderColorStr==null|| borderColorStr.length()<1?"#000000":borderColorStr;
        return bordesr[b]+borderStyles[s]+borderColorStr+" 1px;";
    }

    private static  String getBorderStyle(int b,short s, XSSFColor xc){
        if(s==0)return  bordesr[b]+borderStyles[s]+"#d0d7e5 1px;";;
        if (xc != null && !"".equals(xc)) {
            String borderColorStr = xc.getARGBHex();//t.getARGBHex();
            borderColorStr=borderColorStr==null|| borderColorStr.length()<1?"#000000":borderColorStr.substring(2);
            return bordesr[b]+borderStyles[s]+borderColorStr+" 1px;";
        }
        return "";
    }

}
