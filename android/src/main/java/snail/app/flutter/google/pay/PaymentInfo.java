package snail.app.flutter.google.pay;

import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

final class PaymentInfo {
    private String mTotalPrice;
    private String mCurrencyCode;
    private String mGateway;
    private String mGatewayMerchantId;
    private JSONArray mAllowedCardNetworks;
    private String mStripeToken;
    private String mStripeVersion;
    private String mDirectTokenPublicKey;

    PaymentInfo() {
    }

    PaymentInfo setTotalPrice(String mTotalPrice) {
        this.mTotalPrice = mTotalPrice;
        return this;
    }

    PaymentInfo setCurrencyCode(String mCurrencyCode) {
        this.mCurrencyCode = mCurrencyCode;
        return this;
    }

    PaymentInfo setGateway(String mGateway) {
        this.mGateway = mGateway;
        return this;
    }

    PaymentInfo setGatewayMerchantId(String mGatewayMerchantId) {
        this.mGatewayMerchantId = mGatewayMerchantId;
        return this;
    }

    PaymentInfo setAllowedCardNetworks(Object[] mAllowedCardNetworks) {
        try {
            this.mAllowedCardNetworks = new JSONArray(mAllowedCardNetworks);
        } catch (JSONException e) {
            return null;
        }
        return this;
    }

    PaymentInfo setStripeToken(String mStripeToken) {
        this.mStripeToken = mStripeToken;
        return this;
    }

    PaymentInfo setStripeVersion(String mStripeVersion) {
        this.mStripeVersion = mStripeVersion;
        return this;
    }

    private JSONObject createTokenizationParameters() {
        JSONObject tokenSpecs = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            if (mGateway != null && mStripeToken != null && mStripeVersion != null) {
                params.put("gateway", mGateway);
                params.put("stripe:publishableKey", mStripeToken);
                params.put("stripe:version", mStripeVersion);
                if (mGatewayMerchantId != null) {
                    params.put("gatewayMerchantId", mGatewayMerchantId);
                }
                tokenSpecs.put("type", "PAYMENT_GATEWAY");
                tokenSpecs.put("parameters", params);
            }
        } catch (JSONException e) {
            return null;
        }
        return tokenSpecs;
    }

    PaymentDataRequest createPaymentDataRequest(boolean withTokenizationParameters) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("apiVersion", 2);
            requestJson.put("apiVersionMinor", 0);

            JSONObject allowedPaymentMethod = new JSONObject();
            allowedPaymentMethod.put("type", "CARD");

            JSONObject params = new JSONObject();
            String[] auths = {"PAN_ONLY", "CRYPTOGRAM_3DS"};
            JSONArray authMethods = new JSONArray(auths);

            params.put("allowedAuthMethods", authMethods);
            params.put("allowedCardNetworks", mAllowedCardNetworks);

            allowedPaymentMethod.put("type", "CARD");
            allowedPaymentMethod.put("parameters", params);
            if (withTokenizationParameters) {
                if (createTokenizationParameters() != null) {
                    allowedPaymentMethod.put("tokenizationSpecification", createTokenizationParameters());
                }
            }
            JSONArray allowedPaymentMethods = new JSONArray();
            allowedPaymentMethods.put(allowedPaymentMethod);
            requestJson.put("allowedPaymentMethods", allowedPaymentMethods);

            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("totalPriceStatus", "FINAL");
            transactionInfo.put("totalPrice", mTotalPrice);
            transactionInfo.put("currencyCode", mCurrencyCode);

            requestJson.put("transactionInfo", transactionInfo);
            Log.d("request", String.valueOf(requestJson));
        } catch (JSONException e) {
            return null;
        }
        return PaymentDataRequest.fromJson(requestJson.toString());
    }
}