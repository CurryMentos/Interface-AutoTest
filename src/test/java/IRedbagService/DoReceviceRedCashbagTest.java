package IRedbagService;

import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.ReceviceRedCashbagRequest;
import com.xx.marketing.red.cash.api.model.responseModel.ReceviceRedCashbagResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by zengyouzu on 2019/2/1.
 * 输入手机号领取红包
 */
public class DoReceviceRedCashbagTest extends SkynetTestNGBaseTest {
    String ProductNo;
    String SessionKey;
    String OrderId;
    String Value;

    @BeforeClass
    public void testBefore() throws Exception {
        ProductNo = RedCashBagData.ProductNo();
        SessionKey = RedCashBagData.SessionKey();
        OrderId = RedCashBagData.CreateRedCashBag(ProductNo, SessionKey);
        RedCashBagData.updateRedCashBagState(OrderId);
        //以生成的红包ID查询数据库
        String SQL = "select mu.*,mu.rowid from t_muster_rbag mu where mu.orderid = " + OrderId;
        //获取查到的字段值
        Value = SQLOperation.getValue(SQL);
    }

    @Test(dataProvider = "skynetExcelDataProvider")
    public void DoReceviceRedCashbag(Map<String, String> testData) throws Exception {
        //从取到的值中截取“MacKey”
        int beginIndex = Value.indexOf("macKey=");
        int endIndex = Value.length();
        String MacKey = Value.substring(beginIndex + 7, endIndex);

        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //request映射
        Request<ReceviceRedCashbagRequest> request = new Request<ReceviceRedCashbagRequest>();
        ReceviceRedCashbagRequest receviceRedCashbagRequest = buildJavaBeanModel(ReceviceRedCashbagRequest.class, testData, this);

        //读取Excel的Status列，判断取值情况
        String Status = testData.get("Status");
        if ("normal".equals(Status)) {
            receviceRedCashbagRequest.setProductNo(ProductNo);
            receviceRedCashbagRequest.setOrderId(OrderId);
            receviceRedCashbagRequest.setMacKey(MacKey);
        } else if ("no ProductNo".equals(Status)) {
            receviceRedCashbagRequest.setOrderId(OrderId);
            receviceRedCashbagRequest.setMacKey(MacKey);
        } else if ("no OrderId".equals(Status)) {
            receviceRedCashbagRequest.setProductNo(ProductNo);
            receviceRedCashbagRequest.setMacKey(MacKey);
        } else if ("no MacKey".equals(Status)) {
            receviceRedCashbagRequest.setProductNo(ProductNo);
            receviceRedCashbagRequest.setOrderId(OrderId);
        }

        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(receviceRedCashbagRequest.getProductNo() + receviceRedCashbagRequest.getOrderId() + receviceRedCashbagRequest.getMacKey() + key);
        receviceRedCashbagRequest.setToken(token);
        request.setModel(receviceRedCashbagRequest);
        System.out.println("请求:" + receviceRedCashbagRequest);
        System.out.println("**********************");

        //response映射
        Response responseExpected = buildJavaBeanModel(Response.class, testData, this);
        System.out.println("预期结果:" + responseExpected);
        System.out.println("**********************");
        Response<ReceviceRedCashbagResponse> responseActual = iRedbagService.doReceviceRedCashbag(request);
        System.out.println("实际结果:" + responseActual);
        System.out.println("**********************");

        if (responseExpected.getErrorMsg() == null || responseExpected.getErrorMsg().equals("null")) {
            responseExpected.setErrorMsg("成功");
        }
        if (responseActual.getErrorMsg() == null || responseActual.getErrorMsg().equals("null")) {
            responseActual.setErrorMsg("成功");
        }

        System.out.println("用例ID：" + testData.get("TEST_CASE_ID"));
        System.out.println("是否成功：" + responseActual.isSuccess());
        System.out.println("错误码：" + responseActual.getErrorCode());
        System.out.println("错误信息：" + responseActual.getErrorMsg());
        System.out.println("结果集：" + responseActual.getResult());

        //断言
        Assert.assertEquals(responseActual.getErrorMsg(), responseExpected.getErrorMsg());
    }
}
