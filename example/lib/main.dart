import 'package:flutter/material.dart';
import 'package:flutter_google_pay/flutter_google_pay.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  BuildContext scaffoldContext;
  var data;

  void init() async {
    if (!(await FlutterGooglePay.isAvailable('test'))) {
      _showToast(scaffoldContext, 'Google pay not available');
    } else {
      data = 'google pay is available';
      setState(() {
        
      });
    }
  }

  @override
  void initState() {
    super.initState();
    init();
  }

  _makeStripePayment() async {
    PaymentItem paymentItem = PaymentItem(
      stripeToken: 'tok_visa',
      stripeVersion: "2018-11-08",
      currencyCode: "USD",
      amount: "0.10",
      gateway: "example",
      gatewayMerchantId: "exampleMerchant",
      allowedCardNetworks: [PaymentNetwork.VISA, PaymentNetwork.MASTERCARD, PaymentNetwork.AMEX]
    );

    FlutterGooglePay.makePayment(paymentItem).then((Result result) {
      if (result.status == ResultStatus.SUCCESS) {
        data = result.data;
        setState(() {});
        _showToast(scaffoldContext, 'Success');
      } else {
        print(result.error);
        data = result.status;
        setState(() {});
      }
    }).catchError((dynamic error) {
      print('error');
      _showToast(scaffoldContext, error.toString());
    });
  }

  _makeCustomPayment() async {

    ///docs https://developers.google.com/pay/api/android/guides/tutorial
    PaymentBuilder pb = PaymentBuilder()
      // ..addDirectTokenizationSpecification('BOl2qpEDiwjhdJ+CtGr7EMhjW9Guma1looa3CttysmudCnDlVKV7gDbJCsvdXCd+oAGgEyhQ7nLyd8nw08uNYfA=')
      ..addGateway('stripe')
      ..addStripeKey('pk_test', "2019-12-03")
      ..addTransactionInfo("1", 'HKD', 'HK')
      ..addAllowedCardAuthMethods([AuthMethod.PAN_ONLY, AuthMethod.CRYPTOGRAM_3DS])
      ..addAllowedCardNetworks([PaymentNetwork.VISA, PaymentNetwork.MASTERCARD, PaymentNetwork.AMEX])
      ..addMerchantInfo('myMerchant');

    if (pb.build() != null) 
      FlutterGooglePay.makeCustomPayment(pb.build()).then((Result result) {
        if (result.status == ResultStatus.SUCCESS) {
          data = result.data['paymentMethodData']['tokenizationData']['token'];
          setState(() {});
          _showToast(scaffoldContext, 'Success');
        } else if (result.error != null) {
          _showToast(scaffoldContext, result.error);
        }
      }).catchError((error) {
        //TODO
      });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: Builder(builder: (context) {
            scaffoldContext = context;
            return Center(
              child: Column(
                children: <Widget>[
                  FlatButton(
                    onPressed: _makeStripePayment,
                    child: Text('Stripe pay'),
                  ),
                  FlatButton(
                    onPressed: _makeCustomPayment,
                    child: Text('Custom pay'),
                  ),
                  Text(data.toString() ?? ''),
                ],
              ));
          })),
    );
  }

  void _showToast(BuildContext context, String message) {
    final scaffold = Scaffold.of(context);
    scaffold.showSnackBar(SnackBar(
      content: Text(message),
      action: SnackBarAction(
        label: 'UNDO',
        onPressed: () {},
      ),
    ));
  }
}

