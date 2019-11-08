package IRedbagService;

import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.RedCashbagReceRequest;
import com.xx.marketing.red.cash.api.model.responseModel.RedCashbagReceInfoResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by zengyouzu on 2019/2/13.
 */
public class DoRedCashbagReceTest extends SkynetTestNGBaseTest {
    String ProductNo;
    String SessionKey;
    Date now = new Date();
    String ReceYear = new SimpleDateFormat("yyyy").format(now);

    @BeforeClass
    public void testBefore() throws Exception {
        ProductNo = RedCashBagData.ProductNo();
        SessionKey = RedCashBagData.SessionKey();
    }

    @Test(dataProvider = "skynetExcelDataProvider")
    public void DoRedCashbagRece(Map<String, String> testData) throws Exception {
        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //request映射
        Request<RedCashbagReceRequest> request = new Request<RedCashbagReceRequest>();
        RedCashbagReceRequest redCashbagReceRequest = buildJavaBeanModel(RedCashbagReceRequest.class, testData, this);

        //读取Excel的Status列，判断取值情况
        String Status = testData.get("Status");
        if ("normal".equals(Status)) {
            redCashbagReceRequest.setProductNo(ProductNo);
            redCashbagReceRequest.setReceYear(ReceYear);
            redCashbagReceRequest.setSessionKey(SessionKey);
        } else if ("no ProductNo".equals(Status)) {
            redCashbagReceRequest.setReceYear(ReceYear);
            redCashbagReceRequest.setSessionKey(SessionKey);
        } else if ("no ReceYear".equals(Status)) {
            redCashbagReceRequest.setProductNo(ProductNo);
            redCashbagReceRequest.setSessionKey(SessionKey);
        } else if ("no SessionKey".equals(Status)) {
            redCashbagReceRequest.setProductNo(ProductNo);
            redCashbagReceRequest.setReceYear(ReceYear);
        }

        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(redCashbagReceRequest.getProductNo() + redCashbagReceRequest.getReceYear() + redCashbagReceRequest.getSessionKey() + key);
        redCashbagReceRequest.setToken(token);
        request.setModel(redCashbagReceRequest);

        System.out.println("请求：" + redCashbagReceRequest);
        System.out.println("**********************");

        //response映射
        Response responseExpected = buildJavaBeanModel(Response.class, testData, this);
        System.out.println("预期结果：" + responseExpected);
        System.out.println("**********************");
        Response<RedCashbagReceInfoResponse> responseActual = iRedbagService.doRedCashbagRece(request);
        System.out.println("实际结果：" + responseActual);
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
