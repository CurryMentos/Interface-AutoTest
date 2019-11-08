package IRedbagService;

import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.RedCashbagLinkRequest;
import com.xx.marketing.red.cash.api.model.responseModel.RedCashbagLinkInfoResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by zengyouzu on 2019/1/17.
 * 发现金红包
 */
public class DoRedCashbagLinkTest extends SkynetTestNGBaseTest {
    String ProductNo;
    String SessionKey;

    @BeforeClass
    public void testBefore() throws Exception {
        ProductNo = RedCashBagData.ProductNo();
        SessionKey = RedCashBagData.SessionKey();
    }

    @Test(dataProvider = "skynetExcelDataProvider")
    public void DoRedCashbagLink(Map<String, String> testData) throws Exception {
        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //request映射
        Request<RedCashbagLinkRequest> request = new Request<RedCashbagLinkRequest>();
        RedCashbagLinkRequest redCashbagLinkRequest = buildJavaBeanModel(RedCashbagLinkRequest.class, testData, this);

        //读取Excel的Status列，判断取值情况
        String Status = testData.get("Status");
            if ("normal".equals(Status)) {
            redCashbagLinkRequest.setProductNo(ProductNo);
            redCashbagLinkRequest.setSessionKey(SessionKey);
        } else if ("no ProductNo".equals(Status)) {
            redCashbagLinkRequest.setSessionKey(SessionKey);
        } else if ("no SessionKey".equals(Status)) {
            redCashbagLinkRequest.setProductNo(ProductNo);
        }

        //生成token
        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(redCashbagLinkRequest.getProductNo() + redCashbagLinkRequest.getTotalAmount() + redCashbagLinkRequest.getSubnumber() + redCashbagLinkRequest.getSessionKey() + key);
        redCashbagLinkRequest.setToken(token);
        request.setModel(redCashbagLinkRequest);
        System.out.println("请求:" + redCashbagLinkRequest);
        System.out.println("**********************");

        //response映射
        Response responseExpected = buildJavaBeanModel(Response.class, testData, this);
        System.out.println("预期结果:" + responseExpected);
        System.out.println("**********************");
        Response<RedCashbagLinkInfoResponse> responseActual = iRedbagService.doRedCashbagLink(request);
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
