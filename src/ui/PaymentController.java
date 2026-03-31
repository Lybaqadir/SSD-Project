package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import service.PaymentService;

public class PaymentController{

    @FMXL private Label lblTotalAmount;
    @FMXL private ComboBox<String> cmbPaymentMethod;
    @FMXL private Button btnConfirmPayment;
    @FMXL private Label lblStatus;

    private PaymentService paymentService = new PaymentService();
    private int currentBookingId;

    @FMXL
    public void initialize(){
        cmbPaymentMethod.getItems().addAll("Cash","Card","Online");
    }

    public void handleConfirmPayment(){
        String method = cmbPaymentMethod.getValue();

        if(method==null || method.isEmpty()){
            lblStatus.setText("Please select a payment method");
            return;
        }

        double amount = paymentService.calculateTotal(currentBookingId);
        boolean success = paymentService.processPayment(currentBookingId, amount, method);

        if(success){
            lblStatus.setText("Payment Successful");
            btnConfirmPayment.setDisable(true);
        }
        else{
            lblStatus.setText("Payment failed please Try Again.");
        }
    }
}