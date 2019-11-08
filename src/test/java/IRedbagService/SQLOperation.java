package IRedbagService;

import java.sql.*;

/**
 * Created by zengyouzu on 2019/2/11.
 * 数据库操作方法类
 */
public class SQLOperation extends DatabaseUtil {
    private String ProductNo;
    private String SessionKey;
    private String OrderId;
    private String SQL;

    //数据库操作
    public String getSQL() throws Exception {
        OrderId = RedCashBagData.CreateRedCashBag(ProductNo, SessionKey);
        if (OrderId != null) {
            String SQL = "select mu.*,mu.rowid from t_muster_rbag mu where mu.orderid = " + OrderId;
            System.out.println(SQL);
        } else {
            System.out.println("生成红包失败，获取OrderId失败");
        }
        return SQL;
    }

    public static String getValue(String SQL) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        String columnValue = null;
        try {
            preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery(SQL);
            ResultSetMetaData data = resultSet.getMetaData();

            int i;
            for (i = 1; i <= data.getColumnCount(); i++) {
                // 获得指定列的列名
                String columnName = data.getColumnName(i);
                // 当找到表中具体某个字段时跳出循环
                if (columnName.equals("URLMACKEY")) {
                    break;
                }
            }
            System.out.println("该字段位于表中第" + i + "列");

            // 获取第N列的值
            while (resultSet.next()) {
                columnValue = resultSet.getString(i);
                if (columnValue != null) {
                    System.out.println("获得列" + i + "的值:" + columnValue);
                } else {
                    System.out.println("所取字段为null");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection(connection);
        return columnValue;
    }

    /*public static void main(String[] args) throws Exception {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(SQL);
            resultSet = preparedStatement.executeQuery(SQL);
            ResultSetMetaData data = resultSet.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                // 获得所有列的数目及实际列数
                int columnCount = data.getColumnCount();
                // 获得指定列的名称
                String columnName = data.getColumnName(i);
                //获得指定列的数据类型
                int columnType = data.getColumnType(i);
                // 获得指定列的数据类型名
                String columnTypeName = data.getColumnTypeName(i);
                // 在数据库中类型的最大字符个数
                int columnDisplaySize = data.getColumnDisplaySize(i);
                //指定列是否只读
                boolean isReadOnly = data.isReadOnly(i);
                //指定列的值是否为空（0不可，1可）
                int isNullable = data.isNullable(i);

                System.out.println("***********************************************");
                System.out.println("总列数:" + columnCount);
                System.out.println("第" + i + "列的字段名称:" + columnName);
                System.out.println("第" + i + "列的字段数据类型:" + columnType);
                System.out.println("第" + i + "列的数据类型名:" + columnTypeName);
                System.out.println("第" + i + "列在数据库中类型的最大字符个数:" + columnDisplaySize);
                System.out.println("第" + i + "列的数据是否只读:" + isReadOnly);
                System.out.println("第" + i + "列的数据是否为空:" + isNullable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
