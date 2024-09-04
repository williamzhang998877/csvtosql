 public class WorldServiceImpl{ 
    private static int rows;
    private static String csvSplitBy = "\t";
    public void updateFullRecords () {
        File file = null;
        try {
            //获取输入流
            String path = "F:\\googledownload\\aa.csv";
            file = new File(path);


            rows = 0;
            String data;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.forName("ISO-8859-15")))) {
                while ((data = br.readLine()) != null) {


                    //scanner.useDelimiter(delimiter);
                    // 处理每一行的数据
                    rows++;
                    if (rows == 1){
                        continue;
                    }
                    //System.out.println("insert into rows:" + rows + data);
                    //String str = CodeUtil.isoToUtf8(data);
                    World world = convertToWorld(data);

                    //System.out.println("convert:" + world);
                    World worldRecord = queryOneRecord(world);
                    if (CommonUtils.isNullOrEmpty(worldRecord)) {
                        worldCheck.setCreateTime(TimeUtil.getTodayStartTime());
                        insertOneRecord(world);
                    } else {
                        worldCheck.setUpdateTime(TimeUtil.getTodayStartTime());
                        updateOneRecord(world);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = null;
            if (e instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e;
                String cellMsg = "";
                CellData<?> cellData = excelDataConvertException.getCellData();
                //这里有一个celldatatype的枚举值,用来判断CellData的数据类型
                CellDataTypeEnum type = cellData.getType();
                if (type.equals(CellDataTypeEnum.NUMBER)) {
                    cellMsg = cellData.getNumberValue().toString();
                } else if (type.equals(CellDataTypeEnum.STRING)) {
                    cellMsg = cellData.getStringValue();
                } else if (type.equals(CellDataTypeEnum.BOOLEAN)) {
                    cellMsg = cellData.getBooleanValue().toString();
                }
                errorMsg = String.format("excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!请检查其他的记录是否有同类型的错误!", excelDataConvertException.getRowIndex() + 1, excelDataConvertException.getColumnIndex() + 1, cellMsg);
                log.error(errorMsg);
            }
        } finally {

        }
    }

    private World convertToWorld (String data){
        String[] fields = {"uid", "lastName", "firstName"};
        int index = -1;
        HashMap<String, String> hm = new HashMap<String, String>();
        String[] arrData = data.split(csvSplitBy);
        for (String value : arrData) {
            index++;
            if (index >= fields.length) {
                System.out.println("index:" + index);
                System.out.println("data.get(index):" + value);
            }
            hm.put(fields[index], value);
        }
        World world = MapperUtils.map2pojo(hm, World.class);
        return world;
    }   
}