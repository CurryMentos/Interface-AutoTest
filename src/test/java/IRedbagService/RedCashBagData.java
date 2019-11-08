package IRedbagService;

import IRedbagService.DataEntity.T_MUSTER_RBAG;
import com.xx.infra.skynet.testng.dataProvider.SkynetTestNGBaseTest;
import com.xx.infra.skynet.testng.dataProviderUtils.spring.BeanUtil;
import com.xx.marketing.red.cash.api.IRedbagService;
import com.xx.marketing.red.cash.api.model.Request;
import com.xx.marketing.red.cash.api.model.Response;
import com.xx.marketing.red.cash.api.model.requestModel.RedCashbagLinkRequest;
import com.xx.marketing.red.cash.api.model.responseModel.RedCashbagLinkInfoResponse;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * Created by zengyouzu on 2019/1/21.
 */
public class RedCashBagData extends SkynetTestNGBaseTest {

    public static String ProductNo() {
        String ProductNo = new String();
        ProductNo = "13355999900";
        return ProductNo;
    }

    public static String SessionKey() {
        String SessionKey = new String();
        SessionKey = "5769097019f35f9a7810af0df5387146";
        return SessionKey;
    }

    //生成现金红包
    public static String CreateRedCashBag(String ProductNo, String SessionKey) throws Exception {

        //读取service
        IRedbagService iRedbagService = BeanUtil.getBean("iRedbagService");
        //设置需要返回的值为空
        String OrderId = null;

        Request<RedCashbagLinkRequest> request = new Request<RedCashbagLinkRequest>();
        RedCashbagLinkRequest redCashbagLinkRequest = new RedCashbagLinkRequest();
        redCashbagLinkRequest.setProductNo(ProductNo);//13355999900
        redCashbagLinkRequest.setTotalAmount("100");//红包金总额
        redCashbagLinkRequest.setSubnumber("10");//子红包个数
        redCashbagLinkRequest.setBless("用户状态测试");
        redCashbagLinkRequest.setAllocation("2");//红包分配方式 1-均分，2-随机
        redCashbagLinkRequest.setTheme("collect");
        redCashbagLinkRequest.setSessionKey(SessionKey);

        //生成Token
        String key = "201502049999redbagsign";
        String token = MD5Util.md5Hex(redCashbagLinkRequest.getProductNo() + redCashbagLinkRequest.getTotalAmount() + redCashbagLinkRequest.getSubnumber() + redCashbagLinkRequest.getSessionKey() + key);
        redCashbagLinkRequest.setToken(token);

        //入参封装
        request.setModel(redCashbagLinkRequest);

        Response<RedCashbagLinkInfoResponse> res = iRedbagService.doRedCashbagLink(request);
        if (res.getResult() != null) {
            System.out.println("*****************************************");
            System.out.println("订单金额：" + res.getResult().getOrderAmount());
            System.out.println("网关回调地址：" + res.getResult().getBackMerchantUrl());
            System.out.println("accessToken签名：" + res.getResult().getAccessToken());
            System.out.println("是否可以切换账号：" + res.getResult().getSwtichacc());
            System.out.println("附加金额：" + res.getResult().getAttachAmount());
            System.out.println("产品金额：" + res.getResult().getProductAmount());
            System.out.println("商品代码：" + res.getResult().getProductId());
            System.out.println("用户IP：" + res.getResult().getUserIp());
            System.out.println("商品描述：" + res.getResult().getProductDesc());
            System.out.println("用户手机号：" + res.getResult().getCustomerId());
            System.out.println("商户调用密码：" + res.getResult().getMerchantPwd());
            System.out.println("翼支付账户号：" + res.getResult().getAccountId());
            System.out.println("接口名称：" + res.getResult().getService());
            System.out.println("sign校验：" + res.getResult().getSign());
            System.out.println("分账信息：" + res.getResult().getDivdetails());
            System.out.println("交易代码：" + res.getResult().getTransCode());
            System.out.println("订单币种：" + res.getResult().getCurType());
            System.out.println("商户代码：" + res.getResult().getMerchantId());
            System.out.println("订单号：" + res.getResult().getOrderSeq());
            System.out.println("子商户代码：" + res.getResult().getSubMerchantid());
            System.out.println("业务标识：" + res.getResult().getBusiType());//默认值04  纯业务支付
            System.out.println("订单请求时间：" + res.getResult().getOrderTime());
            System.out.println("附加信息：" + res.getResult().getAttach());
            System.out.println("签名方式：" + res.getResult().getSignType());//MD5、RSA、CA
            System.out.println("商品描述：" + res.getResult().getSubject());
            System.out.println("订单有效时间：" + res.getResult().getOrdervaliditytime());
            System.out.println("商户支付完成有后续流程时使用：" + res.getResult().getOtherflow());
            System.out.println("订单请求流水号：" + res.getResult().getOrderreqtranseq());
            System.out.println("验证会话ID：" + res.getResult().getSessionkey());
            System.out.println("错误码：" + res.getResult().getErrorCode());
            System.out.println("错误描述：" + res.getResult().getErrorMsg());
            System.out.println("token签名：" + res.getResult().getToken());

            OrderId = res.getResult().getOrderSeq();
        }
        return OrderId;
    }

    //更改现金红包状态为“已付款”
    public static String updateRedCashBagState(String OrderId) throws Exception {
        InputStream inputStream = Resources.getResourceAsStream("Configuration.xml");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        T_MUSTER_RBAG updateRedCashBagState = new T_MUSTER_RBAG();
        updateRedCashBagState.setORDERID(OrderId);

        sqlSession.update("RedCashBagTest.updateRedCashBagState", updateRedCashBagState);
        sqlSession.commit();

        return OrderId;
    }

    public static void main(String[] args) throws Exception {
        CreateRedCashBag(ProductNo(), SessionKey());
    }
}
