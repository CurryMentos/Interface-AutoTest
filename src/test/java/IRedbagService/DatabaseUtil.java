package IRedbagService;

import org.testng.annotations.Test;

import java.sql.*;

/**
 * Created by zengyouzu on 2019/1/31.
 * 数据库工具类
 */
public class DatabaseUtil {
    //46环境营销数据库
    /*private static String Driver = "oracle.jdbc.driver.OracleDriver";
    private static String Url = "jdbc:oracle:thin:@xxx.xx.xx.x:1521/test20";
    private static String UserName = "marketing";
    private static String PassWord = "xx";*/

    //沙箱环境营销数据库
    private static String Driver = "oracle.jdbc.driver.OracleDriver";
    private static String Url = "jdbc:oracle:thin:@xxx.xx.xx.x:1521/test20";
    private static String UserName = "marketing_app";
    private static String PassWord = "xx";

    static {
        try {
            Class.forName(Driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //连接数据库
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Url, UserName, PassWord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //关闭数据库连接
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
