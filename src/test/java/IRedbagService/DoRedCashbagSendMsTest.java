package IRedbagService;

import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.RedCashbagSendMsRequest;
import com.xx.marketing.red.cash.api.model.responseModel.RedCashbagSendMsResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by zengyouzu on 2019/2/13.
 */
public class DoRedCashbagSendMsTest extends SkynetTestNGBaseTest {
    String ProductNo;

    @BeforeClass
    public void testBefore() throws Exception {
        ProductNo = RedCashBagData.ProductNo();
    }

    @Test(dataProvider = "skynetExcelDataProvider")
    public void DoRedCashbagSendMs(Map<String, String> testData) throws Exception{
        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //request映射
        Request<RedCashbagSendMsRequest> request = new Request<RedCashbagSendMsRequest>();
        RedCashbagSendMsRequest redCashbagSendMsRequest = buildJavaBeanModel(RedCashbagSendMsRequest.class,testData,this);

        //读取Excel的Status列，判断取值情况
        String Status = testData.get("Status");
        if ("normal".equals(Status)) {
            redCashbagSendMsRequest.setProductNo(ProductNo);
        }

        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(redCashbagSendMsRequest.getProductNo() +key);
        redCashbagSendMsRequest.setToken(token);
        request.setModel(redCashbagSendMsRequest);
        System.out.println("请求:" + redCashbagSendMsRequest);
        System.out.println("**********************");

        //response映射
        Response responseExpected = buildJavaBeanModel(Response.class, testData, this);
        System.out.println("预期结果:" + responseExpected);
        System.out.println("**********************");
        Response<RedCashbagSendMsResponse> responseActual = iRedbagService.doRedCashbagSendMs(request);
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
