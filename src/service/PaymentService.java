package service;

import dao.BookingDAO;
import dao.PaymentDAO;
import dao.RoomDAO;
import model.Booking;
import model.Payment;
import model.Room;
import util.AuditLogger;
import util InputValidator;

import java.util.Date;
//Aljory
public class PaymentService{

    private PaymentDAO paymentDAO = new PaymentDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private InputValidator inputValidator = new InputValidator();
    private AuditLogger auditLogger = new AuditLogger();

    //Payment Processing
    public boolean processPayment(int bookingId, double amount, String method){
        if (!inputValidator.validateAmount(amount))return false;
        if (!inputValidator.validateText(method)) return false;

        double expectedTotal = calculateTotal(bookingId);
        if(amount != expectedTotal){
            System.err.println("Amount does not match. Expected Total: "+expectedTotal);
            return false;
        }

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setTimestamp(new date());
        payment.setStatus("PAID");

        boolean saved = paymentDAO.savePayment(payment);
        if(saved){
            auditLogger.log(bookingId,"PROCESS_PAYMENT",
                    "Payment of "+ amount +" via "+ method +" for booking "+bookingId);
        }
        return saved;
    }

    //To calculate Total
    public double calculateTotal(int bookingId){
        Booking booking = bookingDAO.findById(bookingId);
        if(booking == null) return 0;

        Room room = roomDAO.findById(booking.getRoomId());
        long diff = booking.getCheckOutDate().getTime()-booking.getCheckInDate().getTime();
        long nights = diff / (1000*60*60*24);
        return room.getRate()*nights;
    }
}
