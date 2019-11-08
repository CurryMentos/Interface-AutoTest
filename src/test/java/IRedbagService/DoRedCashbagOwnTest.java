package IRedbagService;

import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.RedCashbagOwnRequest;
import com.xx.marketing.red.cash.api.model.responseModel.RedCashbagOwnInfoResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by zengyouzu on 2019/1/18.
 * 查询已发现金红包
 */
public class DoRedCashbagOwnTest extends SkynetTestNGBaseTest {
    String ProductNo;
    String SessionKey;
    String OrderId;
    Date now = new Date();
    String SendYear = new SimpleDateFormat("yyyy").format(now);

    @BeforeClass
    public void testBefore() throws Exception {
        ProductNo = RedCashBagData.ProductNo();
        SessionKey = RedCashBagData.SessionKey();
        OrderId = RedCashBagData.CreateRedCashBag(ProductNo, SessionKey);
        RedCashBagData.updateRedCashBagState(OrderId);
    }

    @Test(dataProvider = "skynetExcelDataProvider")
    public void DoRedCashbagOwn(Map<String, String> testData) throws Exception {
        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //request映射
        Request<RedCashbagOwnRequest> request = new Request<RedCashbagOwnRequest>();
        RedCashbagOwnRequest redCashbagOwnRequest = buildJavaBeanModel(RedCashbagOwnRequest.class, testData, this);

        //读取Excel的Status列，判断取值情况
        String Status = testData.get("Status");
        if ("normal".equals(Status)) {
            redCashbagOwnRequest.setProductNo(ProductNo);
            redCashbagOwnRequest.setSendYear(SendYear);
            redCashbagOwnRequest.setSessionKey(SessionKey);
        } else if ("no ProductNo".equals(Status)) {
            redCashbagOwnRequest.setSendYear(SendYear);
            redCashbagOwnRequest.setSessionKey(SessionKey);
        } else if ("no SendYear".equals(Status)) {
            redCashbagOwnRequest.setProductNo(ProductNo);
            redCashbagOwnRequest.setSessionKey(SessionKey);
        } else if ("no SessionKey".equals(Status)) {
            redCashbagOwnRequest.setProductNo(ProductNo);
            redCashbagOwnRequest.setSendYear(SendYear);
        }

        //生成token
        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(redCashbagOwnRequest.getProductNo() + redCashbagOwnRequest.getSessionKey() + key);
        redCashbagOwnRequest.setToken(token);
        request.setModel(redCashbagOwnRequest);
        System.out.println("请求：" + redCashbagOwnRequest);
        System.out.println("**********************");

        //response映射
        Response responseExpected = buildJavaBeanModel(Response.class, testData, this);
        System.out.println("预期结果：" + responseExpected);
        System.out.println("**********************");
        Response<RedCashbagOwnInfoResponse> responseActual = iRedbagService.doRedCashbagOwn(request);
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
